/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.badlogic.gdx.backends.gwt.emu.java.nio;

import java.nio.ByteBuffer;
import java.nio.HeapByteBuffer;
import java.nio.ReadOnlyBufferException;

/** HeapByteBuffer, ReadWriteHeapByteBuffer and ReadOnlyHeapByteBuffer compose the implementation of array based byte buffers.
 * <p>
 * ReadOnlyHeapByteBuffer extends HeapByteBuffer with all the write methods throwing read only exception.
 * </p>
 * <p>
 * This class is marked final for runtime performance.
 * </p>
 */
final class ReadOnlyHeapByteBuffer extends java.nio.HeapByteBuffer {

	static ReadOnlyHeapByteBuffer copy (java.nio.HeapByteBuffer other, int markOfOther) {
		ReadOnlyHeapByteBuffer buf = new ReadOnlyHeapByteBuffer(other.backingArray, other.capacity(), other.offset);
		buf.limit = other.limit();
		buf.position = other.position();
		buf.mark = markOfOther;
		buf.order(other.order());
		return buf;
	}

	ReadOnlyHeapByteBuffer (byte[] backingArray, int capacity, int arrayOffset) {
		super(backingArray, capacity, arrayOffset);
	}

	public java.nio.ByteBuffer asReadOnlyBuffer () {
		return copy(this, mark);
	}

	public java.nio.ByteBuffer compact () {
		throw new ReadOnlyBufferException();
	}

	public java.nio.ByteBuffer duplicate () {
		return copy(this, mark);
	}

	public boolean isReadOnly () {
		return true;
	}

	protected byte[] protectedArray () {
		throw new ReadOnlyBufferException();
	}

	protected int protectedArrayOffset () {
		throw new ReadOnlyBufferException();
	}

	protected boolean protectedHasArray () {
		return false;
	}

	public java.nio.ByteBuffer put (byte b) {
		throw new ReadOnlyBufferException();
	}

	public java.nio.ByteBuffer put (int index, byte b) {
		throw new ReadOnlyBufferException();
	}

	public java.nio.ByteBuffer put (byte[] src, int off, int len) {
		throw new ReadOnlyBufferException();
	}

	public java.nio.ByteBuffer putDouble (double value) {
		throw new ReadOnlyBufferException();
	}

	public java.nio.ByteBuffer putDouble (int index, double value) {
		throw new ReadOnlyBufferException();
	}

	public java.nio.ByteBuffer putFloat (float value) {
		throw new ReadOnlyBufferException();
	}

	public java.nio.ByteBuffer putFloat (int index, float value) {
		throw new ReadOnlyBufferException();
	}

	public java.nio.ByteBuffer putInt (int value) {
		throw new ReadOnlyBufferException();
	}

	public java.nio.ByteBuffer putInt (int index, int value) {
		throw new ReadOnlyBufferException();
	}

	public java.nio.ByteBuffer putLong (int index, long value) {
		throw new ReadOnlyBufferException();
	}

	public java.nio.ByteBuffer putLong (long value) {
		throw new ReadOnlyBufferException();
	}

	public java.nio.ByteBuffer putShort (int index, short value) {
		throw new ReadOnlyBufferException();
	}

	public java.nio.ByteBuffer putShort (short value) {
		throw new ReadOnlyBufferException();
	}

	public java.nio.ByteBuffer put (java.nio.ByteBuffer buf) {
		throw new ReadOnlyBufferException();
	}

	public ByteBuffer slice () {
		ReadOnlyHeapByteBuffer slice = new ReadOnlyHeapByteBuffer(backingArray, remaining(), offset + position);
		slice.order = order;
		return slice;
	}
}
