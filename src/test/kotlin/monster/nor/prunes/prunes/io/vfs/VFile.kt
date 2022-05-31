package monster.nor.prunes.prunes.io.vfs

import java.io.*
import java.net.URI
import java.net.URISyntaxException
import java.nio.file.Path

class VFile(pathname: String): File(pathname) {

    private val fs = VFileSystem

    private var path: String = pathname

    private enum class PathStatus {
        INVALID, CHECKED
    }

    @Transient
    private var status: PathStatus? = null

    fun isInvalid(): Boolean {
        if (status == null) {
            status = if (this.path.indexOf('\u0000') < 0) PathStatus.CHECKED else PathStatus.INVALID
        }
        return status == PathStatus.INVALID
    }

    @Transient
    private var prefixLength = 0

    fun getPrefixLength(): Int {
        return prefixLength
    }

    val separatorChar = VFileSystem.separator

    val separator = "" + separatorChar

    val pathSeparatorChar = ':'

    val pathSeparator = "" + pathSeparatorChar

    private constructor(pathname: String, prefixLength: Int) : this(pathname) {
        this.path = pathname
        this.prefixLength = prefixLength
    }

    private constructor(child: String, parent: VFile) : this("") {
        this.path = parent.path.toVPath().resolve(child).toString()
        this.prefixLength = parent.prefixLength
    }

    override fun getName(): String {
        val index = path.lastIndexOf(separatorChar)
        return if (index < prefixLength) path.substring(prefixLength) else path.substring(index + 1)
    }

    override fun getParent(): String? {
        val index = path.lastIndexOf(separatorChar)
        return if (index < prefixLength) {
            if (prefixLength > 0 && path.length > prefixLength) path.substring(0, prefixLength) else null
        } else path.substring(0, index)
    }

    override fun getParentFile(): File? {
        val p = this.parent ?: return null
        return VFile(p, this.prefixLength)
    }

    override fun getPath(): String = path

