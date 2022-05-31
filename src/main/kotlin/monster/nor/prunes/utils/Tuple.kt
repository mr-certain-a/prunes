package monster.nor.prunes.utils

data class Quartet<out F, out S, out T, out FO>(
    val first: F,
    val second: S,
    val third: T,
    val fourth: FO) {

    override fun toString(): String {
        return "Quartet(first=$first, second=$second, third=$third, fourth=$fourth)"
    }
}

data class Quintet<out F, out S, out T, out FO, out FI>(
    val first: F,
    val second: S,
    val third: T,
    val fourth: FO,
    val five: FI) {

    override fun toString(): String {
        return "Quintet(first=$first, second=$second, third=$third, fourth=$fourth, five=$five)"
    }
}

infix fun <A, B, C> Pair<A, B>.to(that: C): Triple<A, B, C> = Triple(this.first, this.second, that)

infix fun <A, B, C, D> Triple<A, B, C>.to(that: D): Quartet<A, B, C, D> = Quartet(this.first, this.second, this.third, that)

infix fun <A, B, C, D, E> Quartet<A, B, C, D>.to(that: E): Quintet<A, B, C, D, E> = Quintet(this.first, this.second, this.third, this.fourth, that)
