package monster.nor.prunes.prunes.io.vfs

import java.io.IOException
import java.io.OutputStream
import java.net.URI
import java.nio.channels.SeekableByteChannel
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.FileAttribute
import java.nio.file.attribute.FileAttributeView
import java.nio.file.spi.FileSystemProvider

object VFileSystemProvider: FileSystemProvider() {
    private val map = mutableMapOf<String, VBasicFileAttributes>()

    fun clear() {
        map.clear()
    }

    fun showAll() {
        map.forEach { (path, attr) ->
            println("${if(attr.isDirectory)"D " else "F "}$path, size=${attr.size}")
        }
    }

    fun exists(path: Path) = map.contains(path.toString())

    private fun Path.attr() = map[toString()]

    override fun newOutputStream(path: Path, vararg options: OpenOption?): OutputStream =
        VOutputStream(VBasicFileAttributes()).apply {
            map[path.toString()] = basicFileAttributes
        }

    override fun newByteChannel(
        path: Path?,
        options: MutableSet<out OpenOption>?,
        vararg attrs: FileAttribute<*>?
    ): SeekableByteChannel = VSeekableByteChannel

    override fun createDirectory(dir: Path, vararg attrs: FileAttribute<*>?) {
        checkMakeFailDirectory(dir)
        VOutputStream(VBasicFileAttributes(true)).apply {
            map[dir.toString()] = basicFileAttributes
        }
    }

    override fun delete(path: Path) {
        checkDeleteFail(path)
        map.remove(path.toString())
    }

    override fun copy(source: Path, target: Path, vararg options: CopyOption?) {
        source.attr()?.let {
            map[target.toString()] = it
        } ?: throw IOException("source is not found. $source")
    }

    override fun checkAccess(path: Path, vararg modes: AccessMode?) {
        when(exists(path)) {
            true -> Unit
            else -> {
                throw NoSuchFileException(path.toString())
            }
        }
    }

    override fun <A : BasicFileAttributes?> readAttributes(
        path: Path,
        type: Class<A>?,
        vararg options: LinkOption?
    ): A {
        @Suppress("UNCHECKED_CAST")
        return map[path.toString()] as A
    }


    override fun getScheme(): String {
        TODO("Not yet implemented")
    }
    override fun newFileSystem(uri: URI?, env: MutableMap<String, *>?): FileSystem {
        TODO("Not yet implemented")
    }
    override fun getFileSystem(uri: URI?): FileSystem {
        TODO("Not yet implemented")
    }
    override fun getPath(uri: URI): Path {
        TODO("Not yet implemented")
    }
    override fun newDirectoryStream(dir: Path?, filter: DirectoryStream.Filter<in Path>?): DirectoryStream<Path> {
        TODO("Not yet implemented")
    }
    override fun move(source: Path?, target: Path?, vararg options: CopyOption?) {
        TODO("Not yet implemented")
    }
    override fun isSameFile(path: Path?, path2: Path?): Boolean {
        TODO("Not yet implemented")
    }
    override fun isHidden(path: Path?): Boolean {
        TODO("Not yet implemented")
    }
    override fun getFileStore(path: Path?): FileStore {
        TODO("Not yet implemented")
    }
    override fun <V : FileAttributeView?> getFileAttributeView(
        path: Path?,
        type: Class<V>?,
        vararg options: LinkOption?
    ): V {
        TODO("Not yet implemented")
    }
    override fun readAttributes(
        path: Path?,
        attributes: String?,
        vararg options: LinkOption?
    ): MutableMap<String, Any> {
        TODO("Not yet implemented")
    }
    override fun setAttribute(path: Path?, attribute: String?, value: Any?, vararg options: LinkOption?) {
        TODO("Not yet implemented")
    }
}
