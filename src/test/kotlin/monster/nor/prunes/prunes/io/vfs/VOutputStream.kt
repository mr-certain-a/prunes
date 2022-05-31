package monster.nor.prunes.prunes.io.vfs

import java.io.OutputStream

class VOutputStream(val basicFileAttributes: VBasicFileAttributes): OutputStream() {
    override fun write(b: Int) {
        basicFileAttributes.size++
    }
}
