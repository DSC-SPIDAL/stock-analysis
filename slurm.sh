#!/bin/sh

#SBATCH -A skamburu
#SBATCH -N 30
#SBATCH --tasks-per-node=1
#SBATCH --time=00:30:00

/N/u/skamburu/projects/software/openmpi-1.8.1/build/bin/mpirun --hostfile nodes -np 24 --report-bindings --mca btl ^tcp java -cp /N/u/skamburu/projects/apps/stock-analysis/mpi/target/stocks-1.0-ompi1.8.1-jar-with-dependencies.jar DistanceCalculator -v /N/u/skamburu/data/2004_2014/vectors -d /tmp/matrix -m

cp /tmp/matrx/* /N/u/skamburu/data/2004_2014/matrix