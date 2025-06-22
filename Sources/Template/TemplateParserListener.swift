// Generated from TemplateParser.g4 by ANTLR 4.13.1
import Antlr4

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link TemplateParser}.
 */
public protocol TemplateParserListener: ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link TemplateParser#template}.
	 - Parameters:
	   - ctx: the parse tree
	 */
	func enterTemplate(_ ctx: TemplateParser.TemplateContext)
	/**
	 * Exit a parse tree produced by {@link TemplateParser#template}.
	 - Parameters:
	   - ctx: the parse tree
	 */
	func exitTemplate(_ ctx: TemplateParser.TemplateContext)
	/**
	 * Enter a parse tree produced by {@link TemplateParser#view}.
	 - Parameters:
	   - ctx: the parse tree
	 */
	func enterView(_ ctx: TemplateParser.ViewContext)
	/**
	 * Exit a parse tree produced by {@link TemplateParser#view}.
	 - Parameters:
	   - ctx: the parse tree
	 */
	func exitView(_ ctx: TemplateParser.ViewContext)
	/**
	 * Enter a parse tree produced by {@link TemplateParser#identifier}.
	 - Parameters:
	   - ctx: the parse tree
	 */
	func enterIdentifier(_ ctx: TemplateParser.IdentifierContext)
	/**
	 * Exit a parse tree produced by {@link TemplateParser#identifier}.
	 - Parameters:
	   - ctx: the parse tree
	 */
	func exitIdentifier(_ ctx: TemplateParser.IdentifierContext)
}