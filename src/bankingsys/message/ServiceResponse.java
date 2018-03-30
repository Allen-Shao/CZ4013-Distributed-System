package bankingsys.message;

import bankingsys.io.Deserializer;
import bankingsys.io.Serializable;
import bankingsys.io.Serializer;

import java.util.Map;

/**
 * Created by koallen on 29/3/18.
 */
public class ServiceResponse implements Serializable {
    private Integer responseCode;

    private Integer responseAccount;
    private String responseError;
    private Float responseAmount;

    public ServiceResponse() {}

    public ServiceResponse(Integer responseCode, Integer responseAccount, String responseError, Float responseAmount) {
        this.responseCode = responseCode;
        this.responseAccount = responseAccount;
        this.responseError = responseError;
        this.responseAmount = responseAmount;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public Integer getResponseAccount() {
        return responseAccount;
    }

    public String getResponseError() {
        return responseError;
    }

    public Float getResponseAmount() {
        return responseAmount;
    }

    @Override
    public void write(Serializer serializer) {
        serializer.writeInt(responseCode);
        if (responseCode == 200) {
            serializer.writeInt(this.responseAccount);
            serializer.writeFloat(this.responseAmount);
        } else {
            serializer.writeString(this.responseError);
        }
    }

    @Override
    public void read(Deserializer deserializer) {
        responseCode = deserializer.readInt();
        if (responseCode == 200) {
            this.responseAccount = deserializer.readInt();
            this.responseAmount = deserializer.readFloat();
        } else {
            this.responseError = deserializer.readString();
        }
    }
}
