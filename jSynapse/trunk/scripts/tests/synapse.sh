#
# synapse
#

gnome-terminal -e "java -cp bin core.experiments.networks2010.CreateSynapse 5000 -a 8004 -j localhost 8000 -a 9004 -j localhost 9000 -l 4000"
sleep 2 # for the stabilization

java -cp bin core.experiments.networks2010.CreateSynapse 5001 -js localhost 5000 -a 8010 -j localhost 8000 -l 4001

