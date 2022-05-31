package monster.nor.prunes.prunes.io.vfs

import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.FileTime

class VBasicFileAttributes(private val directory: Boolean = false): BasicFileAttributes {
    var size = 0L

    override fun lastModifiedTime(): FileTime {
        TODO("Not yet implemented")
    }

    override fun lastAccessTime(): FileTime {
        TODO("Not yet implemented")
    }

    override fun creationTime(): FileTime {
        TODO("Not yet implemented")
    }

    override fun isRegularFile(): Boolean = !directory

    override fun isDirectory(): Boolean = directory

    override fun isSymbolicLink(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isOther(): Boolean {
        TODO("Not yet implemented")
    }

    override fun size(): Long = size

    override fun fileKey(): Any {
        TODO("Not yet implemented")
    }
}