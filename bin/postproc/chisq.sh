#!/usr/bin/env bash

SUMMARY=$1

rm -rf $SUMMARY/../chisq_list.txt
for f in $SUMMARY/*
do
    common_filename="${f##*/}"
    common_filename_ext="${common_filename%.*}"
    v=`tac $f | grep -m 1 "Best Chisq" | awk -F"Chisq" '{print $2}'`
    echo $common_filename_ext","$v >> $SUMMARY/../chisq_list.txt
done

