package Salsa.Core.Configuration.Sections;

import Salsa.Core.*;
import Salsa.Core.Configuration.*;

public class DAVectorSpongeSection extends Section
{
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Constructor

	public DAVectorSpongeSection()
	{
		setUseSponge(false);
		setSpongeFactor1(3.0);
		setSpongeFactor2(3.0);
		setSpongePOption(1);
		setSpongePWeight(0.1);
		setCreateSpongeScaledSquaredWidth(-1.0);
		setContinuousClustering(true);
		setParameterVectorDimension(2);

		setSpongeTemperature1(-1.0);
		setSpongeTemperature2(-1.0);
		setRestartTemperature(-1.0);

		setNumberDataPoints(-1);
		setSelectedInputLabel(6);
		setOutputFileType(0);
		setReplicate(1);

		setSigmaMethod(0);
		setFinalTargetTemperature(3.0);
		setFinalTargetSigma0(0.0);
		setInitialSigma0(0.0);

		setClusterCountOutput(0);
		setNumberNearbyClusters(5);
		setNearbySpongePointLimit(-1.0);

		setProcessingOption(0);

		setCacheLineSize(0);
		setClusterPrintNumber(5);
		setPrintInterval(3);
		setRemovalDiagnosticPrint(false);
		setMagicTemperatures(new double[] {4.0, 3.0, 2.0, 1.0, 0.5});
		setMagicIndex(0);

		setMaxNcentPerNode(0);
		setMaxNcentTotal(0);
		setTargetNcentPerPoint(20);
		setTargetMinimumNcentPerPoint(1);
		setMaxNcentPerPoint(25);

		setMaxIntegerComponents(2);
		setMaxDoubleComponents(3);
		setMaxMPITransportBuffer(500);
		setMaxNumberAccumulationsPerNode(30000);
		setMaxTransportedClusterStorage(500);

		setExpArgumentCut1(20.0);
		setExpArgumentCut2(40.0);
		setExpArgumentCut3(50.0);
		setTminimum(-1000.0);

		setInitalNcent(1);
		setMinimumCountForClusterCk(1.0);
		setMinimumCountForClusterCkWithSponge(1.5);
		setMinimuCountForClusterPoints(2);
		setCountForClusterCkToBeZero(0.001);
		setAddSpongeScaledWidthSquared(-1.0);

		setInitialCoolingFactor(0.9);
		setFineCoolingFactor(0.99);
		setWaitIterations(10);

		setIterationAtEnd(2000);
		setConvergenceLoopLimit(20);

		setFreezingLimit(0.002);
		setMalphaMaxChange(0.005);
		setMaxNumberSplitClusters(3);
		setConvergeIntermediateClusters(false);
		setTooSmallToSplit(4.0);
		setScaledWidthSquaredToSplit(6.0);

		setClusterLimitForDistribution(-1);
		setTemperatureLimitForDistribution(-1.0);

		setDebugPrintOption(1);
		setConsoleDebugOutput(true);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Properties

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Parameters

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public bool UseSponge {get;set;}
	private boolean privateUseSponge;
	public final boolean getUseSponge()
	{
		return privateUseSponge;
	}
	public final void setUseSponge(boolean value)
	{
		privateUseSponge = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public double SpongeFactor1 {get;set;}
	private double privateSpongeFactor1;
	public final double getSpongeFactor1()
	{
		return privateSpongeFactor1;
	}
	public final void setSpongeFactor1(double value)
	{
		privateSpongeFactor1 = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public double SpongeFactor2 {get;set;}
	private double privateSpongeFactor2;
	public final double getSpongeFactor2()
	{
		return privateSpongeFactor2;
	}
	public final void setSpongeFactor2(double value)
	{
		privateSpongeFactor2 = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public int SpongePOption {get;set;}
	private int privateSpongePOption;
	public final int getSpongePOption()
	{
		return privateSpongePOption;
	}
	public final void setSpongePOption(int value)
	{
		privateSpongePOption = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public double SpongePWeight {get;set;}
	private double privateSpongePWeight;
	public final double getSpongePWeight()
	{
		return privateSpongePWeight;
	}
	public final void setSpongePWeight(double value)
	{
		privateSpongePWeight = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public double CreateSpongeScaledSquaredWidth {get;set;}
	private double privateCreateSpongeScaledSquaredWidth;
	public final double getCreateSpongeScaledSquaredWidth()
	{
		return privateCreateSpongeScaledSquaredWidth;
	}
	public final void setCreateSpongeScaledSquaredWidth(double value)
	{
		privateCreateSpongeScaledSquaredWidth = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public bool ContinuousClustering {get;set;}
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
//ORIGINAL LINE: [Category("Parameters")] public int ParameterVectorDimension {get;set;}
	private int privateParameterVectorDimension;
	public final int getParameterVectorDimension()
	{
		return privateParameterVectorDimension;
	}
	public final void setParameterVectorDimension(int value)
	{
		privateParameterVectorDimension = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public double SpongeTemperature1 {get;set;}
	private double privateSpongeTemperature1;
	public final double getSpongeTemperature1()
	{
		return privateSpongeTemperature1;
	}
	public final void setSpongeTemperature1(double value)
	{
		privateSpongeTemperature1 = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public double SpongeTemperature2 {get;set;}
	private double privateSpongeTemperature2;
	public final double getSpongeTemperature2()
	{
		return privateSpongeTemperature2;
	}
	public final void setSpongeTemperature2(double value)
	{
		privateSpongeTemperature2 = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public double RestartTemperature {get;set;}
	private double privateRestartTemperature;
	public final double getRestartTemperature()
	{
		return privateRestartTemperature;
	}
	public final void setRestartTemperature(double value)
	{
		privateRestartTemperature = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public int NumberDataPoints {get;set;}
	private int privateNumberDataPoints;
	public final int getNumberDataPoints()
	{
		return privateNumberDataPoints;
	}
	public final void setNumberDataPoints(int value)
	{
		privateNumberDataPoints = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public int SelectedInputLabel {get;set;}
	private int privateSelectedInputLabel;
	public final int getSelectedInputLabel()
	{
		return privateSelectedInputLabel;
	}
	public final void setSelectedInputLabel(int value)
	{
		privateSelectedInputLabel = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public int OutputFileType {get;set;}
	private int privateOutputFileType;
	public final int getOutputFileType()
	{
		return privateOutputFileType;
	}
	public final void setOutputFileType(int value)
	{
		privateOutputFileType = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public int Replicate {get;set;}
	private int privateReplicate;
	public final int getReplicate()
	{
		return privateReplicate;
	}
	public final void setReplicate(int value)
	{
		privateReplicate = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public int SigmaMethod {get;set;}
	private int privateSigmaMethod;
	public final int getSigmaMethod()
	{
		return privateSigmaMethod;
	}
	public final void setSigmaMethod(int value)
	{
		privateSigmaMethod = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public double FinalTargetTemperature {get;set;}
	private double privateFinalTargetTemperature;
	public final double getFinalTargetTemperature()
	{
		return privateFinalTargetTemperature;
	}
	public final void setFinalTargetTemperature(double value)
	{
		privateFinalTargetTemperature = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public double FinalTargetSigma0 {get;set;}
	private double privateFinalTargetSigma0;
	public final double getFinalTargetSigma0()
	{
		return privateFinalTargetSigma0;
	}
	public final void setFinalTargetSigma0(double value)
	{
		privateFinalTargetSigma0 = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public double InitialSigma0 {get;set;}
	private double privateInitialSigma0;
	public final double getInitialSigma0()
	{
		return privateInitialSigma0;
	}
	public final void setInitialSigma0(double value)
	{
		privateInitialSigma0 = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public int ClusterCountOutput {get;set;}
	private int privateClusterCountOutput;
	public final int getClusterCountOutput()
	{
		return privateClusterCountOutput;
	}
	public final void setClusterCountOutput(int value)
	{
		privateClusterCountOutput = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public int NumberNearbyClusters {get;set;}
	private int privateNumberNearbyClusters;
	public final int getNumberNearbyClusters()
	{
		return privateNumberNearbyClusters;
	}
	public final void setNumberNearbyClusters(int value)
	{
		privateNumberNearbyClusters = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public double NearbySpongePointLimit {get;set;}
	private double privateNearbySpongePointLimit;
	public final double getNearbySpongePointLimit()
	{
		return privateNearbySpongePointLimit;
	}
	public final void setNearbySpongePointLimit(double value)
	{
		privateNearbySpongePointLimit = value;
	}


//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public int ProcessingOption {get;set;}
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
//ORIGINAL LINE: [Category("Parameters")] public int CacheLineSize {get;set;}
	private int privateCacheLineSize;
	public final int getCacheLineSize()
	{
		return privateCacheLineSize;
	}
	public final void setCacheLineSize(int value)
	{
		privateCacheLineSize = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public int ClusterPrintNumber {get;set;}
	private int privateClusterPrintNumber;
	public final int getClusterPrintNumber()
	{
		return privateClusterPrintNumber;
	}
	public final void setClusterPrintNumber(int value)
	{
		privateClusterPrintNumber = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public int PrintInterval {get;set;}
	private int privatePrintInterval;
	public final int getPrintInterval()
	{
		return privatePrintInterval;
	}
	public final void setPrintInterval(int value)
	{
		privatePrintInterval = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public bool RemovalDiagnosticPrint {get;set;}
	private boolean privateRemovalDiagnosticPrint;
	public final boolean getRemovalDiagnosticPrint()
	{
		return privateRemovalDiagnosticPrint;
	}
	public final void setRemovalDiagnosticPrint(boolean value)
	{
		privateRemovalDiagnosticPrint = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public double[] MagicTemperatures {get;set;}
	private double[] privateMagicTemperatures;
	public final double[] getMagicTemperatures()
	{
		return privateMagicTemperatures;
	}
	public final void setMagicTemperatures(double[] value)
	{
		privateMagicTemperatures = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public int MagicIndex {get;set;}
	private int privateMagicIndex;
	public final int getMagicIndex()
	{
		return privateMagicIndex;
	}
	public final void setMagicIndex(int value)
	{
		privateMagicIndex = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public int MaxNcentPerNode {get;set;}
	private int privateMaxNcentPerNode;
	public final int getMaxNcentPerNode()
	{
		return privateMaxNcentPerNode;
	}
	public final void setMaxNcentPerNode(int value)
	{
		privateMaxNcentPerNode = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public int MaxNcentTotal {get;set;}
	private int privateMaxNcentTotal;
	public final int getMaxNcentTotal()
	{
		return privateMaxNcentTotal;
	}
	public final void setMaxNcentTotal(int value)
	{
		privateMaxNcentTotal = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public int TargetNcentPerPoint {get;set;}
	private int privateTargetNcentPerPoint;
	public final int getTargetNcentPerPoint()
	{
		return privateTargetNcentPerPoint;
	}
	public final void setTargetNcentPerPoint(int value)
	{
		privateTargetNcentPerPoint = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public int TargetMinimumNcentPerPoint {get;set;}
	private int privateTargetMinimumNcentPerPoint;
	public final int getTargetMinimumNcentPerPoint()
	{
		return privateTargetMinimumNcentPerPoint;
	}
	public final void setTargetMinimumNcentPerPoint(int value)
	{
		privateTargetMinimumNcentPerPoint = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public int MaxNcentPerPoint {get;set;}
	private int privateMaxNcentPerPoint;
	public final int getMaxNcentPerPoint()
	{
		return privateMaxNcentPerPoint;
	}
	public final void setMaxNcentPerPoint(int value)
	{
		privateMaxNcentPerPoint = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public int MaxIntegerComponents {get;set;}
	private int privateMaxIntegerComponents;
	public final int getMaxIntegerComponents()
	{
		return privateMaxIntegerComponents;
	}
	public final void setMaxIntegerComponents(int value)
	{
		privateMaxIntegerComponents = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public int MaxDoubleComponents {get;set;}
	private int privateMaxDoubleComponents;
	public final int getMaxDoubleComponents()
	{
		return privateMaxDoubleComponents;
	}
	public final void setMaxDoubleComponents(int value)
	{
		privateMaxDoubleComponents = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public int MaxMPITransportBuffer {get;set;}
	private int privateMaxMPITransportBuffer;
	public final int getMaxMPITransportBuffer()
	{
		return privateMaxMPITransportBuffer;
	}
	public final void setMaxMPITransportBuffer(int value)
	{
		privateMaxMPITransportBuffer = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public int MaxNumberAccumulationsPerNode {get;set;}
	private int privateMaxNumberAccumulationsPerNode;
	public final int getMaxNumberAccumulationsPerNode()
	{
		return privateMaxNumberAccumulationsPerNode;
	}
	public final void setMaxNumberAccumulationsPerNode(int value)
	{
		privateMaxNumberAccumulationsPerNode = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public int MaxTransportedClusterStorage {get;set;}
	private int privateMaxTransportedClusterStorage;
	public final int getMaxTransportedClusterStorage()
	{
		return privateMaxTransportedClusterStorage;
	}
	public final void setMaxTransportedClusterStorage(int value)
	{
		privateMaxTransportedClusterStorage = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public double ExpArgumentCut1 {get;set;}
	private double privateExpArgumentCut1;
	public final double getExpArgumentCut1()
	{
		return privateExpArgumentCut1;
	}
	public final void setExpArgumentCut1(double value)
	{
		privateExpArgumentCut1 = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public double ExpArgumentCut2 {get;set;}
	private double privateExpArgumentCut2;
	public final double getExpArgumentCut2()
	{
		return privateExpArgumentCut2;
	}
	public final void setExpArgumentCut2(double value)
	{
		privateExpArgumentCut2 = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public double ExpArgumentCut3 {get;set;}
	private double privateExpArgumentCut3;
	public final double getExpArgumentCut3()
	{
		return privateExpArgumentCut3;
	}
	public final void setExpArgumentCut3(double value)
	{
		privateExpArgumentCut3 = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public double Tminimum {get;set;}
	private double privateTminimum;
	public final double getTminimum()
	{
		return privateTminimum;
	}
	public final void setTminimum(double value)
	{
		privateTminimum = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public int InitalNcent {get;set;}
	private int privateInitalNcent;
	public final int getInitalNcent()
	{
		return privateInitalNcent;
	}
	public final void setInitalNcent(int value)
	{
		privateInitalNcent = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public double MinimumCountForClusterCk {get;set;}
	private double privateMinimumCountForClusterCk;
	public final double getMinimumCountForClusterCk()
	{
		return privateMinimumCountForClusterCk;
	}
	public final void setMinimumCountForClusterCk(double value)
	{
		privateMinimumCountForClusterCk = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public double MinimumCountForClusterCkWithSponge {get;set;}
	private double privateMinimumCountForClusterCkWithSponge;
	public final double getMinimumCountForClusterCkWithSponge()
	{
		return privateMinimumCountForClusterCkWithSponge;
	}
	public final void setMinimumCountForClusterCkWithSponge(double value)
	{
		privateMinimumCountForClusterCkWithSponge = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public int MinimuCountForClusterPoints {get;set;}
	private int privateMinimuCountForClusterPoints;
	public final int getMinimuCountForClusterPoints()
	{
		return privateMinimuCountForClusterPoints;
	}
	public final void setMinimuCountForClusterPoints(int value)
	{
		privateMinimuCountForClusterPoints = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public double CountForClusterCkToBeZero {get;set;}
	private double privateCountForClusterCkToBeZero;
	public final double getCountForClusterCkToBeZero()
	{
		return privateCountForClusterCkToBeZero;
	}
	public final void setCountForClusterCkToBeZero(double value)
	{
		privateCountForClusterCkToBeZero = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public double AddSpongeScaledWidthSquared {get;set;}
	private double privateAddSpongeScaledWidthSquared;
	public final double getAddSpongeScaledWidthSquared()
	{
		return privateAddSpongeScaledWidthSquared;
	}
	public final void setAddSpongeScaledWidthSquared(double value)
	{
		privateAddSpongeScaledWidthSquared = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public double InitialCoolingFactor {get;set;}
	private double privateInitialCoolingFactor;
	public final double getInitialCoolingFactor()
	{
		return privateInitialCoolingFactor;
	}
	public final void setInitialCoolingFactor(double value)
	{
		privateInitialCoolingFactor = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public double FineCoolingFactor {get;set;}
	private double privateFineCoolingFactor;
	public final double getFineCoolingFactor()
	{
		return privateFineCoolingFactor;
	}
	public final void setFineCoolingFactor(double value)
	{
		privateFineCoolingFactor = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public int WaitIterations {get;set;}
	private int privateWaitIterations;
	public final int getWaitIterations()
	{
		return privateWaitIterations;
	}
	public final void setWaitIterations(int value)
	{
		privateWaitIterations = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public int IterationAtEnd {get;set;}
	private int privateIterationAtEnd;
	public final int getIterationAtEnd()
	{
		return privateIterationAtEnd;
	}
	public final void setIterationAtEnd(int value)
	{
		privateIterationAtEnd = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public int ConvergenceLoopLimit {get;set;}
	private int privateConvergenceLoopLimit;
	public final int getConvergenceLoopLimit()
	{
		return privateConvergenceLoopLimit;
	}
	public final void setConvergenceLoopLimit(int value)
	{
		privateConvergenceLoopLimit = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public double FreezingLimit {get;set;}
	private double privateFreezingLimit;
	public final double getFreezingLimit()
	{
		return privateFreezingLimit;
	}
	public final void setFreezingLimit(double value)
	{
		privateFreezingLimit = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public double MalphaMaxChange {get;set;}
	private double privateMalphaMaxChange;
	public final double getMalphaMaxChange()
	{
		return privateMalphaMaxChange;
	}
	public final void setMalphaMaxChange(double value)
	{
		privateMalphaMaxChange = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public int MaxNumberSplitClusters {get;set;}
	private int privateMaxNumberSplitClusters;
	public final int getMaxNumberSplitClusters()
	{
		return privateMaxNumberSplitClusters;
	}
	public final void setMaxNumberSplitClusters(int value)
	{
		privateMaxNumberSplitClusters = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public bool ConvergeIntermediateClusters {get;set;}
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
//ORIGINAL LINE: [Category("Parameters")] public double TooSmallToSplit {get;set;}
	private double privateTooSmallToSplit;
	public final double getTooSmallToSplit()
	{
		return privateTooSmallToSplit;
	}
	public final void setTooSmallToSplit(double value)
	{
		privateTooSmallToSplit = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public double ScaledWidthSquaredToSplit {get;set;}
	private double privateScaledWidthSquaredToSplit;
	public final double getScaledWidthSquaredToSplit()
	{
		return privateScaledWidthSquaredToSplit;
	}
	public final void setScaledWidthSquaredToSplit(double value)
	{
		privateScaledWidthSquaredToSplit = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public int ClusterLimitForDistribution {get;set;}
	private int privateClusterLimitForDistribution;
	public final int getClusterLimitForDistribution()
	{
		return privateClusterLimitForDistribution;
	}
	public final void setClusterLimitForDistribution(int value)
	{
		privateClusterLimitForDistribution = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public double TemperatureLimitForDistribution {get;set;}
	private double privateTemperatureLimitForDistribution;
	public final double getTemperatureLimitForDistribution()
	{
		return privateTemperatureLimitForDistribution;
	}
	public final void setTemperatureLimitForDistribution(double value)
	{
		privateTemperatureLimitForDistribution = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public string Pattern {get;set;}
	private String privatePattern;
	public final String getPattern()
	{
		return privatePattern;
	}
	public final void setPattern(String value)
	{
		privatePattern = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public int NodeCount {get;set;}
	private int privateNodeCount;
	public final int getNodeCount()
	{
		return privateNodeCount;
	}
	public final void setNodeCount(int value)
	{
		privateNodeCount = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public int ThreadCount {get;set;}
	private int privateThreadCount;
	public final int getThreadCount()
	{
		return privateThreadCount;
	}
	public final void setThreadCount(int value)
	{
		privateThreadCount = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Parameters")] public int MPIPerNodeCount {get;set;}
	private int privateMPIPerNodeCount;
	public final int getMPIPerNodeCount()
	{
		return privateMPIPerNodeCount;
	}
	public final void setMPIPerNodeCount(int value)
	{
		privateMPIPerNodeCount = value;
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Debug

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Debug")] public int DebugPrintOption {get;set;}
	private int privateDebugPrintOption;
	public final int getDebugPrintOption()
	{
		return privateDebugPrintOption;
	}
	public final void setDebugPrintOption(int value)
	{
		privateDebugPrintOption = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Debug")] public bool ConsoleDebugOutput {get;set;}
	private boolean privateConsoleDebugOutput;
	public final boolean getConsoleDebugOutput()
	{
		return privateConsoleDebugOutput;
	}
	public final void setConsoleDebugOutput(boolean value)
	{
		privateConsoleDebugOutput = value;
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region I/O

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("I/O")] public string ClusterFile {get;set;}
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
//ORIGINAL LINE: [Category("I/O")] public string DistanceMatrixFile {get;set;}
	private String privateDistanceMatrixFile;
	public final String getDistanceMatrixFile()
	{
		return privateDistanceMatrixFile;
	}
	public final void setDistanceMatrixFile(String value)
	{
		privateDistanceMatrixFile = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("I/O")] public string LabelFile {get;set;}
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
//ORIGINAL LINE: [Category("I/O")] public string TimingFile {get;set;}
	private String privateTimingFile;
	public final String getTimingFile()
	{
		return privateTimingFile;
	}
	public final void setTimingFile(String value)
	{
		privateTimingFile = value;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("I/O")] public string SummaryFile {get;set;}
	private String privateSummaryFile;
	public final String getSummaryFile()
	{
		return privateSummaryFile;
	}
	public final void setSummaryFile(String value)
	{
		privateSummaryFile = value;
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

	@Override
	public void ExpandMacro(MacroReplacement macroReplacement)
	{
		// Empty;
	}
}