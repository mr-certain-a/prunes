package monster.nor.prunes.prunes.toolbox.opt

import monster.nor.prunes.toolbox.opt.DropWindow
import monster.nor.prunes.utils.ZipUtils
import org.spekframework.spek2.Spek
import org.spekframework.spek2.dsl.Skip
import org.spekframework.spek2.style.specification.describe
import kotlin.concurrent.thread

internal class DropWindowTest: Spek({
    describe("UnzipWindow", Skip.Yes()) {
        context("Unzip") {
            DropWindow("Unzip") { window, it ->
                thread {
                    ZipUtils.trace = { window.drawText(it) }
                    ZipUtils.unzipRecursively(it)
                }
            }
        }
    }
})
