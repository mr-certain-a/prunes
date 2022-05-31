package monster.nor.prunes.prunes.io.vfs

import java.io.File
import java.lang.IllegalArgumentException
import java.net.URI
import java.nio.file.*

fun String.toVPath() = VPath(this)

class VPath(var path: String): Path {

    override fun getFileSystem(): FileSystem = VFileSystem

    override fun getFileName(): Path = VPath(hierarchy().last())

    override fun getParent(): Path? =
        when(hierarchy().size) {
            1 -> null
            else -> VPath(hierarchy().dropLast(1).joinToString("/"))
        }

    override fun resolve(other: Path): Path = resolve(other.toString())

    override fun resolve(other: String): Path = VPath("${slash()}$other")

    override fun toAbsolutePath(): Path = this

    override fun relativize(other: Path): Path {
        if(other.toString().startsWith(slash())) {
            return VPath(other.toString().substring(path.length))
        }
        throw IllegalArgumentException("'other' has different root")
    }

    override fun toFile(): File = VFile(path)

    override fun toString() = path

    // -- private functions --

    private fun hierarchy() = path.split("/")
    private fun slash() = if(path.last() != '/') "$path/" else path

    // -- not yet implemented --

    override fun compareTo(other: Path): Int {
        TODO("Not yet implemented")
    }
    override fun iterator(): MutableIterator<Path> =
        mutableListOf<Path>().apply {
            path.split("/").forEach {
                if(it.isNotEmpty())
                    add(VPath(it))
            }
        }.iterator()

    override fun register(
        watcher: WatchService,
        events: Array<out WatchEvent.Kind<*>>,
        vararg modifiers: WatchEvent.Modifier?
    ): WatchKey {
        TODO("Not yet implemented")
    }
    override fun register(watcher: WatchService, vararg events: WatchEvent.Kind<*>?): WatchKey {
        TODO("Not yet implemented")
    }
    override fun isAbsolute(): Boolean {
        TODO("Not yet implemented")
    }
    override fun getRoot(): Path {
        TODO("Not yet implemented")
    }
    override fun getNameCount(): Int {
        TODO("Not yet implemented")
    }
    override fun getName(index: Int): Path {
        TODO("Not yet implemented")
    }
    override fun subpath(beginIndex: Int, endIndex: Int): Path {
        TODO("Not yet implemented")
    }
    override fun startsWith(other: Path): Boolean {
        TODO("Not yet implemented")
    }
    override fun startsWith(other: String): Boolean {
        TODO("Not yet implemented")
    }
    override fun endsWith(other: Path): Boolean {
        TODO("Not yet implemented")
    }
    override fun endsWith(other: String): Boolean {
        TODO("Not yet implemented")
    }
    override fun normalize(): Path {
        TODO("Not yet implemented")
    }
    override fun resolveSibling(other: Path): Path {
        TODO("Not yet implemented")
    }
    override fun resolveSibling(other: String): Path {
        TODO("Not yet implemented")
    }
    override fun toUri(): URI {
        TODO("Not yet implemented")
    }
    override fun toRealPath(vararg options: LinkOption?): Path {
        TODO("Not yet implemented")
    }
}
