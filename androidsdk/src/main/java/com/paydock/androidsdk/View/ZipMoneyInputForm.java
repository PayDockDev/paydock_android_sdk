package com.paydock.androidsdk.View;

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
import com.paydock.javasdk.Models.ExternalCheckoutRequestZipMoney;
import com.paydock.javasdk.Models.ExternalCheckoutResponse;
import com.paydock.javasdk.Models.PaymentType;
import com.paydock.javasdk.Models.TokenRequest;
import com.paydock.javasdk.Models.TokenResponse;
import com.paydock.javasdk.Services.Environment;

import java.math.BigDecimal;

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
        myWebView = findViewById(R.id.webview);

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
    public void getCheckoutLink(Environment environment, String publicKey, String gatewayID, IGetToken delegateInterface) {
        mEnvironment = environment;
        mPublicKey = publicKey;
        mGatewayID = gatewayID;
        mDelegateInterface = delegateInterface;
        try {
            ExternalCheckoutRequestZipMoney token = createCheckoutRequest();
            GetZipMoneyCheckoutLink myTokenTask = new GetZipMoneyCheckoutLink(mEnvironment, mPublicKey, new IGetCheckoutLink() {
                @Override
                public void checkoutLinkCallback(ExternalCheckoutResponse output) {
                    try {
                        mLink = null;
                        mLink = output.resource.data.link;
                        if (mLink != null) {
                            mCheckoutToken = output.resource.data.token;
                            myWebView.setVisibility(View.VISIBLE);
                            myWebView.getSettings().setJavaScriptEnabled(true);
                            myWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
                            myWebView.getSettings().setLoadsImagesAutomatically(true);
//                            myWebView.getSettings().setLoadWithOverviewMode(true);
//                            myWebView.canGoBack();
                            myWebView.setWebViewClient(new HelloWebViewClient());
                            myWebView.loadUrl(mLink);
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }, pbZipMoneyLoadingPanel);
            myTokenTask.execute(token);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void setMeta(ExternalCheckoutRequestZipMoney.Meta zipMeta) {
        mZipMeta = zipMeta;
    }


    private class HelloWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.contains("https://success.com")){
                myWebView.setVisibility(View.GONE);
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
        request.mode = "test";
        request.redirect_url = "https://success.com";
        if (mZipMeta != null) {
            request.meta = mZipMeta;
        } else {
            ExternalCheckoutRequestZipMoney.Meta meta = new ExternalCheckoutRequestZipMoney.Meta();
            meta.first_name = "testFirstName";
            meta.last_name = "testLastName";
            meta.email = "test@test.com";
            meta.tokenize = true;
            meta.gender = "male";
            meta.date_of_birth = "01/01/1980";
            ExternalCheckoutRequestZipMoney.Charge charge = new ExternalCheckoutRequestZipMoney.Charge();
            charge.amount = new BigDecimal(6);
            charge.currency = "AUD";
            ExternalCheckoutRequestZipMoney.ShippingAddress shipping_address = new ExternalCheckoutRequestZipMoney.ShippingAddress();
            shipping_address.first_name = "Shipping FName";
            shipping_address.last_name = "Shipping LName";
            shipping_address.line1 = "Addr Line 1";
            shipping_address.line2 = "Addr Line 2";
            shipping_address.city = "Addr City";
            shipping_address.state = "NSW";
            shipping_address.postcode = "1234";
            shipping_address.country = "AU";
            charge.shipping_address = shipping_address;
            ExternalCheckoutRequestZipMoney.BillingAddress billing_address = new ExternalCheckoutRequestZipMoney.BillingAddress();
            billing_address.first_name = "Billing FName";
            billing_address.last_name = "Billing LName";
            billing_address.line1 = "Addr Line1";
            billing_address.line2 = "Addr Line2";
            billing_address.city = "Addr City";
            billing_address.state = "NSW";
            billing_address.postcode = "1234";
            billing_address.country = "AU";
            charge.billing_address = billing_address;
            ExternalCheckoutRequestZipMoney.Items[] items = new ExternalCheckoutRequestZipMoney.Items[2]; //create new array
            ExternalCheckoutRequestZipMoney.Items items1 = new ExternalCheckoutRequestZipMoney.Items(); //set up first transfer in array
            items1.name = "Shoes";
            items1.amount = "2";
            items1.quantity = 1;
            items1.reference = "sds";
            ExternalCheckoutRequestZipMoney.Items items2 = new ExternalCheckoutRequestZipMoney.Items(); //set up first transfer in array
            items2.name = "Shoes2";
            items2.amount = "4";
            items2.quantity = 1;
            items2.reference = "sds1";
            items[0] = items1; //add the transfers to the array
            items[1] = items2;
            charge.items = items;
            meta.charge = charge;
            ExternalCheckoutRequestZipMoney.Statistics statistics = new ExternalCheckoutRequestZipMoney.Statistics();
            statistics.account_created = "2017-05-05";
            statistics.sales_total_number = "5";
            statistics.sales_total_amount = "4";
            statistics.sales_avg_value = "45";
            statistics.sales_max_value = "400";
            statistics.refunds_total_amount = "1";
            statistics.previous_chargeback = "false";
            statistics.currency = "AUD";
            statistics.last_login = "2017-06-01";
            meta.statistics = statistics;
            request.meta = meta;
        }
        return request;
    }


}




