package monster.nor.prunes.prunes.test

import com.google.common.truth.Truth.assertThat

internal class ExpectedException constructor(private val e: Throwable) {
    companion object {
        inline fun <reified T : Throwable> inspection(closure: () -> Unit): ExpectedException {
            runCatching(closure)
                .onSuccess { fail() }
                .onFailure {
                    assertThat(it is T).isTrue()
                    return ExpectedException(it)
                }
            throw RuntimeException()
        }
    }

    fun checkMessage(expected: String) {
        assertThat(e.message).isEqualTo(expected)
    }
}
