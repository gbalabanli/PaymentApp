package com.portal.qr.paymentapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends AppCompatActivity {

    Context context;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        Button button_confirm = findViewById(R.id.button_confirm);
        final EditText edit_amount = findViewById(R.id.edit_amount);

        button_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount = edit_amount.getText().toString();
                if(amount.isEmpty()) {
                    showAlert("Are you sure you entered the amount correctly?");
                    return;
                }

                progress = ProgressDialog.show(context, "payment in action",
                        "wait until finished", true);

                RequestController.getQrSale(amount, new PaymentSuccessListener() {
                    @Override
                    public void onPaymentSuccess(QRModel qrModel, String amount) {
                        progress.dismiss();
                        int amountDecimal = Integer.parseInt(amount);
                        startPaymentSuccessActivity(amountDecimal, qrModel);
                    }

                    @Override
                    public void onError(Exception e) {
                        progress.dismiss();
                        showAlert(e.getMessage());
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(progress != null)
            progress.dismiss();

    }

    private void startPaymentSuccessActivity(int amount, QRModel qrModel) {
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.putExtra("amount", amount);
        intent.putExtra("qrData", qrModel.QRdata);
        startActivity(intent);
    }

    private void showAlert(String message) {
        AlertDialog.Builder builder;

        builder = new AlertDialog.Builder(context);

        builder.setTitle("Amount Failure!")
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}
