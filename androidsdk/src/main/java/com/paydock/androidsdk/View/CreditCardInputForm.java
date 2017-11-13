package com.paydock.androidsdk.View;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.paydock.androidsdk.GetToken;
import com.paydock.androidsdk.IGetToken;
import com.paydock.androidsdk.Models.TokenCardResponse;
import com.paydock.androidsdk.R;
import com.paydock.androidsdk.Tools.CardType;
import com.paydock.androidsdk.Tools.DateHelper;
import com.paydock.javasdk.Models.ErrorResponse;
import com.paydock.javasdk.Models.ResponseException;
import com.paydock.javasdk.Models.TokenRequest;
import com.paydock.javasdk.Services.Environment;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

@SuppressWarnings({"Convert2Lambda", "SameParameterValue"})
public class CreditCardInputForm extends LinearLayout implements ICreditCardInputForm {

    private static final String TAG = "CardInputForm";

    View rootView;
    private CardType mCardType;

    private static final int[] NORMAL_CARD_SPACES = { 4, 8, 12 };
    private static final int[] SHORT_CARD_SPACES = { 4, 10 };
    private static final int[] DATE_SPACES = { 2 };

    private int[] spaceIndices = NORMAL_CARD_SPACES;

    private EditText etCreditCardName;
    private EditText etCreditCardNumber;
    private EditText etCreditCardExpiry;
    private EditText etCreditCardCVC;
    private EditText etCreditCardEmail;
    private EditText etCreditCardPhone;
    private EditText etCreditCardFirstName;
    private EditText etCreditCardLastName;
    private EditText etCreditCardAddressLine1;
    private EditText etCreditCardAddressLine2;
    private EditText etCreditCardAddressCity;
    private EditText etCreditCardAddressCountry;
    private EditText etCreditCardAddressPostCode;
    private TextInputLayout etCreditCardNameLayout;
    private TextInputLayout etCreditCardNumberLayout;
    private TextInputLayout etCreditCardExpiryLayout;
    private TextInputLayout etCreditCardCVCLayout;
    private TextInputLayout etCreditCardEmailLayout;
    private TextInputLayout etCreditCardPhoneLayout;
    private TextInputLayout etCreditCardFirstNameLayout;
    private TextInputLayout etCreditCardLastNameLayout;
    private TextInputLayout etCreditCardAddressLine1Layout;
    private TextInputLayout etCreditCardAddressLine2Layout;
    private TextInputLayout etCreditCardAddressCityLayout;
    private TextInputLayout etCreditCardAddressCountryLayout;
    private TextInputLayout etCreditCardAddressPostCodeLayout;
    private LinearLayout llCreditCardEmailLayout;
    private LinearLayout llCreditCardPhoneLayout;
    private LinearLayout llCreditCardFirstNameLayout;
    private LinearLayout llCreditCardLastNameLayout;
    private LinearLayout llCreditCardAddressLine1Layout;
    private LinearLayout llCreditCardAddressLine2Layout;
    private LinearLayout llCreditCardAddressCityLayout;
    private LinearLayout llCreditCardAddressCountryLayout;
    private LinearLayout llCreditCardAddressPostCodeLayout;
    private Resources mResources;
    private ImageView ivCreditCardIcons;
    private ImageButton bCardIO;

    private RelativeLayout pbCreditCardLoadingPanel;
    public TokenCardResponse mTokenCardResponse;

    private boolean mCreditCardEmailRequired = false;
    private boolean mCreditCardPhoneRequired = false;
    private boolean mCreditCardFirstNameRequired = false;
    private boolean mCreditCardLastNameRequired = false;
    private boolean mCreditCardAddressLine1Required = false;
    private boolean mCreditCardAddressLine2Required = false;
    private boolean mCreditCardAddressCityRequired = false;
    private boolean mCreditCardAddressCountryRequired = false;
    private boolean mCreditCardAddressPostCodeRequired = false;
    private boolean mCreditCardScanRequired = true;


    public CreditCardInputForm(Context context) {
        super(context);
        init(context);
    }

