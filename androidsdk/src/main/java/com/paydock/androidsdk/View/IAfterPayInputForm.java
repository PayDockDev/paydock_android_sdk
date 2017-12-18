package com.paydock.androidsdk.View;

import com.paydock.androidsdk.IGetToken;
import com.paydock.javasdk.Models.ExternalCheckoutRequestAfterPay;
import com.paydock.javasdk.Services.Environment;

public interface IAfterPayInputForm {

    void getCheckoutToken(Environment environment, String publicKey, String gatewayID,
                          IGetToken delegateInterface) throws Exception;
    AfterPayInputForm setPayDock(Environment environment, String publicKey, String gatewayID,
                                 IGetToken delegateInterface);
    AfterPayInputForm setMeta(ExternalCheckoutRequestAfterPay.Meta afterPayMeta);
    Boolean validate();
    void build();
    void hide();
}
