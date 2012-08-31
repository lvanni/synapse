for i in $(seq $1)
do
    java -cp bin experiments.current.Create node chord n1 -b&
    sleep 10 # for the stabilization
done

