package Salsa.Core.Configuration.Sections;

import Salsa.Core.*;
import Salsa.Core.Configuration.*;

public class FileStageItem
{
	private String privateSource;
	public final String getSource()
	{
		return privateSource;
	}
	public final void setSource(String value)
	{
		privateSource = value;
	}

	private String privateTarget;
	public final String getTarget()
	{
		return privateTarget;
	}
	public final void setTarget(String value)
	{
		privateTarget = value;
	}
}