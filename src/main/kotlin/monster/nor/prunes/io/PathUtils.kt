package monster.nor.prunes.io

import monster.nor.prunes.logger.log
import monster.nor.prunes.utils.Quartet
import monster.nor.prunes.utils.to
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.*
import java.nio.file.Files.newBufferedReader
import java.util.UUID
import kotlin.io.path.*

val LINE: String = System.lineSeparator()

/**
 * uuid() = UUID.randomUUID().toString()
 */
fun uuid() = UUID.randomUUID().toString()

/**
 * Create Path object from this String.
 */
fun String.toPath(): Path = Paths.get(this)

/**
 * ディレクトリを再帰的に削除
 */
fun Path.deleteRecursively() = toFile().deleteRecursively()

/**
 * このディレクトリを distDir の中に移動
 * 移動先は distDir.resolve(this.fileName) となる
 * 移動先に同一フォルダがあり内容が同じ場合、古いフォルダを消して上書きする
 *
 * @see directoryCompare
 */
fun Path.moveDirectoryToDirectory(distDir: Path): Path {
    distDir.createDirectories()
    return when(distDir.resolve(fileName).notExists()) {
        true -> moveTo(distDir.resolve(fileName))
        else -> {
            directoryCompare(distDir).let {
                if(it.first) {
                    deleteRecursively()
                }
                else {
                    if(it.second.isEmpty())
                        deleteRecursively()
                    else {
                        log.warn("移動先に内容の異なるフォルダが存在する $fileName")
                    }
                }
            }
            distDir.resolve(fileName)
        }
    }
}

/**
 * ファイル内容のバイナリ比較
 */
fun Path.contentEquals(file: Path): Boolean =
    readBytes().contentEquals(file.readBytes())

/**
 * ディレクトリの内容を比較する
 *
 * @return Quartet 1st: 同一内容か。同一ならtrue, それ以外はfalse<br>
 *         Quartet 2nd: 元フォルダにのみ存在するファイルリスト<br>
 *         Quartet 3rd: 先フォルダにのみ存在するファイルリスト<br>
 *         Quartet 4th: 同一ファイル名で内容の異なるファイルリスト<br>
 */
fun Path.directoryCompare(other: Path): Quartet<Boolean, List<Path>, List<Path>, List<Path>> {
    val leftOnly = mutableListOf<Path>()
    val rightOnly = mutableListOf<Path>()
    val contentDifferent = mutableListOf<Path>()

    listDirectoryEntries().forEach {
        val right = other.resolve(it.fileName)
        when {
            right.notExists() ->
                leftOnly.add(it.fileName)
            right.isDirectory() and it.isDirectory() -> {
                if(!it.directoryCompare(right).first)
                    contentDifferent.add(it.fileName)
            }

            (it.isDirectory() and right.isRegularFile()) or
            (it.isRegularFile() and right.isDirectory()) ->
                contentDifferent.add(it.fileName)

            !it.contentEquals(right) ->
                contentDifferent.add(it.fileName)
        }
    }

    other.listDirectoryEntries().forEach {
        val left = resolve(it.fileName)
        when {
            left.notExists() ->
                rightOnly.add(it.fileName)
        }
    }

    return (rightOnly.isEmpty() && leftOnly.isEmpty() && contentDifferent.isEmpty()) to
            leftOnly to rightOnly to contentDifferent
}

/**
 * フォルダを再帰的にコピーする
 * dst が存在する場合エラーとなる
 */
fun Path.copyRecursively(dst: Path) = toFile().copyRecursively(dst.toFile())

/**
 * ファイルから先頭行を取り出す
 */
fun Path.firstLine(cs: Charset = StandardCharsets.UTF_8): String =
    newBufferedReader(this, cs).useLines {
        val itr = it.iterator()
        when(itr.hasNext()) {
            true -> itr.next()
            false -> ""
        }
    }

val Path.baseName
    get() = fileName.nameWithoutExtension

fun Path.inCreateFile(name: String? = null) = resolve(name ?: uuid()).createFile()

fun Path.inCreateFiles(vararg names: String) =
    names.map { resolve(it).createFile() }.toList()

fun Path.inCreateFileIfNotExists(name: String): Path =
    runCatching {
        if(notExists())
            createDirectories()

        inCreateFile(name)
    }.getOrElse { resolve(name) }

fun Path.inCreateDirectory(name: String? = null): Path {
    val target = resolve(name ?: uuid())
    if(target.exists()) {
        if (target.isDirectory())
            return target
        else
            throw IOException("作ろうとしたフォルダはファイルとして存在する。$target")
    }
    return target.createDirectories()
}

fun Path.inCreateDirectory(name: Path) = inCreateDirectory(name.toString())

fun Path.newInputStream(): InputStream = Files.newInputStream(this)

fun Path.show() {
    print(showString())
}

fun Path.showString(): String =
    StringBuilder().apply {
        when {
            isDirectory() -> {
                append("+ ${this@showString}").append(LINE)
                listDirectoryEntries().forEach { append(it.showString()) }
            }
            else -> append("- ${this@showString}").append(LINE)
        }
    }.toString()
