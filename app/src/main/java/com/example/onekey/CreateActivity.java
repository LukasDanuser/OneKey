package com.example.onekey;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;


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
        try {
            Intent intent = getIntent();
            HashMap value = (HashMap) intent.getSerializableExtra("tag");
            TextView myTextView = findViewById(R.id.textView);
            saveTagDataToFile(value);
            Intent intent1 = new Intent(CreateActivity.this, MainActivity.class);
            startActivity(intent1);
        } catch (Exception e) {
            notifyUser("No Key Scanned...");
        }
    }

    private void saveTagDataToFile(HashMap tagData) {
        try {
            EditText textField = findViewById(R.id.editTextTextPersonName);
            String input = textField.getText().toString();
            JSONObject json = new JSONObject();
            json.put("name", input);
            json.put("IDdec", tagData.get("IDdec"));
            json.put("ID", tagData.get("ID"));
            json.put("IDhex", tagData.get("IDhex"));
            json.put("IDrevdec", tagData.get("IDrevdec"));
            json.put("IDrevhex", tagData.get("IDrevhex"));

            FileWriter fileWriter = new FileWriter(getFilesDir().getPath() + "/nfc_data.json");
            fileWriter.write(String.valueOf(json));
            fileWriter.close();

        } catch (IOException e) {
            Log.e("NFC", "Error saving NFC tag data to file", e);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void cancel(View view) {
        Intent intent = new Intent(CreateActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void notifyUser(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}