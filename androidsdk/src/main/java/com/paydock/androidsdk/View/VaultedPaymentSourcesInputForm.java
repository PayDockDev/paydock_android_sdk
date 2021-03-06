package com.paydock.androidsdk.View;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.paydock.androidsdk.GetPaymentSources;
import com.paydock.androidsdk.IGetPaymentSources;
import com.paydock.androidsdk.IPaymentSourceResponse;
import com.paydock.androidsdk.Models.PaymentSourceResponse;
import com.paydock.androidsdk.R;
import com.paydock.javasdk.Models.CustomerPaymentSourceSearchRequest;
import com.paydock.javasdk.Models.CustomerPaymentSourceSearchResponse;
import com.paydock.javasdk.Services.Environment;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"Convert2Lambda", "SameParameterValue"})
public class VaultedPaymentSourcesInputForm extends LinearLayout implements IVaultedPaymentSourcesInputForm{

    private static final String TAG = "VaultedPaymentsSources";

    View rootView;

    private String mQueryToken;
    private IPaymentSourceResponse mDelegate = null;

    private List<PaymentSourceResponse> tokensList;

    RecyclerView recyclerView;
    private VaultedPaymentSourcesAdapter mAdapter;

    private RelativeLayout pbPaymentSourceLoadingPanel;

    public VaultedPaymentSourcesInputForm(Context context) {
        super(context);
        init(context);
    }

    public VaultedPaymentSourcesInputForm(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VaultedPaymentSourcesInputForm(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {

        setVisibility(GONE); // hide the view until the build command is sent

        rootView = inflate(context, R.layout.payment_sources, this);

        recyclerView = findViewById(R.id.rvPaymentSources);
        pbPaymentSourceLoadingPanel = findViewById(R.id.pbPaymentSourceLoadingPanel);

        tokensList = new ArrayList<>();
        mAdapter = new VaultedPaymentSourcesAdapter(tokensList, mDelegate);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        prepareData();

    }

    private void prepareData() {
        PaymentSourceResponse tokenCardResponse = new PaymentSourceResponse();
        tokensList.clear();
        tokenCardResponse.resource.data.type = "bsb";
        tokenCardResponse.resource.data.account_number = "Getting payment sources";
        tokensList.add(tokenCardResponse);
        mAdapter.notifyDataSetChanged();
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
    public VaultedPaymentSourcesInputForm getVaultedPaymentSources(Environment environment, String publicKey,
                                         String queryToken, IPaymentSourceResponse delegateInterface) {
        mQueryToken = queryToken;
        mDelegate = delegateInterface;

        mAdapter = new VaultedPaymentSourcesAdapter(tokensList, mDelegate);
        recyclerView.setAdapter(mAdapter);
        pbPaymentSourceLoadingPanel.setVisibility(VISIBLE);

        CustomerPaymentSourceSearchRequest token = mCustomerPaymentSourceSearchRequest();
        GetPaymentSources myTokenTask = new GetPaymentSources(environment, publicKey, new IGetPaymentSources() {
            @Override
            public void paymentSourcesCallback(CustomerPaymentSourceSearchResponse output) {
                try {
                    pbPaymentSourceLoadingPanel.setVisibility(GONE);
                    if (output.error != null) {
                        PaymentSourceResponse response = new PaymentSourceResponse();
                        response.error = output.error;
                        mDelegate.paymentSourceResponseCallback(response);
                    } else if (output.resource != null) {
                        tokensList.clear();
                        if (output.resource.count > 0) {
                            for (int i = 0; i < output.resource.count; i++) {
                                PaymentSourceResponse response = new PaymentSourceResponse();
                                if (output.resource.data[i]._id != null)
                                    response.resource.data._id = output.resource.data[i]._id;
                                if (output.resource.data[i].card_number_last4 != null)
                                    response.resource.data.card_number_last4 = output.resource.data[i].card_number_last4;
                                if (output.resource.data[i].card_scheme != null)
                                    response.resource.data.card_scheme = output.resource.data[i].card_scheme;
                                if (output.resource.data[i].account_name != null)
                                    response.resource.data.account_name = output.resource.data[i].account_name;
                                if (output.resource.data[i].account_number != null)
                                    response.resource.data.account_number = output.resource.data[i].account_number;
                                if (output.resource.data[i].checkout_holder != null)
                                    response.resource.data.checkout_holder = output.resource.data[i].checkout_holder;
                                if (output.resource.data[i].checkout_email != null)
                                    response.resource.data.checkout_email = output.resource.data[i].checkout_email;
                                if (output.resource.data[i].type != null)
                                    response.resource.data.type = output.resource.data[i].type;
                                if (output.resource.data[i].gateway_id != null)
                                    response.resource.data.gateway_id = output.resource.data[i].gateway_id;
                                if (output.resource.data[i].customer_id != null)
                                    response.resource.data.customer_id = output.resource.data[i].customer_id;
                                if (output.resource.data[i].gateway_type != null)
                                    response.resource.data.gateway_type = output.resource.data[i].gateway_type;
                                if (output.resource.data[i].gateway_mode != null)
                                    response.resource.data.gateway_mode = output.resource.data[i].gateway_mode;
                                if (output.resource.data[i].primary != null)
                                    response.resource.data.primary = output.resource.data[i].primary;
                                if (output.resource.data[i].customer_reference != null)
                                    response.resource.data.customer_reference = output.resource.data[i].customer_reference;

                                if (response.resource.data.primary) {
                                    tokensList.add(0, response);
                                    mDelegate.paymentSourceResponseCallback(response);
                                    mAdapter.setSelectedPosition(0);
                                } else {
                                    tokensList.add(response);
                                }
                            }
                        } else {
                            PaymentSourceResponse response = new PaymentSourceResponse();
                            response.resource.data.type = "bsb";
                            response.resource.data.account_number = "No payment sources found";
                            tokensList.add(response);
                            mDelegate.paymentSourceResponseCallback(response);
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        myTokenTask.execute(token);
        return this;
    }




    private CustomerPaymentSourceSearchRequest mCustomerPaymentSourceSearchRequest() {
        CustomerPaymentSourceSearchRequest request = new CustomerPaymentSourceSearchRequest();
        request.query_token = mQueryToken;
        return request;
    }
}




