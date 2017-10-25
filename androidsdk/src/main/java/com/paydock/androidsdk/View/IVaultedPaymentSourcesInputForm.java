package com.paydock.androidsdk.View;

import com.paydock.javasdk.Services.Environment;

import com.paydock.androidsdk.IPaymentSourceResponse;

public interface IVaultedPaymentSourcesInputForm {
    VaultedPaymentSourcesInputForm getVaultedPaymentSources(Environment environment, String publicKey, String queryToken,
                                  IPaymentSourceResponse delegateInterface);
    void build();
    void hide();
}
