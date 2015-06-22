package Salsa.Core.Configuration.Sections;

import Bio.IO.GenBank.*;
import Bio.SimilarityMatrices.*;
import Salsa.Core.Bio.Algorithms.*;
import Salsa.Core.*;
import Salsa.Core.Configuration.*;

public class SmithWatermanMS extends Section
{
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Members

	private String _distanceFile = "distance.bin";
	private DistanceFunctionType _distanceFunction = DistanceFunctionType.PercentIdentity;
	private String _fastaFile = "$(InputRootPath)";
	private int _gapExtension = 4;
	private int _gapOpen = 14;
	private String _indexFile = "$(OutputRootPath)\\index.txt";
	private MoleculeType _moleculeType = getMoleculeType().DNA;
	private int _nodeCount;
	private int _processPerNodeCount;
	private String _scoringMatrixName = "EDNAFULL";
	private int _sequenceCount;
	private String _summaryFile = "$(OutputRootPath)\\swms_summary.txt";
	private String _timingFile = "$(OutputRootPath)\\swms_timing.txt";
	private String _writeAlignmentFile = "$(OutputRootPath)\\swms_alignments.txt";
	private boolean _writeFullMatrix = true;

	/* Note. Saliya - Adding Nblock save and logs as suggested by Prof. Fox on 9/12/2011. See mail "64-bit MBF" Search for "introduce Nbackup set to" */
	// The number of column blocks computed for each partial write. 
	// Default = 0, indicates no partial writing, is not adviced for large runs

	// Directory to write blocks from each process. Adviced is to use a directory local to each process

	// The number of pairs to be calculated before writing status.
	// Default = 0, indicates no writing of status rankwsie status, is not adviced for large runs

	// Directory to write logs for each process. Adviced is to use a directory local to each process

	/* -- End Nblock save and log  members --*/

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Properties

