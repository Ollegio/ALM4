import java.math.BigDecimal
import java.util.regex.Pattern

object Interpreter {
    private var variables = mutableMapOf<String, BigDecimal>()
    var FUNCTIONS = mutableMapOf<String, (Array<out Any>) -> Any>()

    init {
        var read = fun(vararg a: Any?): String {
            return readLine()!!
        }

        var print = fun(vararg a: Any?) {

        }

        FUNCTIONS["read"] = read
        FUNCTIONS["print"] = print
    }


    fun parseLine(line: String, lineNumber: Int) {
        if (line.count({ it.isWhitespace() }) == line.count())
            return
        if (line.substring(0, 3) == "var") {
            assignVar(line)
            return
        }
        if (line.substring(0, 2) == "if") {
            parseCondition(line)
        }
    }

    private fun parseCondition(line: String): Integer {
        val conditionStr = with(line) { substring(indexOf("("), lastIndexOf(")")) }
        val lexemes = conditionStr.split(Pattern.compile("(.+)(<=|>=|<|>|==|!=)(.+)"))
        val lhs = lexemes[0]
        val rhs = lexemes[2]
        val result = when (lexemes[1]) {
            "<=" -> lhs <= rhs
            ">=" -> lhs >= rhs
            "<" -> lhs < rhs
            ">" -> lhs > rhs
            "==" -> lhs == rhs
            "!=" -> lhs != rhs
            else -> throw (Exception("Invalid condition"))
        }
        
    }

    fun assignVar(line: String) {
        val lexemes = line.substring(3).split("=")
        val lhs = lexemes[0].trim()
        val rhs = lexemes[1].trim()
        if (lexemes.size == 1) {
            variables[lhs] = BigDecimal.ZERO
        } else {
            variables[lhs] = ExpressionParser.calculateExpression(rhs)
        }
    }

    fun runFunc(func: String, vararg args: Any): Any {
        val f = FUNCTIONS[func]
        return f?.invoke(args)!!
    }

}
