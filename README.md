# Welcome to Paydock android SDK 

This SDK provides a wrapper around the PayDock REST API.

For more info on the Paydock API, see our [full documentation](https://docs.paydock.com).

## Getting access to the library

To download the library please visit https://bintray.com/markcardamis/androidSDK/androidsdk.
There are different dependency snippets to be inserted in your code based on your selected repository (we support Gradle, Maven or Ivy).

To use the library make sure you have internet enabled in your manifest.

There are three code sets below to get you started:
- activity_main.xml which is a layout in your res folder to define the view
- MainActivity.java which is the main startup activity
- AddCharge.java which is the Async method called to execute the charges


### activity_main

<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
android:layout_width="match_parent"
android:layout_height="match_parent"
android:descendantFocusability="beforeDescendants"
android:focusable="true"
android:focusableInTouchMode="true"
android:orientation="vertical"
android:padding="16dp"
android:weightSum="1">

<RelativeLayout
android:id="@+id/pbLoadingPanel"
android:layout_width="match_parent"
android:layout_height="match_parent"
android:gravity="center"
android:visibility="gone">

<ProgressBar
android:layout_width="match_parent"
android:layout_height="match_parent"
android:indeterminate="true"/>
</RelativeLayout>

<RelativeLayout
android:layout_width="match_parent"
android:layout_height="wrap_content"
android:layout_marginBottom="20dp">

<Button
android:id="@+id/bCreditCard"
android:layout_width="113dp"
android:layout_height="45dp"
android:layout_alignParentStart="true"
android:layout_alignParentTop="true"
android:text="@string/credit_card"
android:textSize="14sp"/>

<Button
android:id="@+id/bBank"
android:layout_width="113dp"
android:layout_height="45dp"
android:layout_alignParentTop="true"
android:layout_centerHorizontal="true"
android:text="@string/bank"
android:textSize="14sp"/>

<Button
android:id="@+id/bVault"
android:layout_width="113dp"
android:layout_height="45dp"
android:layout_alignParentEnd="true"
android:layout_alignParentTop="true"
android:text="@string/vault"
android:textSize="14sp"/>

</RelativeLayout>

<com.paydock.androidsdk.View.CreditCardInputForm
android:id="@+id/creditCardInputForm"
android:layout_width="@dimen/input_layout_width"
android:layout_height="@dimen/input_layout_height"
android:layout_marginLeft="@dimen/input_layout_horizontal_margin"
android:layout_marginRight="@dimen/input_layout_horizontal_margin"
android:visibility="gone"/>

<com.paydock.androidsdk.View.DirectDebitInputForm
android:id="@+id/directDebitInputForm"
android:layout_width="@dimen/input_layout_width"
android:layout_height="@dimen/input_layout_height"
android:layout_marginLeft="@dimen/input_layout_horizontal_margin"
android:layout_marginRight="@dimen/input_layout_horizontal_margin"
android:visibility="gone"/>

<com.paydock.androidsdk.View.VaultedPaymentSourcesInputForm
android:id="@+id/vaultedPaymentsSourcesInputForm"
android:layout_width="@dimen/input_layout_width"
android:layout_height="@dimen/input_layout_height"
android:layout_marginLeft="@dimen/input_layout_horizontal_margin"
android:layout_marginRight="@dimen/input_layout_horizontal_margin"
android:visibility="gone"/>

<Button
android:id="@+id/bAddCharge"
android:layout_width="113dp"
android:layout_height="45dp"
android:layout_gravity="end"
android:layout_marginTop="20dp"
android:text="@string/charge"
android:textSize="14sp"/>

</LinearLayout> 

### MainActivity

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
import com.paydock.javasdk.Services.Environment;

import java.math.BigDecimal;

public class MainActivity extends Activity implements IGetToken, IPaymentSourceResponse {

public static final String sPublicKey = "";
public static final String sPrivateKey = "";
public static final String sCreditCardGatewayID = "";
public static final String sBankGatewayID = "";

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

mCreditCardInputForm.setVisibility(View.VISIBLE);
mDirectDebitInputForm.setVisibility(View.GONE);
mVaultedPaymentSourcesInputForm.setVisibility(View.GONE);

bCreditCard.setOnClickListener(v -> {
mCreditCardInputForm.setVisibility(View.VISIBLE);
mDirectDebitInputForm.setVisibility(View.GONE);
mVaultedPaymentSourcesInputForm.setVisibility(View.GONE);
});

bBank.setOnClickListener(v -> {
mCreditCardInputForm.setVisibility(View.GONE);
mDirectDebitInputForm.setVisibility(View.VISIBLE);
mVaultedPaymentSourcesInputForm.setVisibility(View.GONE);
});

bVault.setOnClickListener(v -> {
mCreditCardInputForm.setVisibility(View.GONE);
mDirectDebitInputForm.setVisibility(View.GONE);
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

} catch (Exception e) {
e.printStackTrace();
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
charge.amount =new BigDecimal(10);
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
chargeResponse.resource.data.amount.toString() + "\r\n" +
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


### AddCharge

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
try{
Config.initialise(Environment.Sandbox, MainActivity.sPrivateKey, MainActivity.sPublicKey);
ch =  new Charges().add(arg0[0]);
}catch (ResponseException er){
//handle Paydock exception
ch.error.message = er.errorResponse.message;
ch.error.http_status_code = er.errorResponse.http_status_code;
ch.error.jsonResponse = er.errorResponse.jsonResponse;
}catch (Exception e){
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





