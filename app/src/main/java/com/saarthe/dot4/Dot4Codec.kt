package com.saarthe.dot4

object Dot4Codec {
    // LOCKED Saarthe DOT4 A-Z mapping. Do not change.
    private val map = mapOf(
        'A' to "...", 'B' to "..,", 'C' to "..'", 'D' to "..;",
        'E' to ".,.", 'F' to ".,,", 'G' to ".,'", 'H' to ".,;",
        'I' to ".'." , 'J' to ".',", 'K' to ".''", 'L' to ".';",
        'M' to ".;.", 'N' to ".;,", 'O' to ".;'", 'P' to ".;;",
        'Q' to ",..", 'R' to ",.,", 'S' to ",.'", 'T' to ",.;",
        'U' to ",,.", 'V' to ",,,", 'W' to ",,'", 'X' to ",,;",
        'Y' to ",'.", 'Z' to ",',"
    )
    private val reverse = map.entries.associate { it.value to it.key }

    fun encode(input: String): String {
        val out = StringBuilder()
        input.forEachIndexed { i, ch ->
            when {
                ch.isLetter() && ch.uppercaseChar() in map -> {
                    if (out.isNotEmpty() && out.last() != ' ' && out.last() != '-') out.append('_')
                    out.append(map[ch.uppercaseChar()])
                }
                ch == ' ' -> out.append(' ')
                ch == '-' || ch == '_' -> out.append(ch)
                ch.isDigit() -> out.append(ch)
                else -> {
                    if (out.isNotEmpty() && out.last() != ' ' && out.last() != '-') out.append('_')
                    out.append(ch)
                }
            }
        }
        return out.toString()
    }

    fun decode(input: String): String =
        input.split("-").joinToString("-") { para ->
            para.split(" ").joinToString(" ") { word ->
                word.split("_").joinToString("") { reverse[it] ?: it }
            }
        }
}
