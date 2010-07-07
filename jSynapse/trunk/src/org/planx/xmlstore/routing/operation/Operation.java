package org.planx.xmlstore.routing.operation;

import java.io.IOException;
import org.planx.xmlstore.routing.RoutingException;

/**
 * User initiated operations should be subclasses of this class.
 **/
public abstract class Operation {
    /**
     * Starts the operation and returns when the operation is finished.
     **/
    public abstract Object execute() throws IOException, RoutingException;
}
