package monster.nor.prunes.utils

import monster.nor.prunes.io.deleteRecursively
import java.io.*
import java.nio.charset.*
import java.nio.file.*
import java.util.zip.*
import kotlin.io.path.*


object ZipUtils {
    var trace: ((Any)->Unit) = {}

    fun unzipRecursively(path: Path) {
        trace("start unzip $path")
        trace("please waiting...")
        when {
            path.isDirectory() -> {
                lick(path)
            }
            path.isRegularFile() -> {
                unzip(path)?.let { lick(it) }
            }
        }
        trace("end of unzip.")
    }

    private fun lick(dir: Path, callback: (Path) -> Unit = {}) {
        dir.listDirectoryEntries("*.zip").filter { it.isRegularFile() }.forEach {
            unzip(it)
        }

        dir.listDirectoryEntries("*.jar").filter { it.isRegularFile() }.forEach {
            unzip(it)
        }

        dir.listDirectoryEntries().filter { it.isDirectory() }.forEach {
            callback(it)
            lick(it, callback)
        }
    }

    private fun unzipMalformed(src: Path, directory: Path) {
        try {
            unzipImpl(src, directory)
        }
        catch (e: IllegalArgumentException) {
            when(e.message) {
                "MALFORMED" -> {
                    unzipImpl(src, directory, Charset.forName("MS932"))
                }
            }
        }
    }

    private fun unzip(zip: Path): Path? =
        runCatching {
            zip.parent.resolve(zip.nameWithoutExtension).createDirectories().also {
                unzipMalformed(zip, it)
            }
        }.onFailure {
            when(it) {
                is ZipException -> {
                    when(it.message) {
                        "zip file is empty" -> {
                            trace("empty. $zip")
                            zip.deleteExisting()
                            return@onFailure
                        }
                        "error in opening zip file" -> {
                            println("=== error in opening zip file ===")
                            FileInputStream(zip.toFile()).use { fs ->
                                BufferedInputStream(fs).use { bis ->
                                    ZipInputStream(bis).use { zis ->
                                        while(true) {
                                            try {
                                                zis.nextEntry?.let { entry ->
                                                    println("Extracting: $entry")
                                                    println(" -> name=${entry.name}, dir=${entry.isDirectory}, size=${entry.size}")
                                                } ?: break
                                            }
                                            catch (e: EOFException) {
                                                trace("No content. $zip")
                                                zip.deleteRecursively()
                                                break
                                            }
                                        }
                                    }
                                }
                            }
                            return@onFailure
                        }
                    }
                }
            }

            System.err.println("unzip failed $zip")
            it.printStackTrace()
        }.onSuccess {
            zip.deleteExisting()
        }.getOrNull()

    private fun unzipImpl(src: Path, directory: Path, charset: Charset = StandardCharsets.UTF_8) {
        ZipFile(src.toFile(), ZipFile.OPEN_READ, charset).use { zipFile ->
            val entries = zipFile.entries()
            while (entries.hasMoreElements()) {
                val entry = entries.nextElement()
                val item = directory.resolve(entry.name)
                when (entry.isDirectory) {
                    true -> item.createDirectories()
                    false -> {
                        item.parent.createDirectories()
                        zipFile.getInputStream(entry).use { copy(it, item) }
                    }
                }
            }
        }
    }

    private fun copy(ins: InputStream, out: OutputStream) {
        val buffer = ByteArray(1024 * 8)
        var len: Int
        while (ins.read(buffer).also { len = it } != -1) {
            out.write(buffer, 0, len)
        }
    }

    private fun copy(ins: InputStream, file: Path) {
        Files.newOutputStream(file).use { copy(ins, it) }
    }
}
