package com.portal.qr.paymentapp;

public interface PaymentSuccessListener {
    void onPaymentSuccess(QRModel model, String amount);
}
