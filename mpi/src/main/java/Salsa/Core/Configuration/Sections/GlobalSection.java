package Salsa.Core.Configuration.Sections;

import Salsa.Core.*;
import Salsa.Core.Configuration.*;

public class GlobalSection extends Section
{
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Constructor

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Members

	private final Collection<String> _emailAddresses = new Collection<String>();
	private String _configRootPath = "";
	private String _inputRootPath = "";
	private String _outputRootPath = "";

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Properties

	public final Collection<String> getEmailAddresses()
	{
		return _emailAddresses;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("I/O")][Description("The full path to the config root folder.  Macro: $(ConfigRootPath)")][Editor(typeof(FolderNameEditor), typeof(UITypeEditor))] public string ConfigRootPath
	public final String getConfigRootPath()
	{
		return _configRootPath;
	}
	public final void setConfigRootPath(String value)
	{
		_configRootPath = value.trim();
		OnPropertyChanged("ConfigRootPath");
	}


//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("I/O")][Description("The full path to the input root folder.   Macro: $(InputRootPath)")][Editor(typeof(FolderNameEditor), typeof(UITypeEditor))] public string InputRootPath
	public final String getInputRootPath()
	{
		return _inputRootPath;
	}
	public final void setInputRootPath(String value)
	{
		_inputRootPath = value.trim();
		OnPropertyChanged("InputRootPath");
	}


//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("I/O")][Description("The full path to the output root folder.  Macro: $(OutputRootPath)")][Editor(typeof(FolderNameEditor), typeof(UITypeEditor))] public string OutputRootPath
	public final String getOutputRootPath()
	{
		return _outputRootPath;
	}
	public final void setOutputRootPath(String value)
	{
		_outputRootPath = value.trim();
		OnPropertyChanged("OutputRootPath");
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion
}