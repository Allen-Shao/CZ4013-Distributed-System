package bankingsys.message;

import bankingsys.io.Deserializer;
import bankingsys.io.Serializable;
import bankingsys.io.Serializer;
import bankingsys.server.model.BankAccount.Currency;

/**
 * Object that encapsulates request message
 */
public class ServiceRequest implements Serializable {

    private Character requestType;
    private String requestName;
    private Integer requestAccount;
    private String requestPassword;
    private Float requestAmount;
    private Integer requestTargetAccount;
    private Currency requestCurrency;

    public ServiceRequest(Character requestType, String requestName, int requestAccount,
                          String requestPassword, Float requestAmount, int requestTargetAccount,
                          Currency requestCurrency) {
        this.requestType = requestType;
        this.requestName = requestName;
        this.requestAccount = requestAccount;
        this.requestPassword = requestPassword;
        this.requestAmount = requestAmount;
        this.requestTargetAccount = requestTargetAccount;
        this.requestCurrency = requestCurrency;
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
        switch (requestType) {
            case 'a':
            case 'd':
                serializer.writeString(requestName);
                serializer.writeInt(requestAccount);
                serializer.writeString(requestPassword);
                break;
            case 'b':
                serializer.writeString(requestName);
                serializer.writeString(requestPassword);
                serializer.writeFloat(requestAmount);
                serializer.writeInt(requestCurrency.ordinal());
                break;
            case 'c':
                break;
            case 'e':
                serializer.writeString(requestName);
                serializer.writeInt(requestAccount);
                serializer.writeString(requestPassword);
                serializer.writeFloat(requestAmount);
                serializer.writeInt(requestCurrency.ordinal());
                break;
            case 'f':
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
        switch (requestType) {
            case 'a':
            case 'd':
                requestName = deserializer.readString();
                requestAccount = deserializer.readInt();
                requestPassword = deserializer.readString();
                break;
            case 'b':
                requestName = deserializer.readString();
                requestPassword = deserializer.readString();
                requestAmount = deserializer.readFloat();
                requestCurrency = Currency.values()[deserializer.readInt()];
                break;
            case 'c':
                break;
            case 'e':
                requestName = deserializer.readString();
                requestAccount = deserializer.readInt();
                requestPassword = deserializer.readString();
                requestAmount = deserializer.readFloat();
                requestCurrency = Currency.values()[deserializer.readInt()];
                break;
            case 'f':
                requestName = deserializer.readString();
                requestAccount = deserializer.readInt();
                requestPassword = deserializer.readString();
                requestAmount = deserializer.readFloat();
                requestTargetAccount = deserializer.readInt();
                break;
        }
    }
}
