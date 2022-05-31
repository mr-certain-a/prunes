package monster.nor.prunes.utils

import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import monster.nor.prunes.io.newInputStream
import java.nio.file.Path
import kotlin.io.path.createDirectory
import kotlin.io.path.createFile
import kotlin.io.path.writeBytes

/**
 * Extract (Unzip) Tar Gz File.
 */
object GzipUtils {
    fun unzip(file: Path, dist: Path) =
        runCatching {
            mutableListOf<Path>().apply {
                file.newInputStream().use { ins ->
                    GzipCompressorInputStream(ins).use { gcis ->
                        TarArchiveInputStream(gcis).use { tais ->
                            while (true) {
                                tais.nextEntry?.let {
                                    when (it.isDirectory) {
                                        true -> dist.resolve(it.name).createDirectory()
                                        else -> {
                                            dist.resolve(it.name).run {
                                                createFile().writeBytes(tais.readBytes())
                                                add(this)
                                            }
                                        }
                                    }
                                } ?: break
                            }
                        }
                    }
                }
            }
        }.getOrNull()
}