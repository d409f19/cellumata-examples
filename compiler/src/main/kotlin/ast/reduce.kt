package dk.aau.cs.d409f19.cellumata.ast

import dk.aau.cs.d409f19.antlr.CellmataParser
import dk.aau.cs.d409f19.cellumata.TerminatedCompilationException
import dk.aau.cs.d409f19.cellumata.ErrorFromContext
import dk.aau.cs.d409f19.cellumata.ErrorLogger
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.TerminalNode

private fun reduceExpr(node: ParseTree): Expr {
    return when (node) {
        is CellmataParser.OrExprContext -> OrExpr(
            ctx = node,
            left = reduceExpr(node.left),
            right = reduceExpr(node.right)
        )
        is CellmataParser.AndExprContext -> AndExpr(
            ctx = node,
            left = reduceExpr(node.left),
            right = reduceExpr(node.right)
        )
        is CellmataParser.NotEqExprContext -> InequalityExpr(
            ctx = node,
            left = reduceExpr(node.left),
            right = reduceExpr(node.right)
        )
        is CellmataParser.EqExprContext -> EqualityExpr(
            ctx = node,
            left = reduceExpr(node.left),
            right = reduceExpr(node.right)
        )
        is CellmataParser.MoreEqExprContext -> MoreEqExpr(
            ctx = node,
            left = reduceExpr(node.left),
            right = reduceExpr(node.right)
        )
        is CellmataParser.MoreExprContext -> MoreThanExpr(
            ctx = node,
            left = reduceExpr(node.left),
            right = reduceExpr(node.right)
        )
        is CellmataParser.LessEqExprContext -> LessEqExpr(
            ctx = node,
            left = reduceExpr(node.left),
            right = reduceExpr(node.right)
        )
        is CellmataParser.LessExprContext -> LessThanExpr(
            ctx = node,
            left = reduceExpr(node.left),
            right = reduceExpr(node.right)
        )
        is CellmataParser.AdditionExprContext -> AdditionExpr(
            ctx = node,
            left = reduceExpr(node.left),
            right = reduceExpr(node.right)
        )
        is CellmataParser.SubstractionExprContext -> SubtractionExpr(
            ctx = node,
            left = reduceExpr(node.left),
            right = reduceExpr(node.right)
        )
        is CellmataParser.MultiplictionExprContext -> MultiplicationExpr(
            ctx = node,
            left = reduceExpr(node.left),
            right = reduceExpr(node.right)
        )
        is CellmataParser.DivisionExprContext -> DivisionExpr(
            ctx = node,
            left = reduceExpr(node.left),
            right = reduceExpr(node.right)
        )
        is CellmataParser.ModuloExprContext -> ModuloExpr(
            ctx = node,
            left = reduceExpr(node.left),
            right = reduceExpr(node.right)
        )
        is CellmataParser.PreIncExprContext -> PreIncExpr(
            ctx = node,
            value = reduceExpr(node.value)
        )
        is CellmataParser.PreDecExprContext -> PreDecExpr(
            ctx = node,
            value = reduceExpr(node.value)
        )
        is CellmataParser.PositiveExprContext -> PositiveExpr(
            ctx = node,
            value = reduceExpr(node.value)
        )
        is CellmataParser.NegativeExprContext -> NegativeExpr(
            ctx = node,
            value = reduceExpr(node.value)
        )
        is CellmataParser.InverseExprContext -> InverseExpr(
            ctx = node,
            value = reduceExpr(node.value)
        )
        is CellmataParser.PostIncExprContext -> PostIncExpr(
            ctx = node,
            value = reduceExpr(node.value)
        )
        is CellmataParser.PostDecExprContext -> PostDecExpr(
            ctx = node,
            value = reduceExpr(node.value)
        )
        is CellmataParser.ArrayLookupExprContext -> ArrayLookupExpr(
            ctx = node,
            index = reduceExpr(node.index)
        )
        is CellmataParser.ParenExprContext -> ParenExpr(
            ctx = node,
            expr = reduceExpr(node.expr())
        ) // ToDo: Should we flatten this?
        is CellmataParser.LiteralExprContext -> reduceExpr(node.value)
        is CellmataParser.VarExprContext -> NamedExpr(
            ctx = node,
            ident = node.ident.text
        )
        is CellmataParser.FuncExprContext -> FuncExpr(
            ctx = node,
            args = node.value.expr().map(::reduceExpr)
        )
        is CellmataParser.StateIndexExprContext -> StateIndexExpr(ctx = node)
        is CellmataParser.ArrayValueExprContext -> ArrayBodyExpr(
            ctx = node,
            values = node.array_value().array_body().expr().map(::reduceExpr)
        )
        is CellmataParser.NumberLiteralContext -> reduceExpr(node.value)
        is CellmataParser.BoolLiteralContext -> reduceExpr(node.value)
        is CellmataParser.Bool_literalContext -> BoolLiteral(ctx = node)
        is CellmataParser.Number_literalContext -> reduceExpr(node.getChild(0))
        is CellmataParser.IntegerLiteralContext -> reduceExpr(node.value)
        is CellmataParser.FloatLiteralContext -> reduceExpr(node.value)
        is CellmataParser.Integer_literalContext -> IntLiteral(ctx = node)
        is CellmataParser.Float_literalContext -> FloatLiteral(ctx = node)
        is CellmataParser.Modifiable_identContext -> reduceExpr(node.getChild(0))
        is CellmataParser.Var_identContext -> NamedExpr(ctx = node)
        // Errors
        is ParserRuleContext -> { registerReduceError(node); ErrorExpr(node) }
        else -> throw TerminatedCompilationException("Statement ${node.javaClass} had no parsing context.")
    }
}

