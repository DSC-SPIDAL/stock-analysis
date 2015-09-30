#!/usr/bin/env bash

SUMMARY=$1

for f in $SUMMARY
do
    v=`tac $f | grep -m 1 "Best Chisq" | awk '/^\Chisq/ {gsub(//,""); print $2}'`
    echo $f","$v >> out.txt
done