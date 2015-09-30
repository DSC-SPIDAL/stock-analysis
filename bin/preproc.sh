#!/bin/sh

#SBATCH -A skamburu
#SBATCH -N 12
#SBATCH --tasks-per-node=1
#SBATCH --time=04:00:00
if [ $# -eq 0 ]
  then
    echo "Directory must be specified as argument"
    exit 1
fi

WD=`pwd`
JAR_FILE=$WD/../mpi/target/stocks-1.0-ompi1.8.1-jar-with-dependencies.jar

# distance 0 means correlation, distance 4 means correlation squared
DISTANCE=0
# stock file name
STOCK_FILE_NAME=2004_2014.csv
# base directory
BASE_DIR=$1
PREPROC_DIR=$BASE_DIR/preproc
YEARLY_PREPROC_DIR=$PREPROC_DIR/yearly
GLOBAL_PREPROC_DIR=$PREPROC_DIR/global
# breaks directory
BREAKS_DIR=$PREPROC_DIR/parts
# vectors dirtectory
VECTOR_DIR=$YEARLY_PREPROC_DIR/vectors
# matrxi directory
MATRIX_DIR=$YEARLY_PREPROC_DIR/distances
# weight matrix directory
WEIGHT_MATRIX_DIR=$YEARLY_PREPROC_DIR/weights/matrix
# simple weights
SIMPLE_WEIGHT_MATRIX_DIR=$YEARLY_PREPROC_DIR/weights/simple
# global vectors directory
GLOBAL_VEC_DIR=$GLOBAL_PREPROC_DIR/vectors
# global directory
INPUT_DIR=$BASE_DIR/input
# global matrix directory
GLOBAL_MATRIX_DIR=$GLOBAL_PREPROC_DIR/distances
# global weight matrxi directory
GLOBAL_WEIGHT_MATRIX_DIR=$GLOBAL_PREPROC_DIR/weights/matrix
# global simple weights
SIMPLE_GLOBAL_WEIGHT_MATRIX_DIR=$GLOBAL_PREPROC_DIR/weights/simple

mkdir -p $MATRIX_DIR
mkdir -p $WEIGHT_MATRIX_DIR
mkdir -p $GLOBAL_MATRIX_DIR
mkdir -p $GLOBAL_WEIGHT_MATRIX_DIR
mkdir -p $GLOBAL_VEC_DIR
mkdir -p $BREAKS_DIR
mkdir -p $VECTOR_DIR

# break the files
echo "breaking files"
$BUILD/bin/mpirun --report-bindings --mca btl ^tcp java -cp $JAR_FILE FileBreaker -i $INPUT_DIR/$STOCK_FILE_NAME -o $BREAKS_DIR -s 20040101 -e 20060101 -d 4 -m | tee $PREPROC_DIR/parts.output.txt

# generate vector files
echo "generate vector files"
$BUILD/bin/mpirun --report-bindings --mca btl ^tcp java -cp $JAR_FILE PVectorGenerator -i $BREAKS_DIR -o $VECTOR_DIR -d 300 -m | tee $PREPROC_DIR/yearly.vector.output.txt

# generate global vector file
echo "generate global vector files"
java -cp $JAR_FILE PVectorGenerator -i $INPUT_DIR -o $GLOBAL_VEC_DIR -d 3000 | tee $GLOBAL_PREPROC_DIR/global.vector.output.txt

echo "caclulate the distance matrix for normal data"
$BUILD/bin/mpirun --report-bindings --mca btl ^tcp java -cp $JAR_FILE DistanceCalculator -v $VECTOR_DIR -d $MATRIX_DIR -m -t 0 -s -o $INPUT_DIR/$STOCK_FILE_NAME | tee $YEARLY_PREPROC_DIR/yearly.distances.output.txt

echo "caclulate the distance matrix for global data set"
$BUILD/bin/mpirun --report-bindings --mca btl ^tcp java -cp $JAR_FILE DistanceCalculator -v $GLOBAL_VEC_DIR -d $GLOBAL_MATRIX_DIR -m -t 0 -s | tee $GLOBAL_PREPROC_DIR/global.distances.output.txt

echo "calculate the weigh matrix for yearly"
$BUILD/bin/mpirun --report-bindings --mca btl ^tcp java -cp $JAR_FILE WeightCalculator -v $VECTOR_DIR -d $WEIGHT_MATRIX_DIR -m -n -sh | tee $YEARLY_PREPROC_DIR/yearly.weights.output.txt

echo "calculate the simple weigh file for yearly"
$BUILD/bin/mpirun --report-bindings --mca btl ^tcp java -cp $JAR_FILE WeightCalculator -v $VECTOR_DIR -d $SIMPLE_WEIGHT_MATRIX_DIR -m -n -sh | tee $YEARLY_PREPROC_DIR/yearly.weights.simple.output.txt

echo "calculate the weight matrix for global"
$BUILD/bin/mpirun --report-bindings java -cp $JAR_FILE WeightCalculator -v $GLOBAL_VEC_DIR -d $GLOBAL_WEIGHT_MATRIX_DIR -m -n -sh | tee $GLOBAL_PREPROC_DIR/global.weights.output.txt

echo "calculate the simple weight file for global"
$BUILD/bin/mpirun --report-bindings java -cp $JAR_FILE WeightCalculator -v $GLOBAL_VEC_DIR -d $SIMPLE_GLOBAL_WEIGHT_MATRIX_DIR -m -n -sh -s | tee $GLOBAL_PREPROC_DIR/global.weights.simple.output.txt

