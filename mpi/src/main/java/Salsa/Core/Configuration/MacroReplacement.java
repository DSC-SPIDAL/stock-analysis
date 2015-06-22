package Salsa.Core.Configuration;

import Salsa.Core.*;

public class MacroReplacement
{
	private String _macroTemplate;
	private String _replacementValue;

	public MacroReplacement(String macroTemplate, String replacementValue)
	{
		_macroTemplate = macroTemplate;
		_replacementValue = replacementValue;
	}

	public final String getMacroTemplate()
	{
		return _macroTemplate;
	}

	public final String getReplacementValue()
	{
		return _replacementValue;
	}

	public final String Expand(String target)
	{
		return target.replace(_macroTemplate, _replacementValue);
	}
}