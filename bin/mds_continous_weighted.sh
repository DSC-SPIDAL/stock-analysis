#!/usr/bin/env bash

#SBATCH -A skamburu
#SBATCH -N 4
#SBATCH --tasks-per-node=24
#SBATCH --time=12:00:00

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
#java -cp ../mpi/target/stocks-1.0-ompi1.8.1-jar-with-dependencies.jar PVizFileListGenerator -s 20040101 -e 20151231 -d 7 -o $INTER_MDS_DIR/list.txt -ext txt -i $ORIGINAL_STOCK_FILE > $INTER_MDS_DIR/mds.list.out.txt 2>&1
CSV_EXT='.csv'
TXT_EXT='.txt'
FILE_LIST=$INTER_MDS_DIR/list.txt
basefile=20040101_20050101
#basefile=20040101_20100320
#{
  #read;
  #while read line; do
  #while IFS='' read -r line || [[ -n "$line" ]]; do
  for line in $(cat $FILE_LIST)
  do
      filename=$line
      fwext="${filename%.*}"
      echo $filename
      vf=$VECTOR_BASE$fwext$CSV_EXT
      f=$MATRIX_DIR/$fwext$CSV_EXT
      echo $vf
      no_of_lines=`sed -n '$=' $vf`
      echo $no_of_lines
      # first one
      echo $WEIGHTS_DIR/$fwext$CSV_EXT
      if [ -z "$basefile" ]; then
        ./internal_mds_weighted.sh $f $no_of_lines $POINTS_DIR/$fwext $WEIGHTS_DIR/$fwext$CSV_EXT $DAMDS_SUMMARY_DIR/$fwext "" .95 2>&1
        echo "First finished"
      else
        java -cp ../mpi/target/stocks-1.0-ompi1.8.1-jar-with-dependencies.jar MDSPointGenerator -v $VECTOR_DIR -p $INTER_POINT_DIR -r $YEARLY_MDS_DIR -ff $basefile -sf $fwext 2>&1 | tee $INTER_MDS_DIR/$line
        while [ ! -f $POINTS_DIR/$fwext$TXT_EXT ]; do 
            ./internal_mds_weighted.sh $f $no_of_lines $POINTS_DIR/$fwext $WEIGHTS_DIR/$fwext$CSV_EXT $DAMDS_SUMMARY_DIR/$fwext $INTER_POINT_DIR/$fwext$TXT_EXT .95 
        done
        echo "Second finished" 
      fi
      echo "333"
      basefile=$fwext
  done 
#} < ${FILE_LIST}

echo "Finished..........."
