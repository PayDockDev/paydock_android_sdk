package com.paydock.androidsdk.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.paydock.androidsdk.GetToken;
import com.paydock.androidsdk.GetZipMoneyCheckoutLink;
import com.paydock.androidsdk.IGetCheckoutLink;
import com.paydock.androidsdk.IGetToken;
import com.paydock.androidsdk.R;
import com.paydock.androidsdk.WebViewActivity;
import com.paydock.javasdk.Models.ErrorResponse;
import com.paydock.javasdk.Models.ExternalCheckoutRequestZipMoney;
import com.paydock.javasdk.Models.ExternalCheckoutResponse;
import com.paydock.javasdk.Models.PaymentType;
import com.paydock.javasdk.Models.ResponseException;
import com.paydock.javasdk.Models.TokenRequest;
import com.paydock.javasdk.Services.Environment;

@SuppressWarnings({"Convert2Lambda", "SameParameterValue"})
public class ZipMoneyInputForm extends LinearLayout implements IZipMoneyInputForm{

    private static final String TAG = "ZipMoneyInputForm";

    View rootView;
    private Context mContext;
    SharedPreferences prefs;

    private RelativeLayout pbZipMoneyLoadingPanel;
    public ExternalCheckoutResponse mTokenCardResponse;
    private String mGatewayID;
    private String mLink;
    private String mCheckoutToken;

    private Environment mEnvironment = Environment.Sandbox;
    private String mPublicKey = "";
    private IGetToken mDelegateInterface = null;
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
        ImageButton bZipMoney = findViewById(R.id.bZipMoney);

        mContext = context;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().clear().apply();

        mTokenCardResponse = new ExternalCheckoutResponse();

        bZipMoney.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    getCheckoutToken(mEnvironment, mPublicKey, mGatewayID, mDelegateInterface);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
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
    public void getCheckoutToken(Environment environment, String publicKey, String gatewayID, IGetToken delegateInterface) throws Exception {
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
                                Intent intent = new Intent(mContext, WebViewActivity.class);
                                intent.putExtra("url", mLink);
                                intent.putExtra("checkoutType", "zipmoney");
                                mContext.startActivity(intent);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
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
    public ZipMoneyInputForm setPayDock(Environment environment, String publicKey, String gatewayID, IGetToken delegateInterface) {
        mEnvironment = environment;
        mPublicKey = publicKey;
        mGatewayID = gatewayID;
        mDelegateInterface = delegateInterface;
        return this;
    }

    @Override
    public ZipMoneyInputForm setMeta(ExternalCheckoutRequestZipMoney.Meta zipMeta) {
        mZipMeta = zipMeta;
        return this;
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
    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);

        boolean success = prefs.getBoolean("success", false);
        if (visibility == 0 && success)
        {
            try {
                TokenRequest tokenRequest = new TokenRequest();
                tokenRequest.type = PaymentType.checkout_token;
                tokenRequest.gateway_id = mGatewayID;
                tokenRequest.checkout_token = mCheckoutToken;
                pbZipMoneyLoadingPanel.setVisibility(View.VISIBLE);
                GetToken myTokenTask = new GetToken(mEnvironment, mPublicKey, mDelegateInterface,
                        null);
                myTokenTask.execute(tokenRequest);

            } catch (Exception e) {
                e.printStackTrace();
                pbZipMoneyLoadingPanel.setVisibility(View.GONE);
            }
        }
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




