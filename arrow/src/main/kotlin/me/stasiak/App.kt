package me.stasiak

import arrow.core.*
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.computations.either
import kotlinx.coroutines.runBlocking

/* A simple model of student and a university */
object NotFound
data class Name(val value: String)
data class UniversityId(val value: String)
data class University(val name: Name, val deanName: Name)
data class Student(val name: Name, val universityId: UniversityId)
data class Dean(val name: Name)

private val students = mapOf(
    Name("Alice") to Student(
        name = Name("Alice"),
        universityId = UniversityId("UCA")
    )
)
private val universities = mapOf(
    UniversityId("UCA") to University(
        name = Name("UCA"),
        deanName = Name("James")
    )
)
private val deans = mapOf(
    Name("James") to Dean(
        name = Name("James")
    )
)

fun student(name: Name): Either<NotFound, Student> =
    students[name]?.let(::Right) ?: Left(NotFound)

fun university(id: UniversityId): Either<NotFound, University> =
    universities[id]?.let(::Right) ?: Left(NotFound)

fun dean(name: Name): Either<NotFound, Dean> =
    deans[name]?.let(::Right) ?: Left(NotFound)

suspend fun bindTest(): Either<NotFound, Student> {
    val either = either<NotFound, Student> {
        val abc = Right(1).bind()
        val def = Right("hello").bind()
        val s1 = student(Name("Alice1")).bind()
        val s2 = student(Name("Alice2")).bind()
        val s3 = student(Name("Alice3")).bind()
        s3
    }.let { it }
    return either
}


suspend fun test(): Either<String, Int> =
    either {
        val one = Right(1).bind()
        1 + one
    }

fun main(): Unit {

    val ok = student(Name("Alice")).flatMap { student ->
        university(student.universityId).flatMap { university ->
            dean(university.deanName)
        }
    }

//    println(ok)

    val student1 = student(Name("Alice"))
    val student2 = student(Name("Alice2"))
    val student3 = student(Name("Alice3"))


    val runBlocking: Either<NotFound, Student> = runBlocking { bindTest() }


    val listOf: List<Either<NotFound, Student>> = listOf(
        student1,
        student2,
        student3
    )
    val let = listOf
        .listOfEitherToEitherOfLists()
        .mapIterable { it }
        .mapLeftIterable { it }


    listOf(
        student1,
        student2,
        student3
    ).listOfEitherToEitherOfLists()
        .map { (s1, s2, s3) ->
            print("Wow! nice Students!")
        }.mapLeft { errorList ->
            print("At least one error $errorList")
        }


    // in: List<Either<NotFound, Student>>
    // out:
    val separatedEither: Pair<List<NotFound>, List<Student>> = listOf(
        student1,
        student2,
        student3
    ).separateEither()

    // in: List<Either<NotFound, Student>>
    // out:
    val sequenceEither: Either<NotFound, List<Student>> = listOf(
        student1,
        student2,
        student3
    ).sequenceEither()

    // in: List<Either<NotFound, Student>>
    // out: forget Left, keep Right
    val uniteEither: List<Student> = listOf(
        student1,
        student2,
        student3
    ).uniteEither()

    //sampleStart
//    val dean = student(Name("Alice")).flatMap { alice ->
//        university(alice.universityId).flatMap { university ->
//            dean(university.deanName)
//        }
//    }
//    sampleEnd
//    println(dean)
}

private fun <Left, Right, Target> Either<List<Left>, List<Right>>.mapIterable(transform: (Right) -> Target): Either<List<Left>, List<Target>> =
    this.map { list -> list.map { transform(it) } }

private fun <Left, Right, Target> Either<List<Left>, List<Right>>.mapLeftIterable(transform: (Left) -> Target): Either<List<Target>, List<Right>> =
    this.mapLeft { list -> list.map { transform(it) } }

private fun <A, B> Iterable<Either<A, B>>.listOfEitherToEitherOfLists(): Either<List<A>, List<B>> = this.let { list ->
    if (list.all { it.isRight() }) list.mapNotNull { it.orNull() }.right()
    else list.mapNotNull { it.swap().orNull() }.left()
}

