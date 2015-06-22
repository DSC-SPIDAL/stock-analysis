package Salsa.Core.Configuration.Sections;

import Salsa.Core.*;
import Salsa.Core.Configuration.*;

public class PairwiseSection extends Section
{
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Constructors

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Properties

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("I/O")][Description("Full Path to the location where the index file is to be read from. Macros will be expanded.")][DefaultValue("$(OutputRootPath)\\index.txt")] public string IndexFile
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
//ORIGINAL LINE: [Category("I/O")][Description("Full Path to the location where the data file is to be read from. Macros will be expanded.")][DefaultValue("$(OutputRootPath)\\distance.txt")] public string DistanceMatrixFile
	public final String getDistanceMatrixFile()
	{
		return _dataFileName;
	}
	public final void setDistanceMatrixFile(String value)
	{
		_dataFileName = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("I/O")][Description("Full Path to the location where the timing file is to be written to.  Macros will be expanded.")][DefaultValue("$(OutputRootPath)\\pwc_timing.txt")] public string TimingFile
	public final String getTimingFile()
	{
		return _timingOutputFileName;
	}
	public final void setTimingFile(String value)
	{
		_timingOutputFileName = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("I/O")][Description("Full Path to the location where the summary file is to be written to.  Macros will be expanded.")][DefaultValue("$(OutputRootPath)\\pwc_summary.txt")] public string SummaryFile
	public final String getSummaryFile()
	{
		return _summaryFileName;
	}
	public final void setSummaryFile(String value)
	{
		_summaryFileName = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("I/O")][Description("Full Path to the location where the plot file is to be written to.  Macros will be expanded.")][DefaultValue("$(OutputRootPath)\\pwc_plot.pviz")] public string CenterPlotFile {get;set;}
	private String privateCenterPlotFile;
	public final String getCenterPlotFile()
	{
		return privateCenterPlotFile;
	}
	public final void setCenterPlotFile(String value)
	{
		privateCenterPlotFile = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("I/O")][Description("Full Path to the location where the cluster file is to be written to. Macros will be expanded.")][DefaultValue("$(OutputRootPath)\\cluster.txt")] public string ClusterFile
	public final String getClusterFile()
	{
		return _clusterFile;
	}
	public final void setClusterFile(String value)
	{
		_clusterFile = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("I/O")] public bool ReadPartialMatrix {get;set;}
	private boolean privateReadPartialMatrix;
	public final boolean getReadPartialMatrix()
	{
		return privateReadPartialMatrix;
	}
	public final void setReadPartialMatrix(boolean value)
	{
		privateReadPartialMatrix = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Browsable(false)] public string Comments {get;set;}
	private String privateComments;
	public final String getComments()
	{
		return privateComments;
	}
	public final void setComments(String value)
	{
		privateComments = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("I/O")][Description("point-num<TAB>name<TAB>length")] public string LabelFile {get;set;}
	private String privateLabelFile;
	public final String getLabelFile()
	{
		return privateLabelFile;
	}
	public final void setLabelFile(String value)
	{
		privateLabelFile = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("I/O")][Description("point-num<TAB>x<TAB>y<TAB>z<TAB>cluster-num")] public string AddMdsFile {get;set;}
	private String privateAddMdsFile;
	public final String getAddMdsFile()
	{
		return privateAddMdsFile;
	}
	public final void setAddMdsFile(String value)
	{
		privateAddMdsFile = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("I/O")][Description("Previous cluster assignmnet of point-num<TAB>clus-num")] public string ClusterNumberFile {get;set;}
	private String privateClusterNumberFile;
	public final String getClusterNumberFile()
	{
		return privateClusterNumberFile;
	}
	public final void setClusterNumberFile(String value)
	{
		privateClusterNumberFile = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")][Description("The number of data points or the dimension of the square distance matrix.")] public int DataPoints {get;set;}
	private int privateDataPoints;
	public final int getDataPoints()
	{
		return privateDataPoints;
	}
	public final void setDataPoints(int value)
	{
		privateDataPoints = value;
	}


//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")][Description("Description Needed")] public int ProcessingOption {get;set;}
	private int privateProcessingOption;
	public final int getProcessingOption()
	{
		return privateProcessingOption;
	}
	public final void setProcessingOption(int value)
	{
		privateProcessingOption = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")][Description("Description Needed")] public int TransformDimension
	public final int getTransformDimension()
	{
		return _transformDimension;
	}
	public final void setTransformDimension(int value)
	{
		_transformDimension = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")][Description("The maximum number of center to consider when clustering.")] public int MaxNcent
	public final int getMaxNcent()
	{
		return _maxNcent;
	}
	public final void setMaxNcent(int value)
	{
		_maxNcent = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")][Description("Description Needed")] public int Splitorexpandit
	public final int getSplitorexpandit()
	{
		return _splitorexpandit;
	}
	public final void setSplitorexpandit(int value)
	{
		_splitorexpandit = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Browsable(false)] public string Pattern
	public final String getPattern()
	{
		return _pattern;
	}
	public final void setPattern(String value)
	{
		_pattern = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Browsable(false)] public int ThreadCount
	public final int getThreadCount()
	{
		return _threadCount;
	}
	public final void setThreadCount(int value)
	{
		_threadCount = value;
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
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Browsable(false)] public int MPIperNodeCount
	public final int getMPIperNodeCount()
	{
		return _mPIperNodeCount;
	}
	public final void setMPIperNodeCount(int value)
	{
		_mPIperNodeCount = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")][Description("Description Needed")] public int MPIIOStrategy {get;set;}
	private int privateMPIIOStrategy;
	public final int getMPIIOStrategy()
	{
		return privateMPIIOStrategy;
	}
	public final void setMPIIOStrategy(int value)
	{
		privateMPIIOStrategy = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")][Description("Description Needed")] public double ToosmalltoSplit
	public final double getToosmalltoSplit()
	{
		return _toosmalltoSplit;
	}
	public final void setToosmalltoSplit(double value)
	{
		_toosmalltoSplit = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")][Description("Description Needed")] public double MinEigtest
	public final double getMinEigtest()
	{
		return _minEigtest;
	}
	public final void setMinEigtest(double value)
	{
		_minEigtest = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")][Description("Description Needed")] public bool ConvergeIntermediateClusters {get;set;}
	private boolean privateConvergeIntermediateClusters;
	public final boolean getConvergeIntermediateClusters()
	{
		return privateConvergeIntermediateClusters;
	}
	public final void setConvergeIntermediateClusters(boolean value)
	{
		privateConvergeIntermediateClusters = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")][Description("Description Needed")] public int Waititerations
	public final int getWaititerations()
	{
		return _waititerations;
	}
	public final void setWaititerations(int value)
	{
		_waititerations = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")][Description("Description Needed")] public double Epsi_max_change
	public final double getEpsi_max_change()
	{
		return _epsi_max_change;
	}
	public final void setEpsi_max_change(double value)
	{
		_epsi_max_change = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")][Description("Description Needed")] public double InitialCoolingFactor
	public final double getInitialCoolingFactor()
	{
		return _initialCoolingFactor;
	}
	public final void setInitialCoolingFactor(double value)
	{
		_initialCoolingFactor = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")][Description("Description Needed")] public double FineCoolingFactor
	public final double getFineCoolingFactor()
	{
		return _fineCoolingFactor;
	}
	public final void setFineCoolingFactor(double value)
	{
		_fineCoolingFactor = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")][Description("Description Needed")] public double Eigenvaluechange
	public final double getEigenvaluechange()
	{
		return _eigenvaluechange;
	}
	public final void setEigenvaluechange(double value)
	{
		_eigenvaluechange = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")][Description("Description Needed")] public double Eigenvectorchange
	public final double getEigenvectorchange()
	{
		return _eigenvectorchange;
	}
	public final void setEigenvectorchange(double value)
	{
		_eigenvectorchange = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")][Description("Description Needed")] public int Iterationatend
	public final int getIterationatend()
	{
		return _iterationatend;
	}
	public final void setIterationatend(int value)
	{
		_iterationatend = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")][Description("Description Needed")] public int ConvergenceLoopLimit
	public final int getConvergenceLoopLimit()
	{
		return _convergenceLoopLimit;
	}
	public final void setConvergenceLoopLimit(int value)
	{
		_convergenceLoopLimit = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")][Description("Description Needed")] public double FreezingLimit
	public final double getFreezingLimit()
	{
		return _freezingLimit;
	}
	public final void setFreezingLimit(double value)
	{
		_freezingLimit = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")][Description("Description Needed")] public int PowerIterationLimit
	public final int getPowerIterationLimit()
	{
		return _powerIterationLimit;
	}
	public final void setPowerIterationLimit(int value)
	{
		_powerIterationLimit = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Debug")][Description("0 = None, 1 = Full, 2 = Summary")][DefaultValue(1)] public int DebugPrintOption
	public final int getDebugPrintOption()
	{
		return _debugPrintOption;
	}
	public final void setDebugPrintOption(int value)
	{
		_debugPrintOption = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Debug")][DefaultValue(true)] public bool ConsoleDebugOutput
	public final boolean getConsoleDebugOutput()
	{
		return _consoleDebugOutput;
	}
	public final void setConsoleDebugOutput(boolean value)
	{
		_consoleDebugOutput = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")][Description("If true use the Ken Rose Continuous Clustering")] public bool ContinuousClustering {get;set;}
	private boolean privateContinuousClustering;
	public final boolean getContinuousClustering()
	{
		return privateContinuousClustering;
	}
	public final void setContinuousClustering(boolean value)
	{
		privateContinuousClustering = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")][Description("Specify MDS versions of center finding; = 0 ignore")] public int AddMds
	public final int getAddMds()
	{
		return _addMDS;
	}
	public final void setAddMds(int value)
	{
		_addMDS = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")][Description("Comma separated list of bucket fractions")] public string BucketFractions
	public final String getBucketFractions()
	{
		return _bucketFractions;
	}
	public final void setBucketFractions(String value)
	{
		_bucketFractions = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")][Description("Number of centers to be found with each method")] public int NumberOfCenters
	public final int getNumberOfCenters()
	{
		return _numberOfCenters;
	}
	public final void setNumberOfCenters(int value)
	{
		_numberOfCenters = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")][Description("Number of centers to include in each center type of the output plot")] public int CenterPointsPerCenterTypeInOuput
	public final int getCenterPointsPerCenterTypeInOuput()
	{
		return _centerPointsPerCenterTypeInOuput;
	}
	public final void setCenterPointsPerCenterTypeInOuput(int value)
	{
		_centerPointsPerCenterTypeInOuput = value;
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Members

	private int _addMDS = 1;
	private String _bucketFractions = "0.15,0.4,0.75";
	private int _centerPointsPerCenterTypeInOuput = 3;
	private String _clusterFile = "$(OutputRootPath)\\cluster.txt";
	private boolean _consoleDebugOutput = true;
	private int _convergenceLoopLimit = 2000;
	private String _dataFileName = "distance.bin";
	private int _debugPrintOption = 1;
	private double _eigenvaluechange = 0.001;
	private double _eigenvectorchange = 0.001;
	private double _epsi_max_change = 0.001;
	private double _fineCoolingFactor = 0.99;
	private double _freezingLimit = 0.002;
	private String _indexFile = "$(OutputRootPath)\\index.txt";
	private double _initialCoolingFactor = 0.9;
	private int _iterationatend = 2000;
	private int _mPIperNodeCount = 24;
	private int _maxNcent = 10;
	private double _minEigtest = -0.01;
	private int _nodeCount = 32;
	private int _numberOfCenters = 8;
	private String _pattern = "";
	private int _powerIterationLimit = 200;
	private int _splitorexpandit = 1;
	private String _summaryFileName = "$(OutputRootPath)\\pwc_summary.txt";
	private int _threadCount = 1;
	private String _timingOutputFileName = "$(OutputRootPath)\\pwc_timing.txt";
	private double _toosmalltoSplit = 50;
	private int _transformDimension = 4;
	private int _waititerations = 10;

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

	@Override
	public void ExpandMacro(MacroReplacement macroReplacement)
	{
		super.ExpandMacro(macroReplacement);
		setDistanceMatrixFile(macroReplacement.Expand(getDistanceMatrixFile()));
		setTimingFile(macroReplacement.Expand(getTimingFile()));
		setSummaryFile(macroReplacement.Expand(getSummaryFile()));
		setClusterFile(macroReplacement.Expand(getClusterFile()));
	}
}