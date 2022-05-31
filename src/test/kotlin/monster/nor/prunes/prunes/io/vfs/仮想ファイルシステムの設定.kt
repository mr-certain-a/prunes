package monster.nor.prunes.prunes.io.vfs

import java.io.IOException
import java.nio.file.Path

private val makeFailDirectories = mutableListOf<String>()

private val deleteFail = mutableListOf<String>()

fun checkMakeFailDirectory(dir: Path) {
    dir.parent?.let {
        if(makeFailDirectories.contains(it.toString()))
            throw IOException("フォルダ作成失敗指示が出ています $it")
    }
}

fun checkDeleteFail(path: Path) {
    if(deleteFail.contains(path.toString()))
        throw IOException("削除失敗指示が出ています")
}

fun ここへのフォルダ作成を失敗させる(dir: Path, 解除: Boolean = false) {
    if(解除)
        makeFailDirectories.remove(dir.toString())
    else
        makeFailDirectories.add(dir.toString())
}

fun このファイルは削除できなくする(path: Path) {
    deleteFail.add(path.toString())
}