    public CreditCardInputForm(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CreditCardInputForm(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {

        setVisibility(GONE); // hide the view until the optional fields are added

        rootView = inflate(context, R.layout.credit_card, this);

        etCreditCardName = findViewById(R.id.etCreditCardName);
        etCreditCardNumber = findViewById(R.id.etCreditCardNumber);
        etCreditCardExpiry = findViewById(R.id.etCreditCardExpiry);
        etCreditCardCVC = findViewById(R.id.etCreditCardCVC);
        etCreditCardEmail = findViewById(R.id.etCreditCardEmail);
        etCreditCardPhone = findViewById(R.id.etCreditCardPhone);
        etCreditCardFirstName = findViewById(R.id.etCreditCardFirstName);
        etCreditCardLastName = findViewById(R.id.etCreditCardLastName);
        etCreditCardAddressLine1 = findViewById(R.id.etCreditCardAddressLine1);
        etCreditCardAddressLine2 = findViewById(R.id.etCreditCardAddressLine2);
        etCreditCardAddressCity = findViewById(R.id.etCreditCardAddressCity);
        etCreditCardAddressCountry = findViewById(R.id.etCreditCardAddressCountry);
        etCreditCardAddressPostCode = findViewById(R.id.etCreditCardAddressPostCode);
        ivCreditCardIcons = findViewById(R.id.ivCreditCardIcon);
        etCreditCardNameLayout = findViewById(R.id.etCreditCardNameLayout);
        etCreditCardNumberLayout = findViewById(R.id.etCreditCardNumberLayout);
        etCreditCardExpiryLayout = findViewById(R.id.etCreditCardExpiryLayout);
        etCreditCardCVCLayout = findViewById(R.id.etCreditCardCVCLayout);
        etCreditCardEmailLayout = findViewById(R.id.etCreditCardEmailLayout);
        etCreditCardPhoneLayout = findViewById(R.id.etCreditCardPhoneLayout);
        etCreditCardFirstNameLayout = findViewById(R.id.etCreditCardFirstNameLayout);
        etCreditCardLastNameLayout = findViewById(R.id.etCreditCardLastNameLayout);
        etCreditCardAddressLine1Layout = findViewById(R.id.etCreditCardAddressLine1Layout);
        etCreditCardAddressLine2Layout = findViewById(R.id.etCreditCardAddressLine2Layout);
        etCreditCardAddressCityLayout = findViewById(R.id.etCreditCardAddressCityLayout);
        etCreditCardAddressCountryLayout = findViewById(R.id.etCreditCardAddressCountryLayout);
        etCreditCardAddressPostCodeLayout = findViewById(R.id.etCreditCardAddressPostCodeLayout);
        llCreditCardEmailLayout = findViewById(R.id.llCreditCardEmailLayout);
        llCreditCardPhoneLayout = findViewById(R.id.llCreditCardPhoneLayout);
        llCreditCardFirstNameLayout = findViewById(R.id.llCreditCardFirstNameLayout);
        llCreditCardLastNameLayout = findViewById(R.id.llCreditCardLastNameLayout);
        llCreditCardAddressLine1Layout = findViewById(R.id.llCreditCardAddressLine1Layout);
        llCreditCardAddressLine2Layout = findViewById(R.id.llCreditCardAddressLine2Layout);
        llCreditCardAddressCityLayout = findViewById(R.id.llCreditCardAddressCityLayout);
        llCreditCardAddressCountryLayout = findViewById(R.id.llCreditCardAddressCountryLayout);
        llCreditCardAddressPostCodeLayout = findViewById(R.id.llCreditCardAddressPostCodeLayout);
        pbCreditCardLoadingPanel = findViewById(R.id.pbCreditCardLoadingPanel);
        bCardIO = findViewById(R.id.bCardIO);

        mResources = getResources();

        mCardType = new CardType();
        mTokenCardResponse = new TokenCardResponse();

        bCardIO.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                scanCard((Activity)context);
            }
        });

