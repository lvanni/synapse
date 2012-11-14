#!/bin/bash

if [[ $# < 1 ]]
then echo 'usage: simulator.sh nb_node';
exit
fi

ulimit -u 8192

#java -jar simulator.jar > log.txt &

for i in $(seq 1 $1)
do
#echo 'creating node...';
#java -d64 -Xmx8192M -jar  oracleSimulator.jar create chord n1;
java -d64 -Xmx8192M -jar  oracleSimulator.jar create kad n1
echo 'node ' $i 'created';
sleep 1;
done