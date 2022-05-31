package monster.nor.prunes.prunes.io

import com.google.common.truth.Truth.assertThat
import monster.nor.prunes.io.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.io.path.createDirectory
import kotlin.io.path.exists
import kotlin.io.path.writeText

internal class PathUtilsKtTest: Spek({

    describe("copyRecursively") {
        context("normal") {
            temporaryDirectory { dir ->
                val src = dir.inCreateDirectory("src")
                val dst = dir.resolve("dst")
                src.inCreateFiles("a", "b", "c").forEach {
                    println("=$it")
                }
                src.copyRecursively(dst)
                assertThat(src.directoryCompare(dst).first).isTrue()
                dir.show()
            }
        }
    }


    describe("PathUtilsKtTest") {

        context("directoryCompare") {
            temporaryDirectory {
                val dir1 = it.inCreateDirectory("left")
                val dir2 = it.inCreateDirectory("right")

                println(dir1.directoryCompare(dir2))

                dir1.inCreateFile("a").writeText("a")
                println(dir1.directoryCompare(dir2))

                dir2.inCreateFile("a").writeText("a")
                println(dir1.directoryCompare(dir2))

                dir1.inCreateFile("b").writeText("b1")
                dir2.inCreateFile("b").writeText("b2")
                println(dir1.directoryCompare(dir2))

                val c1 = dir1.inCreateDirectory("c")
                val c2 = dir2.inCreateDirectory("c")
                println(dir1.directoryCompare(dir2))

                c1.inCreateFile("c000")
                c2.inCreateFile("c000")
                println(dir1.directoryCompare(dir2))

                c2.inCreateDirectory("d")
                println(dir1.directoryCompare(dir2))

                dir1.inCreateFile("e")
                println(dir1.directoryCompare(dir2))

                dir2.inCreateFile("e")
                println(dir1.directoryCompare(dir2))

                dir2.inCreateFile("f")
                println(dir1.directoryCompare(dir2))

                dir1.inCreateDirectory("g")
                dir2.inCreateFile("g")
                println(dir1.directoryCompare(dir2))
            }
        }

        context("PathUtils") {
            temporaryDirectory {
                val src = it.inCreateDirectory("src")
                val dist = it.inCreateDirectory("dist")

                val f1 = src.inCreateFile()
                val f2 = src.inCreateFile()

                src.moveDirectoryToDirectory(dist).let { dir ->
                    assertThat(dir).isNotNull()
                    assertThat(dir).isEqualTo(
                        dist.resolve(src.fileName)
                    )

                    assertThat(dir.resolve(f1.fileName).exists()).isTrue()
                    assertThat(dir.resolve(f2.fileName).exists()).isTrue()
                }
            }
        }

        context("PathUtils already exists") {
            temporaryDirectory {
                val src = it.inCreateDirectory("src")
                val dist = it.inCreateDirectory("dist")

                val f1 = src.inCreateFile()
                val f2 = src.inCreateFile()

                dist.resolve("src").createDirectory()

                src.moveDirectoryToDirectory(dist).let { dir ->
                    assertThat(dir).isNotNull()
                    assertThat(dir).isEqualTo(
                        dist.resolve(src.fileName)
                    )

                    assertThat(dir.resolve(f1.fileName).exists()).isFalse()
                    assertThat(dir.resolve(f2.fileName).exists()).isFalse()
                }
            }
        }
    }

})