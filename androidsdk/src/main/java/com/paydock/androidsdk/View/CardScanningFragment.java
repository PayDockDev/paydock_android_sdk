package com.paydock.androidsdk.View;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

import com.paydock.androidsdk.BuildConfig;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

public class CardScanningFragment extends Fragment{

    private static final int SCAN_REQUEST_CODE = 9999;
    public static final String FRAGMENT_TAG = BuildConfig.APPLICATION_ID + ".CARDIO_FRAGMENT_TAG";

    private CreditCardInputForm mCreditCardInputForm;

    public static void requestScan(Activity activity, CreditCardInputForm creditCardInputForm) {
        CardScanningFragment cardIOFragment = (CardScanningFragment) activity.getFragmentManager().findFragmentByTag(FRAGMENT_TAG);

        if (cardIOFragment != null) {
            activity.getFragmentManager()
                    .beginTransaction()
                    .remove(cardIOFragment)
                    .commit();
        }

        cardIOFragment = new CardScanningFragment();
        cardIOFragment.mCreditCardInputForm = creditCardInputForm;

        activity.getFragmentManager()
                .beginTransaction()
                .add(cardIOFragment, FRAGMENT_TAG)
                .commit();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        Intent scanIntent = new Intent(getActivity(), CardIOActivity.class)
                .putExtra(CardIOActivity.EXTRA_HIDE_CARDIO_LOGO, true)
                .putExtra(CardIOActivity.EXTRA_REQUIRE_CARDHOLDER_NAME, true)
                .putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true)
                .putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true)
                .putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false)
                .putExtra(CardIOActivity.EXTRA_SCAN_EXPIRY, true);

        startActivityForResult(scanIntent, SCAN_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SCAN_REQUEST_CODE) {
            CreditCard scanResult = new CreditCard();
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);
                if (getActivity() != null) {
                    getActivity().getFragmentManager()
                            .beginTransaction()
                            .remove(this)
                            .commit();
                }
            } else {
                scanResult.cardholderName = "Error with card scan";
            }
            mCreditCardInputForm.parseCardIOdata(scanResult);
        }
    }


}
