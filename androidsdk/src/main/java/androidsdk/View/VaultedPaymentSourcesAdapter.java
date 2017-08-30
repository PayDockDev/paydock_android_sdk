package androidsdk.View;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.paydock.androidsdk.R;

import java.util.List;

import androidsdk.IPaymentSourceResponse;
import androidsdk.Models.PaymentSourceResponse;

public class VaultedPaymentSourcesAdapter extends RecyclerView.Adapter<VaultedPaymentSourcesAdapter.MyViewHolder> {
    private List<PaymentSourceResponse> mVaultedPaymentsSources;
    private int selectedPosition = -1;
    private IPaymentSourceResponse mDelegate;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView mIcon;
        public TextView mName, mLast4;

        public MyViewHolder(View view) {
            super(view);
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
        if(selectedPosition==position)
            holder.itemView.setPadding(40,0,0,0);
        else
            holder.itemView.setPadding(0,0,0,0);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition=holder.getAdapterPosition();
                mDelegate.paymentSourceResponseCallback(mVaultedPaymentsSources.get(selectedPosition));
                notifyDataSetChanged();

            }
        });

        if (tokenCardResponse.resource.data.type.equals("bsb")){
            holder.mIcon.setImageResource(R.drawable.ic_bank);
        }else if (tokenCardResponse.resource.data.type.equals("card")){
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
        }else if (tokenCardResponse.resource.data.type.equals("paypal")){
            holder.mIcon.setImageResource(R.drawable.ic_default);
        }

        holder.mName.setText((tokenCardResponse.resource.data.type.equals("bsb")) ?
                tokenCardResponse.resource.data.account_name :
                tokenCardResponse.resource.data._id);
        holder.mLast4.setText((tokenCardResponse.resource.data.type.equals("bsb")) ?
                tokenCardResponse.resource.data.account_number :
                tokenCardResponse.resource.data.card_number_last4);

    }

    @Override
    public int getItemCount() {
        return mVaultedPaymentsSources.size();
    }

}