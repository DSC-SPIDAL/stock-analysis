package Salsa.Core.Configuration.Sections;

import Salsa.Core.Bio.Algorithms.*;
import Salsa.Core.*;
import Salsa.Core.Configuration.*;

public class SmithWatermanSection extends Section
{
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Constructors

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Members

	private AlignmentType _alignmentType = AlignmentType.Nucleic;
	private String _distanceFile = "distance.bin";
	private DistanceFunctionType _distanceFunction = DistanceFunctionType.PercentIdentity;
	private String _fastaFile = "$(InputRootPath)";
	private float _gapExtension = 4;
	private float _gapOpen = 14;
	private String _indexFile = "$(OutputRootPath)\\index.txt";
	private int _nodeCount;
	private int _processPerNodeCount;
	private String _scoringMatrixName = "EDNAFULL";
	private int _sequenceCount;
	private String _summaryFile = "$(OutputRootPath)\\swg_summary.txt";
	private String _timingFile = "$(OutputRootPath)\\swg_timing.txt";
	private String _writeAlignmentFile = "$(OutputRootPath)\\swg_alignments.txt";
	private boolean _writeFullMatrix = true;
	private boolean _writePartialMatrix = true;

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Properties

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
//ORIGINAL LINE: [Description("The type of sequences in the fasta file")] public AlignmentType AlignmentType
	public final AlignmentType getAlignmentType()
	{
		return _alignmentType;
	}
	public final void setAlignmentType(AlignmentType value)
	{
		_alignmentType = value;

		if (_alignmentType == AlignmentType.Nucleic)
		{
			setScoringMatrixName("EDNAFULL");
		}
		else
		{
			_distanceFunction = DistanceFunctionType.PercentIdentity;
		}
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
//ORIGINAL LINE: [Description("If true the partial matrix for each process is written to file.  Good idea to set this to true for long running jobs.")] public bool WritePartialMatrix
	public final boolean getWritePartialMatrix()
	{
		return _writePartialMatrix;
	}
	public final void setWritePartialMatrix(boolean value)
	{
		_writePartialMatrix = value;
		OnPropertyChanged("WritePartialMatrix");
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
//ORIGINAL LINE: [Description("The cost associated with opening a gap in the alignment.")] public float GapOpenPenalty
	public final float getGapOpenPenalty()
	{
		return _gapOpen;
	}
	public final void setGapOpenPenalty(float value)
	{
		_gapOpen = value;
		OnPropertyChanged("GapOpen");
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Description("The cost associated with extending a gap in the alignment.")] public float GapExtensionPenalty
	public final float getGapExtensionPenalty()
	{
		return _gapExtension;
	}
	public final void setGapExtensionPenalty(float value)
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
		setWriteAlignmentsFile(macroReplacement.Expand(getWriteAlignmentsFile()));
	}
}