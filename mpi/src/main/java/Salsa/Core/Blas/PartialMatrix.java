package Salsa.Core.Blas;

import Salsa.Core.*;

import java.io.Serializable;

public class PartialMatrix implements Serializable {
    private int _colCount;
    private double[][] _elements;
    private int _globalColStartIndex;
    private int _globalRowStartIndex;
    private int _rowCount;

    public PartialMatrix(Range globalRowRange, Range globalColumnRange) {
        this(globalRowRange.StartIndex, globalRowRange.EndIndex, globalColumnRange.StartIndex, globalColumnRange.EndIndex);
    }

    public PartialMatrix(Range globalRowRange, int globalColumnStartIndex, int globalColumnEndIndex) {
        this(globalRowRange.StartIndex, globalRowRange.EndIndex, globalColumnStartIndex, globalColumnEndIndex);
    }

    public PartialMatrix(int globalRowStartIndex, int globalRowEndIndex, Range globalColumnRange) {
        this(globalRowStartIndex, globalRowEndIndex, globalColumnRange.StartIndex, globalColumnRange.EndIndex);
    }

    /**
     * Creates a Partial Matrix
     *
     * @param globalRowStartIndex    Inclusive row starting index
     * @param globalRowEndIndex      Inclusive row ending index
     * @param globalColumnStartIndex Inclusive column starting index
     * @param globalColumnEndIndex   Inclusive column ending index
     */
    public PartialMatrix(int globalRowStartIndex, int globalRowEndIndex, int globalColumnStartIndex, int globalColumnEndIndex) {
        _globalRowStartIndex = globalRowStartIndex;
        _globalColStartIndex = globalColumnStartIndex;
        _rowCount = globalRowEndIndex - globalRowStartIndex + 1;
        _colCount = globalColumnEndIndex - globalColumnStartIndex + 1;
        _elements = CreateElements(_rowCount, _colCount);
    }

    public PartialMatrix(int globalRowStartIndex, int globalRowEndIndex, int globalColumnStartIndex, int globalColumnEndIndex, double[][] values) {
        _globalRowStartIndex = globalRowStartIndex;
        _globalColStartIndex = globalColumnStartIndex;
        _rowCount = globalRowEndIndex - globalRowStartIndex + 1;
        _colCount = globalColumnEndIndex - globalColumnStartIndex + 1;
        _elements = values;
    }

    public final double getItem(int globalRowIndex, int globalColumnIndex) {
        return _elements[globalRowIndex - _globalRowStartIndex][globalColumnIndex - _globalColStartIndex];
    }

    public final void setItem(int globalRowIndex, int globalColumnIndex, double value) {
        _elements[globalRowIndex - _globalRowStartIndex][globalColumnIndex - _globalColStartIndex] = value;
    }

    public final int getRowCount() {
        return _rowCount;
    }

    public final int getColumnCount() {
        return _colCount;
    }

    public final int getGlobalRowStartIndex() {
        return _globalRowStartIndex;
    }

    public final int getGlobalRowEndIndex() {
        return _globalRowStartIndex + _rowCount - 1;
    }

    public final int getGlobalColumnStartIndex() {
        return _globalColStartIndex;
    }

    public final int getGlobalColumnEndIndex() {
        return _globalColStartIndex + _colCount - 1;
    }

    public final boolean getIsSquare() {
        return (_rowCount == _colCount);
    }

    public final double[][] getElements() {
        return _elements;
    }

    public void setValue(int row, int col, double val) {
//        if (row == 750 || row == 250) {
//            System.out.println("********** row ************" + row);
//        }
//        if (col == 750 || col == 250) {
//            System.out.println("********** col ************" + col);
//        }
        row = row - _globalRowStartIndex;
        col =  col - _globalColStartIndex;
        _elements[row][col] = val;
    }

