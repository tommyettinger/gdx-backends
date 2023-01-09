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

import java.nio.FloatBuffer;
import java.nio.ReadOnlyBufferException;

/** FloatArrayBuffer, ReadWriteFloatArrayBuffer and ReadOnlyFloatArrayBuffer compose the implementation of array based float
 * buffers.
 * <p>
 * ReadOnlyFloatArrayBuffer extends FloatArrayBuffer with all the write methods throwing read only exception.
 * </p>
 * <p>
 * This class is marked final for runtime performance.
 * </p>
 */
final class ReadOnlyFloatArrayBuffer extends FloatArrayBuffer {

	static ReadOnlyFloatArrayBuffer copy (FloatArrayBuffer other, int markOfOther) {
		ReadOnlyFloatArrayBuffer buf = new ReadOnlyFloatArrayBuffer(other.capacity(), other.backingArray, other.offset);
		buf.limit = other.limit();
		buf.position = other.position();
		buf.mark = markOfOther;
		return buf;
	}

	ReadOnlyFloatArrayBuffer (int capacity, float[] backingArray, int arrayOffset) {
		super(capacity, backingArray, arrayOffset);
	}

	public java.nio.FloatBuffer asReadOnlyBuffer () {
		return duplicate();
	}

	public java.nio.FloatBuffer compact () {
		throw new java.nio.ReadOnlyBufferException();
	}

	public java.nio.FloatBuffer duplicate () {
		return copy(this, mark);
	}

	public boolean isReadOnly () {
		return true;
	}

	protected float[] protectedArray () {
		throw new java.nio.ReadOnlyBufferException();
	}

	protected int protectedArrayOffset () {
		throw new java.nio.ReadOnlyBufferException();
	}

	protected boolean protectedHasArray () {
		return false;
	}

	public java.nio.FloatBuffer put (float c) {
		throw new java.nio.ReadOnlyBufferException();
	}

	public java.nio.FloatBuffer put (int index, float c) {
		throw new java.nio.ReadOnlyBufferException();
	}

	public java.nio.FloatBuffer put (java.nio.FloatBuffer buf) {
		throw new java.nio.ReadOnlyBufferException();
	}

	public final java.nio.FloatBuffer put (float[] src, int off, int len) {
		throw new ReadOnlyBufferException();
	}

	public FloatBuffer slice () {
		return new ReadOnlyFloatArrayBuffer(remaining(), backingArray, offset + position);
	}

}
