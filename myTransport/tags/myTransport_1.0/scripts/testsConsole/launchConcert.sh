# First Chord
gnome-terminal -e "java -cp bin:lib/jSynapse.jar:lib/jSynapse.jar ui.console.LaunchConcert 8000"      # Bootstrap
sleep 2 # for the stabilization
gnome-terminal -e "java -cp bin:lib/jSynapse.jar ui.console.LaunchConcert 8001 -j localhost 8000"
sleep 2
gnome-terminal -e "java -cp bin:lib/jSynapse.jar ui.console.LaunchConcert 8002 -j localhost 8000"
sleep 2
java -cp bin:lib/jSynapse.jar ui.console.LaunchConcert 8003 -j localhost 8000