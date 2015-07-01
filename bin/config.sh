#!/bin/bash
if [ -z "$1" ]
  then
    echo "Usage: $(basename $0) pattern datapoints distancefile ismemorymapped isbigendian(Java) isSammon initialPointsFile"
else
NumberDataPoints=-1
if [ -n "$2" ]
then
	NumberDataPoints=$2
fi
DistanceMatrixFile="none"
if [ -n "$3" ]
then
    DistanceMatrixFile="$3"
fi
isMemoryMapped="true"
if [ -n "$4" ]
then
    isMemoryMapped="$4"
fi
isBigEndian="true"
if [ -n "$5" ]
then
    isBigEndian="$5"
fi
isSammon="false"
if [ -n "$6" ]
then
    isSammon="$6"
fi
initialPointsFile=""
if [ -n "$7" ]
then
    initialPointsFile="$7"
fi
pat=$1
if [ -d "$pat" ]; then
   echo "Directory $pat exists! Do you want to overwrite its content?"
   select yn in "Yes" "No"; do
      case $yn in
          Yes ) rm -rf $pat;break;;
          No ) exit;;
      esac
   done 
fi

mkdir $pat
pwd=`pwd`
printf "DistanceMatrixFile = $DistanceMatrixFile\n" >> config$pat.properties
printf "WeightMatrixFile = \n" >> config$pat.properties
printf "LabelFile =\n" >> config$pat.properties
printf "InitialPointsFile = $initialPointsFile\n" >> config$pat.properties

printf "PointsFile = $pwd/$pat/damds-points.txt\n" >> config$pat.properties
printf "TimingFile = $pwd/$pat/damds-timing.txt\n" >> config$pat.properties
printf "SummaryFile = $pwd/$pat/damds-summary.txt\n" >> config$pat.properties

printf "NumberDataPoints = $NumberDataPoints\n" >> config$pat.properties
printf "TargetDimension = 3\n" >> config$pat.properties
printf "DistanceTransform = 1.0\n" >> config$pat.properties
printf "Threshold = 0.000001\n" >> config$pat.properties
printf "Alpha = 0.95\n" >> config$pat.properties
printf "CGIterations = 100\n" >> config$pat.properties
printf "CGErrorThreshold = 0.00001\n" >> config$pat.properties

printf "IsSammon = $isSammon\n" >> config$pat.properties
printf "IsBigEndian = $isBigEndian\n" >> config$pat.properties
printf "IsMemoryMapped = $isMemoryMapped\n" >> config$pat.properties
fi
