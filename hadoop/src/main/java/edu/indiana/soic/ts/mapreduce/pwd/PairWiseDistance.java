/**
 * Software License, Version 1.0
 * 
 * Copyright 2003 The Trustees of Indiana University.  All rights reserved.
 * 
 *
 *Redistribution and use in source and binary forms, with or without 
 *modification, are permitted provided that the following conditions are met:
 *
 *1) All redistributions of source code must retain the above copyright notice,
 * the list of authors in the original source code, this list of conditions and
 * the disclaimer listed in this license;
 *2) All redistributions in binary form must reproduce the above copyright 
 * notice, this list of conditions and the disclaimer listed in this license in
 * the documentation and/or other materials provided with the distribution;
 *3) Any documentation included with all redistributions must include the 
 * following acknowledgement:
 *
 *"This product includes software developed by the Community Grids Lab. For 
 * further information contact the Community Grids Lab at 
 * http://communitygrids.iu.edu/."
 *
 * Alternatively, this acknowledgement may appear in the software itself, and 
 * wherever such third-party acknowledgments normally appear.
 * 
 *4) The name Indiana University or Community Grids Lab or NaradaBrokering, 
 * shall not be used to endorse or promote products derived from this software 
 * without prior written permission from Indiana University.  For written 
 * permission, please contact the Advanced Research and Technology Institute 
 * ("ARTI") at 351 West 10th Street, Indianapolis, Indiana 46202.
 *5) Products derived from this software may not be called NaradaBrokering, 
 * nor may Indiana University or Community Grids Lab or NaradaBrokering appear
 * in their name, without prior written permission of ARTI.
 * 
 *
 * Indiana University provides no reassurances that the source code provided 
 * does not infringe the patent or any other intellectual property rights of 
 * any other entity.  Indiana University disclaims any liability to any 
 * recipient for claims brought by any other entity based on infringement of 
 * intellectual property rights or otherwise.  
 *
 *LICENSEE UNDERSTANDS THAT SOFTWARE IS PROVIDED "AS IS" FOR WHICH NO 
 *WARRANTIES AS TO CAPABILITIES OR ACCURACY ARE MADE. INDIANA UNIVERSITY GIVES
 *NO WARRANTIES AND MAKES NO REPRESENTATION THAT SOFTWARE IS FREE OF 
 *INFRINGEMENT OF THIRD PARTY PATENT, COPYRIGHT, OR OTHER PROPRIETARY RIGHTS. 
 *INDIANA UNIVERSITY MAKES NO WARRANTIES THAT SOFTWARE IS FREE FROM "BUGS", 
 *"VIRUSES", "TROJAN HORSES", "TRAP DOORS", "WORMS", OR OTHER HARMFUL CODE.  
 *LICENSEE ASSUMES THE ENTIRE RISK AS TO THE PERFORMANCE OF SOFTWARE AND/OR 
 *ASSOCIATED MATERIALS, AND TO THE PERFORMANCE AND VALIDITY OF INFORMATION 
 *GENERATED USING SOFTWARE.
 */

