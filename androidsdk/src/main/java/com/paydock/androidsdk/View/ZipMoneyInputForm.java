package com.paydock.androidsdk.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.paydock.androidsdk.GetToken;
import com.paydock.androidsdk.GetZipMoneyCheckoutLink;
import com.paydock.androidsdk.IGetCheckoutLink;
import com.paydock.androidsdk.IGetToken;
import com.paydock.androidsdk.R;
import com.paydock.javasdk.Models.ErrorResponse;
import com.paydock.javasdk.Models.ExternalCheckoutRequestZipMoney;
import com.paydock.javasdk.Models.ExternalCheckoutResponse;
import com.paydock.javasdk.Models.PaymentType;
import com.paydock.javasdk.Models.ResponseException;
import com.paydock.javasdk.Models.TokenRequest;
import com.paydock.javasdk.Models.TokenResponse;
import com.paydock.javasdk.Services.Environment;

@SuppressWarnings({"Convert2Lambda", "SameParameterValue"})
public class ZipMoneyInputForm extends LinearLayout implements IZipMoneyInputForm {

    private static final String TAG = "ZipMoneyInputForm";

    View rootView;

    private ImageButton bZipMoney;
    private Resources mResources;

    private RelativeLayout pbZipMoneyLoadingPanel;
    public ExternalCheckoutResponse mTokenCardResponse;
    private WebView myWebView;
    private String mGatewayID;
    private String mLink;
    private String mCheckoutToken;

    private Environment mEnvironment = Environment.Sandbox;
    private String mPublicKey = "";
    private IGetToken mDelegateInterface = null;
    private TokenResponse mTokenResponse;
    ExternalCheckoutRequestZipMoney.Meta mZipMeta = new ExternalCheckoutRequestZipMoney.Meta();

    public ZipMoneyInputForm(Context context) {
        super(context);
        init(context);
    }

    public ZipMoneyInputForm(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ZipMoneyInputForm(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {

        setVisibility(GONE); // hide the view until the build command is sent

        rootView = inflate(context, R.layout.zipmoney, this);
        pbZipMoneyLoadingPanel = findViewById(R.id.pbZipMoneyLoadingPanel);
        bZipMoney = findViewById(R.id.bZipMoney);
        myWebView = findViewById(R.id.wvWebview);

        mResources = getResources();
        
        mTokenCardResponse = new ExternalCheckoutResponse();

    }

    @Override
    public void build() {
        setVisibility(VISIBLE);
    }

    @Override
    public void hide() {
        setVisibility(GONE);
    }



    @Override
    public void getCheckoutLink(Environment environment, String publicKey, String gatewayID, IGetToken delegateInterface) throws Exception {
        mEnvironment = environment;
        mPublicKey = publicKey;
        mGatewayID = gatewayID;
        mDelegateInterface = delegateInterface;
        if (validate()) {
            try {
                ExternalCheckoutRequestZipMoney token = createCheckoutRequest();
                GetZipMoneyCheckoutLink myTokenTask = new GetZipMoneyCheckoutLink(mEnvironment, mPublicKey, new IGetCheckoutLink() {
                    @SuppressLint("SetJavaScriptEnabled")
                    @Override
                    public void checkoutLinkCallback(ExternalCheckoutResponse output) {
                        try {
                            mLink = null;
                            mLink = output.resource.data.link;
                            if (mLink != null) {
                                mCheckoutToken = output.resource.data.token;
                                myWebView.setVisibility(View.VISIBLE);
                                myWebView.getSettings().setJavaScriptEnabled(true);
                                myWebView.getSettings().setLoadWithOverviewMode(true);
                                myWebView.setWebViewClient(new HelloWebViewClient());
                                myWebView.loadUrl(mLink);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, pbZipMoneyLoadingPanel);
                myTokenTask.execute(token);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.http_status_code = 400;
            errorResponse.jsonResponse = "";
            errorResponse.message = "invalid user input credentials";
            throw new ResponseException(errorResponse, "400");
        }
    }

    @Override
    public void setMeta(ExternalCheckoutRequestZipMoney.Meta zipMeta) {
        mZipMeta = zipMeta;
    }


    private class HelloWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.contains("https://paydock.com")){
                myWebView.setVisibility(View.GONE);
                myWebView.loadUrl("about:blank");
                try {
                    TokenRequest token = createToken();
                    GetToken myTokenTask = new GetToken(mEnvironment, mPublicKey, mDelegateInterface,
                            null, pbZipMoneyLoadingPanel);
                    myTokenTask.execute(token);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            } else {
                view.loadUrl(url);
                return true;
            }
        }
    }
    @Override
    public Boolean validate() {
        boolean isValid = true;
        try {
            if (mGatewayID == null) {
                isValid = false;
            }
            if (mZipMeta.first_name == null) {
                isValid = false;
            }
            if (mZipMeta.last_name == null) {
                isValid = false;
            }
            if (mZipMeta.email == null) {
                isValid = false;
            }
            if (mZipMeta.charge.amount == null) {
                isValid = false;
            }
            if (mZipMeta.charge.items == null) {
                isValid = false;
            }
            if (mZipMeta.charge.shipping_address == null) {
                isValid = false;
            }
            if (mZipMeta.charge.billing_address == null) {
                isValid = false;
            }
        } catch (Exception e){
            isValid = false;
        }

        return isValid;
    }

    private TokenRequest createToken() {
        TokenRequest tokenRequest = new TokenRequest();
        tokenRequest.type = PaymentType.checkout_token;
        tokenRequest.gateway_id = mGatewayID;
        tokenRequest.checkout_token = mCheckoutToken;
        return tokenRequest;
    }

    private ExternalCheckoutRequestZipMoney createCheckoutRequest() {
        ExternalCheckoutRequestZipMoney request = new ExternalCheckoutRequestZipMoney();
        request.gateway_id = mGatewayID;
        request.mode = (mEnvironment == Environment.Sandbox ? "test" : "live");
        request.redirect_url = "https://paydock.com";
        if (mZipMeta != null) {
            request.meta = mZipMeta;
        }
        return request;
    }


}