private fun reduceStmt(node: ParseTree): Stmt {
    return when (node) {
        is CellmataParser.Assign_stmtContext -> reduceStmt(node.assignment())
        is CellmataParser.AssignmentContext -> AssignStmt(ctx = node, expr = reduceExpr(node.expr()))
        is CellmataParser.If_stmtContext -> IfStmt(
            ctx = node,
            conditionals = listOf( // Create list of list of ConditionalBlocks, then flatten to list of ConditionalBlocks
                listOf(
                    ConditionalBlock( // If clause
                        ctx = node,
                        expr = reduceExpr(node.if_stmt_if().if_stmt_block().if_stmt_condition().expr()),
                        block = reduceCodeBlock(node.if_stmt_if().if_stmt_block().code_block())
                    )
                ),
                node.if_stmt_elif().map { // Elif clauses
                    ConditionalBlock(
                        ctx = it,
                        expr = reduceExpr(it.if_stmt_block().if_stmt_condition().expr()),
                        block = reduceCodeBlock(it.if_stmt_block().code_block())
                    )
                }
            ).flatten(),
            elseBlock = when (node.if_stmt_else()) {
                null -> null
                is CellmataParser.If_stmt_elseContext -> reduceCodeBlock(node.if_stmt_else().code_block())
                else -> throw AssertionError("Unexpected tree node")
            }
        )
        is CellmataParser.For_stmtContext -> ForStmt(
            ctx = node,
            initPart = AssignStmt(node.for_init().assignment(), expr = reduceExpr(node.for_init().assignment().expr())),
            condition = reduceExpr(node.for_condition().expr()),
            postIterationPart = AssignStmt(node.for_post_iteration().assignment(), expr = reduceExpr(node.for_post_iteration().assignment().expr()))
        )
        is CellmataParser.Break_stmtContext -> BreakStmt(ctx = node)
        is CellmataParser.Continue_stmtContext -> ContinueStmt(ctx = node)
        is CellmataParser.Become_stmtContext -> BecomeStmt(ctx = node, state = reduceExpr(node.state))
        is CellmataParser.PreIncStmtContext -> PreIncStmt(ctx = node, variable = reduceExpr(node.modifiable_ident()))
        is CellmataParser.PostIncStmtContext -> PostIncStmt(ctx = node, variable = reduceExpr(node.modifiable_ident()))
        is CellmataParser.PreDecStmtContext -> PreDecStmt(ctx = node, variable = reduceExpr(node.modifiable_ident()))
        is CellmataParser.PostDecStmtContext -> PostDecStmt(ctx = node, variable = reduceExpr(node.modifiable_ident()))
        is CellmataParser.StmtContext -> reduceStmt(node.getChild(0))
        is CellmataParser.Return_stmtContext -> ReturnStmt(ctx = node, value = reduceExpr(node.expr()))
        // Errors
        is ParserRuleContext -> { registerReduceError(node); ErrorStmt(node) }
        else -> throw TerminatedCompilationException("Statement ${node.javaClass} had no parsing context.")
    }
}

private fun reduceDecl(node: ParseTree): Decl {
    return when (node) {
        is CellmataParser.Const_declContext -> ConstDecl(ctx = node, expr = reduceExpr(node.expr()))
        is CellmataParser.State_declContext -> StateDecl(
            ctx = node,
            body = reduceCodeBlock(node.children // Get the body/code block
                .stream()
                .filter { it is CellmataParser.Code_blockContext }
                .findFirst().orElseThrow() as CellmataParser.Code_blockContext)
        )
        is CellmataParser.Neighbourhood_declContext -> NeighbourhoodDecl(
            ctx = node,
            coords = node.neighbourhood_code().coords_decl().map { Coordinate(ctx = it) }
        )
        is CellmataParser.Func_declContext -> FuncDecl(
            ctx = node,
            body = reduceCodeBlock(node.code_block())
        )
        // Errors
        is ParserRuleContext -> { registerReduceError(node); ErrorDecl(node) }
        else -> throw TerminatedCompilationException("Statement ${node.javaClass} had no parsing context.")
    }
}

fun reduceCodeBlock(block: CellmataParser.Code_blockContext): List<Stmt> {
    return block.children
        .filter { it !is TerminalNode } // Remove terminals
        .map { reduceStmt(it) }
}

fun reduce(node: ParserRuleContext): AST {
    return when (node) {
        is CellmataParser.StartContext -> RootNode(
            ctx = node,
            world = WorldNode(ctx = node.world_dcl()),
            body = node.body().children.map(::reduceDecl)
        )
        else -> { registerReduceError(node); ErrorAST(node) }
    }
}

/**
 * Register a reduce error: Unexpected parsing context. This should only happen if we forget to update the reduce.kt
 * after changing the grammar.
 */
fun registerReduceError(ctx: ParserRuleContext) {
    ErrorLogger.registerError(ErrorFromContext(ctx, "Unexpected parsing context (${ctx.javaClass}). Parse tree could not be mapped to AST."))
}