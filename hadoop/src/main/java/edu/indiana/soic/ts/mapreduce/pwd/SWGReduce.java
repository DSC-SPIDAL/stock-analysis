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

import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Reducer;

public class SWGReduce extends Reducer<LongWritable, SWGWritable, LongWritable, SWGWritable> {

	public void reduce(LongWritable key, Iterable<SWGWritable> values,
			Context context) throws IOException {
		long startTime = System.nanoTime();
		Configuration conf = context.getConfiguration();

		long blockSize = conf.getLong(Constants.BLOCK_SIZE, 1000);
		long noOfSequences = conf.getLong(Constants.NO_OF_SEQUENCES,
				blockSize * 10);
		long noOfDivisions = conf.getLong(Constants.NO_OF_DIVISIONS,
				noOfSequences / blockSize);
        boolean weightEnabled = conf.getBoolean(Constants.WEIGHT_ENABLED, false);

		// to handle the edge blocks with lesser number of sequences
		int row = (int)(key.get() * blockSize);
		int currentRowBlockSize = (int) blockSize;
		if ((row + blockSize) > (noOfSequences)) {
			currentRowBlockSize = (int) (noOfSequences - row);
		}
		
		// TODO do this in the byte level
		short[][] alignments = new short[(int) currentRowBlockSize][(int) noOfSequences];
		
		for (SWGWritable alignmentWritable : values) {
			System.out.println("key " + key.get() + " col "
					+ alignmentWritable.getColumnBlock() + " row "
					+ alignmentWritable.getRowBlock() + " blocksize "
					+ blockSize);
			DataInput in = alignmentWritable.getDataInput();
			int column = (int) (alignmentWritable.getColumnBlock() * blockSize);
			
			// to handle the edge blocks with lesser number of sequences
			int currentColumnBlockSize = (int) blockSize;
			if ((column + blockSize) > (noOfSequences)) {
				currentColumnBlockSize = (int) (noOfSequences - column);
			}
			
			for (int i = 0; i < currentRowBlockSize; i++) {
				// byte[] b = new byte[currentBlockSize /* * 2*/];
//				System.out.println("row block "+i+"  currentBlockSize"+currentRowBlockSize);
				for (int j = 0; j < currentColumnBlockSize; j++) {
					short readShort = in.readShort();
//					System.out.print(readShort+" ");
					alignments[i][column+j] = readShort;
				}
//				System.out.println();
				//TODO try to do the above using byte[] copy 
				// in.readFully(b);
				// System.out.println(new String(b));
				// System.arraycopy(b, 0, alignments[i], (column /* * 2*/),
				// currentBlockSize);
			}
		}

		// retrieve the output dir
		String outDir = context.getConfiguration().get("mapred.output.dir");

		FileSystem fs = FileSystem.get(conf);
		// out dir is created in the main driver.
        String childName = "rowblock_cor_" + key.get()+ "_blockSize_" + blockSize;
        if (weightEnabled){
            childName = "rowblock_weight_" + key.get()+ "_blockSize_" + blockSize;
        }
		Path outFilePart = new Path(outDir, childName);
		writeOutFile(alignments, fs, outFilePart);
		System.out.println("Reduce Processing Time: "+((System.nanoTime()-startTime)/1000000));
	}

	private void writeOutFile(short[][] alignments, FileSystem fs,
			Path outFilePart) throws IOException {
		OutputStream partOutStream = fs.create(outFilePart);
		DataOutputStream dataOutputStream = new DataOutputStream(partOutStream);
		// short alignments[][] = new short[(int) blockSize][(int)blockSize *
		// alignmentsMap.size()];
		// SWGWritable output = new SWGWritable(key.get(), (long) 0, blockSize,
		// false);
		// for (int row = 0; row < blockSize; row++) {
		// for (int columnBlockIndex = 0; columnBlockIndex < alignmentsMap
		// .size(); columnBlockIndex++) {
		// byte b[] = new byte[(int) blockSize * 2];
		// byte[] dataInput = (byte[])alignmentsMap.get(columnBlockIndex);
		// dataInput.readFully(b);//Fully(b,0, (int) blockSize * 2);
		// for (int i = 0; i < blockSize; i++) {
		// short readShort = dataInput.readShort();
		// System.out.print(readShort);
		// partOutStream.write(readShort);
		// }
		// }
		// }
		for (int i = 0; i < alignments.length; i++) {
			for (int j = 0; j < alignments[i].length; j++) {
				dataOutputStream.writeShort(alignments[i][j]);
			}
		}
		partOutStream.flush();
		partOutStream.close();
	}
}
