package dk.aau.cs.d409f19

import dk.aau.cs.d409f19.cellumata.CompilerData
import dk.aau.cs.d409f19.cellumata.ast.*
import dk.aau.cs.d409f19.cellumata.visitors.LiteralExtractorVisitor
import dk.aau.cs.d409f19.cellumata.visitors.ScopeCheckVisitor
import dk.aau.cs.d409f19.cellumata.visitors.TypeChecker
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class Utilities {

    companion object {

        /**
         * Compiles a program given as string parameter
         */
        fun compileProgram(program: String): CompilerData {
            val source = CharStreams.fromString(program)
            val lexer = dk.aau.cs.d409f19.antlr.CellmataLexer(source)
            val tokenStream = CommonTokenStream(lexer)
            val parser = dk.aau.cs.d409f19.antlr.CellmataParser(tokenStream)

            // Build AST
            val startContext = parser.start()
            val ast = reduce(startContext)

            // Extract literals
            LiteralExtractorVisitor().visit(ast)

            // Symbol table and scope
            val scopeChecker = ScopeCheckVisitor()
            scopeChecker.visit(ast)
            val symbolTable = scopeChecker.getSymbolTable()

            // Type checking
            TypeChecker(symbolTable).visit(ast)

            return CompilerData(parser, ast, symbolTable)
        }

        fun getParser(program: String): dk.aau.cs.d409f19.antlr.CellmataParser {
            val source = CharStreams.fromString(program)
            val lexer = dk.aau.cs.d409f19.antlr.CellmataLexer(source)
            val tokenStream = CommonTokenStream(lexer)
            return dk.aau.cs.d409f19.antlr.CellmataParser(tokenStream)
        }

        /**
         * Returns a world declaration based on given parameters and with sane defaults
         * Defaults to two dimensional, but can be overriden with the twoDimensional parameter
         */
        fun getWorldDecl(
            dimOneSize: Int = 10,
            dimOneType: String = "wrap",
            dimTwoSize: Int = 20,
            dimTwoType: String = "wrap",
            tickrate: Int = 120,
            cellsize: Int = 5,
            twoDimensional: Boolean = true
        ): String {
            // If two dimensional, declare second dimension, else empty string
            val twoDimDecl = if (twoDimensional) ", $dimTwoSize[$dimTwoType]" else ""
            return """world {
            |  size = $dimOneSize[$dimOneType]$twoDimDecl
            |  tickrate = $tickrate
            |  cellsize = $cellsize
            |}
            |
            | """.trimMargin()
        }

        /**
         * Returns a constant declaration based on given parameters and with sane defaults
         */
        fun getConstDecl(ident: String = "ident", value: String = "false"): String {
            return "const $ident = $value;"
        }

        /**
         * Returns a state array declaration based on given parameters and with sane defaults
         * TODO: stateArray is default empty as compiler on master-branch did not support this yet when branching out
         */
        fun getStateDecl(
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
         * Returns a boilerplate program
         */
        fun getBoilerplate(): String {
            return getWorldDecl() + getConstDecl() + "\n\n" + getStateDecl()
        }
    }
}