package Salsa.Core.Bio.IO;

import Salsa.Core.Bio.Algorithms.*;
import Salsa.Core.Bio.IO.Internal.*;
import Salsa.Core.*;

public class FastaParser<TRecord extends IFastaRecord>
{
	private IFastaRecordFactory<TRecord> _recordFactory;

	public FastaParser(IFastaRecordFactory<TRecord> recordFactory)
	{
		_recordFactory = recordFactory;
	}

	public final java.util.List<TRecord> Parse(String fileName)
	{
		Scanner scanner = new Scanner(fileName);
		Parser<TRecord> parser = new Parser<TRecord>(scanner, _recordFactory);
		parser.Parse();
		return parser.getRecords();
	}
}