package com.paydock.paydockandroidsdk;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.paydock.androidsdk.IGetToken;
import com.paydock.androidsdk.IPaymentSourceResponse;
import com.paydock.androidsdk.Models.PaymentSourceResponse;
import com.paydock.androidsdk.Models.TokenCardResponse;
import com.paydock.androidsdk.View.CreditCardInputForm;
import com.paydock.androidsdk.View.DirectDebitInputForm;
import com.paydock.androidsdk.View.VaultedPaymentSourcesInputForm;
import com.paydock.androidsdk.View.ZipMoneyInputForm;
import com.paydock.javasdk.Models.ChargeRequest;
import com.paydock.javasdk.Models.ChargeResponse;
import com.paydock.javasdk.Models.ExternalCheckoutRequestZipMoney;
import com.paydock.javasdk.Models.ResponseException;
import com.paydock.javasdk.Models.TokenResponse;
import com.paydock.javasdk.Services.Environment;

import java.math.BigDecimal;

public class MainActivity extends Activity implements IGetToken, IPaymentSourceResponse {

    Button bCreditCard, bBank, bVault, bAddCharge;
    Button bZipMoney;
    WebView myWebView;

    CreditCardInputForm mCreditCardInputForm;
    DirectDebitInputForm mDirectDebitInputForm;
    VaultedPaymentSourcesInputForm mVaultedPaymentSourcesInputForm;
    ZipMoneyInputForm mZipMoneyInputForm;

    private RelativeLayout pbLoadingPanel;

    String mToken = null;
    String mCustomerId = null;
    String mPaymentSourceId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bCreditCard = findViewById(R.id.bCreditCard);
        //bBank = findViewById(R.id.bBank);
        bVault = findViewById(R.id.bVault);
        bZipMoney = findViewById(R.id.bZipMoney);
        bAddCharge = findViewById(R.id.bAddCharge);
        mCreditCardInputForm = findViewById(R.id.creditCardInputForm);
        mDirectDebitInputForm = findViewById(R.id.directDebitInputForm);
        mVaultedPaymentSourcesInputForm = findViewById(R.id.vaultedPaymentsSourcesInputForm);
        mZipMoneyInputForm = findViewById(R.id.zipMoneyInputForm);
        pbLoadingPanel = findViewById(R.id.pbLoadingPanel);

        myWebView = findViewById(R.id.webview);

        mCreditCardInputForm.setEmail(true)
                .setCardScan(true)
                .build();

        bCreditCard.setOnClickListener(v -> {
            mCreditCardInputForm.build();
            mDirectDebitInputForm.hide();
            mVaultedPaymentSourcesInputForm.hide();
            mZipMoneyInputForm.hide();
        });

//        bBank.setOnClickListener(v -> {
//            mCreditCardInputForm.hide();
//            mDirectDebitInputForm.build();
//            mVaultedPaymentSourcesInputForm.hide();
//            mZipMoneyInputForm.hide();
//        });

        bVault.setOnClickListener(v -> {
            mCreditCardInputForm.hide();
            mDirectDebitInputForm.hide();
            mVaultedPaymentSourcesInputForm.getVaultedPaymentSources(Environment.Sandbox,
                    PayDock.sPublicKey, PayDock.sQueryString, this)
                    .build();
            mZipMoneyInputForm.hide();

        });

        bZipMoney.setOnClickListener(v -> {
            mCreditCardInputForm.hide();
            mDirectDebitInputForm.hide();
            mVaultedPaymentSourcesInputForm.hide();
            mZipMoneyInputForm.build();
            mZipMoneyInputForm.setMeta(createZipMeta());

        });


