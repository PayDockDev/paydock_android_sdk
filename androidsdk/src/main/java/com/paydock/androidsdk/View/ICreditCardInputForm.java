package com.paydock.androidsdk.View;

import android.app.Activity;

import com.paydock.androidsdk.IGetToken;
import com.paydock.javasdk.Services.Environment;

public interface ICreditCardInputForm {

    void getToken(Environment environment, String publicKey, String gatewayID, IGetToken delegateInterface);
    Boolean validate();
    void clear();
    void scanCard(Activity activity);
}
