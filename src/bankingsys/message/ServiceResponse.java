package bankingsys.message;

import bankingsys.io.Deserializer;
import bankingsys.io.Serializable;
import bankingsys.io.Serializer;

import java.util.Map;

import static bankingsys.message.ServiceResponse.ResponseType.SUCCESS;

/**
 * Created by koallen on 29/3/18.
 */
public class ServiceResponse implements Serializable {

    public enum ResponseType {
        SUCCESS,
        FAILURE;
    }

    private ResponseType responseCode;
    private Integer responseAccount;
    private String responseMessage;
    private Float responseAmount;

    public ServiceResponse() {}

    public ServiceResponse(ResponseType responseCode, Integer responseAccount, String responseMessage, Float responseAmount) {
        this.responseCode = responseCode;
        this.responseAccount = responseAccount;
        this.responseMessage = responseMessage;
        this.responseAmount = responseAmount;
    }

    public ResponseType getResponseCode() {
        return responseCode;
    }

    public Integer getResponseAccount() {
        return responseAccount;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public Float getResponseAmount() {
        return responseAmount;
    }

    @Override
    public void write(Serializer serializer) {
        serializer.writeInt(responseCode.ordinal());
        if (responseCode == SUCCESS) {
            if (responseAccount != null)
                serializer.writeInt(this.responseAccount);
            if (responseAmount != null)
                serializer.writeFloat(this.responseAmount);
        } else {
            serializer.writeString(this.responseMessage);
        }
    }

    @Override
    public void read(Deserializer deserializer) {
        responseCode = ResponseType.values()[deserializer.readInt()];
        if (responseCode == SUCCESS) {
            this.responseAccount = deserializer.readInt();
            this.responseAmount = deserializer.readFloat();
        } else {
            this.responseMessage = deserializer.readString();
        }
    }
}
