package com.portal.qr.paymentapp;


import android.os.Handler;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Request;
import okhttp3.ResponseBody;

public class RequestController {

    public static String URL_MAIN = "https://sandbox-api.payosy.com/api";
    public static String CLIENT_ID = "76c2fa61-4b48-405e-b128-896366a20195";
    public static String CLIENT_SECRET = "vD7lN3eN2xE4pA2lB6gW2xL7gE6xT3oF6dG7wW2lU8rR2gT1jI";

    public static void getQrSale(final String amount, final PaymentSuccessListener paymentSuccessListener) {
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"totalReceiptAmount\":"+ amount+"}");
        Request request = new Request.Builder()
                .url(URL_MAIN + "/get_qr_sale")
                .post(body)
                .addHeader("x-ibm-client-id", CLIENT_ID)
                .addHeader("x-ibm-client-secret", CLIENT_SECRET)
                .addHeader("content-type", "application/json")
                .addHeader("accept", "application/json")
                .build();


            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try (ResponseBody responseBody = response.body()) {
                        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                        Headers responseHeaders = response.headers();
                        for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                            System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                        }
                        Gson gson = new Gson();
                        final QRModel qrModel = gson.fromJson(responseBody.string(), QRModel.class);
                        System.out.println("qrData: " + qrModel.QRdata);
                        Thread.sleep(2000);
                        completePayment(qrModel, amount, paymentSuccessListener);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
    }

    public static void completePayment(final QRModel qrModel, final String amount, final PaymentSuccessListener paymentSuccessListener) {
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"returnCode\":1000,\"returnDesc\":\"success\",\"receiptMsgCustomer\":\"beko Campaign\",\"receiptMsgMerchant\":\"beko Campaign Merchant\",\"paymentInfoList\":[{\"paymentProcessorID\":67,\"paymentActionList\":[{\"paymentType\":3,\"amount\":100,\"currencyID\":949,\"vatRate\":800}]}],\"QRdata\":\""+ qrModel.QRdata + "\"}");



        Request request = new Request.Builder()
                .url( URL_MAIN + "/payment" )
                .post(body)
                .addHeader("x-ibm-client-id", CLIENT_ID)
                .addHeader("x-ibm-client-secret", CLIENT_SECRET)
                .addHeader("content-type", "application/json")
                .addHeader("accept", "application/json")
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    Headers responseHeaders = response.headers();
                    for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                        System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                    }

                    System.out.println(responseBody.string());
                    // PAYMENT SUCCESS WILL BE HERE
                    paymentSuccessListener.onPaymentSuccess(qrModel, amount);
                } catch (IOException e) {
                    e.printStackTrace();
                    paymentSuccessListener.onError(e);
                }
            }
        });
    }
}
