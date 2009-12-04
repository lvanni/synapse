#
# 2nd chord
#

gnome-terminal -e "java -jar node.jar 9000 -l 3000"
sleep 2 # for the stabilization

gnome-terminal -e "java -jar node.jar 9001 -j localhost 9000 -l 3001"
sleep 2 # for the stabilization

gnome-terminal -e "java -jar node.jar 9002 -j localhost 9000 -l 3002"
sleep 2 # for the stabilization

java -jar node.jar 9003 -j localhost 9000 -l 3003

