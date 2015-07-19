#!/bin/bash

BASE_DIR=$1
MATRIX_FILES=$BASE_DIR/matrix/*
VECTOR_BASE=$BASE_DIR/vectors/
WEIGHTS_DI=$BASE_DIR/weight_matrix_n

mkdir -p $BASE_DIR/w_g_points
mkdir -p $BASE_DIR/w_points

for f in $MATRIX_FILES
do
  filename="${f##*/}"
  filenameWithoutExtension="${filename%.*}"
  echo $filename
  vf=$VECTOR_BASE$filename
  echo $vf
  no_of_lines=`sed -n '$=' $vf`
  echo $no_of_lines
  #sbatch damnds_stocks_weights.sh $f $no_of_lines $BASE_DIR/w_points/$filenameWithoutExtension $WEIGHTS_DI/$filename
done

MATRIX_FILES=$BASE_DIR/global_matrix/*
VECTOR_BASE=$BASE_DIR/global_vectors/
WEIGHTS_DI=$BASE_DIR/global_weight_matrix_n
for f in $MATRIX_FILES
do
  filename="${f##*/}"
  filenameWithoutExtension="${filename%.*}"
  echo $filename
  vf=$VECTOR_BASE$filename
  echo $vf
  no_of_lines=`sed -n '$=' $vf`
  echo $no_of_lines
  sbatch damnds_stocks_weights.sh $f $no_of_lines $BASE_DIR/w_g_points/$filenameWithoutExtension $WEIGHTS_DI/$filename
done
