synapse
=======

This Software is an implementation of the synapse P2P protocol: http://hal.inria.fr/inria-00474529/
The synapse protocol is based on a chord implementation.

It use also a KAD implementation (for testing the POC) provided by the project: http://code.google.com/p/p2pfinalproject/

Usage
=======

root path: jSynapse/trunk

1) launch the tracker:
java -cp bin experiments.current.tracker.ui.LaunchTracker [[-p|--port] <port>] [-b|--background]

2) launch serveral nodes or synapses:
java -cp bin experiments.current.Create [node|synapse] [chord|kad] <NetworkID> [[-t|--tracker] <address> <port>] [-b|--background]

3) Use the Oracle (put/get operation):
java -cp bin experiments.current.Oracle put <key> <value> <networkID> <address> <port>
java -cp bin experiments.current.Oracle get <key> <networkID> <address> <port>

L.V
