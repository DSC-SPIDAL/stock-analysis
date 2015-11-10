#!/bin/bash

# configure the environment variable
. ./postproc_env.sh $1

# generate common points
#sh postproc/c_rotations.sh
#sh postproc/cont_rotate.sh
#sh postproc/common_points.sh
### generate histograms
#sh postproc/histogram.sh
## rotations
#sh postproc/rotate.sh
## apply labels
#sh postproc/labelapply.sh
## generate heat maps
#sh postproc/heatmap.sh
#sh postproc/dheatmap.sh
#sh postproc/heatmap_scale.sh
#sh postproc/dheatmap_scale.sh
# convert poitns to pviz
sh postproc/pviz.sh
