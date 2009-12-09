#
# 2nd chord
#

gnome-terminal -e "java -cp bin core.experiments.networks2010.CreateNode 9000 -l 3000"
sleep 2 # for the stabilization

gnome-terminal -e "java -cp bin core.experiments.networks2010.CreateNode 9001 -j localhost 9000 -l 3001"
sleep 2 # for the stabilization

gnome-terminal -e "java -cp bin core.experiments.networks2010.CreateNode 9002 -j localhost 9000 -l 3002"
sleep 2 # for the stabilization

java -cp bin core.experiments.networks2010.CreateNode 9003 -j localhost 9000 -l 3003

