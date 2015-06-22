package Salsa.Core.Bio.IO.Internal;

import Salsa.Core.*;
import Salsa.Core.Bio.IO.*;

// Errors

public class FatalError extends RuntimeException
{
	public FatalError(String m)
	{
		super(m);
	}
}