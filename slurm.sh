#!/bin/sh

#SBATCH -A skamburu
#SBATCH -N 2
#SBATCH --tasks-per-node=1
#SBATCH --time=04:00:00

srun ./rm.sh

/N/u/skamburu/projects/software/openmpi-1.8.1/build/bin/mpirun --report-bindings --mca btl ^tcp java -cp /N/u/skamburu/projects/apps/stock-analysis/mpi/target/stocks-1.0-ompi1.8.1-jar-with-dependencies.jar DistanceCalculator -v /N/u/skamburu/data/2004_2014/global_vectors -d /tmp/global_matrix_1 -m

#srun "cp /tmp/matrx/* /N/u/skamburu/data/2004_2014/matrix/"
srun ./cp.sh
