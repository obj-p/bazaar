// Generated from Sources/Template/TemplateParser.g4 by ANTLR 4.13.1
import Antlr4

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link TemplateParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
open class TemplateParserVisitor<T>: ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link TemplateParser#schema}.
	- Parameters:
	  - ctx: the parse tree
	- returns: the visitor result
	 */
	open func visitSchema(_ ctx: TemplateParser.SchemaContext) -> T {
	 	fatalError(#function + " must be overridden")
	}

	/**
	 * Visit a parse tree produced by {@link TemplateParser#data}.
	- Parameters:
	  - ctx: the parse tree
	- returns: the visitor result
	 */
	open func visitData(_ ctx: TemplateParser.DataContext) -> T {
	 	fatalError(#function + " must be overridden")
	}

	/**
	 * Visit a parse tree produced by {@link TemplateParser#data_body}.
	- Parameters:
	  - ctx: the parse tree
	- returns: the visitor result
	 */
	open func visitData_body(_ ctx: TemplateParser.Data_bodyContext) -> T {
	 	fatalError(#function + " must be overridden")
	}

	/**
	 * Visit a parse tree produced by {@link TemplateParser#data_members}.
	- Parameters:
	  - ctx: the parse tree
	- returns: the visitor result
	 */
	open func visitData_members(_ ctx: TemplateParser.Data_membersContext) -> T {
	 	fatalError(#function + " must be overridden")
	}

	/**
	 * Visit a parse tree produced by {@link TemplateParser#member}.
	- Parameters:
	  - ctx: the parse tree
	- returns: the visitor result
	 */
	open func visitMember(_ ctx: TemplateParser.MemberContext) -> T {
	 	fatalError(#function + " must be overridden")
	}

	/**
	 * Visit a parse tree produced by {@link TemplateParser#member_identifier}.
	- Parameters:
	  - ctx: the parse tree
	- returns: the visitor result
	 */
	open func visitMember_identifier(_ ctx: TemplateParser.Member_identifierContext) -> T {
	 	fatalError(#function + " must be overridden")
	}

	/**
	 * Visit a parse tree produced by {@link TemplateParser#type_identifier}.
	- Parameters:
	  - ctx: the parse tree
	- returns: the visitor result
	 */
	open func visitType_identifier(_ ctx: TemplateParser.Type_identifierContext) -> T {
	 	fatalError(#function + " must be overridden")
	}

	/**
	 * Visit a parse tree produced by {@link TemplateParser#identifier}.
	- Parameters:
	  - ctx: the parse tree
	- returns: the visitor result
	 */
	open func visitIdentifier(_ ctx: TemplateParser.IdentifierContext) -> T {
	 	fatalError(#function + " must be overridden")
	}

}