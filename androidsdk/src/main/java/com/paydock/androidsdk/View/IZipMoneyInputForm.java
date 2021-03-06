package com.paydock.androidsdk.View;

import com.paydock.androidsdk.IGetToken;
import com.paydock.javasdk.Models.ExternalCheckoutRequestZipMoney;
import com.paydock.javasdk.Services.Environment;

public interface IZipMoneyInputForm {

    void getCheckoutToken(Environment environment, String publicKey, String gatewayID,
                  IGetToken delegateInterface) throws Exception;
    ZipMoneyInputForm setPayDock(Environment environment, String publicKey, String gatewayID,
                    IGetToken delegateInterface);
    ZipMoneyInputForm setMeta(ExternalCheckoutRequestZipMoney.Meta zipMeta);
    Boolean validate();
    void build();
    void hide();
}
