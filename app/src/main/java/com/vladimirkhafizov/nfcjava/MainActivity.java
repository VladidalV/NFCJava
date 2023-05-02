package com.vladimirkhafizov.nfcjava;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    //отображение номера NFC-карты на экране
    private TextView mCardNumberTextView;
    //для очистки поля вывода номера NFC-карты;
    private Button mClearButton;
    //для взаимодействия с NFC-считывателем
    private NfcAdapter mNfcAdapter;
    //для обработки событий NFC
    private PendingIntent mPendingIntent;
    //для обработки событий NFC.
    private IntentFilter[] mIntentFiltersArray;

    private boolean mIsNfcEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCardNumberTextView = findViewById(R.id.card_number_text_view);
        mClearButton = findViewById(R.id.clear_button);
        mClearButton.setOnClickListener(v -> clearCardNumber());

        // Проверяем, поддерживается ли NFC на устройстве
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            // NFC не поддерживается на устройстве, выводим сообщение и закрываем приложение
            Toast.makeText(this, R.string.not_available, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Проверяем, включен ли NFC на устройстве
        if (!mNfcAdapter.isEnabled()) {
            // NFC выключен, выводим диалоговое окно для его включения
            new AlertDialog.Builder(this)
                    .setTitle(R.string.nfc_turn_off)
                    .setMessage(R.string.maybe_turn_on)
                    .setPositiveButton(R.string.yes, (dialog, which) -> {
                        Intent enableNfcIntent = new Intent(Settings.ACTION_NFC_SETTINGS);
                        startActivity(enableNfcIntent);
                    })
                    .setNegativeButton(R.string.no, null)
                    .show();
        } else {
            mIsNfcEnabled = true;
        }

        // Создаем PendingIntent для обработки событий NFC
        Intent nfcIntent = new Intent(this, getClass());
        nfcIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mPendingIntent = PendingIntent.getActivity(this, 0, nfcIntent, 0);

        // Создаем IntentFilter для обработки событий NFC
        IntentFilter ndefIntentFilter = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndefIntentFilter.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            Log.e(TAG, "Failed to add MIME type", e);
        }
        mIntentFiltersArray = new IntentFilter[]{ndefIntentFilter};
    }

    private void clearCardNumber() {
        mCardNumberTextView.setText("");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mIsNfcEnabled) {
            // Включаем передний план для обнаружения NFC-карты
            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, mIntentFiltersArray, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mIsNfcEnabled) {
            // Отключаем передний план
            mNfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            String type = intent.getType();
            if ("text/plain".equals(type)) {
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                if (tag != null) {
                    Ndef ndef = Ndef.get(tag);
                    if (ndef != null) {
                        try {
                            ndef.connect();
                            NdefMessage ndefMessage = ndef.getNdefMessage();
                            if (ndefMessage != null) {
                                NdefRecord[] records = ndefMessage.getRecords();
                                if (records != null && records.length > 0) {
                                    NdefRecord record = records[0];
                                    byte[] payloadBytes = record.getPayload();
                                    String payload = new String(payloadBytes, Charset.forName("UTF-8"));
                                    Log.d("MyApp", "NDEF_DISCOVERED intent received.");
                                    Log.d("MyApp", "MIME type: " + type);
                                    Log.d("MyApp", "Payload: " + payload);
                                    // Вывод номера карты на экран
                                    TextView cardNumberTextView = findViewById(R.id.card_number_text_view);
                                    cardNumberTextView.setText(payload);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                ndef.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }
}