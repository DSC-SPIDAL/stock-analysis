package mpi;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.stream.IntStream;

import mpi.*;

public class MpiOps {
    static final String ERR_RECV_UNSUPPORTED_MPIPACKET = "Unsupported receive operation on MPIPacket - Check receive type";

    private IntBuffer intBuff, intBuff2;
    private DoubleBuffer doubleBuff;
    private boolean [] booleanBuff;
    private Object [] objectSendBuff, objectRecvBuff;

    private Intracomm comm;
    private int size;
    private int rank;

    public MpiOps(Intracomm comm) throws MPIException {
        intBuff = MPI.newIntBuffer(1);
        intBuff2 = MPI.newIntBuffer(1); // temporary int buffer used in sendRecv
        doubleBuff = MPI.newDoubleBuffer(1);
        booleanBuff = new boolean[1];
        objectSendBuff = new Object[1];
        objectRecvBuff = new Object[1];
        this.comm = comm;
        size = comm.getSize();
        rank = comm.getRank();
    }

    public MpiOps() throws MPIException {
        this(MPI.COMM_WORLD);
    }

    public int getSize() {
        return size;
    }

    public int getRank() {
        return rank;
    }

    /* AllReduce */
    public int allReduce(int value, Op reduceOp) throws MPIException {
        return allReduce(value, reduceOp, comm);
    }
    public int allReduce(int value, Op reduceOp, Intracomm comm) throws MPIException {
        intBuff.put(0,value);
        comm.allReduce(intBuff, 1, MPI.INT, reduceOp);
        return intBuff.get(0);
    }

    public void allReduce(int [] values, Op reduceOp) throws MPIException{
        allReduce(values, reduceOp, comm);
    }

    public void allReduce(int [] values, Op reduceOp, Intracomm comm) throws MPIException {
        comm.allReduce(values, values.length, MPI.INT, reduceOp);
    }

    public double allReduce(double value, Op reduceOp) throws MPIException {
        return allReduce(value, reduceOp, comm);
    }
    public double allReduce(double value, Op reduceOp, Intracomm comm) throws MPIException {
        doubleBuff.put(0,value);
        comm.allReduce(doubleBuff, 1, MPI.DOUBLE, reduceOp);
        return doubleBuff.get(0);
    }

    public void allReduce(double [] values, Op reduceOp) throws MPIException{
        allReduce(values, reduceOp, comm);
    }

    public void allReduce(double [] values, Op reduceOp, Intracomm comm) throws MPIException {
        comm.allReduce(values, values.length, MPI.DOUBLE, reduceOp);
    }


    public boolean allReduce(boolean value, Op reduceOp) throws MPIException {
        return allReduce(value, reduceOp, comm);
    }
    public boolean allReduce(boolean value, Op reduceOp, Intracomm comm) throws MPIException {
        booleanBuff[0] = value;
        comm.allReduce(booleanBuff, 1, MPI.BOOLEAN, reduceOp);
        return booleanBuff[0];
    }


    public String allReduce(String value) throws MPIException{
        return allReduce(value, comm);
    }

    // TODO - Perf - Probably need to check performance
    public String allReduce(String value, Intracomm comm) throws MPIException {
        int [] lengths = new int[size];
        int length = value.length();
        lengths[rank] = length;
        comm.allGather(lengths, 1, MPI.INT);
        int [] displas = new int[size];
        displas[0] = 0;
        System.arraycopy(lengths, 0, displas, 1, size - 1);
        Arrays.parallelPrefix(displas, (m, n) -> m + n);
        int count = IntStream.of(lengths).sum(); // performs very similar to usual for loop, so no harm done
        char [] recv = new char[count];
        System.arraycopy(value.toCharArray(), 0,recv, displas[rank], length);
        comm.allGatherv(recv, lengths, displas, MPI.CHAR);
        return  new String(recv);
    }


    public MPIReducePlusIndex allReduce(MPIReducePlusIndex value, MPIReducePlusIndex.Op reduceOp) throws MPIException {
        return allReduce(value, reduceOp, comm);
    }

    public MPIReducePlusIndex allReduce(MPIReducePlusIndex value, MPIReducePlusIndex.Op reduceOp, Intracomm comm) throws MPIException {

        ByteBuffer buffer = value.getBuffer();
        if (reduceOp == MPIReducePlusIndex.Op.MAX_WITH_INDEX) {
            comm.allReduce(buffer,MPIReducePlusIndex.extent, MPI.BYTE, MPIReducePlusIndex.getMaxWithIndex());
        } else if (reduceOp == MPIReducePlusIndex.Op.MIN_WITH_INDEX){
            comm.allReduce(buffer, MPIReducePlusIndex.extent, MPI.BYTE, MPIReducePlusIndex.getMinWithIndex());
        }
        return value;
    }

    /* AllGather */
    public int[] allGather(int value) throws MPIException {
        int[] result = new int[size];
        allGather(value, result, comm);
        return result;
    }
    public void allGather(int value, int[] result) throws MPIException {
        allGather(value, result, comm);
    }
    public void allGather(int value, int[] result, Intracomm comm) throws MPIException {
        intBuff.put(0,value);
        comm.allGather(intBuff, 1, MPI.INT, result, 1, MPI.INT);
    }

