package monster.nor.prunes.io

import monster.nor.prunes.utils.UUID
import monster.nor.prunes.utils.randomStringWithHyphenRemoval
import java.io.IOException
import java.nio.file.Path
import kotlin.io.path.*

/**
 * フォルダへの読み込み・書き込みができることを保証する
 */
class GuaranteeDirectory(val path: Path) {

    init {
        if(!path.exists())
            throw IOException("ディレクトリが存在しません。")

        if(!path.isDirectory())
            throw IOException("ディレクトリではありません。")


        // ファイル読み込み・書き込みチェック
        val checkFile = path.resolve(UUID.randomStringWithHyphenRemoval())
        kotlin.runCatching {
            "@-=q1w2e3r4t[]".let {
                checkFile.createFile().writeText(it)
                if(checkFile.fileSize() != it.length.toLong())
                    throw IOException("読み込みでエラーが発生した。(size)")

                if(checkFile.readText() != it)
                    throw IOException("読み込みでエラーが発生した。(content)")
            }
        }.onFailure {
            throw it
        }.also {
            checkFile.deleteExisting()
        }
    }
}
