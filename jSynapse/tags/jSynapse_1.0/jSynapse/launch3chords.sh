# First Chord
gnome-terminal -e "java -cp bin example.chord.LaunchImpl localhost 8000"      # Bootstrap
sleep 2 # for the stabilization
gnome-terminal -e "java -cp bin example.chord.LaunchImpl localhost 8001 -j localhost 8000"
sleep 2
gnome-terminal -e "java -cp bin example.chord.LaunchImpl localhost 8002 -j localhost 8001"
sleep 2
gnome-terminal -e "java -cp bin example.chord.LaunchImpl localhost 8003 -j localhost 8002"

# Second Chord
sleep 2
gnome-terminal -e "java -cp bin example.chord.LaunchImpl localhost 8010"
sleep 2
gnome-terminal -e "java -cp bin example.chord.LaunchImpl localhost 8011 -j localhost 8010"
sleep 2
gnome-terminal -e "java -cp bin example.chord.LaunchImpl localhost 8012 -j localhost 8011"
sleep 2
gnome-terminal -e "java -cp bin example.chord.LaunchImpl localhost 8013 -j localhost 8012"

# Third Chord
sleep 2
gnome-terminal -e "java -cp bin example.chord.LaunchImpl localhost 8020"
sleep 2
gnome-terminal -e "java -cp bin example.chord.LaunchImpl localhost 8021 -j localhost 8020"
sleep 2
gnome-terminal -e "java -cp bin example.chord.LaunchImpl localhost 8022 -j localhost 8021"
sleep 2
gnome-terminal -e "java -cp bin example.chord.LaunchImpl localhost 8023 -j localhost 8022"