    public double [] allGather (double value) throws MPIException {
        double [] result = new double[size];
        allGather(value, result, comm);
        return result;
    }

    public void allGather(double value, double [] result) throws MPIException {
        allGather(value, result, comm);
    }

    public void allGather(double value, double [] result, Intracomm comm) throws MPIException {
        doubleBuff.put(0,value);
        comm.allGather(doubleBuff,1,MPI.DOUBLE,result, 1, MPI.DOUBLE);
    }


    /* Broadcast */
    public int broadcast(int value, int root) throws MPIException{
        return broadcast(value, root, comm);
    }

    public int broadcast(int value, int root, Intracomm comm) throws MPIException {
        intBuff.put(0, value);
        comm.bcast(intBuff, 1, MPI.INT, root);
        return intBuff.get(0);
    }

    public void broadcast(int[] values, int root) throws MPIException {
        broadcast(values, root, comm);
    }

    public void broadcast(int[] values, int root, Intracomm comm) throws MPIException {
        comm.bcast(values, values.length, MPI.INT, root);
    }

    public double broadcast(double value, int root) throws MPIException {
        return broadcast(value, root, comm);
    }

    public double broadcast(double value, int root, Intracomm comm) throws MPIException {
        doubleBuff.put(0,value);
        comm.bcast(doubleBuff, 1, MPI.DOUBLE, root);
        return doubleBuff.get(0);
    }

    public void broadcast(double[] values, int root) throws MPIException {
        broadcast(values, root, comm);
    }

    public void broadcast(double[] values, int root, Intracomm comm) throws MPIException {
        comm.bcast(values, values.length, MPI.DOUBLE, root);
    }

    public boolean broadcast(boolean value, int root) throws MPIException{
        return broadcast(value, root, comm);
    }

    public boolean broadcast(boolean value, int root, Intracomm comm) throws MPIException {
        booleanBuff[0] = value;
        comm.bcast(booleanBuff, 1, MPI.BOOLEAN, root);
        return booleanBuff[0];
    }

    public void broadcast (boolean[] values, int root) throws MPIException{
        broadcast(values, root, comm);
    }

    public void broadcast(boolean[] values, int root, Intracomm comm) throws MPIException{
        comm.bcast(values, values.length, MPI.BOOLEAN, root);
    }

    public MPIPacket broadcast(MPIPacket value, int root) throws MPIException{
        return broadcast(value, root, comm);
    }

    public MPIPacket broadcast(MPIPacket value, int root, Intracomm comm) throws MPIException{
        comm.bcast(value.getBuffer(),value.getExtent(),MPI.BYTE,root);
        return value;
    }


    /* Sendrecv */
    public MPITransportComponentPacket sendReceive(MPITransportComponentPacket sendValue, int dest, int destTag, int src, int srcTag) throws MPIException {
        return sendReceive(sendValue, dest, destTag, src, srcTag,comm);
    }

    public MPITransportComponentPacket sendReceive(MPITransportComponentPacket sendValue, int dest, int destTag, int src, int srcTag, Intracomm comm) throws MPIException {
        int sendExtent = sendValue.getExtent();
        intBuff.put(0, sendExtent);
        comm.sendRecv(intBuff,1,MPI.INT,dest,destTag,intBuff2,1,MPI.INT,src,srcTag);
        int recvExtent = intBuff2.get(0);
        ByteBuffer recvBuff = MPI.newByteBuffer(recvExtent);
        comm.sendRecv(sendValue.getBuffer(),sendExtent,MPI.BYTE,dest,destTag,recvBuff,recvExtent,MPI.BYTE,src,srcTag);
        return MPITransportComponentPacket.loadMPITransportComponentPacket(recvBuff);
    }


    /* Send */
    public void send(MPIPacket value, int dest, int tag) throws MPIException {
        send(value, dest, tag, comm);
    }

    public void send(MPIPacket value, int dest, int tag, Intracomm comm) throws MPIException {
        int extent = value.getExtent();
        intBuff.put(0, extent);
        comm.send(intBuff,1,MPI.INT,dest,tag);
        comm.send(value.getBuffer(), value.getExtent(), MPI.BYTE, dest, tag);
    }


    /* Receive */
    public MPIPacket receive(int src, int tag, MPIPacket.Type type) throws MPIException {
        if (type == MPIPacket.Type.Integer) {
            return MPIPacket.loadIntegerPacket(receive(src, tag, comm));
        } else if (type == MPIPacket.Type.Double){
            return MPIPacket.loadDoublePacket(receive(src, tag, comm));
        }
        throw new UnsupportedOperationException(ERR_RECV_UNSUPPORTED_MPIPACKET);
    }

    private ByteBuffer receive(int src, int tag, Intracomm comm) throws MPIException {
        comm.recv(intBuff,1,MPI.INT,src,tag);
        int extent = intBuff.get(0);
        ByteBuffer buffer = MPI.newByteBuffer(extent);
        comm.recv(buffer, extent, MPI.BYTE, src, tag);
        return buffer;
    }


    /* Barrier */
    public void barrier() throws MPIException {
        barrier(MPI.COMM_WORLD);

    }
    public void barrier(Intracomm comm) throws MPIException {
        comm.barrier();
    }



}
