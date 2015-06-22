package Salsa.Core.Bio.Algorithms;

import Salsa.Core.*;

public enum AlignmentType
{
	Protein,
	Nucleic;

	public int getValue()
	{
		return this.ordinal();
	}

	public static AlignmentType forValue(int value)
	{
		return values()[value];
	}
}