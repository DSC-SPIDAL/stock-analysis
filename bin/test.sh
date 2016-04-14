#!/bin/sh

#SBATCH -A skamburu
#SBATCH -N 4
#SBATCH --tasks-per-node=24
#SBATCH --time=12:00:00

FILE_LIST=/N/u/skamburu/data/7DAYACCULOG/mds/weighted/intermediate/list.txt
#{
  #read;
  #while read line; do
for line in $(cat $FILE_LIST)
do
      echo "for....................................................." $line
      $BUILD/bin/mpirun --report-bindings --mca btl ^tcp ls > /dev/null 2>&1 
      #$BUILD/bin/mpirun --report-bindings --mca btl ^tcp ls 
      #sleep 1m	
      echo "forend...................................................." $line
done


