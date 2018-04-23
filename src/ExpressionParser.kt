import java.math.BigDecimal
import java.util.*
import kotlin.collections.HashMap

object ExpressionParser {

    private val MAIN_MATH_OPERATIONS: Map<String, Int>

    init {
        MAIN_MATH_OPERATIONS = HashMap()
        with(MAIN_MATH_OPERATIONS) {
            put("*", 2)
            put("/", 2)
            put("+", 1)
            put("-", 1)
        }


    }

    fun sortingStation(input: String, operations: Map<String, Int> = MAIN_MATH_OPERATIONS): String {
        val leftBracket = "("
        val rightBracket = ")"
        var opCount = 0
        if (input.isEmpty())
            throw Exception("Expression isn't specified.")
        if (operations.isEmpty())
            throw Exception("Operations aren't specified.")

        val out = ArrayList<String>()
        val stack = Stack<String>()
        val expression = input.replace(" ", "")
        val operationSymbols = HashSet<String>(operations.keys)
        operationSymbols.add(leftBracket)
        operationSymbols.add(rightBracket)

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
                    else -> {
                        while (!stack.empty() && stack.peek() != leftBracket &&
                                (operations[nextOperation]!! <= operations[stack.peek()]!!)) {
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
        val rpn = sortingStation(expression, MAIN_MATH_OPERATIONS)
        val tokenizer = StringTokenizer(rpn, " ")
        val stack = Stack<BigDecimal>()
        while (tokenizer.hasMoreTokens()) {
            val token = tokenizer.nextToken()

            if (!MAIN_MATH_OPERATIONS.keys.contains(token)) {
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