package Salsa.Core.Configuration.Sections;

import Salsa.Core.Bio.Algorithms.*;
import Salsa.Core.*;
import Salsa.Core.Configuration.*;

public class ScoringMatrixTypeConverter extends StringConverter
{
	@Override
	public boolean GetStandardValuesSupported(ITypeDescriptorContext context)
	{
		return true;
	}

	@Override
	public boolean GetStandardValuesExclusive(ITypeDescriptorContext context)
	{
		return true;
	}

	@Override
	public StandardValuesCollection GetStandardValues(ITypeDescriptorContext context)
	{
		return new StandardValuesCollection(ScoringMatrix.getMatrixNames());
	}
}