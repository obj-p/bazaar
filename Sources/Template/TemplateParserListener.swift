// Generated from Sources/Template/TemplateParser.g4 by ANTLR 4.13.1
import Antlr4

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link TemplateParser}.
 */
public protocol TemplateParserListener: ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link TemplateParser#schema}.
	 - Parameters:
	   - ctx: the parse tree
	 */
	func enterSchema(_ ctx: TemplateParser.SchemaContext)
	/**
	 * Exit a parse tree produced by {@link TemplateParser#schema}.
	 - Parameters:
	   - ctx: the parse tree
	 */
	func exitSchema(_ ctx: TemplateParser.SchemaContext)
	/**
	 * Enter a parse tree produced by {@link TemplateParser#data}.
	 - Parameters:
	   - ctx: the parse tree
	 */
	func enterData(_ ctx: TemplateParser.DataContext)
	/**
	 * Exit a parse tree produced by {@link TemplateParser#data}.
	 - Parameters:
	   - ctx: the parse tree
	 */
	func exitData(_ ctx: TemplateParser.DataContext)
	/**
	 * Enter a parse tree produced by {@link TemplateParser#data_body}.
	 - Parameters:
	   - ctx: the parse tree
	 */
	func enterData_body(_ ctx: TemplateParser.Data_bodyContext)
	/**
	 * Exit a parse tree produced by {@link TemplateParser#data_body}.
	 - Parameters:
	   - ctx: the parse tree
	 */
	func exitData_body(_ ctx: TemplateParser.Data_bodyContext)
	/**
	 * Enter a parse tree produced by {@link TemplateParser#data_members}.
	 - Parameters:
	   - ctx: the parse tree
	 */
	func enterData_members(_ ctx: TemplateParser.Data_membersContext)
	/**
	 * Exit a parse tree produced by {@link TemplateParser#data_members}.
	 - Parameters:
	   - ctx: the parse tree
	 */
	func exitData_members(_ ctx: TemplateParser.Data_membersContext)
	/**
	 * Enter a parse tree produced by {@link TemplateParser#member}.
	 - Parameters:
	   - ctx: the parse tree
	 */
	func enterMember(_ ctx: TemplateParser.MemberContext)
	/**
	 * Exit a parse tree produced by {@link TemplateParser#member}.
	 - Parameters:
	   - ctx: the parse tree
	 */
	func exitMember(_ ctx: TemplateParser.MemberContext)
	/**
	 * Enter a parse tree produced by {@link TemplateParser#member_identifier}.
	 - Parameters:
	   - ctx: the parse tree
	 */
	func enterMember_identifier(_ ctx: TemplateParser.Member_identifierContext)
	/**
	 * Exit a parse tree produced by {@link TemplateParser#member_identifier}.
	 - Parameters:
	   - ctx: the parse tree
	 */
	func exitMember_identifier(_ ctx: TemplateParser.Member_identifierContext)
	/**
	 * Enter a parse tree produced by {@link TemplateParser#type_identifier}.
	 - Parameters:
	   - ctx: the parse tree
	 */
	func enterType_identifier(_ ctx: TemplateParser.Type_identifierContext)
	/**
	 * Exit a parse tree produced by {@link TemplateParser#type_identifier}.
	 - Parameters:
	   - ctx: the parse tree
	 */
	func exitType_identifier(_ ctx: TemplateParser.Type_identifierContext)
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