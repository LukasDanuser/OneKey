package com.example.onekey;

import android.app.Activity;
import android.content.Context;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;

public class NFCKeyCreator {
    private final NfcAdapter nfcAdapter;
    private final Context context;
    private String jsonData;

    public NFCKeyCreator(Context context, String filepath) {
        this.context = context;
        // Initialize NFC adapter
        nfcAdapter = NfcAdapter.getDefaultAdapter(context);

        // Read JSON file containing key data
        try {
            File file = new File(filepath);
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();
            jsonData = new String(data, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createNFCKey() {
        try {
            JSONObject json = new JSONObject(jsonData);
            String keyData = json.getString("IDdec");

            // Create an NDEF record with the key data
            NdefRecord ndefRecord = NdefRecord.createMime("application/com.example.key", keyData.getBytes(StandardCharsets.US_ASCII));
            NdefMessage ndefMessage = new NdefMessage(ndefRecord);

            // Set the NDEF message to be beamable
            nfcAdapter.setNdefPushMessage(ndefMessage, (Activity) context);
            notifyUser(new String(ndefMessage.toByteArray(), StandardCharsets.US_ASCII));
            notifyUser(ndefRecord.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void notifyUser(String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
