package com.riiablo.mpq_bytebuf;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.TestInstance.Lifecycle.*;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.ImmediateEventExecutor;
import java.io.IOException;
import org.apache.commons.io.IOUtils;

import com.badlogic.gdx.files.FileHandle;

import com.riiablo.RiiabloTest;
import com.riiablo.logger.Level;
import com.riiablo.logger.LogManager;

import static com.riiablo.mpq_bytebuf.Mpq.DEFAULT_LOCALE;

class MpqBufferStreamTest extends RiiabloTest {
  @BeforeAll
  public static void before() {
    LogManager.setLevel("com.riiablo.mpq_bytebuf", Level.WARN);
    LogManager.setLevel("com.riiablo.mpq_bytebuf.MpqBufferStream", Level.TRACE);
    LogManager.setLevel("com.riiablo.mpq_bytebuf.DecoderExecutorGroup", Level.WARN);
  }

  @Test
  @DisplayName("dispose without MpqFileHandle reference leaks")
  void dispose_without_leaks() {
  }

  @Test
  @DisplayName("dispose with leaked MpqFileHandle references")
  void dispose_with_leaks() {
  }

  static class NestedMpqStreamTest extends MpqTest.NestedMpqTest {
    NestedMpqStreamTest(TestInfo testInfo) {
      super(testInfo);
    }

    void read(String in) throws IOException {
      DecoderExecutorGroup decoder = new DecoderExecutorGroup(4);
      try {
        EventExecutor executor = ImmediateEventExecutor.INSTANCE;
        MpqFileHandle handle = mpq.open(decoder, in, DEFAULT_LOCALE);
        final ByteBuf actual;
        try {
          MpqBufferStream stream = new MpqBufferStream(handle, executor, handle.sectorSize(), false);
          try {
            stream.initialize();
            actual = Unpooled.buffer(handle.FSize, handle.FSize);
            actual.writeBytes(stream, handle.FSize);
          } finally {
            IOUtils.closeQuietly(stream);
          }
        } finally {
          ReferenceCountUtil.release(handle);
        }

        FileHandle handle_out = testAsset(in);
        ByteBuf expected = Unpooled.wrappedBuffer(handle_out.readBytes());
        assertTrue(ByteBufUtil.equals(expected, actual));
      } finally {
        decoder.shutdownGracefully();
      }
    }
  }

  @Nested
  @TestInstance(PER_CLASS)
  class d2music extends NestedMpqStreamTest {
    d2music(TestInfo testInfo) {
      super(testInfo);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "data\\global\\music\\Act1\\andarielaction.wav",
        "data\\local\\video\\New_Bliz640x240.bik",
    })
    @Override
    void read(String in) throws IOException {
      super.read(in);
    }
  }

  @Nested
  @TestInstance(PER_CLASS)
  class d2speech extends NestedMpqStreamTest {
    d2speech(TestInfo testInfo) {
      super(testInfo);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "data\\local\\sfx\\Common\\Amazon\\Ama_needhelp.wav",
    })
    @Override
    void read(String in) throws IOException {
      super.read(in);
    }
  }

  @Nested
  @TestInstance(PER_CLASS)
  class d2data extends NestedMpqStreamTest {
    d2data(TestInfo testInfo) {
      super(testInfo);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "data\\global\\missiles\\blessedhammer.dcc",
    })
    @Override
    void read(String in) throws IOException {
      super.read(in);
    }
  }

  @Nested
  @TestInstance(PER_CLASS)
  class d2exp extends NestedMpqStreamTest {
    d2exp(TestInfo testInfo) {
      super(testInfo);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "data\\global\\ui\\AUTOMAP\\MaxiMap.dc6",
    })
    @Override
    void read(String in) throws IOException {
      super.read(in);
    }
  }
}
