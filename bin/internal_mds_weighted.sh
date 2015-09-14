#!/bin/bash

#SBATCH -A skamburu
#SBATCH -N 4
#SBATCH --tasks-per-node=24
#SBATCH --time=12:00:00

cp=$HOME/.m2/repository/edu/indiana/soic/spidal/common/1.0-SNAPSHOT/common-1.0-SNAPSHOT.jar:$HOME/.m2/repository/com/google/guava/guava/15.0/guava-15.0.jar:$HOME/.m2/repository/commons-cli/commons-cli/1.2/commons-cli-1.2.jar:$HOME/.m2/repository/habanero-java-lib/habanero-java-lib/0.1.1/habanero-java-lib-0.1.1.jar:$HOME/.m2/repository/ompi/ompijavabinding/1.8.1/ompijavabinding-1.8.1.jar:$HOME/.m2/repository/edu/indiana/soic/spidal/damds/1.0-ompi1.8.1/damds-1.0-ompi1.8.1.jar:$HOME/.m2/repository/edu/indiana/soic/spidal/stocks/1.0-ompi1.8.1/stocks-1.0-ompi1.8.1-jar-with-dependencies.jar

x='x'
#opts="-XX:+UseConcMarkSweepGC -XX:ParallelCMSThreads=4 -Xms2G -Xmx2G"
opts="-XX:+UseG1GC -Xms512m -Xmx512m"

tpn=1
echo $SLURM_JOB_NUM_NODES 

wd=`pwd`

$BUILD/bin/mpirun --report-bindings --mca btl ^tcp java $opts -cp $cp -DNumberDataPoints=$2 -DDistanceMatrixFile=$1 -DPointsFile=$3.txt -DTimingFile=$5timing.txt -DSummaryFile=$5.summary.txt -DWeightMatrixFile=$4 -DTransformationFunction=trfm.DistanceTransformer edu.indiana.soic.spidal.damds.Program  -c config.properties -n $SLURM_JOB_NUM_NODES -t $tpn | tee $5.summary.txt
echo "Finished $0 on `date`" >> status.txt


