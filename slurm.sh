#!/bin/sh

#SBATCH -A skamburu
#SBATCH -N 28
#SBATCH --tasks-per-node=1
#SBATCH --time=04:00:00

JAR_FILE=/N/u/skamburu/projects/apps/stock-analysis/mpi/target/stocks-1.0-ompi1.8.1-jar-with-dependencies.jar

BASE_DIR=/N/u/skamburu/data/E2004_2014
VECTOR_DIR=$BASE_DIR/vectors
MATRIX_DIR=$BASE_DIR/matrix
WEIGHT_MATRIX_DIR=$BASE_DIR/weight_matrix
GLOBAL_VEC_DIR=$BASE_DIR/global_vectors
GLOBA_DIR=$BASE_DIR/global
GLOBAL_MATRIX_DIR=$BASE_DIR/global_matrix

mkdir -p $MATRIX_DIR
mkdir -p $WEIGHT_MATRIX_DIR
mkdir -p $GLOBAL_MATRIX_DIR

#srun ./cp_global.sh
srun -l echo "Hello"
#srun -l rm -rf /scratch/2004_2014/continous_breaks
#srun -l rm -rf /scratch/2004_2014/continous_vectors
#srun -l rm -rf /scratch/2004_2014/continous_matrix
#srun -l rm -rf /scratch/2004_2014/continous_weight

#srun -l mkdir -p /scratch/2004_2014/continous_breaks
#srun -l mkdir -p /scratch/2004_2014/continous_vectors
#srun -l mkdir -p /scratch/2004_2014/continous_matrix
#srun -l mkdir -p /scratch/2004_2014/continous_weight
#srun -l cp /N/u/skamburu/data/W2004_2014/2004_2014.csv /scratch/2004_2014/2004_2014.csv


#/N/u/skamburu/projects/software/openmpi-1.8.1/build/bin/mpirun --report-bindings --mca btl ^tcp java -cp /N/u/skamburu/projects/apps/stock-analysis/mpi/target/stocks-1.0-ompi1.8.1-jar-with-dependencies.jar DistanceCalculator -v /N/u/skamburu/data/2004_2014/global_vectors -d /tmp/global_matrix_1 -m
#/N/u/skamburu/projects/software/openmpi-1.8.1/build/bin/mpirun --report-bindings --mca btl ^tcp java -cp /N/u/skamburu/projects/apps/stock-analysis/mpi/target/stocks-1.0-ompi1.8.1-jar-with-dependencies.jar FileBreaker -i /scratch/2004_2014/2004_2014.csv -o /scratch/2004_2014/continous_breaks -s 20040101 -e 20150101 -d 4 -m
#srun -l mkdir -p /N/u/skamburu/data/W2004_2014/continous_breaks
#srun -l cp -rf /scratch/2004_2014/continous_breaks /N/u/skamburu/data/W2004_2014/

#/N/u/skamburu/projects/software/openmpi-1.8.1/build/bin/mpirun --report-bindings --mca btl ^tcp java -cp /N/u/skamburu/projects/apps/stock-analysis/mpi/target/stocks-1.0-ompi1.8.1-jar-with-dependencies.jar PVectorGenerator -i /scratch/2004_2014/continous_breaks -o /scratch/2004_2014/continous_vectors -d 300 -m
#srun -l mkdir -p /N/u/skamburu/data/W2004_2014/continous_vectors
#srun -l cp -rf /scratch/2004_2014/continous_vectors /N/u/skamburu/data/W2004_2014/

# caclulate the distance matrix for normal data
/N/u/skamburu/projects/software/openmpi-1.8.1/build/bin/mpirun --report-bindings --mca btl ^tcp java -cp $JAR_FILE DistanceCalculator -v $VECTOR_DIR -d $MATRIX_DIR -m -t 1 -s
# caclulate the distance matrix for global data set
/N/u/skamburu/projects/software/openmpi-1.8.1/build/bin/mpirun --report-bindings --mca btl ^tcp java -cp $JAR_FILE DistanceCalculator -v $GLOBAL_VEC_DIR -d $GLOBAL_MATRIX_DIR -m -t 1 -s
#srun -l mkdir -p /N/u/skamburu/data/W2004_2014/continous_matrix
#srun -l cp -rf /scratch/2004_2014/continous_matrix /N/u/skamburu/data/W2004_2014/

/N/u/skamburu/projects/software/openmpi-1.8.1/build/bin/mpirun --report-bindings --mca btl ^tcp java -cp $JAR_FILE WeightCalculator -v $VECTOR_DIR -d $MATRIX_DIR -m -n
#srun -l mkdir -p /N/u/skamburu/data/W2004_2014/continous_weight
#srun -l cp -rf /scratch/2004_2014/continous_weight /N/u/skamburu/data/W2004_2014/

#srun "cp /tmp/matrx/* /N/u/skamburu/data/2004_2014/matrix/"
#srun -l rm -rf /scratch/2004_2014/continous_breaks/*
#srun -l rm -rf /scratch/2004_2014/continous_vectors/*
#srun -l rm -rf /scratch/2004_2014/continous_matrix/*
