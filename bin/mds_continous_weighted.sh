#!/usr/bin/env bash

if [ $# -eq 0 ]
  then
    echo "Directory must be specified as argument"
    exit 1
fi

# change these to change the directories
BASE_DIR=$1
PREPROC_DIR=$BASE_DIR/preproc

ORIGINAL_STOCK_FILE=$BASE_DIR/input/2004_2015.csv

# no need to change the below lines
YEARLY_PREPROC_DIR=$PREPROC_DIR/yearly
GLOBAL_PREPROC_DIR=$PREPROC_DIR/global

MDS_DIR=$BASE_DIR/mds/weighted
YEARLY_MDS_DIR=$MDS_DIR/yearly
GLOBAL_MDS_DIR=$MDS_DIR/global
INTER_MDS_DIR=$MDS_DIR/intermediate
INTER_POINT_DIR=$INTER_MDS_DIR/points

POINTS_DIR=$YEARLY_MDS_DIR
MATRIX_DIR=$YEARLY_PREPROC_DIR/distances
VECTOR_DIR=$YEARLY_PREPROC_DIR/vectors
WEIGHTS_DIR=$YEARLY_PREPROC_DIR/weights/matrix
DAMDS_SUMMARY_DIR=$YEARLY_MDS_DIR/summary

GLOBAL_POINTS_DIR=$GLOBAL_MDS_DIR
GLOBAL_MATRIX_DIR=$GLOBAL_PREPROC_DIR/distances
GLOBAL_VECTORS_DIR=$GLOBAL_PREPROC_DIR/vectors
GLOBAL_WEIGHTS_DIR=$GLOBAL_PREPROC_DIR/weights/matrix
GLOBAL_DAMDS_SUMMARY_DIR=$GLOBAL_MDS_DIR/summary

mkdir -p $DAMDS_SUMMARY_DIR
mkdir -p $GLOBAL_DAMDS_SUMMARY_DIR
mkdir -p $INTER_POINT_DIR

MATRIX_FILES=$MATRIX_DIR/*
VECTOR_BASE=$VECTOR_DIR/

echo "Generating list"
basefile=
#java -cp ../mpi/target/stocks-1.0-ompi1.8.1-jar-with-dependencies.jar CCommonGenerator -v $VECTOR_DIR -p $INTER_POINT_DIR -d $INTER_MDS_DIR -r $YEARLY_MDS_DIR -sd 20040101 -ed 20150101 -l -md 5 | tee $BASE_DIR/$INTER_MDS_DIR/mds.list.out.txt
java -cp ../mpi/target/stocks-1.0-ompi1.8.1-jar-with-dependencies.jar PVizFileListGenerator -s 20040101 -e 20151231 -d 9 -o $INTER_MDS_DIR/list.txt -ext "" -i $ORIGINAL_STOCK_FILE | tee $BASE_DIR/$INTER_MDS_DIR/mds.list.out.txt
TXT_EXT='.txt'
FILE_LIST=$INTER_MDS_DIR/list.txt
{
  read;
  while read line; do
      filename=$line$TXT_EXT
      echo $filename
      vf=$VECTOR_BASE$filename
      echo $vf
      no_of_lines=`sed -n '$=' $vf`
      echo $no_of_lines
      java -cp ../mpi/target/stocks-1.0-ompi1.8.1-jar-with-dependencies.jar MDSPointGenerator -v $VECTOR_DIR -p $INTER_POINT_DIR -r $YEARLY_MDS_DIR -ff $line | tee $BASE_DIR/$INTER_MDS_DIR/mds.list.out.txt
      # first one
      if [ -z "$basefile" ]; then
        sbatch internal_mds_weighted.sh $f $no_of_lines $POINTS_DIR/$line $WEIGHTS_DIR/$filename $DAMDS_SUMMARY_DIR/$line
      else
        sbatch internal_mds_weighted.sh $f $no_of_lines $POINTS_DIR/$line $WEIGHTS_DIR/$filename $DAMDS_SUMMARY_DIR/$line $INTER_POINT_DIR/$filename
      fi
      basefile=$line
  done
} < ${FILE_LIST}