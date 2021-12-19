package me.stasiak.ver3.api.extensions

import me.stasiak.ver3.Matcher

fun <T> T.match(init: Matcher<T>.() -> Unit): String =
    Matcher(this).apply(init).match()