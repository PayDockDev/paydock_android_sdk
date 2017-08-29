package androidsdk;

import com.paydock.javasdk.Models.CustomerPaymentSourceSearchResponse;

public interface IGetPaymentSources {
    void paymentSourcesCallback(CustomerPaymentSourceSearchResponse output);
}