    public final PartialMatrix Transpose() {
        double[][] leftElements = CreateElements(_colCount, _rowCount);

        for (int i = 0; i < _rowCount; i++) {
            for (int j = 0; j < _colCount; j++) {
                leftElements[j][i] = _elements[i][j];
            }
        }

        return new PartialMatrix(getGlobalColumnStartIndex(), getGlobalColumnEndIndex(), getGlobalRowStartIndex(), getGlobalRowEndIndex(), leftElements);
    }

    public final void SetAllValues(double value) {
        for (int i = 0; i < getRowCount(); i++) {
            for (int j = 0; j < getColumnCount(); j++) {
                _elements[i][j] = value;
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < Math.min(20, _rowCount); i++) {
            for (int j = 0; j < Math.min(20, _colCount); j++) {
                if (j > 0) {
                    sb.append(", ");
                }

                sb.append(String.format("%1$4s", _elements[i][j]));

                if (i == 20 - 1) {
                    sb.append("...");
                }
            }

            sb.append("\r\n");
        }

        return sb.toString();
    }

    public double[][] CreateElements(int rowCount, int columnCount) {
        double[][] elements = new double[rowCount][];
        for (int i = 0; i < rowCount; i++) {
            elements[i] = new double[columnCount];
        }
        System.out.println("Partial matrix size: " + rowCount + " x " + columnCount);
        return elements;
    }

    public void GetRowColumnCount(double[][] data, int rows, int columns) {
        rows = data.length;
        columns = (rows == 0) ? 0 : data[0].length;
    }

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
    ///#region Row Operations

    public final double[] GetRowValues(int globalRowIndex) {
        globalRowIndex = globalRowIndex - _globalRowStartIndex;
        double[] values = new double[_colCount];
        System.arraycopy(_elements[globalRowIndex], 0, values, 0, _colCount);
        return values;
    }

    public final void SetRowValues(int globalRowIndex, double[] values) {
        globalRowIndex = globalRowIndex - _globalRowStartIndex;
        System.arraycopy(values, 0, _elements[globalRowIndex], 0, values.length);
    }

    public final double[] GetColumnValues(int globalColumnIndex) {
        globalColumnIndex = globalColumnIndex - _globalColStartIndex;
        double[] leftElements = new double[_rowCount];

        for (int i = 0; i < _rowCount; i++) {
            leftElements[i] = _elements[i][globalColumnIndex];
        }

        return leftElements;
    }

	public final void SetColumnValues(int globalColumnIndex, double[] values) {
		globalColumnIndex = globalColumnIndex - _globalColStartIndex;
		for (int i = 0; i < values.length; i++) {
			_elements[i][globalColumnIndex] = values[i];
		}
	}

	public final double[][] GetBlockValues(Block globalBlock) {
		return GetBlockValues(globalBlock.RowRange, globalBlock.ColumnRange);
	}

	public final double[][] GetBlockValues(Range globalRowRange, Range globalColumnRange) {
		return GetBlockValues(globalRowRange.StartIndex, globalRowRange.EndIndex, globalColumnRange.StartIndex, globalColumnRange.EndIndex);
	}

	public final double[][] GetBlockValues(int globalRowStartIndex, int globalRowEndIndex, int globalColumnStartIndex, int globalColumnEndIndex) {
		globalRowStartIndex = globalRowStartIndex - _globalRowStartIndex;
		globalRowEndIndex = globalRowEndIndex - _globalRowStartIndex;
		globalColumnStartIndex = globalColumnStartIndex - _globalColStartIndex;
		globalColumnEndIndex = globalColumnEndIndex - _globalColStartIndex;

		double[][] leftElements = CreateElements(globalRowEndIndex - globalRowStartIndex + 1, globalColumnEndIndex - globalColumnStartIndex + 1);

		for (int i = globalRowStartIndex; i <= globalRowEndIndex; i++) {
			for (int j = globalColumnStartIndex; j <= globalColumnEndIndex; j++) {
				leftElements[i - globalRowStartIndex][j - globalColumnStartIndex] = _elements[i][j];
			}
		}


		return leftElements;
	}


	/** 
	 Gets the block at the given coordinate but transforms the block before returning it.  This is an optimization
	 method.
	 
	 @param globalBlock The block to retrieve
	 @return 
	*/
	public final double[][] GetTransposedBlock(Block globalBlock) {
		return GetTransposedBlock(globalBlock.RowRange, globalBlock.ColumnRange);
	}

	/** 
	 Gets the block at the given coordinate but transforms the block before returning it.  This is an optimization
	 method.
	 
	 @param globalRowRange The range specifiying the rows to retrieve
	 @param globalColumnRange The range specifiying the columns to retrieve
	 @return 
	*/
	public final double[][] GetTransposedBlock(Range globalRowRange, Range globalColumnRange) {
		return GetTransposedBlock(globalRowRange.StartIndex, globalRowRange.EndIndex, globalColumnRange.StartIndex, globalColumnRange.EndIndex);
	}

	/** 
	 Gets the block at the given coordinate but transforms the block before returning it.  This is an optimization
	 method.
	 
	 @param globalRowStartIndex Inclusive starting row index.
	 @param globalRowEndIndex Inclusive ending row index.
	 @param globalColumnStartIndex Inclusive starting column index.
	 @param globalColumnEndIndex Inclusive ending column index.
	 @return 
	*/
    public final double[][] GetTransposedBlock(int globalRowStartIndex, int globalRowEndIndex, int globalColumnStartIndex, int globalColumnEndIndex) {
        globalRowStartIndex = globalRowStartIndex - _globalRowStartIndex;
        globalRowEndIndex = globalRowEndIndex - _globalRowStartIndex;
        globalColumnStartIndex = globalColumnStartIndex - _globalColStartIndex;
        globalColumnEndIndex = globalColumnEndIndex - _globalColStartIndex;

        double[][] leftElements = CreateElements(globalColumnEndIndex - globalColumnStartIndex + 1, globalRowEndIndex - globalRowStartIndex + 1);

        for (int i = globalRowStartIndex; i <= globalRowEndIndex; i++) {
            for (int j = globalColumnStartIndex; j <= globalColumnEndIndex; j++) {
                leftElements[j - globalColumnStartIndex][i - globalRowStartIndex] = _elements[i][j];
            }
        }

        return leftElements;
    }

    public final void SetBlockValues(Block globalBlock, double[][] blockValues) {
        SetBlockValues(globalBlock.RowRange, globalBlock.ColumnRange, blockValues);
    }

    public final void SetBlockValues(Range globalRowRange, Range globalColumnRange, double[][] blockValues) {
        SetBlockValues(globalRowRange.StartIndex, globalRowRange.EndIndex, globalColumnRange.StartIndex, globalColumnRange.EndIndex, blockValues);
    }

    public final void SetBlockValues(int globalRowStartIndex, int globalRowEndIndex, int globalColumnStartIndex, int globalColumnEndIndex, double[][] blockValues) {
        globalRowStartIndex = globalRowStartIndex - _globalRowStartIndex;
        globalRowEndIndex = globalRowEndIndex - _globalRowStartIndex;
        globalColumnStartIndex = globalColumnStartIndex - _globalColStartIndex;
        globalColumnEndIndex = globalColumnEndIndex - _globalColStartIndex;

        for (int i = globalRowStartIndex; i <= globalRowEndIndex; i++) {
            for (int j = globalColumnStartIndex; j <= globalColumnEndIndex; j++) {
                try {
                    _elements[i][j] = blockValues[i - globalRowStartIndex][j - globalColumnStartIndex];
                } catch (RuntimeException ex) {
                    System.out.printf("%1$s,%2$s", i, j, "\r\n");
                    throw (ex);
                }
            }
        }
    }
}