    override fun isAbsolute(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getAbsolutePath(): String {
        TODO("Not yet implemented")
    }

    override fun getAbsoluteFile(): File {
        TODO("Not yet implemented")
    }

    override fun getCanonicalPath(): String {
        TODO("Not yet implemented")
    }

    override fun getCanonicalFile(): File {
        TODO("Not yet implemented")
    }

    private fun slashify(path: String, isDirectory: Boolean): String {
        var p = path
        if (File.separatorChar != '/') p = p.replace(File.separatorChar, '/')
        if (!p.startsWith("/")) p = "/$p"
        if (!p.endsWith("/") && isDirectory) p = "$p/"
        return p
    }

    override fun toURI(): URI? {
        return try {
            val f = absoluteFile
            var sp = slashify(f.path, f.isDirectory)
            if (sp.startsWith("//")) sp = "//$sp"
            URI("file", null, sp, null)
        } catch (x: URISyntaxException) {
            throw Error(x) // Can't happen
        }
    }

    override fun canRead(): Boolean {
        TODO("Not yet implemented")
    }

    override fun canWrite(): Boolean {
        TODO("Not yet implemented")
    }

    override fun exists(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isDirectory(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isFile(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isHidden(): Boolean {
        TODO("Not yet implemented")
    }

    override fun lastModified(): Long {
        TODO("Not yet implemented")
    }

    override fun length(): Long {
        TODO("Not yet implemented")
    }

    override fun createNewFile(): Boolean {
        TODO("Not yet implemented")
    }

    private fun conditional(condition: Boolean, closure: ()->Unit): Boolean =
        when(condition) {
            true -> { closure(); true }
            else -> false
        }

    override fun delete(): Boolean =
        conditional(VFileSystemProvider.exists(toPath())) {
            VFileSystemProvider.delete(toPath())
        }

    override fun deleteOnExit() {
        TODO("Not yet implemented")
    }

    override fun list(): Array<String>? {
        TODO("Not yet implemented")
    }

    override fun list(filter: FilenameFilter?): Array<String>? {
        val names = list()
        if (names == null || filter == null) {
            return names
        }
        val v: MutableList<String> = ArrayList()
        for (i in names.indices) {
            if (filter.accept(this, names[i])) {
                v.add(names[i])
            }
        }
        return v.toTypedArray()
    }

    override fun listFiles(): Array<File?>? {
        val ss = list() ?: return null
        val n = ss.size
        val fs = arrayOfNulls<File>(n)
        for (i in 0 until n) {
            fs[i] = VFile(ss[i], this)
        }
        return fs
    }

    override fun listFiles(filter: FilenameFilter?): Array<VFile?>? {
        val ss = list() ?: return null
        val files = ArrayList<VFile>()
        for (s in ss) if (filter == null || filter.accept(this, s)) files.add(VFile(s, this))
        return files.toTypedArray()
    }

    override fun listFiles(filter: FileFilter?): Array<File?>? {
        val ss = list() ?: return null
        val files = ArrayList<File>()
        for (s in ss) {
            val f = VFile(s, this)
            if (filter == null || filter.accept(f)) files.add(f)
        }
        return files.toTypedArray()
    }

    override fun mkdir(): Boolean {
        TODO("Not yet implemented")
    }

    override fun mkdirs(): Boolean {
        if (exists()) {
            return false
        }
        if (mkdir()) {
            return true
        }
        val canonFile: File? = try {
            canonicalFile
        } catch (e: IOException) {
            return false
        }
        val parent = canonFile!!.parentFile
        return parent != null && (parent.mkdirs() || parent.exists()) &&
                canonFile.mkdir()
    }

    override fun renameTo(dest: File?): Boolean {
        TODO("Not yet implemented")
    }

    override fun setLastModified(time: Long): Boolean {
        TODO("Not yet implemented")
    }

    override fun setReadOnly(): Boolean {
        TODO("Not yet implemented")
    }

    override fun setWritable(writable: Boolean, ownerOnly: Boolean): Boolean {
        TODO("Not yet implemented")
    }

    override fun setWritable(writable: Boolean): Boolean {
        return setWritable(writable, true)
    }

    override fun setReadable(readable: Boolean, ownerOnly: Boolean): Boolean {
        TODO("Not yet implemented")
    }

    override fun setReadable(readable: Boolean): Boolean {
        return setReadable(readable, true)
    }

    override fun setExecutable(executable: Boolean, ownerOnly: Boolean): Boolean {
        TODO("Not yet implemented")
    }

    override fun setExecutable(executable: Boolean): Boolean {
        return setExecutable(executable, true)
    }

    override fun canExecute(): Boolean {
        TODO("Not yet implemented")
    }

    fun listRoots(): Array<VFile?>? {
        TODO("Not yet implemented")
    }

    override fun getTotalSpace(): Long {
        TODO("Not yet implemented")
    }

    override fun getFreeSpace(): Long {
        TODO("Not yet implemented")
    }

    override fun getUsableSpace(): Long {
        TODO("Not yet implemented")
    }

    private fun createTempFile(
        @Suppress("UNUSED_PARAMETER")
        prefix: String,
        @Suppress("UNUSED_PARAMETER")
        suffix: String?,
        @Suppress("UNUSED_PARAMETER")
        directory: File?
    ): VFile {
        TODO("Not yet implemented")
    }

    fun createTempFile(prefix: String, suffix: String?): VFile {
        return createTempFile(prefix, suffix, null)
    }

    override fun compareTo(
        @Suppress("UNUSED_PARAMETER") other: File?): Int {
        TODO("Not yet implemented")
    }

    override fun equals(other: Any?): Boolean {
        return if (other != null && other is File) {
            compareTo(other as File?) == 0
        } else false
    }

    override fun hashCode(): Int {
        TODO("Not yet implemented")
    }

    override fun toString(): String {
        return getPath()
    }

    @Synchronized
    private fun writeObject(
        @Suppress("UNUSED_PARAMETER") s: ObjectOutputStream) {
        TODO("Not yet implemented")
    }

    @Synchronized
    private fun readObject(
        @Suppress("UNUSED_PARAMETER") s: ObjectInputStream) {
        TODO("Not yet implemented")
    }

    override fun toPath(): Path = VPath(path)
}
