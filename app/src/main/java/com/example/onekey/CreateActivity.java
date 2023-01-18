package com.example.onekey;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class CreateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
    }

    public void scanKey(View view) {
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(CreateActivity.this);
        if (nfcAdapter == null) {
            // Device does not support NFC
            notifyUser("Device does not support NFC");
            return;
        }
        Intent intent = new Intent(CreateActivity.this, ScanActivity.class);
        startActivity(intent);
    }


    public void done(View view) {
        Intent intent = getIntent();
        String value = intent.getStringExtra("tag");
        TextView myTextView = findViewById(R.id.textView);
        myTextView.setText(value);

    }

    public void cancel(View view) {
        finish();
    }

    private void notifyUser(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}