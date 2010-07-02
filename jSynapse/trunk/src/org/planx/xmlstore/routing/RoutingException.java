package org.planx.xmlstore.routing;

import java.io.IOException;

public class RoutingException extends IOException {
    public RoutingException() {
        super();
    }

    public RoutingException(String message) {
        super(message);
    }

    public RoutingException(Throwable cause) {
        super(cause.toString());
    }

    public RoutingException(String msg, Throwable cause) {
        super(msg+" "+cause.toString());
    }
}
