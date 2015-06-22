package mpi;

import java.nio.ByteBuffer;

public class MPITransportComponentPacket {

    private ByteBuffer buffer;
    private int extent;

    public int numberOfClustersOffset = 0;
    public int numberOfDoubleComponentsOffset = Integer.BYTES;
    public int numberOfIntegerComponentsOffset = 2*Integer.BYTES;

    public int clusterDoubleComponentsOffset; // Not placed in the buffer to save space
    public int clusterIntegerComponentsOffset;
    public int associatedCreatedIndexOffset;
    public int clusterHostRangeOffset;

    public static MPITransportComponentPacket loadMPITransportComponentPacket(ByteBuffer buffer){
        MPITransportComponentPacket packet = new MPITransportComponentPacket();
        packet.buffer = buffer;
        packet.extent = buffer.capacity();
        packet.clusterDoubleComponentsOffset = 6 * Integer.BYTES;
        packet.clusterIntegerComponentsOffset = buffer.getInt(3*Integer.BYTES);
        packet.associatedCreatedIndexOffset = buffer.getInt(4*Integer.BYTES);
        packet.clusterHostRangeOffset = buffer.getInt(5*Integer.BYTES);
        return packet;
    }

    public int getExtent(){
        return extent;
    }

    public ByteBuffer getBuffer(){
        return buffer;
    }

    /**
     * @return Number of Clusters in Package
     */
    public int getNumberOfClusters(){
        return buffer.getInt(numberOfClustersOffset);
    }

    /**
     * @return Number of Double Components in Package
     */
    public int getNumberOfDoubleComponents(){
        return buffer.getInt(numberOfDoubleComponentsOffset);
    }

    /**
     * @return Number of Integer Components in Package
     */
    public int getNumberOfIntegerComponents(){
        return buffer.getInt(numberOfIntegerComponentsOffset);
    }


    public void setNumberOfClusters(int numberOfClusters){
        buffer.putInt(numberOfClustersOffset, numberOfClusters);
    }

    public void setNumberOfIntegerComponents(int numberOfIntegerComponents){
        buffer.putInt(numberOfIntegerComponentsOffset, numberOfIntegerComponents);
    }

    public void setNumberOfDoubleComponents(int numberOfDoubleComponents){
        buffer.putInt(numberOfDoubleComponentsOffset, numberOfDoubleComponents);
    }


    public double getClusterDoubleComponentAt(int index){
        return buffer.getDouble(clusterDoubleComponentsOffset + index*Double.BYTES);
    }

    public int getClusterIntegerComponentAt(int index){
        return buffer.getInt(clusterIntegerComponentsOffset + index*Integer.BYTES);
    }

    public int getAssociatedCreatedIndexAt(int index){
        return buffer.getInt(associatedCreatedIndexOffset+index*Integer.BYTES);
    }

    public int getClusterHostRangeAt(int index){
        return buffer.getInt(clusterHostRangeOffset + index*Integer.BYTES);
    }


    public void setClusterDoubleComponentAt(int index, double value){
        buffer.putDouble(clusterDoubleComponentsOffset + index*Double.BYTES, value);
    }

    public void setClusterIntegerComponentAt(int index, int value){
        buffer.putInt(clusterIntegerComponentsOffset + index*Integer.BYTES, value);
    }

    public void setAssociatedCreatedIndexAt(int index, int value){
        buffer.putInt(associatedCreatedIndexOffset+index*Integer.BYTES, value);
    }

    public void setClusterHostRangeAt(int index, int value){
        buffer.putInt(clusterHostRangeOffset + index*Integer.BYTES, value);
    }

    private MPITransportComponentPacket(){}

    public MPITransportComponentPacket(int numberOfClusters, int numberOfDoubleComponents,
                                       int numberOfIntegerComponents) {
        extent = 6 * Integer.BYTES; // 3 for three int values and another 3 to point to three arrays - one array doesn't need to be pointed to

        clusterDoubleComponentsOffset = extent;
        int maxLength = numberOfClusters * numberOfDoubleComponents;
        maxLength = Math.max(maxLength, 1);
        extent += maxLength * Double.BYTES;

        clusterIntegerComponentsOffset = extent;
        maxLength = numberOfClusters * numberOfIntegerComponents;
        maxLength = Math.max(maxLength, 1);
        extent += maxLength * Integer.BYTES;

        associatedCreatedIndexOffset = extent;
        maxLength = Math.max(numberOfClusters, 1);
        extent += maxLength * Integer.BYTES;

        clusterHostRangeOffset = extent;
        extent += maxLength * Integer.BYTES;

        buffer = MPI.newByteBuffer(extent);
        buffer.putInt(numberOfClusters).
                putInt(numberOfDoubleComponents).
                putInt(numberOfIntegerComponents).
                putInt(clusterIntegerComponentsOffset).
                putInt(associatedCreatedIndexOffset).
                putInt(clusterHostRangeOffset);

    }
}
