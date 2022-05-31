package monster.nor.prunes.io.monitor

import java.nio.file.Path
import kotlin.io.path.isRegularFile
import kotlin.io.path.listDirectoryEntries

/**
 * File creation monitor.
 * It is possible to report by grouping several files according to the conditions.
 * A class provided because standard WatchService cannot monitor mounted directories.
 *
 * ファイルの作成をモニタする。
 * いくつかのファイルを条件でグルーピングして報告する事ができる。
 *
 * ```kotlin
 * val mon = FileMonitor("dir".toPath(), related)
 * mon.submit {
 *      println(it)
 * }
 * ```
 *
 * @param[dir] Monitored directory. 対象ディレクトリ
 * @param[related]
 */
class FileMonitor(private val dir: Path, private val related: (Path)-> Unit) {

    var cancel = false

    /**
     * Report if the file has been generated.
     * ファイルが生成されていれば報告する。
     */
    fun submit() {
        dir.listDirectoryEntries().filter { it.isRegularFile() }.forEach {
            if(cancel)
                return@forEach

            related(it.fileName)
        }
    }
}
