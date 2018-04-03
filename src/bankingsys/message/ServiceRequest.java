package bankingsys.message;

import bankingsys.io.Deserializer;
import bankingsys.io.Serializable;
import bankingsys.io.Serializer;
import bankingsys.server.model.BankAccount.Currency;

import java.net.InetAddress;

import static bankingsys.Constant.*;

/**
 * Object that encapsulates request message
 */
public class ServiceRequest implements Serializable {

    private Integer requestID;
    private Character requestType;
    private String requestName;
    private Integer requestAccount;
    private String requestPassword;
    private Float requestAmount;
    private Integer requestTargetAccount;
    private Currency requestCurrency;
    private InetAddress requestAddress;
    private Integer requestDelay;

    public int getRequestDelay() {
        return requestDelay;
    }

    public int getRequestPort() {
        return requestPort;
    }

    public void setRequestPort(int requestPort) {
        this.requestPort = requestPort;
    }

    private int requestPort;

    public ServiceRequest() {}

    public ServiceRequest(Integer requestID, Character requestType, String requestName,
                          Integer requestAccount, String requestPassword, Float requestAmount,
                          Integer requestTargetAccount, Currency requestCurrency, Integer requestDelay) {
        this.requestID = requestID;
        this.requestType = requestType;
        this.requestName = requestName;
        this.requestAccount = requestAccount;
        this.requestPassword = requestPassword;
        this.requestAmount = requestAmount;
        this.requestTargetAccount = requestTargetAccount;
        this.requestCurrency = requestCurrency;
        this.requestDelay = requestDelay;
    }

    public Integer getRequestID() {
        return requestID;
    }

    public void setRequestID(Integer requestID) {
        this.requestID = requestID;
    }

    public InetAddress getRequestAddress() {
        return requestAddress;
    }

    public void setRequestAddress(InetAddress requestAddress) {
        this.requestAddress = requestAddress;
    }

    public Character getRequestType(){
        return requestType;
    }

    public String getRequestName() {
        return requestName;
    }

    public int getRequestAccount() {
        return requestAccount;
    }

    public String getRequestPassword() {
        return requestPassword;
    }

    public float getRequestAmount() {
        return requestAmount;
    }

    public int getRequestTargetAccount() {
        return requestTargetAccount;
    }

    public Currency getRequestCurrency() {
        return requestCurrency;
    }

    @Override
    public void write(Serializer serializer) {
        serializer.writeChar(requestType);
        serializer.writeInt(requestID);
        switch (requestType) {
            case ACCOUNT_CREATE:
            case BALANCE_CHECK:
                serializer.writeString(requestName);
                serializer.writeInt(requestAccount);
                serializer.writeString(requestPassword);
                break;
            case ACCOUNT_CANCEL:
                serializer.writeString(requestName);
                serializer.writeString(requestPassword);
                serializer.writeFloat(requestAmount);
                serializer.writeInt(requestCurrency.ordinal());
                break;
            case ACCOUNT_MONITOR:
                serializer.writeInt(requestDelay);
                break;
            case BALANCE_UPDATE:
                serializer.writeString(requestName);
                serializer.writeInt(requestAccount);
                serializer.writeString(requestPassword);
                serializer.writeFloat(requestAmount);
                serializer.writeInt(requestCurrency.ordinal());
                break;
            case TRANSFER:
                serializer.writeString(requestName);
                serializer.writeInt(requestAccount);
                serializer.writeString(requestPassword);
                serializer.writeFloat(requestAmount);
                serializer.writeInt(requestTargetAccount);
                break;
        }
    }

    @Override
    public void read(Deserializer deserializer) {
        requestType = deserializer.readChar();
        requestID = deserializer.readInt();
        switch (requestType) {
            case ACCOUNT_CREATE:
            case BALANCE_CHECK:
                requestName = deserializer.readString();
                requestAccount = deserializer.readInt();
                requestPassword = deserializer.readString();
                break;
            case ACCOUNT_CANCEL:
                requestName = deserializer.readString();
                requestPassword = deserializer.readString();
                requestAmount = deserializer.readFloat();
                requestCurrency = Currency.values()[deserializer.readInt()];
                break;
            case ACCOUNT_MONITOR:
                requestDelay = deserializer.readInt();
                break;
            case BALANCE_UPDATE:
                requestName = deserializer.readString();
                requestAccount = deserializer.readInt();
                requestPassword = deserializer.readString();
                requestAmount = deserializer.readFloat();
                requestCurrency = Currency.values()[deserializer.readInt()];
                break;
            case TRANSFER:
                requestName = deserializer.readString();
                requestAccount = deserializer.readInt();
                requestPassword = deserializer.readString();
                requestAmount = deserializer.readFloat();
                requestTargetAccount = deserializer.readInt();
                break;
        }
    }
}
