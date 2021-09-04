# Kotlin Pattern Matching + DSL

Pokazując DSL i PM można odtworzyć metodę when() {} a później dodawać do niej kolejne featury

## Jakie powinny być możliwości matchowania

### Po typie tak jak to robie

```kotlin
isA<SomeClass>()
isA<SomeClass>() { name == "abc" }

anyOf("abc", "def")
anyOf("abc", "def")
```

### Listy

```kotlin
start[1, 2, 3]
end[2, 3]
exact[1, 2, 3, 4, 5]
like[1, null, 3, 4]

start(1, 2, 3)
end(2, 3)
exact(1, 2, 3, 4, 5)
like(1, null, 3, 4)
```

Przykład

Przykład musi być wieloparametrowy.

Mamy więc użytkownika, któremu trzeba naliczyć jakaś zniżkę. To jest na podstawie parametrów. Można też dorzucić mu coś
do koszyka. Zniżki mogą się łączyć lub nie. Przykład pierwszy, zniżki się nie łączą. Przykłąd drugi ktoś się kwalifikuje
na więcej niż jeden produkt.

- ile zakupów na koncie: do 10 to konto nowe, więcej niż 10 to standard, więcej niż 100 to weteran
- jak długo ma konto: jak mniej niż 3 miesiące to nowe, jak więcej niż 3 standard
- czy wrócił: jeżeli od ostatniego zakupu minęło więcej niż 2 miesiące to znaczy, że wrócił
- ilość punktów na koncie - to się zbiera za zakupy, za wydane pieniądze
- typ konta: standard, pro, proPlus - za to się miesięcznie płaci (allegroSmart)

```kotlin
val u = user

match(
    u.ordersCount to "orders",
    u.registrationTime to "registration",
    u.lastOrderTime to "lastOrder",
    u.points to "points",
    u.accType to " type"
) {
    // to nie może być li
    Case()
        .then {}

}


// lepiej dać całego usera

match(user) {
    Case(
        isA<StandardUser>(),
        prop { registrationTime > 100 },
        prop { lastOrder < mniejNiż10Dni }
    ) {}

    Case(
        isA<ProPlusUser>(),
        prop { registrationTime > 100 },
        prop { lastOrder < mniejNiż10Dni }
    ) {}

}


```

czy jest rozszerzenie dzięki któremu będę mógł pisać markdown in kotlin comment? Dzięki temu będę mógł pisać testy i
opisywać je w komentarzu zamiast tutaj w markdownie bo wtedy lepiej będzie kod opisywało Nie ma pluginu, ale to jest
wbudowane w IntelliJ

- render all doc comment - wyświetla wszystkie komentarze
- toggle rendered view - zmienai jeden komentarz
- prawy klik na komentarzu i można zwiększać wielkość czcionki
- prawy klik na ikonce i można toggle all

Pokazać, że taki DSL jest rozszerzalny i te rozszerzenia mogą być w innych modułach. Np, MatchContext może być w core, a
wszystkie mapowanie związane z obiektem może być w osobnynm module, tak samo wszystkie związane z listami, czy mapami.
Wtedy jak komuś nie podoba się jakaś metoda, to może sobie podmienić dany moduł

```kotalin
match() {
 Case<>() // to sie komuś nie podoba, i nie chce pisać <> to wtedy może ten moduł uzunąć i sobie użyć  
 Case() // o czegoś takiego. Albo w ogóle sobie zmenić co to ma robić.
}
```

```kotlin
someObj.match {

}

===

match(someObj) {

}
```

match ma zwracać wartość albo

# Matchowanie list

```kotlin
match(listOf(1, 2, 3, 4, 5)) {
    Case(lista(_, 3, 4)) { "OK" } // lista ma mieć 3 elementy, drugi to 3, trzeci to 4
    Case(lista(_, 3, 4, REST)) { "OK" } // lista ma mieć minimum 3 elementy, reszta nie ważna
    Case(lista(_, 3, _, 3)) { "OK" } // lista ma mieć 4 elementy
    Case(lista(`..`, 3, _, 3)) { "OK" } // początek nie ma znaczenia, ma się kończyć na 3, _, 3 
    Case(lista(`..`, 2, 2, 1, `..`)) { "OK" } // początek nie ma znaczenia, ma się kończyć na 3, _, 3
    Case(lista(X1, X2, X3)) { "OK" } // lista 3 elementowa, nie ważne jakie elementy w niej są 
    Case(lista(A, B, C)) { "OK" } // lista 3 elementowa, nie ważne jakie elementy w niej są 
    Case(lista(A, B, A)) { "OK" } // lista 3 elementowa, pierwszy element taki jak ostatni... // TODO to jest mocne
}

// _ dowolna wartość
// `..` nieważne ile przed albo po == REST
// trzeba wymyślić coś lepszego niz `lista`
// `..`, REST, _ to jakieś enumy albo Seald Classes - do tego potrzebujemy po liście przechodzić za pomocą okien
// jakichś windowo functions na listach
// X1, X2, X3 == A, B, C

```

