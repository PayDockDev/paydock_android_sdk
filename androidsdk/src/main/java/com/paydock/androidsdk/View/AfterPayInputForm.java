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

import com.paydock.androidsdk.GetAfterPayCheckoutLink;
import com.paydock.androidsdk.GetToken;
import com.paydock.androidsdk.IGetCheckoutLink;
import com.paydock.androidsdk.IGetToken;
import com.paydock.androidsdk.R;
import com.paydock.androidsdk.WebViewActivity;
import com.paydock.javasdk.Models.ErrorResponse;
import com.paydock.javasdk.Models.ExternalCheckoutRequestAfterPay;
import com.paydock.javasdk.Models.ExternalCheckoutResponse;
import com.paydock.javasdk.Models.PaymentType;
import com.paydock.javasdk.Models.ResponseException;
import com.paydock.javasdk.Models.TokenRequest;
import com.paydock.javasdk.Services.Environment;

@SuppressWarnings({"Convert2Lambda", "SameParameterValue"})
public class AfterPayInputForm extends LinearLayout implements IAfterPayInputForm{

    private static final String TAG = "AfterPayInputForm";

    View rootView;
    private Context mContext;
    SharedPreferences prefs;

    public ExternalCheckoutResponse mTokenCardResponse;
    private String mGatewayID;
    private String mLink;
    private String mCheckoutToken;

    private Environment mEnvironment = Environment.Sandbox;
    private String mPublicKey = "";
    private IGetToken mDelegateInterface = null;
    ExternalCheckoutRequestAfterPay.Meta mAfterPayMeta = new ExternalCheckoutRequestAfterPay.Meta();

    public AfterPayInputForm(Context context) {
        super(context);
        init(context);
    }

    public AfterPayInputForm(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AfterPayInputForm(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        setVisibility(GONE); // hide the view until the build command is sent

        rootView = inflate(context, R.layout.afterpay, this);
        ImageButton bAfterPay = findViewById(R.id.bAfterPay);

        mContext = context;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().clear().apply();

        mTokenCardResponse = new ExternalCheckoutResponse();

        bAfterPay.setOnClickListener(new OnClickListener() {
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
                ExternalCheckoutRequestAfterPay token = createCheckoutRequest();
                GetAfterPayCheckoutLink myTokenTask = new GetAfterPayCheckoutLink(mEnvironment, mPublicKey, new IGetCheckoutLink() {
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
                                intent.putExtra("checkoutType", "afterpay");
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
    public AfterPayInputForm setPayDock(Environment environment, String publicKey, String gatewayID, IGetToken delegateInterface) {
        mEnvironment = environment;
        mPublicKey = publicKey;
        mGatewayID = gatewayID;
        mDelegateInterface = delegateInterface;
        return this;
    }

    @Override
    public AfterPayInputForm setMeta(ExternalCheckoutRequestAfterPay.Meta afterPayMeta) {
        mAfterPayMeta = afterPayMeta;
        return this;
    }

    @Override
    public Boolean validate() {
        boolean isValid = true;
        try {
            if (mGatewayID == null) {
                isValid = false;
            }
            if (mAfterPayMeta.amount == null) {
                isValid = false;
            }
            if (mAfterPayMeta.items.length > 0) {
                for (ExternalCheckoutRequestAfterPay.Items item : mAfterPayMeta.items) {
                    if (item.name == null) {
                        isValid = false;
                    }
                    if (item.quantity == null) {
                        isValid = false;
                    }
                    if (item.name == null) {
                        isValid = false;
                    }
                    if (item.price == null) {
                        isValid = false;
                    }
                }
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
                GetToken myTokenTask = new GetToken(mEnvironment, mPublicKey, mDelegateInterface,
                        null);
                myTokenTask.execute(tokenRequest);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private ExternalCheckoutRequestAfterPay createCheckoutRequest() {
        ExternalCheckoutRequestAfterPay request = new ExternalCheckoutRequestAfterPay();
        request.gateway_id = mGatewayID;
        request.mode = (mEnvironment == Environment.Sandbox ? "test" : "live");
        request.success_redirect_url = "https://paydock.com/success";
        request.error_redirect_url = "https://paydock.com/error";
        if (mAfterPayMeta != null) {
            request.meta = mAfterPayMeta;
        }
        return request;
    }
}




