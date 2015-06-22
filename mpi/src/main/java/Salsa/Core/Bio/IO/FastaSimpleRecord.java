package Salsa.Core.Bio.IO;

import Salsa.Core.Bio.Algorithms.*;
import Salsa.Core.Bio.IO.Internal.*;
import Salsa.Core.*;

public class FastaSimpleRecord implements IFastaRecord
{
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Members

	private String _residues = "";

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

	public FastaSimpleRecord()
	{
	}

	public FastaSimpleRecord(String label, String sequence)
	{
		_residues = sequence;
		setLabel(label);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region IFastaRecord Members

	private String privateLabel;
	public final String getLabel()
	{
		return privateLabel;
	}
	public final void setLabel(String value)
	{
		privateLabel = value;
	}

	public final String getResidues()
	{
		return _residues;
	}
	public final void setResidues(String value)
	{
		_residues = value;
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

	@Override
	public String toString()
	{
		return getLabel() + Environment.NewLine + getResidues();
	}
}