        bAddCharge.setOnClickListener(v -> {
            try {
                if (v != null) { // Hide the soft keyboard
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                if (mCreditCardInputForm.getVisibility() == View.VISIBLE) {
                    pbLoadingPanel.setVisibility(View.VISIBLE);
                    mCreditCardInputForm.getToken(Environment.Sandbox, PayDock.sPublicKey, PayDock.sCreditCardGatewayID, this);
                } else if (mDirectDebitInputForm.getVisibility() == View.VISIBLE) {
                    pbLoadingPanel.setVisibility(View.VISIBLE);
                    mDirectDebitInputForm.getToken(Environment.Sandbox,
                            PayDock.sPublicKey, PayDock.sBankGatewayID, this);
                } else if (mVaultedPaymentSourcesInputForm.getVisibility() == View.VISIBLE){
                    pbLoadingPanel.setVisibility(View.VISIBLE);
                    new AddCharge(this::displayPopup).execute(createCharge());
                } else if (mZipMoneyInputForm.getVisibility() == View.VISIBLE){
                    //pbLoadingPanel.setVisibility(View.VISIBLE);
                    mZipMoneyInputForm.getCheckoutLink(Environment.Sandbox,
                            PayDock.sPublicKey, PayDock.sZipMoneyID, this);

                }
            } catch (ResponseException er) {
                // handle local widget validation exception
                pbLoadingPanel.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
                pbLoadingPanel.setVisibility(View.GONE);
            }
        });


    }

    //TODO: Handle Exceptions in Async callback

    @Override
    public void tokenCallback(TokenCardResponse output){
        try {
            mToken = output.data;
            new AddCharge(this::displayPopup).execute(createCharge());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void paymentSourceResponseCallback(PaymentSourceResponse output) {
        try {
            mCustomerId = output.resource.data.customer_id;
            mPaymentSourceId = output.resource.data._id;
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    ChargeRequest createCharge() {
        ChargeRequest charge = new ChargeRequest();
        charge.currency ="AUD";
        double random = (Math.random() * 100);
        charge.amount = BigDecimal.valueOf(random).setScale(0, BigDecimal.ROUND_HALF_UP);
        charge.reference = "Charge reference";
        charge.description = "Charge description";
        if (mToken != null) {
            charge.token = mToken;
            mToken = null;
        } else if (mCustomerId != null){
            charge.customer_id = mCustomerId;
            charge.payment_source_id = mPaymentSourceId;
        }
        return charge;
    }

    ExternalCheckoutRequestZipMoney.Meta createZipMeta() {
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
        shipping_address.first_name = "Shipping First";
        shipping_address.last_name = "Shipping Last";
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
        return meta;
    }



    void displayPopup(ChargeResponse chargeResponse) {
        pbLoadingPanel.setVisibility(View.GONE);
        if (chargeResponse.resource != null) {
            String notification = chargeResponse.resource.data._id + "\r\n" +
                    chargeResponse.resource.data.amount.toString() + "\r\n" +
                    chargeResponse.resource.data.external_id + "\r\n" +
                    chargeResponse.resource.data.reference + "\r\n" +
                    chargeResponse.resource.data._id + "\r\n" +
                    chargeResponse.resource.data.status;
            Toast.makeText(getApplicationContext(), notification, Toast.LENGTH_LONG).show();
        } else if (chargeResponse.error != null) {
            String notification = chargeResponse.error.http_status_code.toString() + "\r\n" +
                    chargeResponse.error.message + "\r\n" + chargeResponse.error.jsonResponse;
            Toast.makeText(getApplicationContext(), notification, Toast.LENGTH_LONG).show();
        }
    }

    void displayPopup2(TokenResponse tokenResponse) {
        pbLoadingPanel.setVisibility(View.GONE);
        if (tokenResponse.resource != null) {
            String notification = tokenResponse.resource.data;
            Toast.makeText(getApplicationContext(), notification, Toast.LENGTH_LONG).show();
        } else if (tokenResponse.error != null) {
            String notification = tokenResponse.error.http_status_code.toString() + "\r\n" +
                    tokenResponse.error.message + "\r\n" + tokenResponse.error.jsonResponse;
            Toast.makeText(getApplicationContext(), notification, Toast.LENGTH_LONG).show();
        }
    }


}
