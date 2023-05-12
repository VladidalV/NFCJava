package com.vladimirkhafizov.card_reader_with_nfc;

import com.vladimirkhafizov.card_reader_with_nfc.model.EmvCard;

public class NfcCardResponse {

  private EmvCard emvCard;
  private NfcCardError error;

  private NfcCardResponse(EmvCard emvCard, NfcCardError error) {
    this.emvCard = emvCard;
    this.error = error;
  }

  public static NfcCardResponse createResponse(EmvCard emvCard) {
    return new NfcCardResponse(emvCard, null);
  }

  public static NfcCardResponse createError(NfcCardError error) {
    return new NfcCardResponse(null, error);
  }

  public EmvCard getEmvCard() {
    return emvCard;
  }

  public NfcCardError getError() {
    return error;
  }
}
