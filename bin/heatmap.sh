#!/bin/sh

# this is a test file so that I can integrate this later to postproc.sh
$YEARLY_MDS_DIR_NAME=
$YEARLY_DISTANCES_DIR_NAME=
$YEARLY_HEATMAP_DIR=

HEAT_POINTS=$YEARLY_MDS_DIR_NAME/*
for f in $HEAT_POINTS
do
  common_filename="${f##*/}"
  common_filename_ext="${common_filename%.*}"
  common_file=$YEARLY_MDS_DIR_NAME/$common_filename
  echo 'common file' $common_file
  no_of_common_lines=`sed -n '$=' $common_file`
  echo $no_of_common_lines

  ext='.csv'
  distance_file=$YEARLY_DISTANCES_DIR_NAME/$common_filename_ext$ext

  java -cp ../mpi/target/stocks-1.0-ompi1.8.1-jar-with-dependencies.jar SHeatMapGenerator \
   -c heatmap.properties \
   -Drows $no_of_common_lines \
   -Dcols $no_of_common_lines \
   -DAmat  $YEARLY_MDS_DIR_NAME\common_filename \
   -DBmat  $YEARLY_DISTANCES_DIR_NAME\$distance_file \
   -Dtitle $common_filename  \
   -Doutdir $YEARLY_HEATMAP_DIR

  cat plot.bat >> plot_master.sh
done


