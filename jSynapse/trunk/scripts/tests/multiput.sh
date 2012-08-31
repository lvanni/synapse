for i in $(seq $1)
#java -cp bin experiments.current.Oracle put 5300 test kadNetwork localhost 40349
do
    java -cp bin experiments.current.Oracle put key$i value$i $2 $3 $4
done

