/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.datasketches.memory;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.testng.annotations.Test;

/**
 * @author Lee Rhodes
 */
public class LeafImplTest {
  static final ByteOrder LE = ByteOrder.LITTLE_ENDIAN;
  static final ByteOrder BE = ByteOrder.BIG_ENDIAN;


  @Test
  public void checkDirectLeafs() {
    long off = 0;
    long cap = 128;
    try (WritableDirectHandle wdh = WritableMemory.allocateDirect(cap)) {
      WritableMemory memLE = wdh.get();
      memLE.putShort(0, (short) 1);
      checkDirectImpl(memLE, off, cap);
    }
  }

  private static void checkDirectImpl(WritableMemory mem, long off, long cap) {
    assertEquals(mem.writableRegion(off, cap, LE).getShort(0), 1);
    assertEquals(mem.writableRegion(off, cap, BE).getShort(0), 256);
    assertEquals(mem.asWritableBuffer(LE).getShort(0), 1);
    assertEquals(mem.asWritableBuffer(BE).getShort(0), 256);

    assertTrue(mem.getByteBuffer() == null);
    assertTrue(mem.getTypeByteOrder() == BaseState.nativeByteOrder);
    assertTrue(mem.getMemoryRequestServer() != null);
    assertTrue(mem.isDirect());
    assertTrue(mem.getUnsafeObject() == null);
    assertTrue(mem.isValid() == true);

    WritableBuffer buf = mem.asWritableBuffer();

    assertEquals(buf.writableRegion(off, cap, LE).getShort(0), 1);
    assertEquals(buf.writableRegion(off, cap, BE).getShort(0), 256);
    assertEquals(buf.writableDuplicate(LE).getShort(0), 1);
    assertEquals(buf.writableDuplicate(BE).getShort(0), 256);

    assertTrue(buf.getByteBuffer() == null);
    assertTrue(buf.getTypeByteOrder() == BaseState.nativeByteOrder);
    assertTrue(buf.getMemoryRequestServer() != null);
    assertTrue(buf.isDirect());
    assertTrue(buf.getUnsafeObject() == null);
    assertTrue(buf.isValid() == true);

    WritableMemory nnMem = mem.writableRegion(off, cap, BaseState.nonNativeByteOrder);

    assertEquals(nnMem.writableRegion(off, cap, LE).getShort(0), 1);
    assertEquals(nnMem.writableRegion(off, cap, BE).getShort(0), 256);
    assertEquals(nnMem.asWritableBuffer(LE).getShort(0), 1);
    assertEquals(nnMem.asWritableBuffer(BE).getShort(0), 256);

    assertTrue(nnMem.getByteBuffer() == null);
    assertTrue(nnMem.getTypeByteOrder() == BaseState.nonNativeByteOrder);
    assertTrue(nnMem.getMemoryRequestServer() != null);
    assertTrue(nnMem.isDirect());
    assertTrue(nnMem.getUnsafeObject() == null);
    assertTrue(nnMem.isValid() == true);

    WritableBuffer nnBuf = mem.asWritableBuffer(BaseState.nonNativeByteOrder);

    assertEquals(nnBuf.writableRegion(off, cap, LE).getShort(0), 1);
    assertEquals(nnBuf.writableRegion(off, cap, BE).getShort(0), 256);
    assertEquals(nnBuf.writableDuplicate(LE).getShort(0), 1);
    assertEquals(nnBuf.writableDuplicate(BE).getShort(0), 256);

    assertTrue(nnBuf.getByteBuffer() == null);
    assertTrue(nnBuf.getTypeByteOrder() == BaseState.nonNativeByteOrder);
    assertTrue(nnBuf.getMemoryRequestServer() != null);
    assertTrue(nnBuf.isDirect());
    assertTrue(nnBuf.getUnsafeObject() == null);
    assertTrue(nnBuf.isValid() == true);
  }

