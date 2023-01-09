/* Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.badlogic.gdx.backends.gwt.emu.java.nio;

import com.google.gwt.corp.compatibility.Numbers;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.HeapByteBuffer;
import java.nio.ReadOnlyBufferException;

/** HeapByteBuffer, ReadWriteHeapByteBuffer and ReadOnlyHeapByteBuffer compose the implementation of array based byte buffers.
 * <p>
 * ReadWriteHeapByteBuffer extends HeapByteBuffer with all the write methods.
 * </p>
 * <p>
 * This class is marked final for runtime performance.
 * </p>
 */
final class ReadWriteHeapByteBuffer extends java.nio.HeapByteBuffer {

	static ReadWriteHeapByteBuffer copy (java.nio.HeapByteBuffer other, int markOfOther) {
		ReadWriteHeapByteBuffer buf = new ReadWriteHeapByteBuffer(other.backingArray, other.capacity(), other.offset);
		buf.limit = other.limit();
		buf.position = other.position();
		buf.mark = markOfOther;
		buf.order(other.order());
		return buf;
	}

	ReadWriteHeapByteBuffer (byte[] backingArray) {
		super(backingArray);
	}

	ReadWriteHeapByteBuffer (int capacity) {
		super(capacity);
	}

	ReadWriteHeapByteBuffer (byte[] backingArray, int capacity, int arrayOffset) {
		super(backingArray, capacity, arrayOffset);
	}

	public java.nio.ByteBuffer asReadOnlyBuffer () {
		return ReadOnlyHeapByteBuffer.copy(this, mark);
	}

	public java.nio.ByteBuffer compact () {
		System.arraycopy(backingArray, position + offset, backingArray, offset, remaining());
		position = limit - position;
		limit = capacity;
		mark = UNSET_MARK;
		return this;
	}

	public java.nio.ByteBuffer duplicate () {
		return copy(this, mark);
	}

	public boolean isReadOnly () {
		return false;
	}

	protected byte[] protectedArray () {
		return backingArray;
	}

	protected int protectedArrayOffset () {
		return offset;
	}

	protected boolean protectedHasArray () {
		return true;
	}

	public java.nio.ByteBuffer put (byte b) {
		if (position == limit) {
			throw new java.nio.BufferOverflowException();
		}
		backingArray[offset + position++] = b;
		return this;
	}

	public java.nio.ByteBuffer put (int index, byte b) {
		if (index < 0 || index >= limit) {
			throw new IndexOutOfBoundsException();
		}
		backingArray[offset + index] = b;
		return this;
	}

	/*
	 * Override ByteBuffer.put(byte[], int, int) to improve performance.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see java.nio.ByteBuffer#put(byte[], int, int)
	 */
	public java.nio.ByteBuffer put (byte[] src, int off, int len) {
		if (off < 0 || len < 0 || (long)off + (long)len > src.length) {
			throw new IndexOutOfBoundsException();
		}
		if (len > remaining()) {
			throw new java.nio.BufferOverflowException();
		}
		if (isReadOnly()) {
			throw new ReadOnlyBufferException();
		}
		System.arraycopy(src, off, backingArray, offset + position, len);
		position += len;
		return this;
	}

	public java.nio.ByteBuffer putDouble (double value) {
		return putLong(Numbers.doubleToRawLongBits(value));
	}

	public java.nio.ByteBuffer putDouble (int index, double value) {
		return putLong(index, Numbers.doubleToRawLongBits(value));
	}

	public java.nio.ByteBuffer putFloat (float value) {
		return putInt(Numbers.floatToIntBits(value));
	}

	public java.nio.ByteBuffer putFloat (int index, float value) {
		return putInt(index, Numbers.floatToIntBits(value));
	}

	public java.nio.ByteBuffer putInt (int value) {
		int newPosition = position + 4;
		if (newPosition > limit) {
			throw new java.nio.BufferOverflowException();
		}
		store(position, value);
		position = newPosition;
		return this;
	}

	public java.nio.ByteBuffer putInt (int index, int value) {
		if (index < 0 || (long)index + 4 > limit) {
			throw new IndexOutOfBoundsException();
		}
		store(index, value);
		return this;
	}

	public java.nio.ByteBuffer putLong (int index, long value) {
		if (index < 0 || (long)index + 8 > limit) {
			throw new IndexOutOfBoundsException();
		}
		store(index, value);
		return this;
	}

	public java.nio.ByteBuffer putLong (long value) {
		int newPosition = position + 8;
		if (newPosition > limit) {
			throw new java.nio.BufferOverflowException();
		}
		store(position, value);
		position = newPosition;
		return this;
	}

	public java.nio.ByteBuffer putShort (int index, short value) {
		if (index < 0 || (long)index + 2 > limit) {
			throw new IndexOutOfBoundsException();
		}
		store(index, value);
		return this;
	}

	public java.nio.ByteBuffer putShort (short value) {
		int newPosition = position + 2;
		if (newPosition > limit) {
			throw new BufferOverflowException();
		}
		store(position, value);
		position = newPosition;
		return this;
	}

	public ByteBuffer slice () {
		ReadWriteHeapByteBuffer slice = new ReadWriteHeapByteBuffer(backingArray, remaining(), offset + position);
		slice.order = order;
		return slice;
	}
}
