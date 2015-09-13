#!/bin/bash bash

# these has to be changed before running the program
#---------------------------------------------------
if [ $# -eq 0 ]
  then
    echo "Directory must be specified as argument"
    exit 1
fi

# MANXCAT_JAR location
export MANXCAT_JAR=$HOME/.m2/repository/com/google/guava/guava/15.0/guava-15.0.jar:$HOME/.m2/repository/commons-cli/commons-cli/1.2/commons-cli-1.2.jar:$HOME/.m2/repository/habanero-java-lib/habanero-java-lib/0.1.4-SNAPSHOT/habanero-java-lib-0.1.4-SNAPSHOT.jar:$HOME/.m2/repository/ompi/ompijavabinding/1.8.1/ompijavabinding-1.8.1.jar:$HOME/.m2/repository/org/jblas/jblas/1.2.3/jblas-1.2.3.jar:/$HOME/.m2/repository/edu/indiana/salsahpc/mdsaschisq/1.0-ompi1.8.1/mdsaschisq-1.0-ompi1.8.1.jar

# the base directory where all the data recides
export BASE_DIR=$1
# weather we are doing weighted or unweighted transformations
export WEIGHTED=unweighted
# input dirs
# ------------
# input
export GLOBAL_STOCK_DIR_NAME=input
# preproc
export PREPROC_DIR_NAME=preproc
# preproc/yearly
export YEARLY_PREPROC_DIR_NAME=$PREPROC_DIR_NAME/yearly
# preproc/yearly/vectors
export VECS_DIR_NAME=$YEARLY_PREPROC_DIR_NAME/vectors
# preproc/global
export GLOBAL_PREPROC_DIR_NAME=$PREPROC_DIR_NAME/global
# preproc/global/vectors
export GLOBAL_VEC_DIR_NAME=$GLOBAL_PREPROC_DIR_NAME/vectors
# mds/$WEIGHTED
export MDS_DIR_NAME=mds/$WEIGHTED
# mds/yearly
export YEARLY_MDS_DIR_NAME=$MDS_DIR_NAME/yearly
# mds/global
export GLOBAL_MDS_DIR_NAME=$MDS_DIR_NAME/global
# name of the global stock file name
export STOCK_FILE_NAME=2004_2014.csv
# preproc/yearly/distances
export YEARLY_DISTANCES_DIR_NAME=$YEARLY_PREPROC_DIR_NAME/distances
# preproc/yearly/weights/simple
export YEARLY_WEIGHT_SIMPLE_NAME=$YEARLY_PREPROC_DIR_NAME/weights/simple
# preproc/global/weights/simple
export GLOBAL_WEIGHT_SIMPLE_NAME=$GLOBAL_PREPROC_DIR_NAME/weights/simple
# output dirs
# ---------------
# postproc/$WEIGHTED
export POSTPROC_DIR_NAME=postproc/$WEIGHTED
# postproc/$WEIGHTED/intermediate
export POSTPROC_INTERMEDIATE_DIR_NAME=$POSTPROC_DIR_NAME/intermediate
# postporc/$WEIGHTED/yearly
export YEARLY_POSTPROC_DIR_NAME=$POSTPROC_DIR_NAME/yearly
# postproc/$WEIGHTED/global;
export GLOBAL_POSTPROC_DIR_NAME=$POSTPROC_DIR_NAME/global

# directory where histograms are created
# postproc/$WEIGHTED/intermediate/histogram
export HIST_DIR_NAME=$POSTPROC_INTERMEDIATE_DIR_NAME/histogram
# postproc/$WEIGHTED/intermediate/histogram/global
export GLOBAL_HIST_DIR_NAME=$POSTPROC_INTERMEDIATE_DIR_NAME/histogram/global
# postproc/$WEIGHTED/intermediate/common_points
export COMMON_POINTS_DIR_NAME=$POSTPROC_INTERMEDIATE_DIR_NAME/common_points
# postproc/$WEIGHTED/intermediate/global_common_points
export GLOBAL_COMMON_POINTS_DIR_NAME=$POSTPROC_INTERMEDIATE_DIR_NAME/global_common_points

#directory name of where points are created by damds
export POINTS_DIR_NAME=$YEARLY_MDS_DIR_NAME
# directory where global points
export GLOBAL_POINTS_DIR_NAME=$GLOBAL_MDS_DIR_NAME

# postporc/$WEIGHTED/yearly/rotate/points
export ROTATE_FINAL_DIR_NAME=$YEARLY_POSTPROC_DIR_NAME/rotate/points
# postporc/$WEIGHTED/yearly/rotate/summary
export ROTATE_FINAL_SUMMARY_DIR_NAME=$YEARLY_POSTPROC_DIR_NAME/rotate/summary
# postporc/$WEIGHTED/yearly/rotate/points/labeled/byhist
export LABEL_OUT_DIR_NAME=$YEARLY_POSTPROC_DIR_NAME/rotate/points/labeled/byhist
# postporc/$WEIGHTED/yearly/rotate/points/labeled/bysec
export SECTOR_LABEL_OUT_DIR_NAME=$YEARLY_POSTPROC_DIR_NAME/rotate/points/labeled/bysec
# postporc/$WEIGHTED/global/rotate/points
export GLOBAL_FINAL_POINTS_DIR=$GLOBAL_POSTPROC_DIR_NAME/points
# postporc/$WEIGHTED/global/rotate/points/labeled/byhist
export GLOBAL_LABEL_OUT_DIR_NAME=$GLOBAL_POSTPROC_DIR_NAME/points/labeled/byhist
# postporc/$WEIGHTED/global/rotate/points/labeled/bysec
export GLOBAL_SECTOR_LABEL_OUT_DIR_NAME=$GLOBAL_POSTPROC_DIR_NAME/points/labeled/bysec
# postporc/$WEIGHTED/yearly/heatmap
export YEARLY_HEATMAP_DIR_NAME=$YEARLY_POSTPROC_DIR_NAME/heatmap

# directory where output of rotation program stored
export ROTATE_OUT_DIR_NAME=$POSTPROC_INTERMEDIATE_DIR_NAME/rotate_ops

#global points file name, this should be in the global points directory
export GLOBAL_POINTS_FILE_NAME=2004_2014.txt

mkdir -p $BASE_DIR/$ROTATE_FINAL_DIR_NAME
mkdir -p $BASE_DIR/$ROTATE_FINAL_SUMMARY_DIR_NAME
mkdir -p $BASE_DIR/$LABEL_OUT_DIR_NAME
mkdir -p $BASE_DIR/$GLOBAL_HIST_DIR_NAME

