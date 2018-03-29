package bankingsys.server.handler;

import bankingsys.message.ServiceRequest;
import bankingsys.message.ServiceResponse;

/**
 * Created by koallen on 29/3/18.
 */
public interface ServiceHandler {
    ServiceResponse handleRequest(ServiceRequest request);
}
