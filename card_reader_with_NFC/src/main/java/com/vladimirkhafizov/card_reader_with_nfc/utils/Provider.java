package com.vladimirkhafizov.card_reader_with_nfc.utils;

import android.nfc.tech.IsoDep;
import android.util.Log;

import com.vladimirkhafizov.card_reader_with_nfc.BuildConfig;
import com.vladimirkhafizov.card_reader_with_nfc.parser.IProvider;

import java.io.IOException;

public class Provider implements IProvider{

    private IsoDep tagCom;

    public void setTagCom(final IsoDep tagCom) {
        this.tagCom = tagCom;
    }

    @Override
    public byte[] transceive(byte[] pCommand) {

        byte[] response = null;
        try {
            // send command to emv card
            response = tagCom.transceive(pCommand);
        } catch (IOException e) {
            if (BuildConfig.LOG_DEBUG_MODE) {
                Log.d("Provider IOException", e.getMessage());
            }
        }
        return response;
    }
}
