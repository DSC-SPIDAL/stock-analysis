#!/usr/bin/env bash

SUMMARY=$1

rm -rf $SUMMARY/../../stress_list.txt
for f in $SUMMARY/*.summary.txt
do
    common_filename="${f##*/}"
    common_filename_ext="${common_filename%.*}"
    v=`tac $f | grep -m 1 "Final Stress:" | awk -F"Stress:" '{print $2}'`
    echo $common_filename_ext","$v >> $SUMMARY/../../stress_list.txt
done

