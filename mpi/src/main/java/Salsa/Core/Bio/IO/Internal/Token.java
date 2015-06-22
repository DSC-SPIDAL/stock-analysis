package Salsa.Core.Bio.IO.Internal;

import Salsa.Core.*;
import Salsa.Core.Bio.IO.*;

public class Token
{
	public int col; // token column (starting at 1)
	public int kind; // token kind
	public int line; // token line (starting at 1)
	public Token next; // ML 2005-03-11 Tokens are kept in linked list
	public int pos; // token position in the source text (starting at 0)
	public String val; // token value
}