package monster.nor.prunes.prunes.io

import monster.nor.prunes.io.*
import monster.nor.prunes.prunes.test.ExpectedException
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.io.IOException

internal class GuaranteeDirectoryTest: Spek({

    describe("GuaranteeDirectoryTest") {
        temporaryDirectory {
            val f = it.inCreateFile("file")
            val d = it.inCreateDirectory("dir")

            ExpectedException.inspection<IOException> {
                GuaranteeDirectory(f)
            }.checkMessage("ディレクトリではありません。")

            ExpectedException.inspection<IOException> {
                GuaranteeDirectory(it.resolve("none"))
            }.checkMessage("ディレクトリが存在しません。")

            GuaranteeDirectory(d)

            it.show()
        }
    }
})
