/*******************************************************************************
 * This file is part of jHDF. A pure Java library for accessing HDF5 files.
 *
 * http://jhdf.io
 *
 * Copyright 2019 James Mudd
 *
 * MIT License see 'LICENSE' file
 ******************************************************************************/
package io.jhdf.object.message;

import java.nio.ByteBuffer;
import java.util.BitSet;

import io.jhdf.exceptions.HdfException;

public class ObjectReferenceCountMessage extends Message {

	private final int referenceCount;

	public ObjectReferenceCountMessage(ByteBuffer bb, BitSet flags) {
		super(flags);

		byte version = bb.get();
		if (version != 0) {
			throw new HdfException("Unreconized version = " + version);
		}

		referenceCount = bb.getInt();
	}

	public int getReferenceCount() {
		return referenceCount;
	}

}
