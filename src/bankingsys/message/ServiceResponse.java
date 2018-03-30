package bankingsys.message;

import bankingsys.io.Deserializer;
import bankingsys.io.Serializable;
import bankingsys.io.Serializer;


import static bankingsys.Constant.*;
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
        switch (responseType) {
            case ACCOUNT_CREATE:
            case ACCOUNT_MONITER:
            case END_MONITER:
                break;
            case ACCOUNT_CANCEL:
            case BALANCE_CHECK:
            case BALANCE_UPDATE:
            case TRANSFER:
                if (responseCode == SUCCESS) {
                    serializer.writeInt(responseAccount);
                    serializer.writeFloat(responseAmount);
                }
                break;
        }
        serializer.writeString(responseMessage);
    }

    @Override
    public void read(Deserializer deserializer) {
        responseType = deserializer.readChar();
        responseCode = ResponseStatus.values()[deserializer.readInt()];
        switch (responseType) {
            case ACCOUNT_CANCEL:
            case BALANCE_CHECK:
            case BALANCE_UPDATE:
            case TRANSFER:
                if (responseCode == SUCCESS) {
                    responseAccount = deserializer.readInt();
                    responseAmount = deserializer.readFloat();
                }
                break;
        }
        this.responseMessage = deserializer.readString();
    }
}
