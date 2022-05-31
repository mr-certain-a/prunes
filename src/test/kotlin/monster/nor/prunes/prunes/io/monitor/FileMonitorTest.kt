package monster.nor.prunes.prunes.io.monitor

import com.google.common.truth.Truth.assertThat
import monster.nor.prunes.io.*
import monster.nor.prunes.io.monitor.FileMonitor
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteExisting
import kotlin.io.path.exists
import kotlin.io.path.extension

internal class FileMonitorTest: Spek({
    describe("FileMonitorTest") {

        fun assertFound(vararg files: String) {
            files.forEach {
                assertThat(it.toPath().exists()).isTrue()
            }
        }

        fun assertNotFound(vararg files: Path) {
            files.forEach {
                assertThat(it.exists()).isFalse()
            }
        }

        it("submit") {
            val dist = "root/dist".toPath().createDirectories()
            dist.deleteRecursively()

            temporaryDirectory("root".toPath()) { dir ->

                dir.inCreateFile("abc.xml")
                dir.inCreateFile("abc_info.xml")
                dir.inCreateFile("def_err.txt")
                dir.inCreateFile("ghi.xml")
                dir.inCreateFile("ghi_info.xml")

                fun errorFile(file: Path) {
                    println("--- found Error ------------")
                    println(file)
                    file.deleteExisting()
                }

                val mon = FileMonitor(dir) { file ->
                    when(file.extension) {
                        "xml" -> when {
                            file.toString().endsWith("_info.xml") -> Unit
                            else -> {
                                dir.resolve("${file.baseName}_info.xml").let {
                                    if(it.exists()) {
                                        (dir.resolve(file) to it).moveToDir(dist)
                                    }
                                }
                            }
                        }
                        "txt" -> {
                            errorFile(dir.resolve(file))
                        }
                    }
                }

                println("submit 1st")
                mon.submit()

                assertFound(
                    "root/dist/abc.xml/abc.xml",
                    "root/dist/abc.xml/abc_info.xml",
                    "root/dist/ghi.xml/ghi.xml",
                    "root/dist/ghi.xml/ghi_info.xml"
                )
                assertNotFound(
                    dir.resolve("abc.xml"),
                    dir.resolve("abc_info.xml"),
                    dir.resolve("ghi.xml"),
                    dir.resolve("ghi_info.xml"),
                    dir.resolve("def_err.txt")
                )
                dist.deleteRecursively()

                println("submit 2nd")
                mon.submit()
                println("submit 3rd")
                mon.submit()
                println("--- end of submit ---")
            }
        }
    }
})
