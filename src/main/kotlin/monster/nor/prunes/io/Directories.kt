package monster.nor.prunes.io

import monster.nor.prunes.logger.log
import monster.nor.prunes.logger.trace
import java.io.IOException
import java.nio.file.Path
import java.util.*
import kotlin.io.path.*

/**
 * テンポラリディレクトリを作成する。
 * 作成されたディレクトリはスコープ終了時に削除される。
 *
 * ```kotlin
 * temporaryDirectory {
 *     // 作成されたテンポラリディレクトリ内にファイルを作る
 *     it.inCreateFile()
 * }
 * ```
 *
 * @param rootDir テンポラリディレクトリのルート。nullの場合デフォルトのテンポラリディレクトリ。
 * @param closure テンポラリディレクトリの寿命範囲。
 */
fun temporaryDirectory(rootDir: Path? = null, closure: (Path)->Unit) {
    val dir = rootDir?.let {
        rootDir.resolve(UUID.randomUUID().toString()).createDirectories() }
            ?: createTempDirectory()
    runCatching {
        closure(dir)
    }.onFailure { throw it }
        .also { dir.toFile().deleteRecursively() }
}

/**
 * Find the file in the directory.
 * Returns the first file found, or NULL if not found.
 *
 * listDirectoryEntries(glob).firstOrNull { it.isRegularFile() }
 *
 * @param glob the globbing pattern. The syntax is specified by the FileSystem.getPathMatcher method.
 */
fun Path.search(glob: String): Path? =
    listDirectoryEntries(glob).firstOrNull { it.isRegularFile() }

fun Path.inUuidDirectory() = resolve(UUID.randomUUID().toString()).createDirectories()

fun Path.confirm(rhs: Path) {
    when {
        rhs.notExists() -> throw IOException("not found $rhs")
        rhs.fileSize() != fileSize() -> throw IOException()
    }
}

fun Path.safeDelete(): Boolean {
    runCatching {
        deleteExisting()
    }.onFailure {
        when(it) {
            is IOException -> { log.error(it.message) }
            is SecurityException -> { log.error(it.message) }
            else -> Unit
        }
    }
    return notExists()
}

fun Boolean.fail(closure: () -> Unit) {
    if(!this) closure()
}

/**
 * dist.resolve(this.first.fileName).createDirectory()
 */
fun Pair<Path, Path>.moveToDir(dist: Path): Path? =
    runCatching {
        dist.inCreateDirectory(first.fileName).let { dir ->
            runCatching {
                val distFirst = dir.resolve(first.fileName)
                val distSecond = dir.resolve(second.fileName)

                first.copyTo(distFirst, true)
                second.copyTo(distSecond, true)

                first.confirm(distFirst)
                second.confirm(distSecond)
                dir
            }.onSuccess {
                first.safeDelete().fail { log.warn("ファイル $first が削除できません。") }
                second.safeDelete().fail { log.warn("ファイル $second が削除できません。") }
            }.onFailure {
                log.warn(it.message)
                log.trace(it)
                dir.deleteRecursively()
            }.getOrNull()
        }
    }.onFailure {
        log.warn(it.message)
    }.getOrNull()

