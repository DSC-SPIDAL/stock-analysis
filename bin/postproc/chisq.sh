#!/usr/bin/env bash

SUMMARY=$1

for f in $SUMMARY/*
do
    common_filename="${f##*/}"
    common_filename_ext="${common_filename%.*}"
    v=`tac $f | grep -m 1 "Best Chisq" | awk '/^\Chisq/ {gsub(//,""); print $2}'`
    echo $common_filename_ext","$v >> out.txt
done