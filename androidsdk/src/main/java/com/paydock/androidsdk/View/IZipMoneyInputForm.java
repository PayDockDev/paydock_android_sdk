package com.paydock.androidsdk.View;

import com.paydock.androidsdk.IGetToken;
import com.paydock.javasdk.Services.Environment;

public interface IZipMoneyInputForm {

    void getToken(Environment environment, String publicKey, String gatewayID,
                  IGetToken delegateInterface) throws Exception;
    void build();
    void hide();
}
