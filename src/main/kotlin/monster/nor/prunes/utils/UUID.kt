package monster.nor.prunes.utils

import java.util.UUID.randomUUID

class UUID {
    companion object
}

fun UUID.Companion.randomString() = randomUUID().toString()

fun String.delete(char: Char) = replace("$char", "")

fun UUID.Companion.randomStringWithHyphenRemoval() = randomString().delete('-')
