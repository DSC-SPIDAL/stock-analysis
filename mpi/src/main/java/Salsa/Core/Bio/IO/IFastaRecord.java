package Salsa.Core.Bio.IO;

import Salsa.Core.Bio.Algorithms.*;
import Salsa.Core.Bio.IO.Internal.*;
import Salsa.Core.*;

public interface IFastaRecord
{
	String getLabel();
	void setLabel(String value);
	String getResidues();
	void setResidues(String value);
}