package com.saarthe.dot4

object Dot4Codec {

    private val encodeMap = mapOf(
        'A' to "...",
        'B' to "..,",
        'C' to "..'",
        'D' to "..;",

        'E' to ".,.",
        'F' to ".,,",
        'G' to ".,'",
        'H' to ".,;",

        'I' to ".'." ,
        'J' to ".',",
        'K' to ".''",
        'L' to ".';",

        'M' to ".;.",
        'N' to ".;,",
        'O' to ".;'",
        'P' to ".;;",

        'Q' to ",..",
        'R' to ",.,",
        'S' to ",.'",
        'T' to ",.;",

        'U' to ",,.",
        'V' to ",,,",
        'W' to ",,'",
        'X' to ",,;",

        'Y' to ",'.",
        'Z' to ",',"
    )

    private val decodeMap = encodeMap.entries.associate { (letter, code) ->
        code to letter
    }

    fun encode(text: String): String {
        return text.map { char ->
            when {
                char.isLetter() -> {
                    encodeMap[char.uppercaseChar()] ?: char.toString()
                }

                char == ' ' -> " "

                char.isDigit() -> char.toString()

                else -> char.toString()
            }
        }.joinToString("_")
            .replace("_ _", " ")
    }

    fun decode(text: String): String {
        return text.split(" ").joinToString(" ") { word ->
            word.split("_").joinToString("") { code ->
                decodeMap[code]?.toString() ?: code
            }
        }
    }
}
