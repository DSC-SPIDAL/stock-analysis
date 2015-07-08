#!/bin/bash

MATRIX_FILES=/N/u/skamburu/data/2004_2014/global_matrix/*
VECTOR_BASE=/N/u/skamburu/data/2004_2014/global_vectors/
for f in $MATRIX_FILES
do
  filename="${f##*/}"
  filenameWithoutExtension="${filename%.*}"
  echo $filename
  vf=$VECTOR_BASE$filename
  echo $vf
  no_of_lines=`sed -n '$=' $vf`
  echo $no_of_lines
  sbatch damnds_stocks.sh $f $no_of_lines global_points/$filenameWithoutExtension
done

