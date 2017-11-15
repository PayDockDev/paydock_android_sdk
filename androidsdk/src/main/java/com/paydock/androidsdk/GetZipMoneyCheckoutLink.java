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
    private ExternalCheckoutResponse mExternalCheckoutResponse;

    private Exception mException = null;

    private RelativeLayout pbLoadingPanel;

    public GetZipMoneyCheckoutLink(Environment environment, String publicKey, IGetCheckoutLink delegateInterface, RelativeLayout relativeLayout){
        mEnvironment = environment;
        mDelegate = delegateInterface;
        mPublicKey = publicKey;
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
            mExternalCheckoutResponse = new ExternalCheckout().create(arg0[0]);
        }catch (ResponseException er){
            //handle Paydock exception
            mExternalCheckoutResponse.error = er.errorResponse;
        }catch (Exception e){
            mExternalCheckoutResponse.error.message = "External Checkout method exception";
            mExternalCheckoutResponse.error.jsonResponse = e.getMessage();
        }
        return mExternalCheckoutResponse;
    }

    @Override
    protected void onPostExecute(ExternalCheckoutResponse ch) {
        super.onPostExecute(ch);
        pbLoadingPanel.setVisibility(View.GONE);
        mDelegate.checkoutLinkCallback(ch);
    }

}



