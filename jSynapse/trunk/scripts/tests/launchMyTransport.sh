# First Chord
gnome-terminal -e "java -cp bin example.ui.LaunchMyTransport 5000"      # Bootstrap
sleep 2 # for the stabilization
java -cp bin example.ui.LaunchMyTransport 5001 -j localhost 5000
