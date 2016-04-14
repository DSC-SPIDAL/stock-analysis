

count=$(ls $1/mds/weighted/yearly |wc -l)
echo "count" $count
while [ ! $count -eq $2 ]
do
echo "count" $count
sleep 5
count=$(ls $1/mds/weighted/yearly |wc -l)
done

echo "files equal count" $count
sh postproc_all.sh $1

