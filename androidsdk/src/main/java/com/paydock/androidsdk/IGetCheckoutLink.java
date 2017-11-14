package com.paydock.androidsdk;

import com.paydock.javasdk.Models.ExternalCheckoutResponse;

public interface IGetCheckoutLink {
    void checkoutLinkCallback(ExternalCheckoutResponse output);
}
