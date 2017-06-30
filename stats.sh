#!/bin/bash

#parameters in console
# from = yyyy-mm-dd
# to = yyyy-mm-dd

from=$1
to=$2

addedLines=0
deletedLines=0

for i in `git log --after="${from} 00:00" --before="${to} 00:00" --pretty=format:"%H"`; do
    commitString=${i}"^!"

    #echo "commitString: " $commitString
    
    values=($(git log --numstat --pretty=\"%H\" ${commitString} | awk 'NF==3 {plus+=$1; minus+=$2} END {printf("%d,%d\n", plus, minus)}'))

    IFS=', ' read -r -a valuesArray <<< "$values"
    addedLines=$((addedLines + ${valuesArray[0]}))
    deletedLines=$((deletedLines + ${valuesArray[1]}))

done;
echo ${addedLines}","${deletedLines}
