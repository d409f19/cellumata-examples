package dk.aau.cs.d409f19.cellumata.visitors

import dk.aau.cs.d409f19.cellumata.ast.*
import dk.aau.cs.d409f19.cellumata.ast.WorldType.EDGE
import dk.aau.cs.d409f19.cellumata.ast.WorldType.WRAPPING

class PrettyPrinter : BaseASTVisitor() {

    // String builder for accumulating the pretty-printed program source
    var stringBuilder = StringBuilder()

    /**
     * Print accumulated program from string builder
     */
    fun print() {
        println(stringBuilder.toString())
    }

    /**
     * Print newlines for each distinct block
     */
    override fun visit(node: RootNode) {
        // First print world declaration and add linebreak afterwards
        visit(node.world)
        stringBuilder.appendln()

        // For each declaration in body, print it and add linebreak afterwards
        node.body.forEach {
            visit(it)
            stringBuilder.appendln()
        }
    }

    /*
     * Declarations
     */
    /**
     * Build pretty-printed world declaration
     */
    override fun visit(node: WorldNode) {
        // Begin world block boilerplate
        stringBuilder.appendln("world {")

        // Begin size-declaration boilerplate
        stringBuilder.append("\tsize = ")

        // For each dimension, print size and type
        for (n in node.dimensions.indices) {

            val isLast: Boolean = node.dimensions.lastIndex == n

            // Set separator to ", " if not last, else empty
            val separator = if (!isLast) ", " else ""

            // Print size and depending on type, print both type and identifier within brackets appended by separator
            stringBuilder.append(
                "${node.dimensions[n].size} ${if (node.dimensions[n].type == WRAPPING) {
                    "[wrap]$separator"
                } else if (node.dimensions[n].type == EDGE) { // Note the non-null assertion, required for field access
                    "[edge=${node.dimensions[n].edge!!.spelling}]$separator"
                } else {
                    "[${node.dimensions[n].type}]$separator"
                }
                }"
            )
        }

        // When done with printing dimension, add linebreak
        stringBuilder.appendln()

        // Print tickrate and cellsize if not null
        if (node.tickrate != null) {
            stringBuilder.appendln("\ttickrate = ${node.tickrate}")
        }

        if (node.cellSize != null) {
            stringBuilder.appendln("\tcellsize = ${node.cellSize}")
        }

        // When done with all world declaration printing, print closing curly bracket and newline
        stringBuilder.appendln("}")

    }

    /**
     * Build pretty printed constant declaration
     */
    override fun visit(node: ConstDecl) {
        // Begin constant declaration with const-keyword
        stringBuilder.append("const ${node.ident} = ")

        // Print expression
        visit(node.expr)

        // When done with printing expression, print semicolon and newline
        stringBuilder.appendln(";")
    }

    /**
     * Build pretty printed state declaration
     */
    override fun visit(node: StateDecl) {
        // Print signature of state. TODO: must take multi-state-declaration into account when implemented
        stringBuilder.appendln("state ${node.ident} (${node.red}, ${node.green}, ${node.blue}) {")

        // Print body
        visit(node.body)

        // When done with printing body of state, print closing curly bracket and newline
        stringBuilder.append("}\n")
    }

    /**
     * Build pretty printed function declaration
     */
    override fun visit(node: FuncDecl) {
        // Print signature until formal arguments
        stringBuilder.append("function ${node.ident}(")

        for (n in node.args.indices) {

            // Boolean of is the current iteration at the last index
            val isLast = node.args.lastIndex == n

            // Print each argument
            visit(node.args[n])

            // Print separator for given iteration, empty if last, else given separator with space appended
            stringBuilder.append(if (!isLast) ", " else "")
        }

        // Print remaining signature with return type
        stringBuilder.appendln(") ${node.returnType} {")

        // Print body of function
        visit(node.body)

        // Print closing curly bracket and newline
        stringBuilder.append("}\n")
    }

    /**
     * Print a formal argument of a function declaration
     */
    override fun visit(node: FunctionArgument) {
        stringBuilder.append("${node.getType()} ${node.ident}")
    }

    /*
     * Statements
     */
    override fun visit(node: AssignStmt) {
        // If assignment is declaration, put 'let' keyword in front of statement
        stringBuilder.append("\t${if (node.isDeclaration) "let" else ""} ${node.ident} = ")

        // Print expression
        visit(node.expr)

        // When done printing expression, print semicolon and newline
        stringBuilder.appendln(";")
    }

    override fun visit(node: BecomeStmt) {
        // Begin become statement with the 'become'-keyword
        stringBuilder.append("\tbecome ")

        // Print state-name
        visit(node.state)

        // When done printing expression, print semicolon and newline
        stringBuilder.appendln(";")
    }

    override fun visit(node: ReturnStmt) {
        stringBuilder.append("\treturn ")

        // Continue printing expression
        visit(node.value)

        // When done with printing expression, print semicolon and newline
        stringBuilder.appendln(";")
    }

    /*
     * Expressions
     */
    override fun visit(node: FuncCallExpr) {
        // Print function-name postfixed an opening parenthesis
        stringBuilder.append("${node.ident}(")

        // Print each actual parameter
        node.args.forEach(::visit)

        // Print closing parenthesis
        stringBuilder.append(")")
    }

    override fun visit(node: Identifier) {
        // Simply print spelling of identifier
        stringBuilder.append(node.spelling)
    }

    override fun visit(node: BoolLiteral) {
        stringBuilder.append(node.value)
    }

    override fun visit(node: IntLiteral) {
        stringBuilder.append(node.value)
    }

    override fun visit(node: FloatLiteral) {
        stringBuilder.append(node.value)
    }

    override fun visit(node: AdditionExpr) {
        // Print left-side first
        visit(node.left)

        // Print addition symbol with spaces padding expressions
        stringBuilder.append(" + ")

        // Print right-side
        visit(node.right)
    }

    override fun visit(node: SubtractionExpr) {
        // Print left-side first
        visit(node.left)

        // Print subtraction symbol with spaces padding expressions
        stringBuilder.append(" - ")

        // Print right-side
        visit(node.right)
    }

    override fun visit(node: MultiplicationExpr) {
        // Print left-side first
        visit(node.left)

        // Print multiplication symbol with spaces padding expressions
        stringBuilder.append(" * ")

        // Print right-side
        visit(node.right)
    }

    override fun visit(node: DivisionExpr) {
        // Print left-side first
        visit(node.left)

        // Print division symbol with spaces padding expressions
        stringBuilder.append(" / ")

        // Print right-side
        visit(node.right)
    }

    override fun visit(node: NegationExpr) {
        stringBuilder.append("-")

        // Continue printing value
        visit(node.value)
    }

    override fun visit(node: NotExpr) {
        stringBuilder.append("!")

        // Continue printing value
        visit(node.value)
    }

    override fun visit(node: ArrayLookupExpr) {
        // First print array expression
        visit(node.arr)

        // Lastly print index expression within brackets
        stringBuilder.append("[")
        visit(node.index)
        stringBuilder.append("]")
    }

    /**
     * Prints each element of the array body expression comma-separated
     */
    override fun visit(node: ArrayBodyExpr) {
        // Open expression with opening curly bracket
        stringBuilder.append("{")

        for (n in node.values.indices) {

            // Boolean of is the current iteration at the last index
            val isLast = node.values.lastIndex == n

            // Print each element
            visit(node.values[n])

            // Print separator for given iteration, empty if last, else given separator with space appended
            stringBuilder.append(if (!isLast) ", " else "")

        }
        // When done with list, print closing curly bracket
        stringBuilder.append("}")
    }

    override fun visit(node: OrExpr) {
        // Print left-side first
        visit(node.left)

        // Print boolean 'or' symbol with spaces padding expressions
        stringBuilder.append(" || ")

        // Print right-side
        visit(node.right)
    }

    override fun visit(node: AndExpr) {
        // Print left-side first
        visit(node.left)

        // Print boolean 'and' symbol with spaces padding expressions
        stringBuilder.append(" && ")

        // Print right-side
        visit(node.right)
    }

    override fun visit(node: InequalityExpr) {
        // Print left-side first
        visit(node.left)

        // Print boolean inequality symbol with spaces padding expressions
        stringBuilder.append(" != ")

        // Print right-side
        visit(node.right)
    }

    override fun visit(node: EqualityExpr) {
        // Print left-side first
        visit(node.left)

        // Print boolean equality symbol with spaces padding expressions
        stringBuilder.append(" == ")

        // Print right-side
        visit(node.right)
    }

    override fun visit(node: GreaterThanExpr) {
        // Print left-side first
        visit(node.left)

        // Print boolean 'greater than' symbol with spaces padding expressions
        stringBuilder.append(" > ")

        // Print right-side
        visit(node.right)
    }

    override fun visit(node: GreaterOrEqExpr) {
        // Print left-side first
        visit(node.left)

        // Print boolean 'greater than or equal' symbol with spaces padding expressions
        stringBuilder.append(" >= ")

        // Print right-side
        visit(node.right)
    }

    override fun visit(node: LessThanExpr) {
        // Print left-side first
        visit(node.left)

        // Print boolean 'less than' symbol with spaces padding expressions
        stringBuilder.append(" < ")

        // Print right-side
        visit(node.right)
    }

    override fun visit(node: LessOrEqExpr) {
        // Print left-side first
        visit(node.left)

        // Print boolean 'less than or equal' symbol with spaces padding expressions
        stringBuilder.append(" <= ")

        // Print right-side
        visit(node.right)
    }
}