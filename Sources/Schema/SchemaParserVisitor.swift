// Generated from SchemaParser.g4 by ANTLR 4.13.1
import Antlr4

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link SchemaParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
open class SchemaParserVisitor<T>: ParseTreeVisitor<T> {
    /**
      * Visit a parse tree produced by {@link SchemaParser#schema}.
     - Parameters:
       - ctx: the parse tree
     - returns: the visitor result
      */
    open func visitSchema(_: SchemaParser.SchemaContext) -> T {
        fatalError(#function + " must be overridden")
    }

    /**
      * Visit a parse tree produced by {@link SchemaParser#data}.
     - Parameters:
       - ctx: the parse tree
     - returns: the visitor result
      */
    open func visitData(_: SchemaParser.DataContext) -> T {
        fatalError(#function + " must be overridden")
    }

    /**
      * Visit a parse tree produced by {@link SchemaParser#data_body}.
     - Parameters:
       - ctx: the parse tree
     - returns: the visitor result
      */
    open func visitData_body(_: SchemaParser.Data_bodyContext) -> T {
        fatalError(#function + " must be overridden")
    }

    /**
      * Visit a parse tree produced by {@link SchemaParser#data_members}.
     - Parameters:
       - ctx: the parse tree
     - returns: the visitor result
      */
    open func visitData_members(_: SchemaParser.Data_membersContext) -> T {
        fatalError(#function + " must be overridden")
    }

    /**
      * Visit a parse tree produced by {@link SchemaParser#member}.
     - Parameters:
       - ctx: the parse tree
     - returns: the visitor result
      */
    open func visitMember(_: SchemaParser.MemberContext) -> T {
        fatalError(#function + " must be overridden")
    }

    /**
      * Visit a parse tree produced by {@link SchemaParser#member_identifier}.
     - Parameters:
       - ctx: the parse tree
     - returns: the visitor result
      */
    open func visitMember_identifier(_: SchemaParser.Member_identifierContext) -> T {
        fatalError(#function + " must be overridden")
    }

    /**
      * Visit a parse tree produced by {@link SchemaParser#type_identifier}.
     - Parameters:
       - ctx: the parse tree
     - returns: the visitor result
      */
    open func visitType_identifier(_: SchemaParser.Type_identifierContext) -> T {
        fatalError(#function + " must be overridden")
    }

    /**
      * Visit a parse tree produced by {@link SchemaParser#identifier}.
     - Parameters:
       - ctx: the parse tree
     - returns: the visitor result
      */
    open func visitIdentifier(_: SchemaParser.IdentifierContext) -> T {
        fatalError(#function + " must be overridden")
    }
}
