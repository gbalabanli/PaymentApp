package com.portal.qr.paymentapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PaymentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        TextView text_receipt = findViewById(R.id.text_receipt);
        Button button_restart = findViewById(R.id.button_restart);
        button_restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMainActivity();
            }
        });

        Intent iin= getIntent();
        Bundle b = iin.getExtras();
        int amount = (int) b.get("amount");
        String qrData = (String) b.get("qrData");

        text_receipt.setText(Html.fromHtml("Amount: ..........................................................." + amount + " TRY<br><br>"
                                                + "QRdata: " + qrData+ " <br><br>"
                                                ));
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}
