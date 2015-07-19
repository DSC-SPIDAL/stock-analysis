#!/bin/sh

#SBATCH -A skamburu
#SBATCH -N 12
#SBATCH --tasks-per-node=1
#SBATCH --time=04:00:00

JAR_FILE=/N/u/skamburu/projects/apps/stock-analysis/mpi/target/stocks-1.0-ompi1.8.1-jar-with-dependencies.jar

# distance 0 means correlation, distance 4 means correlation squared
DISTANCE=0
# stock file name
STOCK_FILE_NAME=2004_2014.csv
# base directory
BASE_DIR=/N/u/skamburu/data/N2004_2014
# breaks directory
BREAKS_DIR=$BASE_DIR/breaks
# vectors dirtectory
VECTOR_DIR=$BASE_DIR/vectors
# matrxi directory
MATRIX_DIR=$BASE_DIR/matrix
# weight matrix directory
WEIGHT_MATRIX_DIR=$BASE_DIR/weight_matrix
# global vectors directory
GLOBAL_VEC_DIR=$BASE_DIR/global_vectors
# global directory
GLOBA_DIR=$BASE_DIR/global
# global matrix directory
GLOBAL_MATRIX_DIR=$BASE_DIR/global_matrix
# global weight matrxi directory
GLOBAL_WEIGHT_MATRIX_DIR=$BASE_DIR/global_weight_matrix


mkdir -p $MATRIX_DIR
mkdir -p $WEIGHT_MATRIX_DIR
mkdir -p $GLOBAL_MATRIX_DIR
mkdir -p $GLOBAL_WEIGHT_MATRIX_DIR
mkdir -p $GLOBAL_VEC_DIR
mkdir -p $BREAKS_DIR

# break the files
echo "breaking files"
/N/u/skamburu/projects/software/openmpi-1.8.1/build/bin/mpirun --report-bindings --mca btl ^tcp java -cp $JAR_FILE FileBreaker -i $GLOBA_DIR/$STOCK_FILE_NAME -o $BREAKS_DIR -s 20040101 -e 20150101 -d 2 -m

# generate vector files
echo "generate vector files"
/N/u/skamburu/projects/software/openmpi-1.8.1/build/bin/mpirun --report-bindings --mca btl ^tcp java -cp $JAR_FILE PVectorGenerator -i $BREAKS_DIR -o $VECTOR_DIR -d 300 -m

# generate global vector file
java -cp $JAR_FILE PVectorGenerator -i $GLOBA_DIR/$STOCK_FILE_NAME -o $GLOBAL_VEC_DIR -d 3000

echo "caclulate the distance matrix for normal data"
/N/u/skamburu/projects/software/openmpi-1.8.1/build/bin/mpirun --report-bindings --mca btl ^tcp java -cp $JAR_FILE DistanceCalculator -v $VECTOR_DIR -d $MATRIX_DIR -m -t 0 -s

echo "caclulate the distance matrix for global data set"
/N/u/skamburu/projects/software/openmpi-1.8.1/build/bin/mpirun --report-bindings --mca btl ^tcp java -cp $JAR_FILE DistanceCalculator -v $GLOBAL_VEC_DIR -d $GLOBAL_MATRIX_DIR -m -t 0 -s

echo "calculate the weigh matrix for yearly"
/N/u/skamburu/projects/software/openmpi-1.8.1/build/bin/mpirun --report-bindings --mca btl ^tcp java -cp $JAR_FILE WeightCalculator -v $VECTOR_DIR -d $WEIGHT_MATRIX_DIR -m -n -sh

echo "calculate the weight matrix for global"
/N/u/skamburu/projects/software/openmpi-1.8.1/build/bin/mpirun --report-bindings java -cp $JAR_FILE WeightCalculator -v $GLOBAL_VEC_DIR -d $GLOBAL_WEIGHT_MATRIX_DIR -m -n -sh

