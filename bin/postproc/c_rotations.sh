#!/bin/sh

GLOBAL_VECS=$BASE_DIR/$GLOBAL_VEC_DIR_NAME
GLOBAL=$BASE_DIR/$GLOBAL_STOCK_DIR_NAME
ORIGINAL_STOCK_FILE=$GLOBAL/$STOCK_FILE_NAME
GLOBAL_POINTS=$BASE_DIR/$GLOBAL_POINTS_DIR_NAME
CONT_VECS=$BASE_DIR/$VECS_DIR_NAME
CONT_POINTS=$BASE_DIR/$POINTS_DIR_NAME
GLOBAL_CONT_COMMON_POINTS=$BASE_DIR/$GLOBAL_COMMON_POINTS_DIR_NAME
CONT_COMMON_POINTS=$BASE_DIR/$CONT_COMMON_POINTS_DIR_NAME
ext='.txt'
# rotate the points
# ******************
ROTATE_POINTS=$CONT_COMMON_POINTS/*
ROTATE_BASE_FILE=$GLOBAL_CONT_COMMON_POINTS/$STOCK_FILE_NAME
FULL_POINTS=$CONT_POINTS

FINAL_ROTATE_SUMMARY=$BASE_DIR/$ROTATE_FINAL_SUMMARY_DIR_NAME
FINAL_ROTATE=$BASE_DIR/$ROTATE_FINAL_DIR_NAME

mkdir -p $CONT_COMMON_POINTS
mkdir -p $FINAL_ROTATE

echo "Generating point list"
java -cp ../mpi/target/stocks-1.0-ompi1.8.1-jar-with-dependencies.jar CCommonGenerator -v $CONT_VECS -p $CONT_POINTS -d $CONT_COMMON_POINTS -r $FINAL_ROTATE -sd 20040101 -ed 20060101 -l -md 5 | tee $BASE_DIR/$POSTPROC_INTERMEDIATE_DIR_NAME/common.points.list.out.txt

FILE_LIST=$CONT_COMMON_POINTS/list.txt
echo "Copying the first point file to rotate folder"
first_file_name=$(head -n 1 $FILE_LIST)
cp $CONT_POINTS/$first_file_name$ext $FINAL_ROTATE

first_common_file=$first_file_name

{
  read;
  while read line; do
      ROTATE_OUT=$CONT_COMMON_POINTS/$line/rotate
      mkdir -p $ROTATE_OUT
      ROTATE_CONTROL=$ROTATE_OUT/rotate_control
      mkdir -p $ROTATE_CONTROL

      second_common_file=$line

      java -cp ../mpi/target/stocks-1.0-ompi1.8.1-jar-with-dependencies.jar CCommonGenerator -v $CONT_VECS -p $CONT_POINTS -d $CONT_COMMON_POINTS -r $FINAL_ROTATE -ff $first_common_file -sf $second_common_file | tee $BASE_DIR/$POSTPROC_INTERMEDIATE_DIR_NAME/common.points.out.txt

      first_file=$CONT_COMMON_POINTS/$line/points/first.txt
      second_file=$CONT_COMMON_POINTS/$line/points/second.txt
      weight_file=$CONT_COMMON_POINTS/$line/weights/second.csv
      filename=$line

      no_of_lines=`sed -n '$=' $first_file`
      echo $no_of__lines


      full_file=$FULL_POINTS/$filename$ext
      echo 'full file' $full_file
      no_of_full_lines=`sed -n '$=' $full_file`
      echo $no_of_full_lines

      java -DBaseResultDirectoryName=$ROTATE_OUT \
      -DControlDirectoryName=$ROTATE_CONTROL \
      -DInitializationFileName=$first_file -DReducedVectorOutputFileName=$ROTATE_OUT/$filename$ext \
      -DRotationLabelsFileName=$second_file \
      -DfinalRotationFileName=$full_file \
      -DWeightingOption=1 -DWeightingFileName=$weight_file -DfinalRotationPointCount=$no_of_full_lines \
      -DDataPoints=$no_of_lines \
      -cp $MANXCAT_JAR salsa.mdsaschisq.ManxcatCentral -c mconfig.properties -n 1 -t 1 2>&1 | tee $FINAL_ROTATE_SUMMARY/$filename.rotation.summary.txt

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
      first_common_file=$line
  done
} < ${FILE_LIST}




