package com.example.onekey;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class KeyActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] intentFilters;

    public static String readJsonFile(String filePath) {
        byte[] jsonData = {};
        try {
            jsonData = Files.readAllBytes(Paths.get(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(jsonData, StandardCharsets.UTF_8);
    }

    public static boolean writeNdefMessage(NdefMessage message, Tag tag) {
        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();
                ndef.writeNdefMessage(message);
                ndef.close();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            notifyUser("This device does not support NFC.");
            finish();
            return;
        }

        pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_MUTABLE);
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndef.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }
        intentFilters = new IntentFilter[]{ndef};

        handleIntent(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if (tag != null) {
                String jsonString = readJsonFile(getFilesDir().getPath() + "/nfc_data.json");
                try {
                    JSONObject json = new JSONObject(jsonString);
                    String data = json.getString("data");
                    NdefRecord record = NdefRecord.createTextRecord("en", data);
                    NdefMessage message = new NdefMessage(new NdefRecord[]{record});
                    if (writeNdefMessage(message, tag)) {
                        notifyUser("NFC Tag written successfully!");
                    } else {
                        notifyUser("Error writing NFC Tag");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void done(View view) {
        Intent intent = new Intent(KeyActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void notifyUser(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
