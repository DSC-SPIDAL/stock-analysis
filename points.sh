#!/bin/sh

BASE_DIR=/home/supun/dev/projects/dsspidal/data/W2004_2014
GLOBAL_VECS=$BASE_DIR/global_vectors
GLOBAL=$BASE_DIR/global
GLOBAL_POINTS=$BASE_DIR/global_points
CONT_VECS=$BASE_DIR/continous_vectors
CONT_POINTS=$BASE_DIR/continous_points
CONT_COMMON_POINTS=$BASE_DIR/continous_common_points

mkdir -p $CONT_COMMON_POINTS

# run the program to calculate the global vectors
#java -cp mpi/target/stocks-1.0-ompi1.8.1-jar-with-dependencies.jar PVectorGenerator -i $GLOBAL -o $GLOBAL_VECS -d 3000

# generate the common points
java -cp mpi/target/stocks-1.0-ompi1.8.1-jar-with-dependencies.jar PointTransformer -g $GLOBAL_VECS/2004_2014.csv -gp $GLOBAL_POINTS/2004_2014.txt -v $CONT_VECS -p $CONT_POINTS -d $CONT_COMMON_POINTS