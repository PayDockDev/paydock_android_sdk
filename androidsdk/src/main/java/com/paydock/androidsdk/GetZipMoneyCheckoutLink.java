package com.paydock.androidsdk;

import android.os.AsyncTask;
import android.view.View;
import android.widget.RelativeLayout;

import com.paydock.javasdk.Models.ExternalCheckoutRequestZipMoney;
import com.paydock.javasdk.Models.ExternalCheckoutResponse;
import com.paydock.javasdk.Models.ResponseException;
import com.paydock.javasdk.Services.Config;
import com.paydock.javasdk.Services.Environment;
import com.paydock.javasdk.Services.ExternalCheckout;

public class GetZipMoneyCheckoutLink extends AsyncTask<ExternalCheckoutRequestZipMoney, Void, ExternalCheckoutResponse>{

    private Environment mEnvironment = Environment.Sandbox;
    private IGetCheckoutLink mDelegate = null;
    private String mPublicKey = "";
    private ExternalCheckoutResponse mTokenCardResponse;

    private Exception mException = null;

    private RelativeLayout pbLoadingPanel;

    public GetZipMoneyCheckoutLink(Environment environment, String publicKey, IGetCheckoutLink delegateInterface,
                                   ExternalCheckoutResponse tokenCardResponse, RelativeLayout relativeLayout){
        mEnvironment = environment;
        mDelegate = delegateInterface;
        mPublicKey = publicKey;
        mTokenCardResponse = tokenCardResponse;
        pbLoadingPanel = relativeLayout;
    }

    @Override
    protected void onPreExecute() {
        pbLoadingPanel.setVisibility(View.VISIBLE);
    }


    @Override
    protected ExternalCheckoutResponse doInBackground(ExternalCheckoutRequestZipMoney... arg0) {

        try{
            Config.initialise(mEnvironment, "", mPublicKey);
            ExternalCheckoutResponse ch =  new ExternalCheckout().create(arg0[0]);
            mTokenCardResponse = ch;
        }catch (ResponseException er){
            //handle Paydock exception
            mTokenCardResponse.error = er.errorResponse;
        }catch (Exception e){
            mTokenCardResponse.error.message = "External Checkout method exception";
            mTokenCardResponse.error.jsonResponse = e.getMessage();
        }
        return mTokenCardResponse;
    }

    @Override
    protected void onPostExecute(ExternalCheckoutResponse ch) {
        super.onPostExecute(ch);
        pbLoadingPanel.setVisibility(View.GONE);
        mDelegate.checkoutLinkCallback(ch);
    }

}



