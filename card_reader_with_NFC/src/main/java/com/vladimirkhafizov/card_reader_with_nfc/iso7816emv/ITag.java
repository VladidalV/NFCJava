package com.vladimirkhafizov.card_reader_with_nfc.iso7816emv;

import com.vladimirkhafizov.card_reader_with_nfc.enums.TagTypeEnum;
import com.vladimirkhafizov.card_reader_with_nfc.enums.TagValueTypeEnum;


public interface ITag {

	enum Class {
		UNIVERSAL, APPLICATION, CONTEXT_SPECIFIC, PRIVATE
	}

	boolean isConstructed();

	byte[] getTagBytes();

	String getName();

	String getDescription();

	TagTypeEnum getType();

	TagValueTypeEnum getTagValueType();

	Class getTagClass();

	int getNumTagBytes();

}
