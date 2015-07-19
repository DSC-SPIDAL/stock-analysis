#!/bin/sh

# these has to be changed before running the program
#---------------------------------------------------

# the base directory where all the data recides
BASE_DIR=/N/u/skamburu/data/N2004_2014

#### names of directories inside the base dir
# directory where histograms are created
HIST_DIR_NAME=histogram
# directory where the global vectors
GLOBAL_VEC_DIR_NAME=global_vectors
# directory where the global stock file with all stocks for that period
GLOBA_STOCK_DIR_NAME=global
# directory name of the vectors
VECS_DIR_NAME=vectors
#directory name of common points
COMMON_POINTS_DIR_NAME=common_points
#directory name of where points are created by damnds
POINTS_DIR_NAME=points
# directory where global points
GLOBA_POINTS_DIR_NAME=global_points
# directory where final labeled point output
LABEL_OUT_DIR_NAME=label_points_hist
# directory where output of rotation program stored
ROTATE_OUT_DIR_NAME=rotate
# directory where rotated files are stored
ROTATE_FINAL_DIR_NAME=rotate_final
# name of the global stock file name
STOCK_FILE_NAME=2004_2014.csv
#global points file name, this should be in the global points directory
GLOBAL_POINTS_FILE_NAME=2004_2014.txt

MANXCAT_JAR=/N/u/skamburu/projects/apps/MDSasChisq/target/mdsaschisq-1.0-ompi1.8.1-jar-with-dependencies.jar

### don't change the following uness you knwo exactly what you change
# -------------------------------------------------------------------
GLOBAL_VECS=$BASE_DIR/$GLOBAL_VEC_DIR_NAME
GLOBAL=$BASE_DIR/$GLOBA_STOCK_DIR_NAME
ORIGINAL_STOCK_FILE=$GLOBAL/$STOCK_FILE_NAME
#CAT_FILE=$BASE_DIR/all_companylist.csv
CAT_FILE=$BASE_DIR/$HIST_DIR_NAME
GLOBAL_POINTS=$BASE_DIR/$GLOBA_POINTS_DIR_NAME
CONT_VECS=$BASE_DIR/$VECS_DIR_NAME
CONT_POINTS=$BASE_DIR/$POINTS_DIR_NAME
CONT_COMMON_POINTS=$BASE_DIR/$COMMON_POINTS_DIR_NAME
HIST_DIR=$BASE_DIR/$HIST_DIR_NAME

mkdir -p $CONT_COMMON_POINTS

# run the program to calculate the global vectors
# ----------------------------------------------
#java -cp mpi/target/stocks-1.0-ompi1.8.1-jar-with-dependencies.jar PVectorGenerator -i $GLOBAL -o $GLOBAL_VECS -d 3000

# generate the common points
# --------------------------
#java -cp ../mpi/target/stocks-1.0-ompi1.8.1-jar-with-dependencies.jar PointTransformer -g $GLOBAL_VECS/$STOCK_FILE_NAME -gp $GLOBAL_POINTS/$GLOBAL_POINTS_FILE_NAME -v $CONT_VECS -p $CONT_POINTS -d $CONT_COMMON_POINTS
# generate histogram
# --------------------
#java -cp ../mpi/target/stocks-1.0-ompi1.8.1-jar-with-dependencies.jar Histogram -v $CONT_VECS -s $ORIGINAL_STOCK_FILE -d $HIST_DIR -b 10

# rotate the points
# ******************
ROTATE_POINTS=$CONT_COMMON_POINTS/*
ROTATE_POINT_DIR=$CONT_COMMON_POINTS
ROTATE_BASE_FILE=$CONT_COMMON_POINTS/2004_2014.csv
ROTATE_OUT=$BASE_DIR/$ROTATE_OUT_DIR_NAME
ROTATE_CONTROL=$ROTATE_OUT/rotate_control
FULL_POINTS=$CONT_POINTS

mkdir -p $ROTATE_OUT
mkdir -p $ROTATE_CONTROL

for f in $ROTATE_POINTS
do
  common_filename="${f##*/}"
  common_filename_ext="${common_filename%.*}"
  common_file=$CONT_COMMON_POINTS/$common_filename
  echo 'common file' $common_file
  no_of_common_lines=`sed -n '$=' $common_file`
  echo $no_of_common_lines

  ext='.txt'
  full_file=$FULL_POINTS/$common_filename_ext$ext
  echo 'full file' $full_file
  no_of_full_lines=`sed -n '$=' $full_file`
  echo $no_of_full_lines

  full='full'
  java -DBaseResultDirectoryName=$ROTATE_OUT \
  -DControlDirectoryName=$ROTATE_CONTROL \
  -DInitializationFileName=$ROTATE_BASE_FILE -DReducedVectorOutputFileName=$ROTATE_OUT/$common_filename_ext$ext \
  -DRotationLabelsFileName=$common_file \
  -DfinalRotationFileName=$full_file \
  -DfinalRotationPointCount=$no_of_full_lines \
  -DDataPoints=$no_of_common_lines \
  -cp $MANXCAT_JAR salsa.mdsaschisq.ManxcatCentral -c mconfig.properties -n 1 -t 1
done

# apply labels to points
# ------------------------
# copy the fully rotated files to directory
FINAL_ROTATE=$BASE_DIR/$ROTATE_FINAL_DIR_NAME
mkdir -p $FINAL_ROTATE
cp $ROTATE_OUT/*full.txt $FINAL_ROTATE
dir=`pwd`
cd $FINAL_ROTATE
for i in SIMPLE*
do
    mv "$i" "`echo $i | sed 's/SIMPLE//'`"
done
for i in *full.txt
do
    mv "$i" "`echo $i | sed 's/full\.txt//'`"
done
cd $dir
LABEL_OUT=$BASE_DIR/$LABEL_OUT_DIR_NAME
mkdir -p $LABEL_OUT

java -cp ../mpi/target/stocks-1.0-ompi1.8.1-jar-with-dependencies.jar LabelApply \
-v $CONT_VECS \
-p $FINAL_ROTATE \
-d $LABEL_OUT \
-o $ORIGINAL_STOCK_FILE \
-s $HIST_DIR -h
