import java.math.BigDecimal

object Interpreter {
    private var variables = mutableMapOf<String, BigDecimal>()
    private var functions = mutableMapOf<String, (Array<out Any>) -> Any>()

    init {
        var read = fun(vararg a: Any): String {
            return readLine()!!
        }

        var print = fun(vararg a: Any) {

        }

        functions["read"] = read
        functions["print"] = print
    }


    fun parseLine(line: String) {
        if (line.count({ it.isWhitespace() }) == line.count())
            return
        if (line.substring(0, 3) == "var") {
            var lexemes = line.substring(3).split("=")
            var lhs = lexemes[0].trim()
            var rhs = lexemes[1].trim()
            if (lexemes.size == 1) {
                variables[lhs] = BigDecimal.ZERO
            } else {
                if (rhs[0].isDigit())
                    variables[lhs] = lexemes[2].toBigDecimal()
                else {
                    calculateExpr(rhs)
                    var rhs = lexemes[1].split("(", ")")
                    if ()
                    variables[lhs] = functions[lexemes[1]]?.invoke(emptyArray()).toString().toBigDecimal()
                }
            }
        }
    }

}
