package dk.aau.cs.d409f19.cellumata.ast

import dk.aau.cs.d409f19.antlr.CellmataParser
import org.antlr.v4.runtime.tree.ParseTree

/**
 * Base class for all type classes. This class should be viewed as effectively being an enum.
 */
sealed class Type(val name: String) {

    override fun toString(): String {
        return name
    }
}

object IntegerType : Type("int")
object FloatType : Type("float")
object BooleanType : Type("bool")
object StateType : Type("state")
object ActualNeighbourhoodType : Type("neighbourhood") // An evaluated neighbourhood
data class ArrayType(val subtype: Type) : Type("array<$subtype>")

/**
 * Default value for types before they're checked by the type checker
 */
object UncheckedType : Type("UncheckedType")

/**
 * Convert a parse tree node to the type it represents
 */
fun typeFromCtx(ctx: ParseTree): Type {
    return when(ctx) {
        is CellmataParser.TypeArrayContext -> ArrayType(subtype = typeFromCtx(ctx.array_decl().type_ident()))
        is CellmataParser.TypeBooleanContext -> BooleanType
        is CellmataParser.TypeIntegerContext -> IntegerType
        is CellmataParser.TypeFloatContext -> FloatType
        is CellmataParser.TypeNeighbourContext -> ActualNeighbourhoodType
        is CellmataParser.TypeStateContext -> StateType
        else -> error("Unknown type")
    }
}

