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
#java -cp mpi/target/stocks-1.0-ompi1.8.1-jar-with-dependencies.jar PointTransformer -g $GLOBAL_VECS/2004_2014.csv -gp $GLOBAL_POINTS/2004_2014.txt -v $CONT_VECS -p $CONT_POINTS -d $CONT_COMMON_POINTS

# rotate the points
MANXCAT_JAR=/home/supun/dev/projects/dsspidal/rotate/target/mdsaschisq-1.0-ompi1.8.1-jar-with-dependencies.jar
ROTATE_POINTS=$CONT_COMMON_POINTS/*
ROTATE_OUT=$BASE_DIR/continous_rotate
ROTATE_CONTROL=$ROTATE_OUT/rotate
FULL_POINTS=$CONT_POINTS
for f in $ROTATE_POINTS
do
  common_filename="${f##*/}"
  common_filenameWithoutExtension="${common_filename%.*}"
  echo $common_filename
  common_file=$CONT_COMMON_POINTS/$common_filename
  no_of_common_lines=`sed -n '$=' $common_file`
  echo $no_of_common_lines

  ext='.txt'
  full_file=$FULL_POINTS/$common_filenameWithoutExtension$ext
  echo $full_file
  no_of_full_lines=`sed -n '$=' $full_file`
  echo $no_of_full_lines

  java -DBaseResultDirectoryName=$ROTATE_OUT -DControlDirectoryName=$ROTATE_CONTROL -DInitializationFileName= -cp $MANXCAT_JAR salsa.mdsaschisq.ManxcatCentral -c config.properties -n 1 -t 1
done