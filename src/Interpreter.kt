object Interpreter {
    var variables = mutableMapOf<String, Double>()
    private val variableBounds = 0.0..65535.0
    var FUNCTIONS = mutableMapOf<String, (Array<out Any>) -> Any>()
    private var scope = 1
    private var ignore = Array(16, { false })

    init {

        val read = fun(a: Any?): String {
            return readLine()!!
        }

        val print = fun(vararg a: Any?) {
            when (a[0]) {
                is String -> {
                    var arg = a[0].toString()
                    arg = arg.substring(1, arg.length - 1).replace("\\n","\n")
                    print(arg)
                }
                is Double -> print(a[0])
            }
        }

        FUNCTIONS["read"] = read
        FUNCTIONS["print"] = print
    }

    fun parseLine(line: String, lineNumber: Int) {
        with(line) {
            if (count({ it.isWhitespace() }) == count())
                return
            try {
                val identifier = Regex("""(\w+)""").find(line)?.value
                if (contains("}")) {
                    scope--
                    return
                }
                if (contains("{")) {
                    if (ignore[scope])
                        ignore[scope + 1] = true
                    scope++
                }
                if (!ignore[scope - 1] && identifier == "else") {
                    ignore[scope] = !ignore[scope]
                    return
                }
                if (!ignore[scope] && identifier != null) {
                    when (identifier) {
                        "var" -> {
                            declareVars(line)
                            return
                        }
                        "if" -> {
                            if (!parseCondition(line))
                                ignore[scope] = true
                            return
                        }

                        in variables.keys -> {
                            assignVar(line)
                        }
                        in FUNCTIONS.keys -> {
                            val args = line.substring(line.indexOf("(") + 1, line.length - 1)
                            if (args[0] != '\"') {
                                val num = ExpressionParser.calculateExpression(args)
                                runFunc(identifier, num)
                            } else {
                                runFunc(identifier, args)
                            }
                        }
                        else -> throw Exception("Unknown identifier : $identifier")
                    }

                }
            } catch (e: Exception) {
                println("Error in line $lineNumber: ${e.message}")
            }
        }

    }

    private fun parseCondition(line: String): Boolean {
        val conditionStr = with(line) { substring(indexOf("(") + 1, lastIndexOf(")")) }
        val lexemes = Regex("""(\w+)\s*(<=|>=|<|>|==|!=)\s*(\w+)""").findAll(conditionStr).first().groupValues
        try {
            val lhs = ExpressionParser.calculateExpression(lexemes[1].trim())
            val rhs = ExpressionParser.calculateExpression(lexemes[3].trim())
            return when (lexemes[2]) {
                "<=" -> lhs <= rhs
                ">=" -> lhs >= rhs
                "<" -> lhs < rhs
                ">" -> lhs > rhs
                "==" -> lhs == rhs
                "!=" -> lhs != rhs
                else -> throw (Exception("Invalid condition"))
            }
        } catch (e: Exception) {
            throw Exception("Invalid condition")
        }
    }

    private fun declareVars(line: String) {
        if (line.contains("=")) {
            assignVar(line.substring(3))
        } else {
            val vars = line.substring(3).split(",")
            vars.forEach { it -> variables[it] = 0.0 }
        }
    }

    private fun assignVar(line: String) {
        val lexemes = line.split("=")
        val lhs = lexemes[0].trim()
        val rhs = lexemes[1].trim()
        val res = ExpressionParser.calculateExpression(rhs)
        if (res in variableBounds)
            variables[lhs] = res
        else
            throw (Exception("Value of variable $lhs out of bounds (0 to 65535)"))
    }

    fun runFunc(func: String, vararg args: Any): Any {
        val f = FUNCTIONS[func]
        return f?.invoke(args)!!
    }

}
