#!/bin/bash

if [ $# -eq 0 ]
  then
    echo "Directory must be specified as argument"
    exit 1
fi

BASE_DIR=$1
MATRIX_FILES=$BASE_DIR/matrix/*
VECTOR_BASE=$BASE_DIR/vectors/
POINTS=$BASE_DIR/points
GLOBAL_POINTS=$BASE_DIR/global_points

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
  #sbatch damnds_stocks.sh $f $no_of_lines $POINTS/$filenameWithoutExtension
done

MATRIX_FILES=$BASE_DIR/global_matrix/*
VECTOR_BASE=$BASE_DIR/global_vectors/
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

