package com.paydock.androidsdk.View;

import com.paydock.androidsdk.IGetCheckoutLink;
import com.paydock.javasdk.Services.Environment;

public interface IZipMoneyInputForm {

    void getCheckoutLink(Environment environment, String publicKey, String gatewayID,
                  IGetCheckoutLink delegateInterface) throws Exception;
    void build();
    void hide();
}
