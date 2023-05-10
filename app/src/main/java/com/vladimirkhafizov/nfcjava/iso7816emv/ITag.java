package com.vladimirkhafizov.nfcjava.iso7816emv;

import com.vladimirkhafizov.nfcjava.enums.TagTypeEnum;
import com.vladimirkhafizov.nfcjava.enums.TagValueTypeEnum;


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
