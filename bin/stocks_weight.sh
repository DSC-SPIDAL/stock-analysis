#!/bin/bash

BASE_DIR=$1
MATRIX_FILES=$BASE_DIR/matrix/*
VECTOR_BASE=$BASE_DIR/vectors/
WEIGHTS_DI=$BASE_DIR/weight_matrix

mkdir -p $BASE_DIR/points
mkdir -p $BASE_DIR/global_points

for f in $MATRIX_FILES
do
  filename="${f##*/}"
  filenameWithoutExtension="${filename%.*}"
  echo $filename
  vf=$VECTOR_BASE$filename
  echo $vf
  no_of_lines=`sed -n '$=' $vf`
  echo $no_of_lines
  sbatch damnds_stocks_weights.sh $f $no_of_lines $BASE_DIR/points/$filenameWithoutExtension $WEIGHTS_DI/$f
done

MATRIX_FILES=/N/u/skamburu/data/C2004_2014/global_matrix/*
VECTOR_BASE=/N/u/skamburu/data/C2004_2014/global_vectors/
for f in $MATRIX_FILES
do
  filename="${f##*/}"
  filenameWithoutExtension="${filename%.*}"
  echo $filename
  vf=$VECTOR_BASE$filename
  echo $vf
  no_of_lines=`sed -n '$=' $vf`
  echo $no_of_lines
  sbatch damnds_stocks_weights.sh $f $no_of_lines $BASE_DIR/global_points/$filenameWithoutExtension $WEIGHTS_DI/$f
done
