package Salsa.Core.Bio.IO.Internal;

import Salsa.Core.*;
import Salsa.Core.Bio.IO.*;

public class Parser<TRecord extends IFastaRecord>
{
	public static final int _EOF = 0;
	public static final int _string = 1;
	public static final int maxT = 5;

	private static final boolean T = true;
	private static final boolean x = false;
	private static final int minErrDist = 2;

	private static final boolean[][] set = {{T, x, x, x, x, x, x}};

	private IFastaRecordFactory<TRecord> _factory;
	private final java.util.ArrayList<TRecord> _records = new java.util.ArrayList<TRecord>();
	private int errDist = minErrDist;

	public Errors errors;

	public Token la; // lookahead token
	public Scanner scanner;
	public Token t; // last recognized token


	public Parser(Scanner scanner, IFastaRecordFactory<TRecord> factory)
	{
		this.scanner = scanner;
		_factory = factory;
		errors = new Errors();
	}

	public final java.util.ArrayList<TRecord> getRecords()
	{
		return _records;
	}

	private void SynErr(int n)
	{
		if (errDist >= minErrDist)
		{
			errors.SynErr(la.line, la.col, n);
		}
		errDist = 0;
	}

	public final void SemErr(String msg)
	{
		if (errDist >= minErrDist)
		{
			errors.SemErr(t.line, t.col, msg);
		}
		errDist = 0;
	}

	private void Get()
	{
		for (;;)
		{
			t = la;
			la = scanner.Scan();
			if (la.kind <= maxT)
			{
				++errDist;
				break;
			}

			la = t;
		}
	}

	private void Expect(int n)
	{
		if (la.kind == n)
		{
			Get();
		}
		else
		{
			SynErr(n);
		}
	}

	private boolean StartOf(int s)
	{
		return set[s][la.kind];
	}

	private void ExpectWeak(int n, int follow)
	{
		if (la.kind == n)
		{
			Get();
		}
		else
		{
			SynErr(n);
			while (!StartOf(follow))
			{
				Get();
			}
		}
	}


	private boolean WeakSeparator(int n, int syFol, int repFol)
	{
		int kind = la.kind;
		if (kind == n)
		{
			Get();
			return true;
		}
		else if (StartOf(repFol))
		{
			return false;
		}
		else
		{
			SynErr(n);
			while (!(set[syFol][kind] || set[repFol][kind] || set[0][kind]))
			{
				Get();
				kind = la.kind;
			}
			return StartOf(syFol);
		}
	}


	private void FastaFile()
	{
		TRecord record = null;
		while (la.kind == 2)
		{
			tangible.RefObject<TRecord> tempRef_record = new tangible.RefObject<TRecord>(record);
			FastaEntry(tempRef_record);
			record = tempRef_record.argValue;
			_records.add(record);
		}
	}

	private void FastaEntry(tangible.RefObject<TRecord> record)
	{
		record.argValue = _factory.Create();
		String label = "";
		String residues = "";
		tangible.RefObject<String> tempRef_label = new tangible.RefObject<String>(label);
		LabelLine(tempRef_label);
		label = tempRef_label.argValue;
		record.argValue.Label = label;
		while (la.kind == 1)
		{
			tangible.RefObject<String> tempRef_residues = new tangible.RefObject<String>(residues);
			ResidueLine(tempRef_residues);
			residues = tempRef_residues.argValue;
			record.argValue.Residues += residues.toUpperCase();
		}
	}

	private void LabelLine(tangible.RefObject<String> label)
	{
		Expect(2);
		Expect(1);
		label.argValue = t.val.trim();
		while (la.kind == 3 || la.kind == 4)
		{
			if (la.kind == 3)
			{
				Get();
			}
			else
			{
				Get();
			}
		}
	}

	private void ResidueLine(tangible.RefObject<String> residues)
	{
		Expect(1);
		residues.argValue = t.val.trim();
		while (la.kind == 3 || la.kind == 4)
		{
			if (la.kind == 3)
			{
				Get();
			}
			else
			{
				Get();
			}
		}
	}


	public final void Parse()
	{
		la = new Token();
		la.val = "";
		Get();
		FastaFile();

		Expect(0);
	}
}