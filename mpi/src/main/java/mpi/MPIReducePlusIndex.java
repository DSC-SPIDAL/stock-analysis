package mpi;

import mpi.*;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class MPIReducePlusIndex implements Serializable
{
    private static final int indexOffset = 0;
    private static final int valueOffset = 4;
    static final int extent = 12;
    private ByteBuffer buffer;

    public MPIReducePlusIndex(int index, double value)
    {
        buffer = MPI.newByteBuffer(extent);
        buffer.putInt(index).putDouble(value);
    }

    public MPIReducePlusIndex(){}

    public int getIndex() {
        return buffer.getInt(0);
    }

    public double getValue() {
        return buffer.getDouble(4);
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }

    private static final String MPI_USER_FUNCTION_MIN_WITH_INDEX_INVALID_DATA_TYPE = "MPI User Function - MinWithIndex: Invalid data type";
    private static final String MPI_USER_FUNCTION_MAX_WITH_INDEX_INVALID_DATA_TYPE = "MPI User Function - MaxWithIndex: Invalid data type";

    public static mpi.Op getMaxWithIndex() throws MPIException{
        return new mpi.Op(new UserFunction() {
            @Override
            public void call(Object inVec, Object inOutVec, int count, Datatype datatype) throws MPIException {
                // Nothing to do here
            }

            @Override
            public void call(ByteBuffer in, ByteBuffer inOut, int count, Datatype datatype) throws MPIException {
                if (count != extent) {
                    System.out.println(
                            MPI_USER_FUNCTION_MAX_WITH_INDEX_INVALID_DATA_TYPE);
                    MPI.COMM_WORLD.abort(1);
                }
                int inOutIndex = inOut.getInt(indexOffset);
                double inOutValue = inOut.getDouble(valueOffset);
                double inValue = in.getDouble(valueOffset);
                if (inOutIndex < 0 || inValue > inOutValue){
                    inOut.putInt(indexOffset,in.getInt(indexOffset));
                    inOut.putDouble(valueOffset,inValue);
                }
            }
        }, true);
    }

    public static mpi.Op getMinWithIndex() throws MPIException{
        return new mpi.Op(new UserFunction() {
            @Override
            public void call(Object inVec, Object inOutVec, int count, Datatype datatype) throws MPIException {
                // Nothing to do here
            }

            @Override
            public void call(ByteBuffer in, ByteBuffer inOut, int count, Datatype datatype) throws MPIException {
                if (count != extent) {
                    System.out.println(
                            MPI_USER_FUNCTION_MIN_WITH_INDEX_INVALID_DATA_TYPE);
                    MPI.COMM_WORLD.abort(1);
                }
                int inOutIndex = inOut.getInt(indexOffset);
                double inOutValue = inOut.getDouble(valueOffset);
                double inValue = in.getDouble(valueOffset);
                if (inOutIndex < 0 || inValue < inOutValue){
                    inOut.putInt(indexOffset,in.getInt(indexOffset));
                    inOut.putDouble(valueOffset,inValue);
                }
            }
        }, true);
    }

    public enum  Op{
        MIN_WITH_INDEX, MAX_WITH_INDEX
    }
}