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

import edu.indiana.soic.ts.utils.VectorPoint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SequenceParser {
	public static VectorPoint parseVectorLine(String line) {
		String parts[] = line.trim().split(",");
		if (parts.length > 0 && !(parts.length == 1 && parts[0].equals(""))) {
			int key = Integer.parseInt(parts[0]);
			double cap = Double.parseDouble(parts[2]);

			int vectorLength = parts.length - 3;
			double[] numbers = new double[vectorLength];
			for (int i = 3; i < parts.length; i++) {
				numbers[i - 3] = Double.parseDouble(parts[i]);
			}
			VectorPoint p = new VectorPoint(key, numbers);
			p.addCap(cap);
			return p;
		}
		return null;
	}

	public static List<VectorPoint> ParseFile(InputStream inStream) throws IOException {
		ArrayList<VectorPoint> sequences = new ArrayList<VectorPoint>();
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inStream));

		VectorPoint s = null;
		while ((s = parse(bufferedReader)) != null) {
			sequences.add(s);
		}
		return sequences;
	}

	public static VectorPoint parse(BufferedReader reader) {
		String sequenceName = null;
		String sequenceDescription = null;
		try {
			StringBuffer buffer = new StringBuffer();

			// Read & parse the first line
			String line = reader.readLine();
			if (line == null || line == "") {
				return null;
			}

			return parseVectorLine(line);
		} catch (Exception e) {
			throw new RuntimeException("Failed to parse", e);
		}
	}
}
