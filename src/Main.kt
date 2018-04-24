import java.io.FileInputStream

fun main(vararg: Array<String>) {

    val file = FileInputStream("script.txt")
    var reader = file.bufferedReader()
    var lines = reader.lineSequence().toList()
    var lineNumber = 1;
    for (line in lines) {
        Interpreter.parseLine(line.trim(), lineNumber++)
    }
}

