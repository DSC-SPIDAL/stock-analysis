package Salsa.Core.Bio.IO;

import Salsa.Core.Bio.Algorithms.*;
import Salsa.Core.Bio.IO.Internal.*;
import Salsa.Core.*;

public class FastaSequenceFactory implements IFastaRecordFactory<Sequence>
{
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region IFastaRecordFactory<Sequence> Members

	public final Sequence Create()
	{
		return new Sequence();
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion
}