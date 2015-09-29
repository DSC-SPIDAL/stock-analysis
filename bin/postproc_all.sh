#!/bin/bash

#SBATCH -A skamburu
#SBATCH -N 28
#SBATCH --tasks-per-node=1
#SBATCH --time=04:00:00

# configure the environment variable
. ./postproc_env.sh $1

# generate common points
#sh postproc/cont_common_points.sh
#sh postproc/cont_rotate.sh
#sh postproc/common_points.sh
## generate histograms
#sh postproc/histogram.sh
## rotations
#sh postproc/rotate.sh
## apply labels
#sh postproc/labelapply.sh
## generate heat maps
#sh postproc/heatmap.sh
#sh postproc/heatmap_scale.sh
# convert poitns to pviz
sh postproc/pviz.sh
