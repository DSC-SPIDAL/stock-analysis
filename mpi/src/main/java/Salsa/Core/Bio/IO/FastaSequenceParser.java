package Salsa.Core.Bio.IO;

import Salsa.Core.Bio.Algorithms.*;
import Salsa.Core.Bio.IO.Internal.*;
import Salsa.Core.*;

public class FastaSequenceParser extends FastaParser<Sequence>
{
	public FastaSequenceParser()
	{
		super(new FastaSequenceFactory());
	}
}