package androidsdk.View;

import com.paydock.javasdk.Services.Environment;

import androidsdk.IPaymentSourceResponse;

public interface IVaultedPaymentSourcesInputForm {
    void getVaultedPaymentSources(Environment environment, String publicKey, String queryToken,
                                  IPaymentSourceResponse delegateInterface);
}
