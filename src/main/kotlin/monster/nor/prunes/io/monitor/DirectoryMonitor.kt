package monster.nor.prunes.io.monitor

import java.nio.file.Path
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile
import kotlin.io.path.listDirectoryEntries

/**
 * This feature is provided for mount directories.
 * It is usually better to use WatchService.
 *
 * Monitor the creation of subdirectories.
 * The reporting timing is when a file that matches the conditions exists in the subdirectory.
 * By default, if a file exists in a subdirectory, it will be reported to the listener.
 * サブディレクトリの作成をモニタする。
 * 報告タイミングは、サブディレクトリ内に条件に一致したファイルが存在している場合になる。
 * デフォルトではサブディレクトリ内にファイルが存在すればリスナーに報告する。
 *
 * ```kotlin
 * val mon = Monitor("dir".toPath(), "*.ind")
 * mon.submit {
 *      println(it)
 * }
 * ```
 *
 * @param[dir] 対象ディレクトリ
 * @param[glob] the globbing pattern. The syntax is specified by the FileSystem.getPathMatcher method.
 *
 *
 */
class DirectoryMonitor(private val dir: Path, private val glob: String = "*") {
    var cancel = false

    /**
     * ディレクトリの状態を受け取る。報告すべきサブディレクトリが生成されていれば報告する。
     *
     * @param[listener] 報告を受けるリスナー
     */
    fun submit(listener: (Path) -> Unit) {
        dir.listDirectoryEntries().filter { it.isDirectory() }.forEach { parent ->

            if(cancel)
                return@forEach

            parent.listDirectoryEntries(glob).filter { it.isRegularFile() }.forEach { _ ->
                listener(parent)
            }
        }
    }
}
