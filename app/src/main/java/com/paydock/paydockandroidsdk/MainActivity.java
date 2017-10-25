package com.paydock.paydockandroidsdk;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import com.paydock.javasdk.Models.ChargeRequest;
import com.paydock.javasdk.Models.ChargeResponse;
import com.paydock.javasdk.Models.ResponseException;
import com.paydock.javasdk.Services.Environment;

import java.math.BigDecimal;

public class MainActivity extends Activity implements IGetToken, IPaymentSourceResponse {

    public static final String sPublicKey = "8b2dad5fcf18f6f504685a46af0df82216781f3b";
    public static final String sPrivateKey = "c3de8f40ebbfff0fb74c11154274c080dfb8e3f9";
    public static final String sCreditCardGatewayID = "58dba2dc5219634f922f79c3";
    public static final String sBankGatewayID = "58bf7dd43c541b5b87f741df";
    public static final String sQueryString = "eyJhbGciOiJIUzI1NiIsInR5cC" +
            "I6IkpXVCJ9.eyJpZCI6IjU4YjY0Y2UzNmRhN2U0MjVkNmU0ZjcwNSIsImxpbWl" +
            "0IjpudWxsLCJza2lwIjpudWxsLCJpYXQiOjE1MDMzMTg2NzV9.U6ziYMwuviOWY" +
            "vAtp_16dwE4HDXRGVOOvdkUnhtEALE";

    Button bCreditCard, bBank, bVault, bAddCharge;

    CreditCardInputForm mCreditCardInputForm;
    DirectDebitInputForm mDirectDebitInputForm;
    VaultedPaymentSourcesInputForm mVaultedPaymentSourcesInputForm;

    private RelativeLayout pbLoadingPanel;

    String mToken = null;
    String mCustomerId = null;
    String mPaymentSourceId = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bCreditCard = findViewById(R.id.bCreditCard);
        bBank = findViewById(R.id.bBank);
        bVault = findViewById(R.id.bVault);
        bAddCharge = findViewById(R.id.bAddCharge);
        mCreditCardInputForm = findViewById(R.id.creditCardInputForm);
        mDirectDebitInputForm = findViewById(R.id.directDebitInputForm);
        mVaultedPaymentSourcesInputForm = findViewById(R.id.vaultedPaymentsSourcesInputForm);
        pbLoadingPanel = findViewById(R.id.pbLoadingPanel);

        mCreditCardInputForm.setEmail(true)
                            .build();


        bCreditCard.setOnClickListener(v -> {
            mCreditCardInputForm.build();
            mDirectDebitInputForm.hide();
            mVaultedPaymentSourcesInputForm.hide();
        });

        bBank.setOnClickListener(v -> {
            mCreditCardInputForm.hide();
            mDirectDebitInputForm.build();
            mVaultedPaymentSourcesInputForm.hide();

        });

        bVault.setOnClickListener(v -> {
            mCreditCardInputForm.hide();
            mDirectDebitInputForm.hide();
            mVaultedPaymentSourcesInputForm.build();
            mVaultedPaymentSourcesInputForm.getVaultedPaymentSources(Environment.Sandbox,
                    sPublicKey, sQueryString, this);

            if (mVaultedPaymentSourcesInputForm.getVisibility() == View.GONE){
                mVaultedPaymentSourcesInputForm.setVisibility(View.VISIBLE);
                mVaultedPaymentSourcesInputForm.getVaultedPaymentSources(Environment.Sandbox,
                        sPublicKey, sQueryString, this);
            }
        });


        bAddCharge.setOnClickListener(v -> {
            try {
                if (v != null) { // Hide the soft keyboard
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                if (mCreditCardInputForm.getVisibility() == View.VISIBLE) {
                    pbLoadingPanel.setVisibility(View.VISIBLE);
                    mCreditCardInputForm.getToken(Environment.Sandbox, sPublicKey, sCreditCardGatewayID, this);
                } else if (mDirectDebitInputForm.getVisibility() == View.VISIBLE) {
                    pbLoadingPanel.setVisibility(View.VISIBLE);
                    mDirectDebitInputForm.getToken(Environment.Sandbox,
                            sPublicKey, sBankGatewayID, this);
                } else if (mVaultedPaymentSourcesInputForm.getVisibility() == View.VISIBLE){
                    pbLoadingPanel.setVisibility(View.VISIBLE);
                    new AddCharge(this::displayPopup).execute(createCharge());
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

}
