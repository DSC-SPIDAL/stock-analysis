#!/bin/bash

if [ $# -eq 0 ]
  then
    echo "Directory must be specified as argument"
    exit 1
fi

BASE_DIR=$1

# change these to change the directories
MATRIX_DIR_NAME=matrix
VECTOR_DIR_NAME=vectors
POINTS_DIR_NAME=points
GLOBAL_POINTS_DIR_NAME=global_points
GLOBAL_MATRIX_DIR_NAME=global_matrix
GLOBAL_VECTORS_DIR_NAME=global_vectors

# no need to change the below lines
MATRIX_FILES=$BASE_DIR/$MATRIX_DIR_NAME/*
VECTOR_BASE=$BASE_DIR/$VECTOR_DIR_NAME/
POINTS=$BASE_DIR/$POINTS_DIR_NAME
GLOBAL_POINTS=$BASE_DIR/$GLOBAL_POINTS_DIR_NAME

mkdir -p $POINTS
mkdir -p $GLOBAL_POINTS

for f in $MATRIX_FILES
do
  filename="${f##*/}"
  filenameWithoutExtension="${filename%.*}"
  echo $filename
  vf=$VECTOR_BASE$filename
  echo $vf
  no_of_lines=`sed -n '$=' $vf`
  echo $no_of_lines
  sbatch damnds_stocks.sh $f $no_of_lines $POINTS/$filenameWithoutExtension
done

MATRIX_FILES=$BASE_DIR/$GLOBAL_MATRIX_DIR_NAME/*
VECTOR_BASE=$BASE_DIR/$GLOBAL_VECTORS_DIR_NAME/
for f in $MATRIX_FILES
do
  filename="${f##*/}"
  filenameWithoutExtension="${filename%.*}"
  echo $filename
  vf=$VECTOR_BASE$filename
  echo $vf
  no_of_lines=`sed -n '$=' $vf`
  echo $no_of_lines
  sbatch damnds_stocks.sh $f $no_of_lines $GLOBAL_POINTS/$filenameWithoutExtension
done

