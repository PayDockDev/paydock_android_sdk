package com.paydock.androidsdk.View;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.paydock.androidsdk.IPaymentSourceResponse;
import com.paydock.androidsdk.Models.PaymentSourceResponse;
import com.paydock.androidsdk.R;

import java.util.List;

public class VaultedPaymentSourcesAdapter extends RecyclerView.Adapter<VaultedPaymentSourcesAdapter.MyViewHolder> {
    private List<PaymentSourceResponse> mVaultedPaymentsSources;
    private int mSelectedPosition = -1;
    private IPaymentSourceResponse mDelegate;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView mIcon, mTick;
        public TextView mName, mLast4;

        public MyViewHolder(View view) {
            super(view);
            mTick = view.findViewById(R.id.ivTick);
            mIcon = view.findViewById(R.id.ivIcon);
            mName = view.findViewById(R.id.tvName);
            mLast4 = view.findViewById(R.id.tvLast4);
        }
    }

    public VaultedPaymentSourcesAdapter(List<PaymentSourceResponse> vaultedPaymentsSources,
                                        IPaymentSourceResponse paymentSourceResponse) {
        this.mVaultedPaymentsSources = vaultedPaymentsSources;
        this.mDelegate = paymentSourceResponse;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.payment_sources_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        PaymentSourceResponse tokenCardResponse = mVaultedPaymentsSources.get(position);
        if(mSelectedPosition==position) {
            holder.mTick.setVisibility(View.VISIBLE);
        } else {
            holder.mTick.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(v -> {
            mSelectedPosition=holder.getAdapterPosition();
            mDelegate.paymentSourceResponseCallback(mVaultedPaymentsSources.get(mSelectedPosition));
            notifyDataSetChanged();

        });

        switch (tokenCardResponse.resource.data.type){
            case ("bsb"):
                holder.mIcon.setImageResource(R.drawable.ic_bank);
                holder.mName.setText(tokenCardResponse.resource.data.account_number);
                holder.mLast4.setText(tokenCardResponse.resource.data.account_name);
                break;
            case ("card"):
                switch (tokenCardResponse.resource.data.card_scheme){
                    case ("visa"):
                        holder.mIcon.setImageResource(R.drawable.ic_visa);
                        break;
                    case ("mastercard"):
                        holder.mIcon.setImageResource(R.drawable.ic_mastercard);
                        break;
                    case ("amex"):
                        holder.mIcon.setImageResource(R.drawable.ic_amex);
                        break;
                    case ("diners"):
                        holder.mIcon.setImageResource(R.drawable.ic_diners);
                        break;
                    case ("cup"):
                        holder.mIcon.setImageResource(R.drawable.ic_cup);
                        break;
                    default: holder.mIcon.setImageResource(R.drawable.ic_default);
                }
                holder.mName.setText("****");
                holder.mLast4.setText(tokenCardResponse.resource.data.card_number_last4);
                break;
            case ("checkout"):
                holder.mIcon.setImageResource(R.drawable.ic_paypal);
                holder.mName.setText(tokenCardResponse.resource.data.checkout_email);
                holder.mLast4.setText(tokenCardResponse.resource.data.checkout_holder);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return mVaultedPaymentsSources.size();
    }

    @SuppressWarnings("SameParameterValue")
    public void setSelectedPosition(int selectedPosition){
        mSelectedPosition = selectedPosition;
    }

}
