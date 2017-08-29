package androidsdk.View;

import com.paydock.javasdk.Services.Environment;

import androidsdk.IGetToken;

public interface ICreditCardInputForm {

    void getToken(Environment environment, String publicKey, String gatewayID, IGetToken delegateInterface);
    Boolean validate();
    void clear();
}