	private String privateProjectName;
	public final String getProjectName()
	{
		return privateProjectName;
	}
	public final void setProjectName(String value)
	{
		privateProjectName = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("I/O")] public string DoneStatusDir {get;set;}
	private String privateDoneStatusDir;
	public final String getDoneStatusDir()
	{
		return privateDoneStatusDir;
	}
	public final void setDoneStatusDir(String value)
	{
		privateDoneStatusDir = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("I/O")] public string RestartFile {get;set;}
	private String privateRestartFile;
	public final String getRestartFile()
	{
		return privateRestartFile;
	}
	public final void setRestartFile(String value)
	{
		privateRestartFile = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("I/O")] public int BlockWriteFrequency {get;set;}
	private int privateBlockWriteFrequency;
	public final int getBlockWriteFrequency()
	{
		return privateBlockWriteFrequency;
	}
	public final void setBlockWriteFrequency(int value)
	{
		privateBlockWriteFrequency = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("I/O")] public int LogWriteFrequency {get;set;}
	private int privateLogWriteFrequency;
	public final int getLogWriteFrequency()
	{
		return privateLogWriteFrequency;
	}
	public final void setLogWriteFrequency(int value)
	{
		privateLogWriteFrequency = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("I/O")] public string BlockDir {get;set;}
	private String privateBlockDir;
	public final String getBlockDir()
	{
		return privateBlockDir;
	}
	public final void setBlockDir(String value)
	{
		privateBlockDir = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("I/O")] public string LogDir {get;set;}
	private String privateLogDir;
	public final String getLogDir()
	{
		return privateLogDir;
	}
	public final void setLogDir(String value)
	{
		privateLogDir = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("I/O")] public string FastaFile
	public final String getFastaFile()
	{
		return _fastaFile;
	}
	public final void setFastaFile(String value)
	{
		_fastaFile = value;
		OnPropertyChanged("FastaFile");
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("I/O")][Description("Full Path to the location where the distance file is to be written to. Macros will be expanded.")] public string DistanceMatrixFile
	public final String getDistanceMatrixFile()
	{
		return _distanceFile;
	}
	public final void setDistanceMatrixFile(String value)
	{
		_distanceFile = value;
		OnPropertyChanged("DistanceFile");
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("I/O")][Description("Full Path to the location where the index file is to be written to. Macros will be expanded.")] public string IndexFile
	public final String getIndexFile()
	{
		return _indexFile;
	}
	public final void setIndexFile(String value)
	{
		_indexFile = value;
		OnPropertyChanged("IndexFile");
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("I/O")] public string TimingFile
	public final String getTimingFile()
	{
		return _timingFile;
	}
	public final void setTimingFile(String value)
	{
		_timingFile = value;
		OnPropertyChanged("TimingFile");
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("I/O")] public string SummaryFile
	public final String getSummaryFile()
	{
		return _summaryFile;
	}
	public final void setSummaryFile(String value)
	{
		_summaryFile = value;
		OnPropertyChanged("SummaryFile");
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("I/O")][Description("The file where the alignments are written to.")] public string WriteAlignmentsFile
	public final String getWriteAlignmentsFile()
	{
		return _writeAlignmentFile;
	}
	public final void setWriteAlignmentsFile(String value)
	{
		_writeAlignmentFile = value;
		OnPropertyChanged("WriteAlignmentsFile");
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [TypeConverter(typeof(ScoringMatrixTypeConverter))][Description("The name of scoring matrix to use")] public string ScoringMatrixName
	public final String getScoringMatrixName()
	{
		return _scoringMatrixName;
	}
	public final void setScoringMatrixName(String value)
	{
		_scoringMatrixName = value;
		OnPropertyChanged("ScoringMatrixName");
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Description("The type of molecule")] public MoleculeType MoleculeType
	public final MoleculeType getMoleculeType()
	{
		return _moleculeType;
	}
	public final void setMoleculeType(MoleculeType value)
	{
		_moleculeType = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Description("The distance function to use when coverting alignments to distance measures.")] public DistanceFunctionType DistanceFunctionType
	public final DistanceFunctionType getDistanceFunctionType()
	{
		return _distanceFunction;
	}
	public final void setDistanceFunctionType(DistanceFunctionType value)
	{
		_distanceFunction = value;
		OnPropertyChanged("DistanceFunctionType");
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Browsable(false)] public int NodeCount
	public final int getNodeCount()
	{
		return _nodeCount;
	}
	public final void setNodeCount(int value)
	{
		_nodeCount = value;
		OnPropertyChanged("NodeCount");
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Browsable(false)] public int ProcessPerNodeCount
	public final int getProcessPerNodeCount()
	{
		return _processPerNodeCount;
	}
	public final void setProcessPerNodeCount(int value)
	{
		_processPerNodeCount = value;
		OnPropertyChanged("ProcessPerNodeCount");
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Browsable(false)] public int SequenceCount
	public final int getSequenceCount()
	{
		return _sequenceCount;
	}
	public final void setSequenceCount(int value)
	{
		_sequenceCount = value;
		OnPropertyChanged("SequenceCount");
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Description("If true the full matrix is written at the specified location by the minimum rank mpi process on a given node.")] public bool WriteFullMatrix
	public final boolean getWriteFullMatrix()
	{
		return _writeFullMatrix;
	}
	public final void setWriteFullMatrix(boolean value)
	{
		_writeFullMatrix = value;
		OnPropertyChanged("WriteFullMatrix");
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Description("If true the alignment for each pair is written to the file specified in the WriteAlignmentsPath property.")] public bool WriteAlignments {get;set;}
	private boolean privateWriteAlignments;
	public final boolean getWriteAlignments()
	{
		return privateWriteAlignments;
	}
	public final void setWriteAlignments(boolean value)
	{
		privateWriteAlignments = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Description("The cost associated with opening a gap in the alignment.")] public int GapOpenPenalty
	public final int getGapOpenPenalty()
	{
		return _gapOpen;
	}
	public final void setGapOpenPenalty(int value)
	{
		_gapOpen = value;
		OnPropertyChanged("GapOpen");
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Description("The cost associated with extending a gap in the alignment.")] public int GapExtensionPenalty
	public final int getGapExtensionPenalty()
	{
		return _gapExtension;
	}
	public final void setGapExtensionPenalty(int value)
	{
		_gapExtension = value;
		OnPropertyChanged("GapExtensionPenalty");
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

	@Override
	public void ExpandMacro(MacroReplacement macroReplacement)
	{
		super.ExpandMacro(macroReplacement);
		setFastaFile(macroReplacement.Expand(getFastaFile()));
		setDistanceMatrixFile(macroReplacement.Expand(getDistanceMatrixFile()));
		setIndexFile(macroReplacement.Expand(getIndexFile()));
		setTimingFile(macroReplacement.Expand(getTimingFile()));
		setSummaryFile(macroReplacement.Expand(getSummaryFile()));
		//WriteAlignmentsFile = macroReplacement.Expand(WriteAlignmentsFile);
	}

	/** 
	 Loads a scoring matrix from the predefined set of matrices inside Salsa.Core.Bio.Algorithms.Matrices
	 
	 @param matrixName The name of the matrix
	 @param moleculeType
	 Type of molecule for which this matrix is designed. 
	 Must be DNA, RNA (may have variants like tRNA, mRNA, etc.) or Protein
	 @return The SimilarityMatrix
	*/
	public final SimilarityMatrix LoadSimilarityMatrix(String matrixName, MoleculeType moleculeType)
	{
		/*
		 * MBF 2.0 requires the format of the matrix to be as (without angle brackets), 
		 * <Name>
		 * <MoleculeType>
		 * <Alphabet>
		 * <ScoreRow0>
		 * <ScoreRow1>
		 * ...
		 * ...
		 * <ScoreRowN>
		 */

		if (moleculeType == getMoleculeType().DNA || moleculeType == getMoleculeType().mRNA || moleculeType == getMoleculeType().RNA || moleculeType == getMoleculeType().rRNA || moleculeType == getMoleculeType().snoRNA || moleculeType == getMoleculeType().snRNA || moleculeType == getMoleculeType().tRNA || moleculeType == getMoleculeType().uRNA || moleculeType == getMoleculeType().Protein)
		{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//			using (Stream stream = Assembly.GetExecutingAssembly().GetManifestResourceStream("Salsa.Core.Bio.Algorithms.Matrices." + matrixName))
			Stream stream = Assembly.GetExecutingAssembly().GetManifestResourceStream("Salsa.Core.Bio.Algorithms.Matrices." + matrixName);
			try
			{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//				using (var reader = new StreamReader(stream))
				StreamReader reader = new StreamReader(stream);
				try
				{
					char commentStarter = '#';
					String line;
					// Skip comments
					while ((line = reader.ReadLine()) != null && line.trim().charAt(0) == commentStarter)
					{
						;
					}

					StringBuilder sb = new StringBuilder();
					sb.AppendLine(matrixName); // Matrix name

					String mt = moleculeType.toString();
					sb.AppendLine((moleculeType == getMoleculeType().Protein) ? mt : mt.substring(mt.length() - 3));
						// Molecule Type

					sb.AppendLine(line); // Alphabet line

					while ((line = reader.ReadLine()) != null)
					{
						sb.AppendLine(line.substring(1).trim());
							// ScoreRow i (ignores the first symbol in current file format)
					}
					return new SimilarityMatrix(new StringReader(sb.toString()));
				}
				finally
				{
					reader.dispose();
				}
			}
			finally
			{
				stream.dispose();
			}
		}
		else
		{
			throw new RuntimeException("Unsupported molecule type: " + moleculeType);
		}
	}
}