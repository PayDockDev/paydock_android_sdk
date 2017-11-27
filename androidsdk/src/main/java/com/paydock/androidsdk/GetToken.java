package com.paydock.androidsdk;

import android.os.AsyncTask;

import com.paydock.androidsdk.Models.TokenCardResponse;
import com.paydock.javasdk.Models.ResponseException;
import com.paydock.javasdk.Models.TokenRequest;
import com.paydock.javasdk.Models.TokenResponse;
import com.paydock.javasdk.Services.Config;
import com.paydock.javasdk.Services.Environment;
import com.paydock.javasdk.Services.Tokens;

public class GetToken extends AsyncTask<TokenRequest, Void, TokenCardResponse>{

    private Environment mEnvironment = Environment.Sandbox;
    private IGetToken mDelegate = null;
    private String mPublicKey = "";
    private TokenCardResponse mTokenCardResponse;

    private Exception mException = null;

    public GetToken(Environment environment, String publicKey, IGetToken delegateInterface,
                    TokenCardResponse tokenCardResponse){
        mEnvironment = environment;
        mDelegate = delegateInterface;
        mPublicKey = publicKey;
        if (tokenCardResponse == null) {
            mTokenCardResponse = new TokenCardResponse();
        } else {
            mTokenCardResponse = tokenCardResponse;
        }
    }

    @Override
    protected void onPreExecute() {
    }


    @Override
    protected TokenCardResponse doInBackground(TokenRequest... arg0) {

        try{
            Config.initialise(mEnvironment, "", mPublicKey);
            TokenResponse ch =  new Tokens().create(arg0[0]);
            mTokenCardResponse.data = ch.resource.data;
            mTokenCardResponse.type = ch.resource.type;
        }catch (ResponseException er){
            //handle Paydock exception
            mTokenCardResponse.error = er.errorResponse;
        }catch (Exception e){
            mTokenCardResponse.error.message = "Token create method exception";
            mTokenCardResponse.error.jsonResponse = e.getMessage();
        }
        return mTokenCardResponse;
    }

    @Override
    protected void onPostExecute(TokenCardResponse ch) {
        super.onPostExecute(ch);
        mDelegate.tokenCallback(ch);
    }

}



