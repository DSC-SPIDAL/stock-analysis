package edu.indiana.soic.ts.mapreduce.pwd;

public class Constants {
	public static String BLOCK_SIZE = "BLOCK SIZE";

	public static String NO_OF_DIVISIONS = "#divisions";
	
	public static String NO_OF_SEQUENCES = "#sequences";
	public static String DIST_FUNC = "distFuc";

	public static String HDFS_SEQ_FILENAME = "inputSequencePart";
	
	public static String BREAK =  "#";
	
	static enum RecordCounters { ALIGNMENTS, REDUCES };
}
