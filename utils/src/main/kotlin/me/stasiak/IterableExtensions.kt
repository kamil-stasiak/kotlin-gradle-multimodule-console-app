package me.stasiak

fun <T> Iterable<T>.tap(tap: (T) -> Unit): Iterable<T> {
    forEach { tap(it) }
    return this
}