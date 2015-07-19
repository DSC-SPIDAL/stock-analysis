#!/bin/bash

BASE_DIR=$1

MATRIX_DIR_NAME=matrix
VECTOR_DIR_NAME=vectors
POINTS_DIR_NAME=weight_points
WEIGHT_MATRIX_DIR_NAME=weight_matrix
GLOBAL_POINTS_DIR_NAME=weight_global_points
GLOBAL_MATRIX_DIR_NAME=global_matrix
GLOBAL_VECTORS_DIR_NAME=global_vectors

MATRIX_FILES=$BASE_DIR/$MATRIX_DIR_NAME/*
VECTOR_BASE=$BASE_DIR/$VECTOR_DIR_NAME/
WEIGHTS_DI=$BASE_DIR/$WEIGHT_MATRIX_DIR_NAME

mkdir -p $BASE_DIR/$GLOBAL_POINTS_DIR_NAME
mkdir -p $BASE_DIR/$POINTS_DIR_NAME

for f in $MATRIX_FILES
do
  filename="${f##*/}"
  filenameWithoutExtension="${filename%.*}"
  echo $filename
  vf=$VECTOR_BASE$filename
  echo $vf
  no_of_lines=`sed -n '$=' $vf`
  echo $no_of_lines
  sbatch damnds_stocks_weights.sh $f $no_of_lines $BASE_DIR/$POINTS_DIR_NAME/$filenameWithoutExtension $WEIGHTS_DI/$filename
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
  sbatch damnds_stocks_weights.sh $f $no_of_lines $BASE_DIR/$GLOBAL_POINTS_DIR_NAME/$filenameWithoutExtension $WEIGHTS_DI/$filename
done
