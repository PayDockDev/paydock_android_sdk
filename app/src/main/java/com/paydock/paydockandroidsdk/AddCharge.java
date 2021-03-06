package com.paydock.paydockandroidsdk;

import android.os.AsyncTask;

import com.paydock.javasdk.Models.ChargeRequest;
import com.paydock.javasdk.Models.ChargeResponse;
import com.paydock.javasdk.Models.ResponseException;
import com.paydock.javasdk.Services.Charges;
import com.paydock.javasdk.Services.Config;
import com.paydock.javasdk.Services.Environment;


class AddCharge extends AsyncTask<ChargeRequest, Void, ChargeResponse>{


    interface AsyncResponse {
        void processFinish(ChargeResponse output);
    }

    private AsyncResponse delegate = null;

    AddCharge(AsyncResponse delegate){
        this.delegate = delegate;
    }

    @Override
    protected void onPreExecute() {
    }


    @Override
    protected ChargeResponse doInBackground(ChargeRequest... arg0) {
          ChargeResponse ch = new ChargeResponse();
            try {
                Config.initialise(Environment.Sandbox, PayDock.sPrivateKey, PayDock.sPublicKey);
                ch =  new Charges().add(arg0[0]);
            } catch (ResponseException er){
                //handle Paydock exception
                ch.error.message = er.errorResponse.message;
                ch.error.http_status_code = er.errorResponse.http_status_code;
                ch.error.jsonResponse = er.errorResponse.jsonResponse;
            } catch (Exception e){
                //handle general exception
            }
        return ch;
    }

    @Override
    protected void onPostExecute(ChargeResponse ch) {
        super.onPostExecute(ch);
        delegate.processFinish(ch);
    }
}



