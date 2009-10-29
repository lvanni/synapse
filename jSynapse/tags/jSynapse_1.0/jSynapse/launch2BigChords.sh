# First Chord
gnome-terminal -e "java -cp bin example.chord.LaunchImpl localhost 8000"      # Bootstrap
sleep 2 # for the stabilization
gnome-terminal -e "java -cp bin example.chord.LaunchImpl localhost 8001 -j localhost 8000"
sleep 2
gnome-terminal -e "java -cp bin example.chord.LaunchImpl localhost 8002 -j localhost 8000"
sleep 2
gnome-terminal -e "java -cp bin example.chord.LaunchImpl localhost 8003 -j localhost 8000"
sleep 2
gnome-terminal -e "java -cp bin example.chord.LaunchImpl localhost 8004 -j localhost 8000"
sleep 2
gnome-terminal -e "java -cp bin example.chord.LaunchImpl localhost 8005 -j localhost 8000"
sleep 2
gnome-terminal -e "java -cp bin example.chord.LaunchImpl localhost 8006 -j localhost 8000"



# Second Chord
sleep 2
gnome-terminal -e "java -cp bin example.chord.LaunchImpl localhost 8010"      # Bootstrap
sleep 2 # for the stabilization
gnome-terminal -e "java -cp bin example.chord.LaunchImpl localhost 8011 -j localhost 8010"
sleep 2
gnome-terminal -e "java -cp bin example.chord.LaunchImpl localhost 8012 -j localhost 8010"
sleep 2
gnome-terminal -e "java -cp bin example.chord.LaunchImpl localhost 8013 -j localhost 8010"
sleep 2
gnome-terminal -e "java -cp bin example.chord.LaunchImpl localhost 8014 -j localhost 8010"
sleep 2
gnome-terminal -e "java -cp bin example.chord.LaunchImpl localhost 8015 -j localhost 8010"
sleep 2
gnome-terminal -e "java -cp bin example.chord.LaunchImpl localhost 8016 -j localhost 8010"


