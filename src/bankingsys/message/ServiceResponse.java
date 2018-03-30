package bankingsys.message;

import bankingsys.io.Deserializer;
import bankingsys.io.Serializable;
import bankingsys.io.Serializer;

import static bankingsys.message.ServiceResponse.ResponseStatus.SUCCESS;

/**
 * Created by koallen on 29/3/18.
 */
public class ServiceResponse implements Serializable {

    public enum ResponseStatus {
        SUCCESS,
        FAILURE;
    }

    private Character responseType;
    private ResponseStatus responseCode;
    private Integer responseAccount;
    private String responseMessage;
    private Float responseAmount;

    public ServiceResponse() {}

    public ServiceResponse(Character responseType, ResponseStatus responseCode,
                           Integer responseAccount, String responseMessage, Float responseAmount) {
        this.responseType = responseType;
        this.responseCode = responseCode;
        this.responseAccount = responseAccount;
        this.responseMessage = responseMessage;
        this.responseAmount = responseAmount;
    }

    public Character getResponseType() {
        return responseType;
    }

    public ResponseStatus getResponseCode() {
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
        serializer.writeChar(responseType);
        serializer.writeInt(responseCode.ordinal());
        if (responseCode == SUCCESS) {
            serializer.writeInt(this.responseAccount);
            serializer.writeFloat(this.responseAmount);
        }
        serializer.writeString(this.responseMessage);
    }

    @Override
    public void read(Deserializer deserializer) {
        responseType = deserializer.readChar();
        responseCode = ResponseStatus.values()[deserializer.readInt()];
        if (responseCode == SUCCESS) {
            this.responseAccount = deserializer.readInt();
            this.responseAmount = deserializer.readFloat();
        }
        this.responseMessage = deserializer.readString();
    }
}
