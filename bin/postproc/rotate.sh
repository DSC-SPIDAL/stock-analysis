#!/bin/sh

GLOBAL_VECS=$BASE_DIR/$GLOBAL_VEC_DIR_NAME
GLOBAL=$BASE_DIR/$GLOBAL_STOCK_DIR_NAME
ORIGINAL_STOCK_FILE=$GLOBAL/$STOCK_FILE_NAME
#CAT_FILE=$BASE_DIR/all_companylist.csv
CAT_FILE=$BASE_DIR/$HIST_DIR_NAME
GLOBAL_POINTS=$BASE_DIR/$GLOBAL_POINTS_DIR_NAME
CONT_VECS=$BASE_DIR/$VECS_DIR_NAME
CONT_POINTS=$BASE_DIR/$POINTS_DIR_NAME
GLOBAL_CONT_COMMON_POINTS=$BASE_DIR/$GLOBAL_COMMON_POINTS_DIR_NAME
CONT_COMMON_POINTS=$BASE_DIR/$COMMON_POINTS_DIR_NAME
HIST_DIR=$BASE_DIR/$HIST_DIR_NAME
GLOBAL_HIST_DIR=$BASE_DIR/$GLOBAL_HIST_DIR_NAME

# rotate the points
# ******************
ROTATE_POINTS=$CONT_COMMON_POINTS/*
ROTATE_POINT_DIR=$CONT_COMMON_POINTS
ROTATE_BASE_FILE=$GLOBAL_CONT_COMMON_POINTS/$STOCK_FILE_NAME
ROTATE_OUT=$BASE_DIR/$ROTATE_OUT_DIR_NAME
ROTATE_CONTROL=$ROTATE_OUT/rotate_control
FULL_POINTS=$CONT_POINTS

mkdir -p $ROTATE_OUT
mkdir -p $ROTATE_CONTROL

FINAL_ROTATE_SUMMARY=$BASE_DIR/$ROTATE_FINAL_SUMMARY_DIR_NAME

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

  echo $FINAL_ROTATE_SUMMARY/$common_filename.rotation.summary.txt

  full='full'
  java -DBaseResultDirectoryName=$ROTATE_OUT \
  -DControlDirectoryName=$ROTATE_CONTROL \
  -DInitializationFileName=$ROTATE_BASE_FILE -DReducedVectorOutputFileName=$ROTATE_OUT/$common_filename_ext$ext \
  -DRotationLabelsFileName=$common_file \
  -DfinalRotationFileName=$full_file \
  -DWeightingOption=1 -DWeightingFileName=$BASE_DIR/$YEARLY_WEIGHT_SIMPLE_NAME/$common_filename -DfinalRotationPointCount=$no_of_full_lines \
  -DDataPoints=$no_of_common_lines \
  -cp $MANXCAT_JAR salsa.mdsaschisq.ManxcatCentral -c mconfig.properties -n 1 -t 1 2>&1 | tee $FINAL_ROTATE_SUMMARY/$common_filename.rotation.summary.txt
done

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