        etCreditCardName.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean event) {
                if (!event) {
                    CreditCardInputForm.this.validateCreditCardName(true);
                }
            }
        });

        etCreditCardNumber.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean event) {
                if (!event) {
                    validateCreditCardNumber(true);
                }
            }
        });

        etCreditCardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                addTextSpan(s, spaceIndices, " "); // span to seperate input text box
                updateCardType();
                if (validateCreditCardNumber(false)) {
                    etCreditCardExpiry.requestFocus();
                }
            }
        });

        etCreditCardExpiry.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean event) {
                etCreditCardExpiry.setHint(R.string.btn_credit_card_expiry_hint);
                if (!event) {
                    etCreditCardExpiry.setHint("");
                    validateCreditCardExpiry(true);
                }
            }
        });


        etCreditCardExpiry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                addTextSpan(s, DATE_SPACES, "/");
                if (validateCreditCardExpiry(false)) {
                    etCreditCardCVC.requestFocus();
                }
            }
        });

        etCreditCardCVC.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean event) {
                if (!event) {
                    CreditCardInputForm.this.validateCreditCardCVC(true);
                }
            }
        });

    }

    private void updateCardType() {
        try {
            CardType cardType = CardType.getCreditCardTypeByNumber(getCreditCardNumber());
            assert cardType != null;
            if ((mCardType == null) || (mResources.getString(mCardType.getCardScheme()) !=
                    mResources.getString(cardType.getCardScheme()))){
                mCardType = cardType;
                ivCreditCardIcons.setImageResource(mCardType.getImage());

                if (mCardType.getMaxCardLength() == 15) {
                    spaceIndices = SHORT_CARD_SPACES;
                    InputFilter[] filterCVCLength = {new InputFilter.LengthFilter(4)};
                    etCreditCardCVC.setFilters(filterCVCLength);
                } else {
                    spaceIndices = NORMAL_CARD_SPACES;
                    InputFilter[] filterCVCLength = {new InputFilter.LengthFilter(3)};
                    etCreditCardCVC.setFilters(filterCVCLength);
                }

                InputFilter[] filterMaxDigits = {new InputFilter.LengthFilter(mCardType.getMaxCardLength())};
                etCreditCardNumber.setFilters(filterMaxDigits);

                invalidate();
            }
        }catch (NullPointerException e){
                Log.e(TAG, "updateCardType: ", e);
            }
    }

    private void addTextSpan(Editable s, int[] spaceIndices, String text) {
        Object[] paddingSpans = s.getSpans(0, s.length(), TextSpan.class);
        for (Object span : paddingSpans) {
            s.removeSpan(span);
        }
        for (int index : spaceIndices) {
            if (index <= s.length()) {
                s.setSpan(new TextSpan(text), index - 1, index,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    public CreditCardInputForm setEmail(boolean optionalField) {
        mCreditCardEmailRequired = optionalField;
        return this;
    }

    public CreditCardInputForm setPhone(boolean optionalField) {
        mCreditCardPhoneRequired = optionalField;
        return this;
    }

    public CreditCardInputForm setFirstName(boolean optionalField) {
        mCreditCardFirstNameRequired = optionalField;
        return this;
    }

    public CreditCardInputForm setLastName(boolean optionalField) {
        mCreditCardLastNameRequired = optionalField;
        return this;
    }

    public CreditCardInputForm setAddressLine1(boolean optionalField) {
        mCreditCardAddressLine1Required = optionalField;
        return this;
    }

    public CreditCardInputForm setAddressLine2(boolean optionalField) {
        mCreditCardAddressLine2Required = optionalField;
        return this;
    }

    public CreditCardInputForm setCity(boolean optionalField) {
        mCreditCardAddressCityRequired = optionalField;
        return this;
    }

    public CreditCardInputForm setCountry(boolean optionalField) {
        mCreditCardAddressCountryRequired = optionalField;
        return this;
    }

    public CreditCardInputForm setPostCode(boolean optionalField) {
        mCreditCardAddressPostCodeRequired = optionalField;
        return this;
    }

    public CreditCardInputForm setCardScan(boolean optionalField) {
        mCreditCardScanRequired = optionalField;
        return this;
    }

    public void build() {
        llCreditCardEmailLayout.setVisibility(mCreditCardEmailRequired ? VISIBLE : GONE );
        llCreditCardPhoneLayout.setVisibility(mCreditCardPhoneRequired ? VISIBLE : GONE );
        llCreditCardFirstNameLayout.setVisibility(mCreditCardFirstNameRequired ? VISIBLE : GONE );
        llCreditCardLastNameLayout.setVisibility(mCreditCardLastNameRequired ? VISIBLE : GONE );
        llCreditCardAddressLine1Layout.setVisibility(mCreditCardAddressLine1Required ? VISIBLE : GONE );
        llCreditCardAddressLine2Layout.setVisibility(mCreditCardAddressLine2Required ? VISIBLE : GONE );
        llCreditCardAddressCityLayout.setVisibility(mCreditCardAddressCityRequired ? VISIBLE : GONE );
        llCreditCardAddressCountryLayout.setVisibility(mCreditCardAddressCountryRequired ? VISIBLE : GONE );
        llCreditCardAddressPostCodeLayout.setVisibility(mCreditCardAddressPostCodeRequired ? VISIBLE : GONE );
        bCardIO.setVisibility(mCreditCardScanRequired ? VISIBLE : INVISIBLE);
        setVisibility(VISIBLE);
    }

    public void hide() {
        setVisibility(GONE);
    }

    private String getCreditCardName() {
        return etCreditCardName.getText().toString();
    }

    private String getCreditCardNumber() {
        return etCreditCardNumber.getText().toString();
    }

    private String getCreditCardExpiryMonth() {
        String mDateMonth = etCreditCardExpiry.getText().toString();
        mDateMonth = mDateMonth.substring(0, 2);
        return mDateMonth;
    }

    private String getCreditCardExpiryYear() {
        String mDateYear = etCreditCardExpiry.getText().toString();
        mDateYear = "20" + mDateYear.substring(2, 4);
        return mDateYear;
    }

    private String getCreditCardCVC() {
        return etCreditCardCVC.getText().toString();
    }

    private String getCreditCardEmail() {
        return etCreditCardEmail.getText().toString();
    }

    private String getCreditCardPhone() {
        return etCreditCardPhone.getText().toString();
    }

    private String getCreditCardFirstName() {
        return etCreditCardFirstName.getText().toString();
    }

    private String getCreditCardLastName() {
        return etCreditCardLastName.getText().toString();
    }

    private String getCreditCardAddressLine1() {
        return etCreditCardAddressLine1.getText().toString();
    }

    private String getCreditCardAddressLine2() {
        return etCreditCardAddressLine2.getText().toString();
    }

    private String getCreditCardAddressCity() {
        return etCreditCardAddressCity.getText().toString();
    }

    private String getCreditCardAddressCountry() {
        return etCreditCardAddressCountry.getText().toString();
    }

    private String getCreditCardAddressPostCode() {
        return etCreditCardAddressPostCode.getText().toString();
    }


    private Boolean validateCreditCardName(boolean showErrors)
    {
        if (getCreditCardName().length() == 0) {
            if (showErrors)etCreditCardName.setError(mResources.getString(R.string.btn_credit_card_name_required));
            return false;
        } else {
            etCreditCardNameLayout.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateCreditCardNumber(boolean showErrors)
    {
        String number = etCreditCardNumber.getText().toString();
        if (number.length() == 0) {
            if (showErrors)etCreditCardNumber.setError(mResources.getString(R.string.btn_credit_card_number_required));
            return false;
        } else if (mCardType == null){
            if (showErrors)etCreditCardNumber.setError(mResources.getString(R.string.btn_credit_card_number_required));
            return false;
        } else if (mCardType.isValid(getCreditCardNumber())) {
            etCreditCardNumberLayout.setErrorEnabled(false);
            return true;
        } else {
            if (showErrors)etCreditCardNumber.setError(mResources.getString(R.string.btn_credit_card_number_invalid));
            return false;
        }
    }


    private Boolean validateCreditCardExpiry(boolean showErrors)
    {
        String date = etCreditCardExpiry.getText().toString();
        if (date.length() == 0) {
            if (showErrors)etCreditCardExpiry.setError(mResources.getString(R.string.btn_credit_card_expiry_required));
            return false;
        } else if (date.length() < 4) {
            if (showErrors)etCreditCardExpiry.setError(mResources.getString(R.string.btn_credit_card_expiry_invalid));
            return false;
        } else if (date.length() == 4) {
            boolean dateValid = DateHelper.isFuture(date.substring(0, 2), date.substring(2, 4));
            if (dateValid) {
                etCreditCardExpiryLayout.setErrorEnabled(false);
                return true;
            } else {
                if (showErrors)etCreditCardExpiry.setError(mResources.getString(R.string.btn_credit_card_expiry_invalid));
                return false;
            }
        }
        return false;
    }

    private Boolean validateCreditCardCVC(boolean showErrors)
    {
        String CVC = getCreditCardCVC();
        if (CVC.length() != 0) {
            if ((mCardType.getMaxCardLength() == 15) && (CVC.length() == 4)) {
                etCreditCardCVCLayout.setErrorEnabled(false);
                return true;
            } else if (!(mCardType.getMaxCardLength() == 15) && (CVC.length() == 3)) {
                etCreditCardCVCLayout.setErrorEnabled(false);
                return true;
            } else {
                if (showErrors) etCreditCardCVC.setError(mResources.getString(R.string.btn_credit_card_cvc_invalid));
                return false;
            }
        }else {
            etCreditCardCVCLayout.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateCreditCardEmail(boolean showErrors)
    {
        if (getCreditCardEmail().length() == 0) {
            if (showErrors)etCreditCardEmail.setError(mResources.getString(R.string.btn_credit_card_email_required));
            return false;
        } else {
            etCreditCardEmailLayout.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateCreditCardPhone(boolean showErrors)
    {
        if (getCreditCardPhone().length() == 0) {
            if (showErrors)etCreditCardPhone.setError(mResources.getString(R.string.btn_credit_card_phone_required));
            return false;
        } else {
            etCreditCardPhoneLayout.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateCreditCardFirstName(boolean showErrors)
    {
        if (getCreditCardFirstName().length() == 0) {
            if (showErrors)etCreditCardFirstName.setError(mResources.getString(R.string.btn_credit_card_first_name_required));
            return false;
        } else {
            etCreditCardFirstNameLayout.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateCreditCardLastName(boolean showErrors)
    {
        if (getCreditCardLastName().length() == 0) {
            if (showErrors)etCreditCardLastName.setError(mResources.getString(R.string.btn_credit_card_last_name_required));
            return false;
        } else {
            etCreditCardLastNameLayout.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateCreditCardAddressLine1(boolean showErrors)
    {
        if (getCreditCardAddressLine1().length() == 0) {
            if (showErrors)etCreditCardAddressLine1.setError(mResources.getString(R.string.btn_credit_card_address_line_1_required));
            return false;
        } else {
            etCreditCardAddressLine1Layout.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateCreditCardAddressLine2(boolean showErrors)
    {
        if (getCreditCardAddressLine2().length() == 0) {
            if (showErrors)etCreditCardAddressLine2.setError(mResources.getString(R.string.btn_credit_card_address_line_2_required));
            return false;
        } else {
            etCreditCardAddressLine2Layout.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateCreditCardAddressCity(boolean showErrors)
    {
        if (getCreditCardAddressCity().length() == 0) {
            if (showErrors)etCreditCardAddressCity.setError(mResources.getString(R.string.btn_credit_card_address_city_required));
            return false;
        } else {
            etCreditCardAddressCityLayout.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateCreditCardAddressCountry(boolean showErrors)
    {
        if (getCreditCardAddressCountry().length() == 0) {
            if (showErrors)etCreditCardAddressCountry.setError(mResources.getString(R.string.btn_credit_card_address_country_required));
            return false;
        } else {
            etCreditCardAddressCountryLayout.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateCreditCardAddressPostCode(boolean showErrors)
    {
        if (getCreditCardAddressPostCode().length() == 0) {
            if (showErrors)etCreditCardAddressPostCode.setError(mResources.getString(R.string.btn_credit_card_address_post_code_required));
            return false;
        } else {
            etCreditCardAddressPostCodeLayout.setErrorEnabled(false);
            return true;
        }
    }

    @Override
    public void getToken(Environment environment, String publicKey, String gatewayID,
                         IGetToken delegateInterface) throws Exception {
        if (validate()){
            try {
                pbCreditCardLoadingPanel.setVisibility(VISIBLE);
                TokenRequest token = createToken(gatewayID);
                GetToken myTokenTask = new GetToken(environment, publicKey, delegateInterface,
                        mTokenCardResponse, pbCreditCardLoadingPanel);
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
    public Boolean validate()
    {
        boolean isValid = true;
        if (mCreditCardAddressPostCodeRequired) {
            if (!validateCreditCardAddressPostCode(true)) {
                etCreditCardAddressPostCodeLayout.requestFocus();
                isValid = false;
            }
        }
        if (mCreditCardAddressCountryRequired) {
            if (!validateCreditCardAddressCountry(true)) {
                etCreditCardAddressCountryLayout.requestFocus();
                isValid = false;
            }
        }
        if (mCreditCardAddressCityRequired) {
            if (!validateCreditCardAddressCity(true)) {
                etCreditCardAddressCityLayout.requestFocus();
                isValid = false;
            }
        }
        if (mCreditCardAddressLine2Required) {
            if (!validateCreditCardAddressLine2(true)) {
                etCreditCardAddressLine2Layout.requestFocus();
                isValid = false;
            }
        }
        if (mCreditCardAddressLine1Required) {
            if (!validateCreditCardAddressLine1(true)) {
                etCreditCardAddressLine1Layout.requestFocus();
                isValid = false;
            }
        }
        if (mCreditCardLastNameRequired) {
            if (!validateCreditCardLastName(true)) {
                etCreditCardLastNameLayout.requestFocus();
                isValid = false;
            }
        }
        if (mCreditCardFirstNameRequired) {
            if (!validateCreditCardFirstName(true)) {
                etCreditCardFirstNameLayout.requestFocus();
                isValid = false;
            }
        }
        if (mCreditCardPhoneRequired) {
            if (!validateCreditCardPhone(true)) {
                etCreditCardPhoneLayout.requestFocus();
                isValid = false;
            }
        }
        if (mCreditCardEmailRequired) {
            if (!validateCreditCardEmail(true)) {
                etCreditCardEmailLayout.requestFocus();
                isValid = false;
            }
        }
        if(!validateCreditCardCVC(true)){
            etCreditCardCVCLayout.requestFocus();
            isValid = false;
        }
        if(!validateCreditCardExpiry(true)){
            etCreditCardExpiryLayout.requestFocus();
            isValid = false;
        } else {
            mTokenCardResponse.expiry_month = getCreditCardExpiryMonth();
            mTokenCardResponse.expiry_year = getCreditCardExpiryYear();
        }
        if(!validateCreditCardNumber(true)){
            etCreditCardNumberLayout.requestFocus();
            isValid = false;
        } else {
            String creditCardNumber = getCreditCardNumber();
            int creditCardNumberLength = creditCardNumber.length();
            if (creditCardNumberLength >= 4) {
                mTokenCardResponse.card_number_last4 = (creditCardNumber.substring
                        (creditCardNumberLength - 4, creditCardNumberLength));
                mTokenCardResponse.card_scheme = mResources.getString(mCardType.getCardScheme());
            } else {
                mTokenCardResponse.card_number_last4 = "";
            }
        }
        if(!validateCreditCardName(true)){
            etCreditCardNameLayout.requestFocus();
            isValid = false;
        } else {
            mTokenCardResponse.card_name = getCreditCardName();
        }
        return isValid;
    }

    @Override
    public void clear() {
        etCreditCardName.setText("");
        etCreditCardName.setError(null);
        etCreditCardNameLayout.setErrorEnabled(false);
        etCreditCardNumber.setText("");
        etCreditCardNumber.setError(null);
        etCreditCardNumberLayout.setErrorEnabled(false);
        etCreditCardExpiry.setText("");
        etCreditCardExpiry.setError(null);
        etCreditCardExpiryLayout.setErrorEnabled(false);
        etCreditCardCVC.setText("");
        etCreditCardCVC.setError(null);
        etCreditCardCVCLayout.setErrorEnabled(false);
        etCreditCardEmail.setText("");
        etCreditCardEmail.setError(null);
        etCreditCardEmailLayout.setErrorEnabled(false);
        etCreditCardPhone.setText("");
        etCreditCardPhone.setError(null);
        etCreditCardPhoneLayout.setErrorEnabled(false);
        etCreditCardFirstName.setText("");
        etCreditCardFirstName.setError(null);
        etCreditCardFirstNameLayout.setErrorEnabled(false);
        etCreditCardLastName.setText("");
        etCreditCardLastName.setError(null);
        etCreditCardLastNameLayout.setErrorEnabled(false);
        etCreditCardAddressLine1.setText("");
        etCreditCardAddressLine1.setError(null);
        etCreditCardAddressLine1Layout.setErrorEnabled(false);
        etCreditCardAddressLine2.setText("");
        etCreditCardAddressLine2.setError(null);
        etCreditCardAddressLine2Layout.setErrorEnabled(false);
        etCreditCardAddressCity.setText("");
        etCreditCardAddressCity.setError(null);
        etCreditCardAddressCityLayout.setErrorEnabled(false);
        etCreditCardAddressCountry.setText("");
        etCreditCardAddressCountry.setError(null);
        etCreditCardAddressCountryLayout.setErrorEnabled(false);
        etCreditCardAddressPostCode.setText("");
        etCreditCardAddressPostCode.setError(null);
        etCreditCardAddressPostCodeLayout.setErrorEnabled(false);
    }



    private TokenRequest createToken(String gatewayID) {
        TokenRequest tokenRequest = new TokenRequest();
        if (mCreditCardEmailRequired) tokenRequest.email = getCreditCardEmail();
        if (mCreditCardPhoneRequired) tokenRequest.phone = getCreditCardPhone();
        if (mCreditCardFirstNameRequired) tokenRequest.first_name = getCreditCardFirstName();
        if (mCreditCardLastNameRequired) tokenRequest.last_name = getCreditCardLastName();
        if (mCreditCardAddressLine1Required) tokenRequest.address_line1 = getCreditCardAddressLine1();
        if (mCreditCardAddressLine2Required) tokenRequest.address_line2 = getCreditCardAddressLine2();
        if (mCreditCardAddressCityRequired) tokenRequest.address_city = getCreditCardAddressCity();
        if (mCreditCardAddressCountryRequired) tokenRequest.address_country = getCreditCardAddressCountry();
        if (mCreditCardAddressPostCodeRequired) tokenRequest.address_postcode = getCreditCardAddressPostCode();
        tokenRequest.gateway_id = gatewayID;
        tokenRequest.card_name = getCreditCardName();
        tokenRequest.card_number = getCreditCardNumber();
        tokenRequest.expire_month = getCreditCardExpiryMonth();
        tokenRequest.expire_year = getCreditCardExpiryYear();
        tokenRequest.card_ccv = getCreditCardCVC();
        return tokenRequest;
    }

    void scanCard(Activity activity) {
        try {
            CardIOActivity.canReadCardWithCamera();
            CardScanningFragment.requestScan(activity, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void parseCardIOdata (CreditCard data){
        clear();
        etCreditCardName.setText(data.cardholderName);
        etCreditCardNumber.setText(data.cardNumber);
        etCreditCardExpiry.setText(DateHelper.convertCardIOFormat(data.expiryMonth
                , data.expiryYear));
        etCreditCardCVC.setText(data.cvv);
        updateCardType();
        validate();
    }
}




