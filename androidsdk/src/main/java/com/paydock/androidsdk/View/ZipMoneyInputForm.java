package com.paydock.androidsdk.View;

import android.content.Context;
import android.content.res.Resources;
import android.support.design.widget.TextInputLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.paydock.androidsdk.GetToken;
import com.paydock.androidsdk.IGetToken;
import com.paydock.androidsdk.Models.TokenCardResponse;
import com.paydock.androidsdk.R;
import com.paydock.javasdk.Models.PaymentType;
import com.paydock.javasdk.Models.TokenRequest;
import com.paydock.javasdk.Services.Environment;

@SuppressWarnings({"Convert2Lambda", "SameParameterValue"})
public class ZipMoneyInputForm extends LinearLayout implements IZipMoneyInputForm {

    private static final String TAG = "ZipMoneyInputForm";

    View rootView;

    private ImageButton bZipMoney;
    private Resources mResources;

    private RelativeLayout pbBankLoadingPanel;
    public TokenCardResponse mTokenCardResponse;
    private String mGatewayID;

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
        pbBankLoadingPanel = findViewById(R.id.pbBankLoadingPanel);

        bZipMoney = findViewById(R.id.bZipMoney);

        mResources = getResources();
        
        mTokenCardResponse = new TokenCardResponse();

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
    public void getToken(Environment environment, String publicKey, String gatewayID, IGetToken delegateInterface) {
        mGatewayID = gatewayID;
        if (validate()){
            try {
                TokenRequest token = createToken();
                GetToken myTokenTask = new GetToken(environment, publicKey, delegateInterface, mTokenCardResponse, pbBankLoadingPanel);
                myTokenTask.execute(token);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}




