package classes

data class Rosout(
    val file: String,
    val function: String,
    val level: Int,
    val line: Int,
    val msg: String,
    val name: String,
    val stamp: Stamp,
    val topic: String
)