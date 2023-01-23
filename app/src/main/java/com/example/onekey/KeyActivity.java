package com.example.onekey;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class KeyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key);
        NFCKeyCreator nfcKeyCreator = new NFCKeyCreator(this, getFilesDir().getPath() + "/nfc_data.json");
        nfcKeyCreator.createNFCKey();

    }

    public void done(View view) {
        Intent intent = new Intent(KeyActivity.this, MainActivity.class);
        startActivity(intent);
    }
}

