package me.stasiak.ver3.api.onthis

import me.stasiak.ver3.CasePredicate
import me.stasiak.ver3.Matcher

fun <T> Matcher<T>.case(predicate: T.() -> Boolean): CasePredicate<T> {
    return CasePredicate(this, predicate)
}