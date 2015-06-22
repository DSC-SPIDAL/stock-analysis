package Salsa.Core.Bio.IO;

import Salsa.Core.Bio.Algorithms.*;
import Salsa.Core.Bio.IO.Internal.*;
import Salsa.Core.*;

public interface IFastaRecordFactory<TRecord extends IFastaRecord>
{
	TRecord Create();
}