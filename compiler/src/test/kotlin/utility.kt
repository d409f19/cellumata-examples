package dk.aau.cs.d409f19

import dk.aau.cs.d409f19.antlr.CellmataLexer
import dk.aau.cs.d409f19.antlr.CellmataParser
import dk.aau.cs.d409f19.cellumata.CompilerData
import dk.aau.cs.d409f19.cellumata.ast.*
import dk.aau.cs.d409f19.cellumata.visitors.FlowChecker
import dk.aau.cs.d409f19.cellumata.visitors.ScopeCheckVisitor
import dk.aau.cs.d409f19.cellumata.visitors.TypeChecker
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

/**
 * Compiles a Cellmata program given as string parameter
 */
fun compileProgram(program: String): CompilerData {
    val source = CharStreams.fromString(program)
    val lexer = CellmataLexer(source)
    val tokenStream = CommonTokenStream(lexer)
    val parser = CellmataParser(tokenStream)

    // Build AST
    val startContext = parser.start()
    val ast = reduce(startContext)

    // Symbol table and scope
    val scopeChecker = ScopeCheckVisitor()
    scopeChecker.visit(ast)
    val symbolTable = scopeChecker.getSymbolTable()

    // Type checking
    TypeChecker(symbolTable).visit(ast)

    // Flow checking
    val flowChecker = FlowChecker()
    flowChecker.visit(ast)

    return CompilerData(parser, ast, symbolTable)
}

/**
 * Sets up a Cellmata parser and returns it
 */
fun getParser(program: String): CellmataParser {
    val source = CharStreams.fromString(program)
    val lexer = CellmataLexer(source)
    val tokenStream = CommonTokenStream(lexer)
    return CellmataParser(tokenStream)
}

/**
 * Returns a Cellmata world declaration as a string based on given parameters and with sane defaults
 * Defaults to two dimensional, but can be overridden with the twoDimensional parameter
 */
fun getWorldDeclString(
    dimOneSize: String = "10",
    dimOneType: String = "wrap",
    dimTwoSize: String = "20",
    dimTwoType: String = "wrap",
    tickrate: String = "120",
    cellsize: String = "4",
    twoDimensional: Boolean? = false
): String {
    // If two dimensional, declare second dimension, else empty string
    val twoDimDecl = if (twoDimensional!!) ", $dimTwoSize[$dimTwoType]" else ""
    return """world {
            |  size = $dimOneSize[$dimOneType]$twoDimDecl
            |  tickrate = $tickrate
            |  cellsize = $cellsize
            |}
            |
            | """.trimMargin()
}

/**
 * Returns a Cellmata constant declaration as a string based on given parameters and with sane defaults
 */
fun getConstDeclString(ident: String = "ident", value: String = "false"): String {
    return "const $ident = $value;"
}

/**
 * Returns a Cellmata state array declaration as a string based on given parameters and with sane defaults
 * TODO: stateArray is default empty as compiler on master-branch did not support this yet when branching out
 */
fun getStateDeclString(
    ident: String = "stage",
    stateArray: String = "",
    red: Int = 255,
    green: Int = 200,
    blue: Int = 100,
    body: String = "",
    become: String = "become $ident;"
): String {
    return """state $ident $stateArray ($red, $green, $blue) {
            |  $body
            |  $become
            |}
            |""".trimMargin()
}

/**
 * Returns a boilerplate Cellmata program as a string
 */
fun getBoilerplateProgramString(): String {
    return getWorldDeclString() + getConstDeclString() + "\n\n" + getStateDeclString()
}
