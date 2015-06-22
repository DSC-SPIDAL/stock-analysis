package Salsa.Core.Configuration.Sections;

import Salsa.Core.*;
import Salsa.Core.Configuration.*;

public class ManxcatSection extends Section
{
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Constructors

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Properties

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region I/O Propertis

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("I/O")] public string BaseResultDirectoryName
	public final String getBaseResultDirectoryName()
	{
		return _baseResultDirectoryName;
	}
	public final void setBaseResultDirectoryName(String value)
	{
		_baseResultDirectoryName = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("I/O")] public string ControlDirectoryName
	public final String getControlDirectoryName()
	{
		return _controlDirectoryName;
	}
	public final void setControlDirectoryName(String value)
	{
		_controlDirectoryName = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("I/O")] public string ClusterDirectory
	public final String getClusterDirectory()
	{
		return _clusterDirectory;
	}
	public final void setClusterDirectory(String value)
	{
		_clusterDirectory = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("I/O")] public string DistanceMatrixFile
	public final String getDistanceMatrixFile()
	{
		return _dataFileName;
	}
	public final void setDistanceMatrixFile(String value)
	{
		_dataFileName = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("I/O")] public string DataLabelsFileName
	public final String getDataLabelsFileName()
	{
		return _dataLabelsFileName;
	}
	public final void setDataLabelsFileName(String value)
	{
		_dataLabelsFileName = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("I/O")] public string ReducedVectorOutputFileName
	public final String getReducedVectorOutputFileName()
	{
		return _reducedVectorOutputFileName;
	}
	public final void setReducedVectorOutputFileName(String value)
	{
		_reducedVectorOutputFileName = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("I/O")] public string ResultDirectoryExtension
	public final String getResultDirectoryExtension()
	{
		return _resultDirectoryExtension;
	}
	public final void setResultDirectoryExtension(String value)
	{
		_resultDirectoryExtension = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("I/O")] public string TimingOutputFileName
	public final String getTimingOutputFileName()
	{
		return _timingOutputFileName;
	}
	public final void setTimingOutputFileName(String value)
	{
		_timingOutputFileName = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("I/O")] public string SummaryOutputFileName
	public final String getSummaryOutputFileName()
	{
		return _summaryOutputFileName;
	}
	public final void setSummaryOutputFileName(String value)
	{
		_summaryOutputFileName = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("I/O")] public string InitializationFileName
	public final String getInitializationFileName()
	{
		return _initializationFileName;
	}
	public final void setInitializationFileName(String value)
	{
		_initializationFileName = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("I/O")] public string WeightingFileName
	public final String getWeightingFileName()
	{
		return _weightingFileName;
	}
	public final void setWeightingFileName(String value)
	{
		_weightingFileName = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("I/O")] public string ScalingFileName
	public final String getScalingFileName()
	{
		return _scalingFileName;
	}
	public final void setScalingFileName(String value)
	{
		_scalingFileName = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("I/O")] public string Selectedvariedpointfile
	public final String getSelectedvariedpointfile()
	{
		return _selectedvariedpointfile;
	}
	public final void setSelectedvariedpointfile(String value)
	{
		_selectedvariedpointfile = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("I/O")] public string Selectedfixedpointfile
	public final String getSelectedfixedpointfile()
	{
		return _selectedfixedpointfile;
	}
	public final void setSelectedfixedpointfile(String value)
	{
		_selectedfixedpointfile = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("I/O")] public string RotationLabelsFileName
	public final String getRotationLabelsFileName()
	{
		return _rotationLabelsFileName;
	}
	public final void setRotationLabelsFileName(String value)
	{
		_rotationLabelsFileName = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("I/O")] public bool ReadPartialMatrix
	public final boolean getReadPartialMatrix()
	{
		return _readPartialMatrix;
	}
	public final void setReadPartialMatrix(boolean value)
	{
		_readPartialMatrix = value;
	}


