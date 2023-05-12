package com.vladimirkhafizov.nfcjava;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.widget.TextView;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.vladimirkhafizov.card_reader_with_nfc.NfcCardManager;
import com.vladimirkhafizov.card_reader_with_nfc.NfcCardReader;
import com.vladimirkhafizov.card_reader_with_nfc.NfcCardResponse;
import com.vladimirkhafizov.card_reader_with_nfc.R;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private NfcCardManager nfcCardManager;
    private NfcCardReader nfcCardReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.text);
        nfcCardManager = new NfcCardManager(this);
        nfcCardReader = new NfcCardReader();
    }

    @Override
    protected void onPause() {
        super.onPause();
        textView.setText(null);
        nfcCardManager.disableDispatch();
    }

    @Override
    protected void onResume() {
        super.onResume();
        nfcCardManager.enableDispatch();
        handleNfcIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleNfcIntent(intent);
    }

    private void handleNfcIntent(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag != null) {
            NfcCardResponse cardResponse = nfcCardReader.readCard(tag);
            if (cardResponse != null && cardResponse.getEmvCard() != null) {
                String pan = cardResponse.getEmvCard().getCardNumber();
                String maskedPan = maskPAN(pan);
                String hs1 = hashPAN(pan, "HSA1");
                String sha256 = hashPAN(pan, "SHA-256");
                textView.setText("Номер карты: " + maskedPan +
                        "\nДействительна до: " + cardResponse.getEmvCard().getExpireDate() +
                        "\nHSA1: " + hs1 +
                        "\nSHA256: " + sha256);
            }
        }
    }

    private String maskPAN(String pan) {
        return pan.replaceAll("\\d(?=\\d{4})", "*");
    }

    private String hashPAN(String pan, String algorithm) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] digest = md.digest(pan.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}