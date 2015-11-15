#!/bin/sh

GLOBAL=$BASE_DIR/$GLOBAL_STOCK_DIR_NAME
ORIGINAL_STOCK_FILE=$GLOBAL/$STOCK_FILE_NAME
CONT_VECS=$BASE_DIR/$VECS_DIR_NAME

LABEL_OUT=$BASE_DIR/$LABEL_OUT_DIR_NAME
SEC_LABEL_OUT=$BASE_DIR/$SECTOR_LABEL_OUT_DIR_NAME
GLOBAL_LABEL_OUT=$BASE_DIR/$GLOBAL_LABEL_OUT_DIR_NAME
GLOBAL_SEC_LABEL_OUT=$BASE_DIR/$GLOBAL_SECTOR_LABEL_OUT_DIR_NAME

# generate pviz file
# yearly histo
java -cp ../mpi/target/stocks-1.0-ompi1.8.1-jar-with-dependencies.jar PvizGenerator -c $BASE_DIR/$LABELED_FINAL_DIR_NAME/histo_clusters.xml -p $LABEL_OUT -d $LABEL_OUT/pviz -o $ORIGINAL_STOCK_FILE -v $CONT_VECS
java -cp ../mpi/target/stocks-1.0-ompi1.8.1-jar-with-dependencies.jar PvizGenerator -c $BASE_DIR/$LABELED_FINAL_DIR_NAME/histo_global_clusters.xml -p $GLOBAL_LABEL_OUT -d $GLOBAL_LABEL_OUT/pviz -o $ORIGINAL_STOCK_FILE -v $BASE_DIR/$GLOBAL_VEC_DIR_NAME

# yearly sectors
java -cp ../mpi/target/stocks-1.0-ompi1.8.1-jar-with-dependencies.jar PvizGenerator -c $BASE_DIR/$LABELED_FINAL_DIR_NAME/sector_clusters.xml -p $SEC_LABEL_OUT -d $SEC_LABEL_OUT/pviz -o $ORIGINAL_STOCK_FILE -v $CONT_VECS
java -cp ../mpi/target/stocks-1.0-ompi1.8.1-jar-with-dependencies.jar PvizGenerator -c $BASE_DIR/$LABELED_FINAL_DIR_NAME/sector_global_clusters.xml -p $GLOBAL_SEC_LABEL_OUT -d $GLOBAL_SEC_LABEL_OUT/pviz -o $ORIGINAL_STOCK_FILE -v $BASE_DIR/$GLOBAL_VEC_DIR_NAME

# translate points
java -cp ../mpi/target/stocks-1.0-ompi1.8.1-jar-with-dependencies.jar TranslatePoints -p $LABEL_OUT/pviz -d $LABEL_OUT/pviz_translated
java -cp ../mpi/target/stocks-1.0-ompi1.8.1-jar-with-dependencies.jar TranslatePoints -p $GLOBAL_LABEL_OUT/pviz -d $GLOBAL_LABEL_OUT/pviz_translated
