#!/bin/bash

#parameters in console
# from = yyyy-mm-dd
# to = yyyy-mm-dd

DETAILLOG=stats.log

from=$1
to=$2

addedLines=0
deletedLines=0

echo ${from} - ${to} >> $DETAILLOG
for i in `git log --after="${from} 00:00" --before="${to} 00:00" --pretty=format:"%H"`; do
    commitString=${i}"^!"
    
    values=($(git log --numstat --pretty=\"%H\" ${commitString} | awk 'NF==3 {plus+=$1; minus+=$2} END {printf("%d,%d\n", plus, minus)}'))

    IFS=', ' read -r -a valuesArray <<< "$values"
    addedLines=$((addedLines + ${valuesArray[0]}))
    deletedLines=$((deletedLines + ${valuesArray[1]}))
    
    echo "commit: " ${i} " : " ${valuesArray[0]} " " ${valuesArray[1]} >> $DETAILLOG

done;
echo ${addedLines}","${deletedLines}