package edu.indiana.soic.ts.mapreduce.pwd;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import edu.indiana.soic.ts.utils.TSConfiguration;
import edu.indiana.soic.ts.utils.Utils;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.SequenceFile.CompressionType;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PairWiseDistance {
	private static final Logger LOG = LoggerFactory.getLogger(PairWiseDistance.class);
	private int blockSize;
	private String distFunc;
	private String interDistDir;
	private String distDir;
	private String vectDir;

	public static void main(String[] args) throws Exception {
		PairWiseDistance pwd = new PairWiseDistance();
		pwd.configure(args);
		pwd.submitJob();
	}

	public int execJob(Configuration conf, String sequenceFileFullPath, String sequenceFile, String distDir) throws Exception {
		/* input parameters */
        LOG.info(sequenceFileFullPath);
		Job job = new Job(conf, "Pairwise-calc-" + sequenceFile);

		/* create the base dir for this job. Delete and recreates if it exists */
		Path hdMainDir = new Path(distDir + "/" + sequenceFile);
        FileSystem fs = FileSystem.get(conf);
		fs.delete(hdMainDir, true);
		Path hdInputDir = new Path(hdMainDir, "data");
		if (!fs.mkdirs(hdInputDir)) {
			throw new IOException("Mkdirs failed to create " + hdInputDir.toString());
		}

		int noOfSequences = getNoOfSequences(sequenceFileFullPath, fs);
		int noOfDivisions = (int) Math.ceil(noOfSequences / (double) blockSize);
		int noOfBlocks = (noOfDivisions * (noOfDivisions + 1)) / 2;
		LOG.info("No of divisions :" + noOfDivisions + "\nNo of blocks :" +
				noOfBlocks + "\nBlock size :" + blockSize);

		// Retrieving the configuration form the job to set the properties
		// Setting properties to the original conf does not work (possible
		// Hadoop bug)
		Configuration jobConf = job.getConfiguration();

		// Input dir in HDFS. Create this in newly created job base dir
		Path inputDir = new Path(hdMainDir, "input");
		if (!fs.mkdirs(inputDir)) {
			throw new IOException("Mkdirs failed to create "
					+ inputDir.toString());
		}

		Long dataPartitionStartTime = System.nanoTime();
		partitionData(sequenceFileFullPath, noOfSequences, blockSize, fs,
				noOfDivisions, jobConf, inputDir);

		distributeData(blockSize, conf, fs, hdInputDir, noOfDivisions);

		long dataPartTime = (System.nanoTime() - dataPartitionStartTime) / 1000000;
		LOG.info("Data Partition & Scatter Completed in (ms):"
				+ dataPartTime);

		// Output dir in HDFS
		Path hdOutDir = new Path(hdMainDir, "out");

		jobConf.setInt(Constants.BLOCK_SIZE, blockSize);
		jobConf.setInt(Constants.NO_OF_DIVISIONS, noOfDivisions);
		jobConf.setInt(Constants.NO_OF_SEQUENCES, noOfSequences);
        jobConf.set(Constants.DIST_FUNC, distFunc);

		job.setJarByClass(PairWiseDistance.class);
		job.setMapperClass(SWGMap.class);
		job.setReducerClass(SWGReduce.class);
		job.setOutputKeyClass(LongWritable.class);
		job.setOutputValueClass(SWGWritable.class);
		FileInputFormat.setInputPaths(job, hdInputDir);
		FileOutputFormat.setOutputPath(job, hdOutDir);
		job.setInputFormatClass(SequenceFileInputFormat.class);
		job.setOutputFormatClass(SequenceFileOutputFormat.class);
		job.setNumReduceTasks(noOfDivisions);

		long startTime = System.currentTimeMillis();
		int exitStatus = job.waitForCompletion(true) ? 0 : 1;
		double executionTime = (System.currentTimeMillis() - startTime) / 1000.0;
		LOG.info("Job Finished in " + executionTime + " seconds");
		LOG.info("# #seq\t#blockS\tTtime\tinput\tdataDistTime\toutput" + noOfSequences + "\t" + noOfBlocks + "\t"
				+ executionTime + "\t" + sequenceFileFullPath + "\t" + dataPartTime
				+ "\t" + hdMainDir);

		return exitStatus;
	}

	public void configure(String[] args) {
		String  configFile = Utils.getConfigurationFile(args);
		TSConfiguration tsConfiguration = new TSConfiguration(configFile);
		Map tsConf = tsConfiguration.getConf();

		this.blockSize = (int) tsConf.get(TSConfiguration.MATRIX_BLOCK_SIZE);
		this.distFunc = (String) tsConf.get(TSConfiguration.DISTANCE_FUNCTION);
		this.interDistDir = tsConfiguration.getInterMediateDistanceDir();
		this.distDir = tsConfiguration.getDistDir();
		this.vectDir = tsConfiguration.getVectorDir();
	}

	public void submitJob() throws IOException {
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);
		FileStatus[] status = fs.listStatus(new Path(vectDir));
		for (int i = 0; i < status.length; i++) {
			String sequenceFile = status[i].getPath().getName();
			String sequenceFileFullPath = vectDir + "/" + sequenceFile;
			try {
				execJob(conf, sequenceFileFullPath, sequenceFile, interDistDir);
				concatOutput(conf, sequenceFile, interDistDir, distDir);
			} catch (Exception e) {
				String message = "Failed to executed PWD calculation:" + sequenceFileFullPath + " " + interDistDir;
				LOG.info(message, e);
				throw new RuntimeException(message);
			}
		}
	}

	private class OutFile implements Comparable<OutFile> {
		int no;
		String file;

		public OutFile(int no, String file) {
			this.no = no;
			this.file = file;
		}

		@Override
		public int compareTo(OutFile o) {
			return o.no - this.no;
		}
	}

	public void concatOutput(Configuration conf, String sequenceFile, String distDirIntermediate, String distDir) throws IOException {
		FileSystem fs = FileSystem.get(conf);
		Path outDir = new Path(distDirIntermediate + "/" + sequenceFile + "/out");
		FileStatus[] status = fs.listStatus(outDir);
		List<OutFile> outFiles = new ArrayList<OutFile>();
		for (int i = 0; i < status.length; i++) {
			String name = status[i].getPath().getName();
			String split[] = name.split("_");
			if (split.length > 2 && split[0].equals("row")) {
				OutFile o = new OutFile(Integer.parseInt(split[1]), name);
				outFiles.add(o);
			}
		}

		Collections.sort(outFiles);
		String destFile = distDir + "/" + sequenceFile;
		Path outFile = new Path(destFile);
		FSDataOutputStream outputStream = fs.create(outFile);
		for (OutFile o : outFiles) {
			Path inFile = new Path(outDir, o.file);
			FSDataInputStream inputStream = fs.open(inFile);
			IOUtils.copy(inputStream, outputStream);
			inputStream.close();
		}
		outputStream.flush();
		outputStream.close();
	}

	private void distributeData(int blockSize, Configuration conf,
			FileSystem fs, Path hdInputDir, int noOfDivisions) throws IOException {
		// Writing block meta data to for each block in a separate file so that
		// Hadoop will create separate Map tasks for each block..
		// Key : block number
		// Value: row#column#isDiagonal#base_file_name
		// TODO : find a better way to do this.
		for (int row = 0; row < noOfDivisions; row++) {
			for (int column = 0; column < noOfDivisions; column++) {
				// using the load balancing algorithm to select the blocks
				// include the diagonal blocks as they are blocks, not
				// individual pairs
				if (((row >= column) & ((row + column) % 2 == 0))
						| ((row <= column) & ((row + column) % 2 == 1))) {
					Path vFile = new Path(hdInputDir, "data_file_" + row + "_"
							+ column);
					SequenceFile.Writer vWriter = SequenceFile.createWriter(fs,
							conf, vFile, LongWritable.class, Text.class,
							CompressionType.NONE);

					boolean isDiagonal = false;
					if (row == column) {
						isDiagonal = true;
					}
					String value = row + Constants.BREAK + column
							+ Constants.BREAK + isDiagonal + Constants.BREAK
							+ Constants.HDFS_SEQ_FILENAME;
					vWriter.append(new LongWritable(row * blockSize + column),
							new Text(value));
					vWriter.close();
				}
			}
		}
	}

	private int getNoOfSequences(String sequenceFile, FileSystem fs) throws FileNotFoundException,
			IOException, URISyntaxException {
		Path path = new Path(sequenceFile);
		int count = 0;
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fs.open(path)));
		while ((bufferedReader.readLine()) != null) {
			count++;
		}
		bufferedReader.close();
		return count;
	}

	private void partitionData(String sequenceFile, int noOfSequences,
			int blockSize, FileSystem fs, int noOfDivisions,
			Configuration jobConf, Path inputDir) throws FileNotFoundException,
			IOException, URISyntaxException {
		// Break the sequences file in to parts based on the block size. Stores
		// the parts in HDFS and add them to the Hadoop distributed cache.
        Path path = new Path(sequenceFile);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fs.open(path)));

		LOG.info("noOfDivisions : " + noOfDivisions);
		LOG.info("blockSize : " + blockSize);
		for (int partNo = 0; partNo < noOfDivisions; partNo++) {
			//
			String filePartName = Constants.HDFS_SEQ_FILENAME + "_" + partNo;
			Path inputFilePart = new Path(inputDir, filePartName);
			OutputStream partOutStream = fs.create(inputFilePart);
			BufferedWriter bufferedWriter = new BufferedWriter(
					new OutputStreamWriter(partOutStream));

			for (int sequenceIndex = 0; ((sequenceIndex < blockSize) & (sequenceIndex
					+ (partNo * blockSize) < noOfSequences)); sequenceIndex++) {
				String line;
                line = bufferedReader.readLine();
                if (line == null) {
					throw new IOException(
							"Cannot read the sequence from input file.");
				}
				// write the sequence name
				bufferedWriter.write(line);
				bufferedWriter.newLine();
			}
			bufferedWriter.flush();
			bufferedWriter.close();
			// Adding the sequences file to Hadoop cache
			URI cFileURI = new URI(inputFilePart.toUri() + "#" + filePartName);
			DistributedCache.addCacheFile(cFileURI, jobConf);
			DistributedCache.createSymlink(jobConf);
		}
	}
}