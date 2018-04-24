import java.math.BigDecimal
import java.util.*
import kotlin.collections.HashMap

object ExpressionParser {

    private val OPERATIONS: Map<String, Int>
    val operationSymbols: HashSet<String>
    val functions = HashSet<String>(Interpreter.FUNCTIONS.keys)

    init {
        OPERATIONS = HashMap()
        with(OPERATIONS) {
            put("*", 2)
            put("/", 2)
            put("+", 1)
            put("-", 1)
        }

        operationSymbols = HashSet<String>(OPERATIONS.keys)
        operationSymbols.add("(")
        operationSymbols.add(")")

    }

    fun sortingStation(input: String): String {
        val leftBracket = "("
        val rightBracket = ")"
        var opCount = 0
        if (input.isEmpty())
            throw Exception("Expression isn't specified.")

        val out = ArrayList<String>()
        val stack = Stack<String>()
        val expression = input.replace(" ", "")


        var index = 0
        var findNext = true
        while (findNext) {
            var nextOperationIndex = expression.length
            var nextOperation = ""

            for (operation in operationSymbols) {
                val i = expression.indexOf(operation, index)
                if (i in 0 until nextOperationIndex) {
                    nextOperation = operation
                    nextOperationIndex = i
                }
            }

            for (func in functions) {
                val i = expression.indexOf(func, index)
                if (i in 0 until nextOperationIndex) {
                    nextOperation = func
                    nextOperationIndex = i
                }
            }

            if (nextOperationIndex == expression.length) {
                findNext = false
            } else {
                if (index != nextOperationIndex) {
                    out.add(expression.substring(index, nextOperationIndex))
                }
                when (nextOperation) {
                    leftBracket -> stack.push(nextOperation)
                    rightBracket -> {
                        while (stack.peek() != leftBracket) {
                            out.add(stack.pop())
                            opCount++
                            if (stack.empty()) {
                                throw IllegalArgumentException("Unmatched brackets")
                            }
                        }
                        stack.pop()
                    }
                    in functions -> {
                        val args = with(expression) {
                            try {
                                substring(indexOf("(" + 1, nextOperationIndex), indexOf(")", nextOperationIndex)).split(",")
                            } catch (e: Exception) {
                                emptyArray<Any>()
                            }
                        }
                        val res = Interpreter.runFunc(nextOperation, arrayOf(args))
                        out.add(res.toString())
                    }
                    else -> {
                        while (!stack.empty() && stack.peek() != leftBracket &&
                                (OPERATIONS[nextOperation]!! <= OPERATIONS[stack.peek()]!!)) {
                            out.add(stack.pop())
                            opCount++
                        }
                        stack.push(nextOperation)
                    }
                }
                index = nextOperationIndex + nextOperation.length
            }
        }
        if (index != expression.length) {
            out.add(expression.substring(index))
        }
        if (stack.size + opCount + 1 != out.size - opCount) {
            throw Exception("Wrong expression")
        }
        while (!stack.empty()) {
            out.add(stack.pop())
        }

        val result = StringBuffer()
        if (!out.isEmpty())
            result.append(out.removeAt(0))
        while (!out.isEmpty())
            result.append(" ").append(out.removeAt(0))
        return result.toString()
    }

    fun calculateExpression(expression: String): BigDecimal {
        val rpn = sortingStation(expression)
        val tokenizer = StringTokenizer(rpn, " ")
        val stack = Stack<BigDecimal>()
        while (tokenizer.hasMoreTokens()) {
            val token = tokenizer.nextToken()

            if (!OPERATIONS.keys.contains(token)) {
                stack.push(BigDecimal(token))
            } else {
                val operand2 = stack.pop()
                val operand1 = if (stack.empty()) BigDecimal.ZERO else stack.pop()
                when (token) {
                    "*" -> stack.push(operand1 * operand2)
                    "/" -> stack.push(operand1 / operand2)
                    "+" -> stack.push(operand1 + operand2)
                    "-" -> stack.push(operand1 - operand2)
                }
            }
        }
        if (stack.size != 1)
            throw IllegalArgumentException("Expression syntax error.")
        return stack.pop()
    }

    fun checkExpression(expression: String): Boolean {
        return try {
            sortingStation(expression); true
        } catch (e: Exception) {
            false
        }
    }
}