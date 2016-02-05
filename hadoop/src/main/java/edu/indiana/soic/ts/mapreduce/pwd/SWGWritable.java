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
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

/**
 * @author Thilina Gunarathne (tgunarat@cs.indiana.edu)
 */

public class SWGWritable implements Writable {

	private long rowBlock, columnBlock, blockSize;

	private short[][] alignments;

	private DataInput dataInput;

	boolean isInverse = false;

	public SWGWritable() {}

	public SWGWritable(long rowBlock, long columnBlock, long blockSize,
			boolean isInverse) {
		this.rowBlock = rowBlock;
		this.columnBlock = columnBlock;
		this.blockSize = blockSize;
		this.isInverse = isInverse;
	}

	public void readFields(DataInput in) throws IOException {
		rowBlock = in.readLong();
		columnBlock = in.readLong();
		blockSize = in.readLong();
		dataInput = in;
	}

	public void write(DataOutput out) throws IOException {
//		System.out.println("serialized");
		out.writeLong(rowBlock);
		out.writeLong(columnBlock);
		out.writeLong(blockSize);
		if (isInverse) {
			serializeArrayInverse(out);
		} else {
			serializeArray(out);
		}
	}

	private void serializeArray(DataOutput out) throws IOException {
		for (int i = 0; i < alignments.length; i++) {
			//out.write(alignments[i]);
			for (int j = 0; j < alignments[0].length; j++) {
				out.writeShort(alignments[i][j]);
//				out.writeBytes(alignments[i][j]+""); //debug
			}
		}
	}

	private void serializeArrayInverse(DataOutput out) throws IOException {
		for (int j = 0; j < alignments[0].length; j++) {
			for (int i = 0; i < alignments.length; i++) {
				out.writeShort(alignments[i][j]);
//				out.writeBytes(alignments[i][j]+""); //debug
			}
		}
	}

	public short[] readRow() throws IOException {
		short[] row = new short[(int) blockSize];
		for (int i = 0; i < blockSize; i++) {
			row[i] = dataInput.readShort();
		}
		return row;
	}

	public long getRowBlock() {
		return rowBlock;
	}

	public void setRowBlock(long rowBlock) {
		this.rowBlock = rowBlock;
	}

	public long getColumnBlock() {
		return columnBlock;
	}

	public void setColumnBlock(long columnBlock) {
		this.columnBlock = columnBlock;
	}

	public long getBlockSize() {
		return blockSize;
	}

	public void setBlockSize(long blockSize) {
		this.blockSize = blockSize;
	}

	public void setAlignments(short[][] alignments) {
		this.alignments = alignments;
	}
	
	public DataInput getDataInput(){
		return dataInput;
	}
}
