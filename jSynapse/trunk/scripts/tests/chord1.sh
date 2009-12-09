#
# 1st chord
#

gnome-terminal -e "java -cp bin core.experiments.networks2010.CreateNode 8000 -l 2000"
sleep 2 # for the stabilization

gnome-terminal -e "java -cp bin core.experiments.networks2010.CreateNode 8001 -j localhost 8000 -l 2001"
sleep 2 # for the stabilization

gnome-terminal -e "java -cp bin core.experiments.networks2010.CreateNode 8002 -j localhost 8000 -l 2002"
sleep 2 # for the stabilization

java -cp bin core.experiments.networks2010.CreateNode 8003 -j localhost 8000 -l 2003
