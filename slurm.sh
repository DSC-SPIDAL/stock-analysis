#!/bin/sh

#SBATCH -A skamburu
#SBATCH -N 24
#SBATCH --tasks-per-node=1
#SBATCH --time=04:00:00

#srun ./cp_global.sh
srun -l mkdir -p /scratch/2004_2014/continous_breaks
srun -l rm -f /scratch/2004_2014/continous_breaks/*
cp -l /N/u/skamburu/data/W2004_2014/2004_2014.csv /scratch/2004_2014/2004_2014.csv

#/N/u/skamburu/projects/software/openmpi-1.8.1/build/bin/mpirun --report-bindings --mca btl ^tcp java -cp /N/u/skamburu/projects/apps/stock-analysis/mpi/target/stocks-1.0-ompi1.8.1-jar-with-dependencies.jar DistanceCalculator -v /N/u/skamburu/data/2004_2014/global_vectors -d /tmp/global_matrix_1 -m
/N/u/skamburu/projects/software/openmpi-1.8.1/build/bin/mpirun --report-bindings --mca btl ^tcp java -cp /N/u/skamburu/projects/apps/stock-analysis/mpi/target/stocks-1.0-ompi1.8.1-jar-with-dependencies.jar FileBreaker -i /scratch/2004_2014/2004_2014.csv -o /scratch/2004_2014/continous_breaks -s 20040101 -e 20150101 -d 4 -m

#srun "cp /tmp/matrx/* /N/u/skamburu/data/2004_2014/matrix/"
srun -l mkdir -p /N/u/skamburu/data/W2004_2014/continous_breaks
srun -l cp -rf /scratch/2004_2014/continous_breaks /N/u/skamburu/data/W2004_2014/continous_breaks

srun -l rm -rf /scratch/2004_2014/continous_breaks/*
