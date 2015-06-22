package Salsa.Core.Bio.Algorithms;

import Salsa.Core.*;

public enum DistanceFunctionType
{
	PercentIdentity(0),
	Kimura2(1),
	JukesCantor(2),
	MinMaxNormScore(3);

	private int intValue;
	private static java.util.HashMap<Integer, DistanceFunctionType> mappings;
	private static java.util.HashMap<Integer, DistanceFunctionType> getMappings()
	{
		if (mappings == null)
		{
			synchronized (DistanceFunctionType.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, DistanceFunctionType>();
				}
			}
		}
		return mappings;
	}

	private DistanceFunctionType(int value)
	{
		intValue = value;
		getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static DistanceFunctionType forValue(int value)
	{
		return getMappings().get(value);
	}
}