package Salsa.Core.Bio.IO;

import Salsa.Core.Bio.Algorithms.*;
import Salsa.Core.Bio.IO.Internal.*;
import Salsa.Core.*;

public class FastaSimpleParser extends FastaParser<FastaSimpleRecord>
{
	public FastaSimpleParser()
	{
		super(new FastaSimpleRecordFactory());
	}
}