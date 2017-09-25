package com.paydock.androidsdk.View;

import com.paydock.javasdk.Services.Environment;

import com.paydock.androidsdk.IGetToken;

public interface IDirectDebitInputForm {

    void getToken(Environment environment, String publicKey, String gatewayID,
                  IGetToken delegateInterface) throws Exception;
    Boolean validate();
    void clear();
}
