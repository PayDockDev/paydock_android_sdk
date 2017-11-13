# Paydock SDK for Android

This SDK provides a wrapper around the PayDock REST API.
For more info on the Paydock API, see our [full documentation](https://docs.paydock.com).

## Getting access to the library

To download the library please visit [JCentre](https://bintray.com/markcardamis/androidSDK/androidsdk).
There are different dependency snippets to be inserted in your code based on your selected build settings (we support Gradle, Maven or Ivy). Example Gradle dependency is:
```
compile 'com.paydock:androidsdk:1.0.9'
```
Note that the library will need both INTERNET and CAMERA permission to function.
Sample app to reference can be found [here](https://github.com/PayDockDev/paydock_android_sdk/tree/master/app/src/main)

## Hello world example

### Setup
Add the dependency in your ```build.gradle```:
```
compile 'com.paydock:androidsdk:1.0.9'
```

### Sample code (See the Sample app above for a complete example)
Add the credit card widget in your layout XML
```xml
<com.paydock.androidsdk.View.CreditCardInputForm
android:id="@+id/creditCardInputForm"
android:layout_width="300dp"
android:layout_height="220dp"
android:layout_marginLeft="10dp"
android:layout_marginRight="10dp"/>
```

To interact with the widget we are going to assume you have set the correct ContentView and referenced the CreditCardInputForm widget in that view. Set your optional fields and then run .build().
```Java
mCreditCardInputForm.setEmail(false)
.setPhone(true)
.setFirstName(true)
.setLastName(true)
.setAddressLine1(true)
.setAddressLine2(true)
.setCity(true)
.setCountry(true)
.setPostCode(true)
.build();
```

You can then trigger the GetToken method using a button, in this example bGetToken, which you have already set the ```onClick``` listener for :

```Java
    bGetToken.setOnClickListener(v -> {
        mCreditCardInputForm.getToken(Environment.Sandbox, <Public_key>, <Gateway_id>,  this);
    }
```
Make sure you implement the IGetToken interface in your activity so you can receive the callback when there is a response from PayDock.
```Java
    @Override
    public void tokenCallback(TokenCardResponse output){
        String mToken = output.data;
    }
```

## Get Payment sources

### Setup
Add the dependency in your ```build.gradle```:
```
compile 'com.paydock:androidsdk:1.0.6'
```

### Sample code (See the Sample app above for a complete example)
Add the  payment sources widget in your layout XML
```xml
<com.paydock.androidsdk.View.VaultedPaymentSourcesInputForm
android:id="@+id/vaultedPaymentsSourcesInputForm"
android:layout_width="300dp"
android:layout_height="460dp"
android:layout_marginLeft="10dp"
android:layout_marginRight="10dp"/>
```

To interact with the widget we are going to assume you have set the correct ContentView and referenced the VaultedPaymentSourcesInputForm widget in that view. Make sure you have the correct Public Key and the correct Query String set as parameters then run .build().
More information about how to get the Query string can be found [here](https://docs.paydock.com/?javascript#get-customer-list-with-parameters)
```Java
mVaultedPaymentSourcesInputForm.getVaultedPaymentSources(Environment.Sandbox,
sPublicKey, sQueryString, this)
.build();
```

Make sure you implement the IPaymentSourceResponse interface in your activity so you can receive the callback when there is a response from the widget. The default payment source id will be sent back when the widget is initialised and then everytime the user presses on a different entry.
```Java
    @Override
    public void paymentSourceResponseCallback(PaymentSourceResponse output) {
        try {
            mCustomerId = output.resource.data.customer_id;
            mPaymentSourceId = output.resource.data._id;
        } catch (Exception e){
            e.printStackTrace();
        }
    }
```