  @Test
  public void checkMapLeafs() throws IOException {
    long off = 0;
    long cap = 128;
    File file = new File("TestFile2.bin");
    if (file.exists()) {
      try {
        java.nio.file.Files.delete(file.toPath());
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    assertTrue(file.createNewFile());
    assertTrue(file.setWritable(true, false)); //writable=true, ownerOnly=false
    assertTrue(file.isFile());
    file.deleteOnExit();  //comment out if you want to examine the file.

    try (WritableMapHandle wmh = WritableMemory.map(file, off, cap, BaseState.nativeByteOrder)) {
      WritableMemory mem = wmh.get();
      mem.putShort(0, (short) 1);
      assertEquals(mem.getByte(0), (byte) 1);
      checkMapImpl(mem, off, cap);
    }
  }

  private static void checkMapImpl(WritableMemory mem, long off, long cap) {
    assertEquals(mem.writableRegion(off, cap, LE).getShort(0), 1);
    assertEquals(mem.writableRegion(off, cap, BE).getShort(0), 256);
    assertEquals(mem.asWritableBuffer(LE).getShort(0), 1);
    assertEquals(mem.asWritableBuffer(BE).getShort(0), 256);

    assertTrue(mem.getByteBuffer() == null);
    assertTrue(mem.getTypeByteOrder() == BaseState.nativeByteOrder);
    assertTrue(mem.getMemoryRequestServer() == null);
    assertTrue(mem.isDirect());
    assertTrue(mem.getUnsafeObject() == null);
    assertTrue(mem.isValid() == true);

    WritableBuffer buf = mem.asWritableBuffer();

    assertEquals(buf.writableRegion(off, cap, LE).getShort(0), 1);
    assertEquals(buf.writableRegion(off, cap, BE).getShort(0), 256);
    assertEquals(buf.writableDuplicate(LE).getShort(0), 1);
    assertEquals(buf.writableDuplicate(BE).getShort(0), 256);

    assertTrue(buf.getByteBuffer() == null);
    assertTrue(buf.getTypeByteOrder() == BaseState.nativeByteOrder);
    assertTrue(buf.getMemoryRequestServer() == null);
    assertTrue(buf.isDirect());
    assertTrue(buf.getUnsafeObject() == null);
    assertTrue(buf.isValid() == true);

    WritableMemory nnMem = mem.writableRegion(off, cap, BaseState.nonNativeByteOrder);

    assertEquals(nnMem.writableRegion(off, cap, LE).getShort(0), 1);
    assertEquals(nnMem.writableRegion(off, cap, BE).getShort(0), 256);
    assertEquals(nnMem.asWritableBuffer(LE).getShort(0), 1);
    assertEquals(nnMem.asWritableBuffer(BE).getShort(0), 256);

    assertTrue(nnMem.getByteBuffer() == null);
    assertTrue(nnMem.getTypeByteOrder() == BaseState.nonNativeByteOrder);
    assertTrue(nnMem.getMemoryRequestServer() == null);
    assertTrue(nnMem.isDirect());
    assertTrue(nnMem.getUnsafeObject() == null);
    assertTrue(nnMem.isValid() == true);

    WritableBuffer nnBuf = mem.asWritableBuffer(BaseState.nonNativeByteOrder);

    assertEquals(nnBuf.writableRegion(off, cap, LE).getShort(0), 1);
    assertEquals(nnBuf.writableRegion(off, cap, BE).getShort(0), 256);
    assertEquals(nnBuf.writableDuplicate(LE).getShort(0), 1);
    assertEquals(nnBuf.writableDuplicate(BE).getShort(0), 256);

    assertTrue(nnBuf.getByteBuffer() == null);
    assertTrue(nnBuf.getTypeByteOrder() == BaseState.nonNativeByteOrder);
    assertTrue(nnBuf.getMemoryRequestServer() == null);
    assertTrue(nnBuf.isDirect());
    assertTrue(nnBuf.getUnsafeObject() == null);
    assertTrue(nnBuf.isValid() == true);
  }

  @Test
  public void checkByteBufferLeafs() {
    long off = 0;
    long cap = 128;
    ByteBuffer bb = ByteBuffer.allocate((int)cap);
    bb.order(ByteOrder.nativeOrder());
    bb.putShort(0, (short) 1);
    WritableMemory mem = WritableMemory.wrap(bb);
    checkByteBufferImpl(mem, off, cap, false);

    ByteBuffer dbb = ByteBuffer.allocateDirect((int)cap);
    dbb.order(ByteOrder.nativeOrder());
    dbb.putShort(0, (short) 1);
    mem = WritableMemory.wrap(dbb);
    checkByteBufferImpl(mem, off, cap, true);
  }

  private static void checkByteBufferImpl(WritableMemory mem, long off, long cap, boolean direct) {
    assertEquals(mem.writableRegion(off, cap, LE).getShort(0), 1);
    assertEquals(mem.writableRegion(off, cap, BE).getShort(0), 256);
    assertEquals(mem.asWritableBuffer(LE).getShort(0), 1);
    assertEquals(mem.asWritableBuffer(BE).getShort(0), 256);

    assertTrue(mem.getByteBuffer() != null);
    assertTrue(mem.getTypeByteOrder() == BaseState.nativeByteOrder);
    assertTrue(mem.getMemoryRequestServer() == null);
    Object obj = mem.getUnsafeObject();
    if (direct) {
      assertTrue(mem.isDirect());
      assertNull(obj);
    } else {
      assertFalse(mem.isDirect());
      assertNotNull(obj);
    }
    assertTrue(mem.isValid() == true);

    WritableBuffer buf = mem.asWritableBuffer();

    assertEquals(buf.writableRegion(off, cap, LE).getShort(0), 1);
    assertEquals(buf.writableRegion(off, cap, BE).getShort(0), 256);
    assertEquals(buf.writableDuplicate(LE).getShort(0), 1);
    assertEquals(buf.writableDuplicate(BE).getShort(0), 256);

    assertTrue(buf.getByteBuffer() != null);
    assertTrue(buf.getTypeByteOrder() == BaseState.nativeByteOrder);
    assertTrue(buf.getMemoryRequestServer() == null);
    obj = buf.getUnsafeObject();
    if (direct) {
      assertTrue(buf.isDirect());
      assertNull(obj);
    } else {
      assertFalse(buf.isDirect());
      assertNotNull(obj);
    }
    assertTrue(buf.isValid() == true);

    WritableMemory nnMem = mem.writableRegion(off, cap, BaseState.nonNativeByteOrder);

    assertEquals(nnMem.writableRegion(off, cap, LE).getShort(0), 1);
    assertEquals(nnMem.writableRegion(off, cap, BE).getShort(0), 256);
    assertEquals(nnMem.asWritableBuffer(LE).getShort(0), 1);
    assertEquals(nnMem.asWritableBuffer(BE).getShort(0), 256);

    assertTrue(nnMem.getByteBuffer() != null);
    assertTrue(nnMem.getTypeByteOrder() == BaseState.nonNativeByteOrder);
    assertTrue(nnMem.getMemoryRequestServer() == null);
    obj = nnMem.getUnsafeObject();
    if (direct) {
      assertTrue(nnMem.isDirect());
      assertNull(obj);
    } else {
      assertFalse(nnMem.isDirect());
      assertNotNull(obj);
    }
    assertTrue(nnMem.isValid() == true);

    WritableBuffer nnBuf = mem.asWritableBuffer(BaseState.nonNativeByteOrder);

    assertEquals(nnBuf.writableRegion(off, cap, LE).getShort(0), 1);
    assertEquals(nnBuf.writableRegion(off, cap, BE).getShort(0), 256);
    assertEquals(nnBuf.writableDuplicate(LE).getShort(0), 1);
    assertEquals(nnBuf.writableDuplicate(BE).getShort(0), 256);

    assertTrue(nnBuf.getByteBuffer() != null);
    assertTrue(nnBuf.getTypeByteOrder() == BaseState.nonNativeByteOrder);
    assertTrue(nnBuf.getMemoryRequestServer() == null);
    obj = nnBuf.getUnsafeObject();
    if (direct) {
      assertTrue(nnBuf.isDirect());
      assertNull(obj);
    } else {
      assertFalse(nnBuf.isDirect());
      assertNotNull(obj);
    }
    assertTrue(nnBuf.isValid() == true);
  }

  @Test
  public void checkHeapLeafs() {
    long off = 0;
    long cap = 128;
    WritableMemory mem = WritableMemory.allocate((int)cap);
    mem.putShort(0, (short) 1);
    checkHeapImpl(mem, off, cap);
  }

  private static void checkHeapImpl(WritableMemory mem, long off, long cap) {
    assertEquals(mem.writableRegion(off, cap, LE).getShort(0), 1);
    assertEquals(mem.writableRegion(off, cap, BE).getShort(0), 256);
    assertEquals(mem.asWritableBuffer(LE).getShort(0), 1);
    assertEquals(mem.asWritableBuffer(BE).getShort(0), 256);

    assertTrue(mem.getByteBuffer() == null);
    assertTrue(mem.getTypeByteOrder() == BaseState.nativeByteOrder);
    assertTrue(mem.getMemoryRequestServer() == null);
    assertFalse(mem.isDirect());
    assertTrue(mem.getUnsafeObject() != null);
    assertTrue(mem.isValid() == true);

    WritableBuffer buf = mem.asWritableBuffer();

    assertEquals(buf.writableRegion(off, cap, LE).getShort(0), 1);
    assertEquals(buf.writableRegion(off, cap, BE).getShort(0), 256);
    assertEquals(buf.writableDuplicate(LE).getShort(0), 1);
    assertEquals(buf.writableDuplicate(BE).getShort(0), 256);

    assertTrue(buf.getByteBuffer() == null);
    assertTrue(buf.getTypeByteOrder() == BaseState.nativeByteOrder);
    assertTrue(buf.getMemoryRequestServer() == null);
    assertFalse(buf.isDirect());
    assertTrue(buf.getUnsafeObject() != null);
    assertTrue(buf.isValid() == true);

    WritableMemory nnMem = mem.writableRegion(off, cap, BaseState.nonNativeByteOrder);

    assertEquals(nnMem.writableRegion(off, cap, LE).getShort(0), 1);
    assertEquals(nnMem.writableRegion(off, cap, BE).getShort(0), 256);
    assertEquals(nnMem.asWritableBuffer(LE).getShort(0), 1);
    assertEquals(nnMem.asWritableBuffer(BE).getShort(0), 256);

    assertTrue(nnMem.getByteBuffer() == null);
    assertTrue(nnMem.getTypeByteOrder() == BaseState.nonNativeByteOrder);
    assertTrue(nnMem.getMemoryRequestServer() == null);
    assertFalse(nnMem.isDirect());
    assertTrue(nnMem.getUnsafeObject() != null);
    assertTrue(nnMem.isValid() == true);

    WritableBuffer nnBuf = mem.asWritableBuffer(BaseState.nonNativeByteOrder);

    assertEquals(nnBuf.writableRegion(off, cap, LE).getShort(0), 1);
    assertEquals(nnBuf.writableRegion(off, cap, BE).getShort(0), 256);
    assertEquals(nnBuf.writableDuplicate(LE).getShort(0), 1);
    assertEquals(nnBuf.writableDuplicate(BE).getShort(0), 256);

    assertTrue(nnBuf.getByteBuffer() == null);
    assertTrue(nnBuf.getTypeByteOrder() == BaseState.nonNativeByteOrder);
    assertTrue(nnBuf.getMemoryRequestServer() == null);
    assertFalse(nnBuf.isDirect());
    assertTrue(nnBuf.getUnsafeObject() != null);
    assertTrue(nnBuf.isValid() == true);
  }

}
