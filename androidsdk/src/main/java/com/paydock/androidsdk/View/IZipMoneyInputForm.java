package com.paydock.androidsdk.View;

import com.paydock.androidsdk.IGetToken;
import com.paydock.javasdk.Models.ExternalCheckoutRequestZipMoney;
import com.paydock.javasdk.Services.Environment;

public interface IZipMoneyInputForm {

    void getCheckoutLink(Environment environment, String publicKey, String gatewayID,
                  IGetToken delegateInterface) throws Exception;
    void setMeta(ExternalCheckoutRequestZipMoney.Meta zipMeta);
    void build();
    void hide();
}
