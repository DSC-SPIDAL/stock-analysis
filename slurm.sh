#!/bin/sh

#SBATCH -A skamburu
#SBATCH -N 24
#SBATCH --tasks-per-node=1
#SBATCH --time=00:30:00

#/N/u/skamburu/projects/software/openmpi-1.8.1/build/bin/mpirun --hostfile nodes --report-bindings --mca btl ^tcp java -cp /N/u/skamburu/projects/apps/stock-analysis/mpi/target/stocks-1.0-ompi1.8.1-jar-with-dependencies.jar DistanceCalculator -v /N/u/skamburu/data/2004_2014/vectors -d /tmp/matrix -m

#srun "cp /tmp/matrx/* /N/u/skamburu/data/2004_2014/matrix/"
srun ./cp.sh 
