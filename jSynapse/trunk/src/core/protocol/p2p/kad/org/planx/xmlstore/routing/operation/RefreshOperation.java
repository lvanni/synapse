package core.protocol.p2p.kad.org.planx.xmlstore.routing.operation;

import java.io.IOException;
import java.util.List;

import core.protocol.p2p.kad.org.planx.xmlstore.routing.Configuration;
import core.protocol.p2p.kad.org.planx.xmlstore.routing.Identifier;
import core.protocol.p2p.kad.org.planx.xmlstore.routing.Node;
import core.protocol.p2p.kad.org.planx.xmlstore.routing.RoutingException;
import core.protocol.p2p.kad.org.planx.xmlstore.routing.Space;
import core.protocol.p2p.kad.org.planx.xmlstore.routing.messaging.*;

/**
 * Refreshes all buckets.
 **/
public class RefreshOperation extends Operation {
    private Configuration conf;
    private MessageServer server;
    private Space space;
    private Node local;

    public RefreshOperation(Configuration conf, MessageServer server, Space space, Node local) {
        this.conf = conf;
        this.server = server;
        this.space = space;
        this.local = local;
    }

    /**
     * @return <code>null</code>
     * @throws RoutingException If a suboperation timed out
     * @throws IOException      If a network error occurred
     **/
    public synchronized Object execute() throws IOException, RoutingException {
        List refreshIds = space.getRefreshList();
        for (int i = 0, max = refreshIds.size(); i < max; i++) {
            Identifier id = (Identifier) refreshIds.get(i);
            Operation op = new NodeLookupOperation(conf, server, space, local, id);
            op.execute();
        }
        return null;
    }
}

