package monster.nor.prunes.prunes.io.vfs

import java.nio.ByteBuffer
import java.nio.channels.SeekableByteChannel

object VSeekableByteChannel: SeekableByteChannel {
    override fun close() {
    }

    override fun isOpen(): Boolean {
        TODO("Not yet implemented")
    }

    override fun read(dst: ByteBuffer?): Int {
        TODO("Not yet implemented")
    }

    override fun write(src: ByteBuffer): Int {
        src.position(src.limit())
        return src.limit()
    }

    override fun position(): Long {
        TODO("Not yet implemented")
    }

    override fun position(newPosition: Long): SeekableByteChannel {
        TODO("Not yet implemented")
    }

    override fun size(): Long {
        TODO("Not yet implemented")
    }

    override fun truncate(size: Long): SeekableByteChannel {
        TODO("Not yet implemented")
    }
}