```kotlin

data class IT(
    val name: String = nameFromProps,
    val age: Integer = calculatedAge,
    val addresses: List<Address> = addresses
    // ...
)
match(IT()) {
    // samo Case to na całym obiekcie
    Case(prop { name.length < 100 }, prop { age > 18 }) { "RESULT_1" }
    Case(prop { name.length < 100 and age > 18 }) { "RESULT_1" }

    // CaseOn albo CaseProp zmienia typ na ten co podamy 
    CaseProp({ name }, prop { length < 100 and notBlank() and lowercase() }) { "Everthing on property of type string" }
    CaseOn({ name }, prop { length < 100 and notBlank() and lowercase() }) { "Everthing on property of type string" }

    // do Case zostanie przekazany obiekt person tylko jeżeli będzie on obiektem typu Person, w przeciwnym wypadku będzie false
    // to samo co Case isA<Person>
    Case<Person>() { "Jeżeli to będzie obiekt person" }
    Case(isA<Person>()) { "Jeżeli to będzie obiekt person" }

    //
    Case(isA<Dog>() and prop { bark() > 3 }) { "Loud dog" } // pies głośniejszy niż 3db
    Case(isA<Person>() and prop { isBald() }) { "Bald person" }

    // OR
    Case(isA<Dog> { bark() > 3 }) { "Loud dog" } // pies głośniejszy niż 3db
    Case(isA<Person> { isBald() }) { "Bald person" }
}

```

```kotlin

val time: String = "20:12:43"
//val time: = "20:12"

// jak to fajnie zapisać?
match(time.split(":")) {
    Case(lista(A, B, C)) { Time() }
    // or 
    Case(lista(X1, X2, X3)) {
        val (x1, x2, x3) = it // this
        Time(x1, x2, x3)
    }

    Case(lista(X1, X2)) {
        val (x1, x2) = it // this
        Time(x1, x2, "00")
    }

    Case(lista(X1)) {
        val (x1) = it // this
        Time(x1, "00", "00")
    }
}
```

## Problem

Nalicz bonus, jeżli ktoś w trakcie - taką odznakę, trofeum dajemy

- przez 3 tury nie dobrał karty (1)
- przez 4 tury dobierał po 1 karcie (2)
- przez 3 tury dobierał karty rosnąco o 1 (3)
- przez 5 tur dobierał na zmianę ilość kart (4)

```kotlin
match(someList) {
    Case(lista(`..`, 0, 0, 0, `..`)) { "(1)" }
    Case(lista(`..`, 1, 1, 1, 1, `..`)) { "(2)" }
    Case(lista(`..`, 1, 2, 3, `..`)) { "(3)" }
    Case(lista(`..`, X1, X2, X1, X2, X1, `..`)) { "(4a)" }
    Case(lista(`..`, A, B, A, B, A, `..`)) { "(4b)" }
}
```

# a teraz zamiast match czyli pierwszego dopasowania trzeba zerbać wszystkie które pasują

// dla pierwszych 5 licząc od 0, 3 razy zagrał to samo pod rząd

```kotlin
match(someList) {
    // można normalnie programować 
    (0..4).forEach { index ->
        Case(lista(`..`, index, index, index, `..`)) { "Zagranie 3 razy tej samej ilość kart" }
    }
}
```

Złap wszystkie które spełniają Case

```kotlin
val results: List<String> = matchAll(someList) {
    Case(lista(`..`, 0, 0, 0, `..`)) { "(1)" }
    Case(lista(`..`, 1, 1, 1, 1, `..`)) { "(2)" }
    Case(lista(`..`, 1, 2, 3, `..`)) { "(3)" }
    Case(lista(`..`, X1, X2, X1, X2, X1, `..`)) { "(4a)" }
    Case(lista(`..`, A, B, A, B, A, `..`)) { "(4b)" }
}
```

### Na podstawie VAVR
https://www.baeldung.com/vavr-pattern-matching

Złap wszystkie które spełniają Case

```kotlin
fun isA(a: Int) {}
fun isIn(range: Range) {}

match(someList) {
    Case(lista(`..`, isA(0), isA(0), isA(0), `..`)) { "(1)" }
    Case(lista(`..`, isIn(1, 2, 4), isA(1), isA(1), isA(1), `..`)) { "(2)" }
    Case(lista(`..`, isA(1), isA(2), isA(3), `..`)) { "(3)" }
    Case(lista(`..`, X1, X2, X1, X2, X1, `..`)) { "(4a)" }
    Case(lista(`..`, A, B, A, B, A, `..`)) { "(4b)" }
}
```