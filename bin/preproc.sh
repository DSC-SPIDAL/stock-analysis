#!/bin/sh

#SBATCH -A skamburu
#SBATCH -N 12
#SBATCH --tasks-per-node=1
#SBATCH --time=04:00:00

JAR_FILE=/N/u/skamburu/projects/apps/stock-analysis/mpi/target/stocks-1.0-ompi1.8.1-jar-with-dependencies.jar

BASE_DIR=/N/u/skamburu/data/C2004_2014
VECTOR_DIR=$BASE_DIR/vectors
MATRIX_DIR=$BASE_DIR/matrix
WEIGHT_MATRIX_DIR=$BASE_DIR/weight_matrix
GLOBAL_VEC_DIR=$BASE_DIR/global_vectors
GLOBA_DIR=$BASE_DIR/global
GLOBAL_MATRIX_DIR=$BASE_DIR/global_matrix
GLOBAL_WEIGHT_MATRIX_DIR=$BASE_DIR/global_weight_matrix

mkdir -p $MATRIX_DIR
mkdir -p $WEIGHT_MATRIX_DIR
mkdir -p $GLOBAL_MATRIX_DIR
mkdir -p $GLOBAL_WEIGHT_MATRIX_DIR

# break the files
/N/u/skamburu/projects/software/openmpi-1.8.1/build/bin/mpirun --report-bindings --mca btl ^tcp java -cp $JAR_FILE FileBreaker -i /scratch/2004_2014/2004_2014.csv -o /scratch/2004_2014/continous_breaks -s 20040101 -e 20150101 -d 4 -m

# generate vector files
/N/u/skamburu/projects/software/openmpi-1.8.1/build/bin/mpirun --report-bindings --mca btl ^tcp java -cp $JAR_FILE PVectorGenerator -i /scratch/2004_2014/continous_breaks -o /scratch/2004_2014/continous_vectors -d 300 -m

# caclulate the distance matrix for normal data
/N/u/skamburu/projects/software/openmpi-1.8.1/build/bin/mpirun --report-bindings --mca btl ^tcp java -cp $JAR_FILE DistanceCalculator -v $VECTOR_DIR -d $MATRIX_DIR -m -t 0 -s

# caclulate the distance matrix for global data set
/N/u/skamburu/projects/software/openmpi-1.8.1/build/bin/mpirun --report-bindings --mca btl ^tcp java -cp $JAR_FILE DistanceCalculator -v $GLOBAL_VEC_DIR -d $GLOBAL_MATRIX_DIR -m -t 0 -s

#/N/u/skamburu/projects/software/openmpi-1.8.1/build/bin/mpirun --report-bindings --mca btl ^tcp java -cp $JAR_FILE WeightCalculator -v $VECTOR_DIR -d $WEIGHT_MATRIX_DIR -m -n -sh
/N/u/skamburu/projects/software/openmpi-1.8.1/build/bin/mpirun --report-bindings java -cp $JAR_FILE WeightCalculator -v $GLOBAL_VEC_DIR -d $GLOBAL_WEIGHT_MATRIX_DIR -m -n -sh