//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("I/O")][Description("Frequency, in iterations, with which to write out MDS coordinates.")][DefaultValue(80)] public int CoordinateWriteFrequency {get;set;}
	private int privateCoordinateWriteFrequency;
	public final int getCoordinateWriteFrequency()
	{
		return privateCoordinateWriteFrequency;
	}
	public final void setCoordinateWriteFrequency(int value)
	{
		privateCoordinateWriteFrequency = value;
	}


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

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

	private int privateDataPoints;
	public final int getDataPoints()
	{
		return privateDataPoints;
	}
	public final void setDataPoints(int value)
	{
		privateDataPoints = value;
	}

	public final boolean getCalcFixedCrossFixed()
	{
		return _calcFixedCrossFixed;
	}
	public final void setCalcFixedCrossFixed(boolean value)
	{
		_calcFixedCrossFixed = value;
	}

	public final int getStoredDistanceOption()
	{
		return _storedDistanceOption;
	}
	public final void setStoredDistanceOption(int value)
	{
		_storedDistanceOption = value;
	}

	public final int getDiskDistanceOption()
	{
		return _diskDistanceOption;
	}
	public final void setDiskDistanceOption(int value)
	{
		_diskDistanceOption = value;
	}

	private int privateProcessingOption;
	public final int getProcessingOption()
	{
		return privateProcessingOption;
	}
	public final void setProcessingOption(int value)
	{
		privateProcessingOption = value;
	}

	private int privateDistanceProcessingOption;
	public final int getDistanceProcessingOption()
	{
		return privateDistanceProcessingOption;
	}
	public final void setDistanceProcessingOption(int value)
	{
		privateDistanceProcessingOption = value;
	}

	private int privateInitializationOption;
	public final int getInitializationOption()
	{
		return privateInitializationOption;
	}
	public final void setInitializationOption(int value)
	{
		privateInitializationOption = value;
	}

	private int privateWeightingOption;
	public final int getWeightingOption()
	{
		return privateWeightingOption;
	}
	public final void setWeightingOption(int value)
	{
		privateWeightingOption = value;
	}

	public final boolean getWrite2Das3D()
	{
		return _write2Das3D;
	}
	public final void setWrite2Das3D(boolean value)
	{
		_write2Das3D = value;
	}

	public final int getLocalVectorDimension()
	{
		return _localVectorDimension;
	}
	public final void setLocalVectorDimension(int value)
	{
		_localVectorDimension = value;
	}

	public final String getSelectedvariedpoints()
	{
		return _selectedvariedpoints;
	}
	public final void setSelectedvariedpoints(String value)
	{
		_selectedvariedpoints = value;
	}

	public final String getVariedPointCriterion()
	{
		return _variedPointCriterion;
	}
	public final void setVariedPointCriterion(String value)
	{
		_variedPointCriterion = value;
	}

	public final String getSelectedfixedpoints()
	{
		return _selectedfixedpoints;
	}
	public final void setSelectedfixedpoints(String value)
	{
		_selectedfixedpoints = value;
	}

	public final String getFixedPointCriterion()
	{
		return _fixedPointCriterion;
	}
	public final void setFixedPointCriterion(String value)
	{
		_fixedPointCriterion = value;
	}

	public final String getConversionOption()
	{
		return _conversionOption;
	}
	public final void setConversionOption(String value)
	{
		_conversionOption = value;
	}

	public final String getConversionInformation()
	{
		return _conversionInformation;
	}
	public final void setConversionInformation(String value)
	{
		_conversionInformation = value;
	}

	private int privateRotationOption;
	public final int getRotationOption()
	{
		return privateRotationOption;
	}
	public final void setRotationOption(int value)
	{
		privateRotationOption = value;
	}

	public final int getInitializationLoops()
	{
		return _initializationLoops;
	}
	public final void setInitializationLoops(int value)
	{
		_initializationLoops = value;
	}

	public final int getChisqnorm()
	{
		return _chisqnorm;
	}
	public final void setChisqnorm(int value)
	{
		_chisqnorm = value;
	}

	public final int getDistanceFormula()
	{
		return _distanceFormula;
	}
	public final void setDistanceFormula(int value)
	{
		_distanceFormula = value;
	}

	private int privateFullSecondDerivativeOption;
	public final int getFullSecondDerivativeOption()
	{
		return privateFullSecondDerivativeOption;
	}
	public final void setFullSecondDerivativeOption(int value)
	{
		privateFullSecondDerivativeOption = value;
	}

	public final double getMinimumDistance()
	{
		return _minimumDistance;
	}
	public final void setMinimumDistance(double value)
	{
		_minimumDistance = value;
	}

	public final int getFunctionErrorCalcMultiplier()
	{
		return _functionErrorCalcMultiplier;
	}
	public final void setFunctionErrorCalcMultiplier(int value)
	{
		_functionErrorCalcMultiplier = value;
	}

	public final int getChisqPrintConstant()
	{
		return _chisqPrintConstant;
	}
	public final void setChisqPrintConstant(int value)
	{
		_chisqPrintConstant = value;
	}

	public final int getMaxit()
	{
		return _maxit;
	}
	public final void setMaxit(int value)
	{
		_maxit = value;
	}

	public final int getNbadgo()
	{
		return _nbadgo;
	}
	public final void setNbadgo(int value)
	{
		_nbadgo = value;
	}

	public final double getChisqChangePerPoint()
	{
		return _chisqChangePerPoint;
	}
	public final void setChisqChangePerPoint(double value)
	{
		_chisqChangePerPoint = value;
	}

	public final double getFletcherRho()
	{
		return _fletcherRho;
	}
	public final void setFletcherRho(double value)
	{
		_fletcherRho = value;
	}

	public final double getFletcherSigma()
	{
		return _fletcherSigma;
	}
	public final void setFletcherSigma(double value)
	{
		_fletcherSigma = value;
	}

	public final double getOmega()
	{
		return _omega;
	}
	public final void setOmega(double value)
	{
		_omega = value;
	}

	private int privateOmegaOption;
	public final int getOmegaOption()
	{
		return privateOmegaOption;
	}
	public final void setOmegaOption(int value)
	{
		privateOmegaOption = value;
	}

	public final double getQHighInitialFactor()
	{
		return _qHighInitialFactor;
	}
	public final void setQHighInitialFactor(double value)
	{
		_qHighInitialFactor = value;
	}

	public final double getQgoodReductionFactor()
	{
		return _qgoodReductionFactor;
	}
	public final void setQgoodReductionFactor(double value)
	{
		_qgoodReductionFactor = value;
	}

	public final int getQLimitscalculationInterval()
	{
		return _qLimitscalculationInterval;
	}
	public final void setQLimitscalculationInterval(int value)
	{
		_qLimitscalculationInterval = value;
	}

	public final double getExtraprecision()
	{
		return _extraprecision;
	}
	public final void setExtraprecision(double value)
	{
		_extraprecision = value;
	}

	public final int getAddonforQcomputation()
	{
		return _addonforQcomputation;
	}
	public final void setAddonforQcomputation(int value)
	{
		_addonforQcomputation = value;
	}

	private int privateInitialSteepestDescents;
	public final int getInitialSteepestDescents()
	{
		return privateInitialSteepestDescents;
	}
	public final void setInitialSteepestDescents(int value)
	{
		privateInitialSteepestDescents = value;
	}

	public final int getTimeCutmillisec()
	{
		return _timeCutmillisec;
	}
	public final void setTimeCutmillisec(int value)
	{
		_timeCutmillisec = value;
	}

	public final double getCGResidualLimit()
	{
		return _cGResidualLimit;
	}
	public final void setCGResidualLimit(double value)
	{
		_cGResidualLimit = value;
	}

	public final int getPowerIterationLimit()
	{
		return _powerIterationLimit;
	}
	public final void setPowerIterationLimit(int value)
	{
		_powerIterationLimit = value;
	}

	public final double getEigenvaluechange()
	{
		return _eigenvaluechange;
	}
	public final void setEigenvaluechange(double value)
	{
		_eigenvaluechange = value;
	}

	public final double getEigenvectorchange()
	{
		return _eigenvectorchange;
	}
	public final void setEigenvectorchange(double value)
	{
		_eigenvectorchange = value;
	}

	private boolean privateDerivtest;
	public final boolean getDerivtest()
	{
		return privateDerivtest;
	}
	public final void setDerivtest(boolean value)
	{
		privateDerivtest = value;
	}

	public final int getRunNumber()
	{
		return _runNumber;
	}
	public final void setRunNumber(int value)
	{
		_runNumber = value;
	}

	public final String getRunSetLabel()
	{
		return _runSetLabel;
	}
	public final void setRunSetLabel(String value)
	{
		_runSetLabel = value;
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

	private int privateMPIIOStrategy;
	public final int getMPIIOStrategy()
	{
		return privateMPIIOStrategy;
	}
	public final void setMPIIOStrategy(int value)
	{
		privateMPIIOStrategy = value;
	}

	public final int getHistogramBinCount()
	{
		return _histogramBinCount;
	}
	public final void setHistogramBinCount(int value)
	{
		_histogramBinCount = value;
	}

	public final String getExtradata1()
	{
		return _extradata1;
	}
	public final void setExtradata1(String value)
	{
		_extradata1 = value;
	}

	public final String getExtradata2()
	{
		return _extradata2;
	}
	public final void setExtradata2(String value)
	{
		_extradata2 = value;
	}

	public final String getExtradata3()
	{
		return _extradata3;
	}
	public final void setExtradata3(String value)
	{
		_extradata3 = value;
	}

	public final String getExtradata4()
	{
		return _extradata4;
	}
	public final void setExtradata4(String value)
	{
		_extradata4 = value;
	}

	private int privateExtraOption1;
	public final int getExtraOption1()
	{
		return privateExtraOption1;
	}
	public final void setExtraOption1(int value)
	{
		privateExtraOption1 = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Debug")] public int DebugPrintOption
	public final int getDebugPrintOption()
	{
		return _debugPrintOption;
	}
	public final void setDebugPrintOption(int value)
	{
		_debugPrintOption = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Debug")] public bool ConsoleDebugOutput
	public final boolean getConsoleDebugOutput()
	{
		return _consoleDebugOutput;
	}
	public final void setConsoleDebugOutput(boolean value)
	{
		_consoleDebugOutput = value;
	}

	public final String getComment()
	{
		return _comment;
	}
	public final void setComment(String value)
	{
		_comment = value;
	}

	public final double getUndefinedDistanceValue()
	{
		return _undefinedDistanceValue;
	}
	public final void setUndefinedDistanceValue(double value)
	{
		_undefinedDistanceValue = value;
	}

	public final String getDistanceWeightsCuts()
	{
		return _distanceWeightsCuts;
	}
	public final void setDistanceWeightsCuts(String value)
	{
		_distanceWeightsCuts = value;
	}

	public final float getDistanceCut()
	{
		return _distanceCut;
	}
	public final void setDistanceCut(float value)
	{
		_distanceCut = value;
	}

	public final int getLinkCut()
	{
		return _linkCut;
	}
	public final void setLinkCut(int value)
	{
		_linkCut = value;
	}

	private int privateTransformMethod;
	public final int getTransformMethod()
	{
		return privateTransformMethod;
	}
	public final void setTransformMethod(int value)
	{
		privateTransformMethod = value;
	}


	public final float getTransformParameter()
	{
		return _transformParameter;
	}
	public final void setTransformParameter(float value)
	{
		_transformParameter = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Description")] public string ManxcatRunDescription
	public final String getManxcatRunDescription()
	{
		return _manxcatRunDescription;
	}
	public final void setManxcatRunDescription(String value)
	{
		_manxcatRunDescription = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Description")] public string ManxcatRunName
	public final String getManxcatRunName()
	{
		return _manxcatRunName;
	}
	public final void setManxcatRunName(String value)
	{
		_manxcatRunName = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Density")] public string SelectedClusters {get;set;}
	private String privateSelectedClusters;
	public final String getSelectedClusters()
	{
		return privateSelectedClusters;
	}
	public final void setSelectedClusters(String value)
	{
		privateSelectedClusters = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Density")] public string ClusterFile {get;set;}
	private String privateClusterFile;
	public final String getClusterFile()
	{
		return privateClusterFile;
	}
	public final void setClusterFile(String value)
	{
		privateClusterFile = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Density")] public double Pcutf
	public final double getPcutf()
	{
		return _pcutf;
	}
	public final void setPcutf(double value)
	{
		_pcutf = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Density")] public double Alpha
	public final double getAlpha()
	{
		return _alpha;
	}
	public final void setAlpha(double value)
	{
		_alpha = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Density")] public int Yres
	public final int getYres()
	{
		return _yres;
	}
	public final void setYres(int value)
	{
		_yres = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Density")] public int Xres
	public final int getXres()
	{
		return _xres;
	}
	public final void setXres(int value)
	{
		_xres = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Density")] public double YmaxBound
	public final double getYmaxBound()
	{
		return _ymaxBound;
	}
	public final void setYmaxBound(double value)
	{
		_ymaxBound = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Density")] public double XmaxBound
	public final double getXmaxBound()
	{
		return _xmaxBound;
	}
	public final void setXmaxBound(double value)
	{
		_xmaxBound = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Density")] public bool Normalize
	public final boolean getNormalize()
	{
		return _normalize;
	}
	public final void setNormalize(boolean value)
	{
		_normalize = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Publish Settings")] public string ServerUrlPrefix
	public final String getServerUrlPrefix()
	{
		return _serverUrlPrefix;
	}
	public final void setServerUrlPrefix(String value)
	{
		_serverUrlPrefix = value;
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Members

	private int _addonforQcomputation = 2;
	private double _alpha = 2;
	private String _baseResultDirectoryName = "$(OutputRootPath)";
	private double _cGResidualLimit = 1E-05;
	private boolean _calcFixedCrossFixed = true;
	private double _chisqChangePerPoint = 0.001;
	private int _chisqPrintConstant = 1;
	private int _chisqnorm = 2;
	private String _clusterDirectory = "";
	private String _comment = "";
	private boolean _consoleDebugOutput = true;
	private String _controlDirectoryName = "$(ConfigRootPath)";
	private String _conversionInformation = "";
	private String _conversionOption = "";
	private String _dataFileName = "$(OutputRootPath)\\distance.txt";
	private String _dataLabelsFileName = "";
	private int _debugPrintOption = 2;
	private int _diskDistanceOption = 2;
	private float _distanceCut = -1.0f; // -1 to indicate no cut on distance
	private int _distanceFormula = 1;

	private String _distanceWeightsCuts = "";
				   // List of Distance Cuts for Weights. These define upper limits of distance bins with "infinity" as upper and 0 of course as lower

	private double _eigenvaluechange = 0.001;
	private double _eigenvectorchange = 0.001;
	private String _extradata1 = "";
	private String _extradata2 = "";
	private String _extradata3 = "";
	private String _extradata4 = "";
	private double _extraprecision = 0.05;
	private String _fixedPointCriterion = "none";
	private double _fletcherRho = 0.25;
	private double _fletcherSigma = 0.75;

	private int _functionErrorCalcMultiplier = 10;
	private int _histogramBinCount = 100;
	private String _indexFile = "$(OutputRootPath)\\index.txt";
	private String _initializationFileName = "";
	private int _initializationLoops = 4;
	private int _linkCut = 5; // Delete from fit all points with <= LinkCut connections
	private int _localVectorDimension = 3;
	private int _mPIperNodeCount = 1;
	private String _manxcatRunDescription = "Unspecified Description";
	private String _manxcatRunName = "Unspecified Run";
	private int _maxit = 80;
	private double _minimumDistance = -0.001;
	private int _nbadgo = 6;
	private int _nodeCount = 30;
	private boolean _normalize = true;
	private double _omega = 1.25;
	private String _pattern = "";
	private double _pcutf = 0.85;
	private int _powerIterationLimit = 200;
	private double _qHighInitialFactor = 0.01;
	private int _qLimitscalculationInterval = 1;
	private double _qgoodReductionFactor = 0.5;
	private boolean _readPartialMatrix = true;
	private String _reducedVectorOutputFileName = "$(OutputRootPath)\\points.txt";
	private String _resultDirectoryExtension = "";
	private String _rotationLabelsFileName = "";
	private int _runNumber = 27;
	private String _runSetLabel = "";
	private String _scalingFileName = "";
	private String _selectedfixedpointfile = "";
	private String _selectedfixedpoints = "";
	private String _selectedvariedpointfile = "";
	private String _selectedvariedpoints = "";
	private String _serverUrlPrefix = "http://salsahpc.indiana.edu/manxcat";
	private int _storedDistanceOption = 2;
	private String _summaryOutputFileName = "$(OutputRootPath)\\mds_summary.txt";
	private int _threadCount = 24;
	private int _timeCutmillisec = -1;
	private String _timingOutputFileName = "$(OutputRootPath)\\mds_timings.txt";
	private float _transformParameter = 0.125f; // Ignored unless _transformParameter = 11

	private double _undefinedDistanceValue = -1.0;
				   // If positive replace undefined distances by this value; use -1.0 if want to unset these distances

	private String _variedPointCriterion = "all";

	private String _weightingFileName = "";
				   // the format of the file is row<tab>rowcount<tab>col<tab>colcount<tab>scalingfactor

	private boolean _write2Das3D = true;

	/* Density and Web page creation members */
	private double _xmaxBound = 1;
	private int _xres = 50;
	private double _ymaxBound = 1;
	private int _yres = 50;

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

	@Override
	public void ExpandMacro(MacroReplacement macroReplacement)
	{
		super.ExpandMacro(macroReplacement);
		setBaseResultDirectoryName(macroReplacement.Expand(getBaseResultDirectoryName()));
		setControlDirectoryName(macroReplacement.Expand(getControlDirectoryName()));
		setClusterDirectory(macroReplacement.Expand(getClusterDirectory()));
		setDistanceMatrixFile(macroReplacement.Expand(getDistanceMatrixFile()));
		setDataLabelsFileName(macroReplacement.Expand(getDataLabelsFileName()));
		setReducedVectorOutputFileName(macroReplacement.Expand(getReducedVectorOutputFileName()));
		setTimingOutputFileName(macroReplacement.Expand(getTimingOutputFileName()));
		setInitializationFileName(macroReplacement.Expand(getInitializationFileName()));
		setWeightingFileName(macroReplacement.Expand(getWeightingFileName()));
		setSelectedvariedpointfile(macroReplacement.Expand(getSelectedvariedpointfile()));
		setSelectedfixedpointfile(macroReplacement.Expand(getSelectedfixedpointfile()));
		setRotationLabelsFileName(macroReplacement.Expand(getRotationLabelsFileName()));
		setSummaryOutputFileName(macroReplacement.Expand(getSummaryOutputFileName()));
	}
}