#!/bin/sh

# this is a test file so that I can integrate this later to postproc.sh
HEAT_POINTS=$YEARLY_MDS_DIR_NAME/*
for f in $HEAT_POINTS
do
  common_filename="${f##*/}"
  common_filename_ext="${common_filename%.*}"

  java -cp ../mpi/target/stocks-1.0-ompi1.8.1-jar-with-dependencies.jar SHeatMapGenerator \
   -c heatmap.properties \
   -Drows $rows \
   -Dcols $rows \
   -DAmat  \
   -DBmat   \
   -Dtitle  \

  cat plot.bat >> plot_master.sh
done


