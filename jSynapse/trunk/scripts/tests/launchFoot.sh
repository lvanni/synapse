# First Chord
gnome-terminal -e "java -cp bin example.ui.LaunchFoot 9000"      # Bootstrap
sleep 2 # for the stabilization
gnome-terminal -e "java -cp bin example.ui.LaunchFoot 9001 -j localhost 9000"
sleep 2
gnome-terminal -e "java -cp bin example.ui.LaunchFoot 9002 -j localhost 9000"
sleep 2
java -cp bin example.ui.LaunchFoot 9003 -j localhost 9000