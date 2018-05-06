import java.io.FileInputStream

fun main(vararg: Array<String>) {

    val file = FileInputStream("script.txt")
    var reader = file.bufferedReader()
    var text = reader.readText()
    var lines = reader.lineSequence().toList()
    var lineNumber = 1
    for (line in lines) {
        try {
            Interpreter.parseLine(line.trim(), lineNumber++)
        } catch(e: Exception) {
            break
        }
    }
}

