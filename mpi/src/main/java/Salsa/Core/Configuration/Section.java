package Salsa.Core.Configuration;

import Salsa.Core.*;

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Category("Configuration")][TypeConverter(typeof(ExpandableObjectConverter))] public abstract class Section : INotifyPropertyChanged
public abstract class Section implements INotifyPropertyChanged
{
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region INotifyPropertyChanged Members

//C# TO JAVA CONVERTER TODO TASK: Events are not available in Java:
//	public event PropertyChangedEventHandler PropertyChanged;

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

	protected final void OnPropertyChanged(String propertyName)
	{
		if (PropertyChanged != null)
		{
			PropertyChanged(this, new PropertyChangedEventArgs(propertyName));
		}
	}

	public void ExpandMacro(MacroReplacement macroReplacement)
	{
	}

	public void ExpandEnvVars()
	{
	}
}