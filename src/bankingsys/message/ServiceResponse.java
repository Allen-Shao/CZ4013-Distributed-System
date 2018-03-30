package bankingsys.message;

import bankingsys.io.Deserializer;
import bankingsys.io.Serializable;
import bankingsys.io.Serializer;

/**
 * Created by koallen on 29/3/18.
 */
public class ServiceResponse implements Serializable {
    private int responseCode;

    public ServiceResponse() {}

    public ServiceResponse(int responseCode) {
        this.responseCode = responseCode;
    }

    public int getResponseCode() {
        return responseCode;
    }

    @Override
    public void write(Serializer serializer) {
        serializer.writeInt(responseCode);
    }

    @Override
    public void read(Deserializer deserializer) {
        responseCode = deserializer.readInt();
    }
}
