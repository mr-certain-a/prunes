package monster.nor.prunes.prunes.io

import com.google.common.truth.Truth.assertThat
import monster.nor.prunes.io.*
import monster.nor.prunes.logger.debugTraceMode
import monster.nor.prunes.prunes.io.vfs.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.dsl.Skip
import org.spekframework.spek2.style.specification.describe
import test.TestUtils.Red
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.*

// 仮想ファイルシステムを使うかどうか
const val USE_VIRTUAL_FILESYSTEM = true

internal class DirectoriesKtTest: Spek({

    fun String.toTestPath(): Path =
        when(USE_VIRTUAL_FILESYSTEM) {
            true -> VPath(this)
            else -> Paths.get(this)
        }

    describe("DirectoriesKtTest") {

        beforeGroup {
            Red.println("debug on.")
            debugTraceMode = true
        }
        beforeEachTest {
            println("-- beforeEachTest --")
            when(USE_VIRTUAL_FILESYSTEM) {
                true -> VFileSystemProvider.clear()
                else -> "root".toPath().toFile().deleteRecursively()
            }
        }
        afterEachTest {
            println("-- afterEachTest --")
            when(USE_VIRTUAL_FILESYSTEM) {
                true -> VFileSystemProvider.showAll()
                else -> "root".toPath().toFile().deleteRecursively()
            }
        }
        afterGroup {
        }

        context("moveToUniqueDir") {
            val dist = "root/dist".toTestPath()
            fun createPair(): Pair<Path, Path> =
                "root/src".toTestPath().let {
                    it.inCreateFileIfNotExists("one.xml").apply { writeText("1") } to
                    it.inCreateFileIfNotExists("two.pdf").apply { writeText("22") }
                }

            it("正常移動") {
                createPair().let {
                    println("success= ${it.first}")
                    val result = it.moveToDir(dist)
                    assertThat(result).isNotNull()
                    assertThat(it.first.exists()).isFalse()
                    assertThat(it.second.exists()).isFalse()
                    result!!
                    assertThat(result.resolve(it.first.fileName).exists()).isTrue()
                    assertThat(result.resolve(it.second.fileName).exists()).isTrue()
                }
            }

            it("正常動作２回実行") {
                var it = createPair()
                var result = it.moveToDir(dist)
                assertThat(result).isNotNull()
                assertThat(it.first.exists()).isFalse()
                assertThat(it.second.exists()).isFalse()
                assertThat(result!!.resolve(it.first.fileName).exists()).isTrue()
                assertThat(result!!.resolve(it.second.fileName).exists()).isTrue()

                it = createPair()
                result = it.moveToDir(dist)
                assertThat(result).isNotNull()
                assertThat(it.first.exists()).isFalse()
                assertThat(it.second.exists()).isFalse()
                assertThat(result!!.resolve(it.first.fileName).exists()).isTrue()
                assertThat(result!!.resolve(it.second.fileName).exists()).isTrue()
            }

            it("送出先にフォルダを作れない",
                if(USE_VIRTUAL_FILESYSTEM) Skip.No else Skip.Yes("only USE_VIRTUAL_FILESYSTEM")) {

                createPair().let {
                    val dir = "root/fail".toTestPath()
                    dir.createDirectory()
                    ここへのフォルダ作成を失敗させる(dir)

                    val result = it.moveToDir(dir)

                    assertThat(result).isNull()
                    assertThat(it.first.exists()).isTrue()
                    assertThat(it.second.exists()).isTrue()
                }
            }
            it("既にフォルダがある") {
                createPair().let {
                    dist.resolve(it.first.fileName).createDirectories()
                    val result = it.moveToDir(dist)
                    assertThat(result).isNotNull()
                    assertThat(it.first.exists()).isFalse()
                    assertThat(it.second.exists()).isFalse()
                    result!!
                    assertThat(result.resolve(it.first.fileName).exists()).isTrue()
                    assertThat(result.resolve(it.second.fileName).exists()).isTrue()
                }
            }
            it("既にフォルダもファイルもある") {
                createPair().let {
                    val dir = dist.resolve(it.first.fileName).createDirectories()
                    dir.resolve(it.first.fileName).createFile()
                    dir.resolve(it.second.fileName).createFile()

                    val result = it.moveToDir(dist)
                    assertThat(result).isNotNull()
                    assertThat(it.first.exists()).isFalse()
                    assertThat(it.second.exists()).isFalse()
                    result!!
                    assertThat(result.resolve(it.first.fileName).exists()).isTrue()
                    assertThat(result.resolve(it.second.fileName).exists()).isTrue()
                }
            }
            it("firstが消せない",
                if(USE_VIRTUAL_FILESYSTEM) Skip.No else Skip.Yes("only USE_VIRTUAL_FILESYSTEM")) {

                createPair().let {
                    このファイルは削除できなくする(it.first)

                    val result = it.moveToDir(dist)

                    assertThat(result).isNotNull()
                    assertThat(it.first.exists()).isTrue()
                    assertThat(it.second.exists()).isFalse()
                    result!!
                    assertThat(result.resolve(it.first.fileName).exists()).isTrue()
                    assertThat(result.resolve(it.second.fileName).exists()).isTrue()
                }
            }
        }
    }
})
