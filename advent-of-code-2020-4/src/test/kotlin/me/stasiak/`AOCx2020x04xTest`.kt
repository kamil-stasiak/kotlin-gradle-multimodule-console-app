package me.stasiak

import org.junit.Test
import java.io.File
import kotlin.test.assertEquals

sealed class Field(
    val value: String,
) {
    class Byr(value: String) : Field(value = value) {
        // four digits; at least 1920 and at most 2002
        override fun isValid(): Boolean = value.length == 4 && value.toIntOrNull() in (1920..2002)
    }

    class Iyr(value: String) : Field(value = value) {
        // four digits; at least 2010 and at most 2020.
        override fun isValid(): Boolean = value.length == 4 && value.toIntOrNull() in (2010..2020)
    }

    class Eyr(value: String) : Field(value = value) {
        // four digits; at least 2020 and at most 2030
        override fun isValid(): Boolean = value.length == 4 && value.toIntOrNull() in (2020..2030)
    }

    class Hgt(value: String) : Field(value = value) {
        // a number followed by either cm or in:
        override fun isValid(): Boolean = when (value.takeLast(2)) {
            // If cm, the number must be at least 150 and at most 193.
            "cm" -> value.removeSuffix("cm").toIntOrNull() in (150..193)
            // If in, the number must be at least 59 and at most 76.
            "in" -> value.removeSuffix("in").toIntOrNull() in (59..76)
            else -> false
        }
    }

    class Hcl(value: String) : Field(value = value) {
        companion object {
            private val REGEX = """#[a-f0-9]{6}""".toRegex()
        }

        // a # followed by exactly six characters 0-9 or a-f.
        override fun isValid(): Boolean {
            return value matches REGEX
        }
    }

    class Ecl(value: String) : Field(value = value) {
        companion object {
            private val VALID_VALUES = listOf("amb", "blu", "brn", "gry", "grn", "hzl", "oth")
        }

        // exactly one of: amb blu brn gry grn hzl oth.
        override fun isValid(): Boolean = VALID_VALUES.contains(value)
    }

    class Pid(value: String) : Field(value = value) {
        // a nine-digit number, including leading zeroes.
        override fun isValid(): Boolean = value.toBigDecimalOrNull() != null && value.length == 9
    }

    class Cid(value: String) : Field(value = value) {
        override fun isValid(): Boolean = false
    }

    abstract fun isValid(): Boolean
}

class AOCx2020x04xTest {
    companion object {
        val REQUIRED_FIELDS = listOf(
            "byr",
            "iyr",
            "eyr",
            "hgt",
            "hcl",
            "ecl",
            "pid",
        )
    }

    @Test
    fun `1  on example data`() {
        val input = """
            ecl:gry pid:860033327 eyr:2020 hcl:#fffffd
            byr:1937 iyr:2017 cid:147 hgt:183cm
            
            iyr:2013 ecl:amb cid:350 eyr:2023 pid:028048884
            hcl:#cfa07d byr:1929
            
            hcl:#ae17e1 iyr:2013
            eyr:2024
            ecl:brn pid:760753108 byr:1931
            hgt:179cm
            
            hcl:#cfa07d eyr:2025 pid:166559648
            iyr:2011 ecl:brn hgt:59in
        """.trimIndent()
            .split("\n\n")

        val result = input
            .map { it.replace("\n", " ") } // every line
            .filter { passport -> REQUIRED_FIELDS.all { fieldName -> passport.contains(fieldName) } }

        assertEquals(2, result.size, "Error!")
    }

    @Test
    fun `1 test on file`() {
        val result = File("src/test/resources/input.txt")
            .readText()
            .split("\n\n")
            .map { it.replace("\n", " ") }
            .map { it.split(" ") }
            .filter { fields ->
                fields
                    .filter { field ->
                        REQUIRED_FIELDS.any { req ->
                            field.startsWith(req)
                        }
                    }.size == 7
            }

        assertEquals(200, result.size, "Error!")
    }

    @Test
    fun `2  on example valid data`() {
        val input = """
            pid:087499704 hgt:74in ecl:grn iyr:2012 eyr:2030 byr:1980
            hcl:#623a2f
            
            eyr:2029 ecl:blu cid:129 byr:1989
            iyr:2014 pid:896056539 hcl:#a97842 hgt:165cm
            
            hcl:#888785
            hgt:164cm byr:2001 iyr:2015 cid:88
            pid:545766238 ecl:hzl
            eyr:2022
            
            iyr:2010 hgt:158cm hcl:#b6652a ecl:blu byr:1944 eyr:2021 pid:093154719
            """.trimIndent()
            .split("\n\n")

        val result = input
            .asSequence()
            .map { it.replace("\n", " ") } // every line
            .map { it.split(" ") }
            .map { lineToFields(it) }
            .filter { isValidList(it) }
            .toList()

        assertEquals(4, result.size, "Error!")
    }

    @Test
    fun `2  on example invalid data`() {
        val input = """
            eyr:1972 cid:100
            hcl:#18171d ecl:amb hgt:170 pid:186cm iyr:2018 byr:1926
            
            iyr:2019
            hcl:#602927 eyr:1967 hgt:170cm
            ecl:grn pid:012533040 byr:1946
            
            hcl:dab227 iyr:2012
            ecl:brn hgt:182cm pid:021572410 eyr:2020 byr:1992 cid:277
            
            hgt:59cm ecl:zzz
            eyr:2038 hcl:74454a iyr:2023
            pid:3556412378 byr:2007
            """.trimIndent()
            .split("\n\n")

        val result = input
            .asSequence()
            .map { it.replace("\n", " ") } // every line
            .map { it.split(" ") }
            .map { lineToFields(it) }
            .filter { isValidList(it) }
            .toList()

        assertEquals(0, result.size, "Error!")
    }

    private fun lineToFields(it: List<String>) = it.mapNotNull { field -> toField(field) }

    @Test
    fun `2 test on file`() {
        val result = File("src/test/resources/input.txt")
            .readText()
            .split("\n\n")
            .map { it.replace("\n", " ") }
            .map { it.split(" ") }
            .map { lineToFields(it) }
            .filter { fields -> isValidList(fields) }

        assertEquals(116, result.size, "Error!")
    }

    private fun isValidList(fields: List<Field>): Boolean = fields.filter { it.isValid() }.size in 7..8

    private fun toField(field: String): Field? {
        val key = field.substringBefore(":")
        val value = field.substringAfter(":")
        return when (key) {
            "byr" -> Field.Byr(value)
            "iyr" -> Field.Iyr(value)
            "eyr" -> Field.Eyr(value)
            "hgt" -> Field.Hgt(value)
            "hcl" -> Field.Hcl(value)
            "ecl" -> Field.Ecl(value)
            "pid" -> Field.Pid(value)
            "cid" -> Field.Cid(value)
            else -> null
        }
    }
}
