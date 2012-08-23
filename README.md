synapse
=======

This Software is an implementation of the synapse P2P protocol: http://hal.inria.fr/inria-00474529/
The synapse protocol is based on a chord implementation.

It use also an KAD implementation (for testing the POC) provided by the project: http://code.google.com/p/p2pfinalproject/

Usage
=======

root path: jSynapse/trunk

1) launch the tracker:
java -cp bin experiments.current.tracker.ui.LaunchTracker

2) launch serveral node or synapse:
java -cp bin experiments.current.Create node [chord|kad] <IDNetwork> -t trackerAddress trackerPort
java -cp bin experiments.current.Create synapse (-a [chord|kad] <IDNetwork>)+ -t trackerAddress trackerPort

L.V
