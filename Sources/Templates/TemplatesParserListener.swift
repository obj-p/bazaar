// Generated from Sources/Templates/TemplatesParser.g4 by ANTLR 4.13.1
import Antlr4

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link TemplatesParser}.
 */
public protocol TemplatesParserListener: ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link TemplatesParser#schema}.
	 - Parameters:
	   - ctx: the parse tree
	 */
	func enterSchema(_ ctx: TemplatesParser.SchemaContext)
	/**
	 * Exit a parse tree produced by {@link TemplatesParser#schema}.
	 - Parameters:
	   - ctx: the parse tree
	 */
	func exitSchema(_ ctx: TemplatesParser.SchemaContext)
	/**
	 * Enter a parse tree produced by {@link TemplatesParser#data}.
	 - Parameters:
	   - ctx: the parse tree
	 */
	func enterData(_ ctx: TemplatesParser.DataContext)
	/**
	 * Exit a parse tree produced by {@link TemplatesParser#data}.
	 - Parameters:
	   - ctx: the parse tree
	 */
	func exitData(_ ctx: TemplatesParser.DataContext)
	/**
	 * Enter a parse tree produced by {@link TemplatesParser#data_body}.
	 - Parameters:
	   - ctx: the parse tree
	 */
	func enterData_body(_ ctx: TemplatesParser.Data_bodyContext)
	/**
	 * Exit a parse tree produced by {@link TemplatesParser#data_body}.
	 - Parameters:
	   - ctx: the parse tree
	 */
	func exitData_body(_ ctx: TemplatesParser.Data_bodyContext)
	/**
	 * Enter a parse tree produced by {@link TemplatesParser#data_members}.
	 - Parameters:
	   - ctx: the parse tree
	 */
	func enterData_members(_ ctx: TemplatesParser.Data_membersContext)
	/**
	 * Exit a parse tree produced by {@link TemplatesParser#data_members}.
	 - Parameters:
	   - ctx: the parse tree
	 */
	func exitData_members(_ ctx: TemplatesParser.Data_membersContext)
	/**
	 * Enter a parse tree produced by {@link TemplatesParser#member}.
	 - Parameters:
	   - ctx: the parse tree
	 */
	func enterMember(_ ctx: TemplatesParser.MemberContext)
	/**
	 * Exit a parse tree produced by {@link TemplatesParser#member}.
	 - Parameters:
	   - ctx: the parse tree
	 */
	func exitMember(_ ctx: TemplatesParser.MemberContext)
	/**
	 * Enter a parse tree produced by {@link TemplatesParser#member_identifier}.
	 - Parameters:
	   - ctx: the parse tree
	 */
	func enterMember_identifier(_ ctx: TemplatesParser.Member_identifierContext)
	/**
	 * Exit a parse tree produced by {@link TemplatesParser#member_identifier}.
	 - Parameters:
	   - ctx: the parse tree
	 */
	func exitMember_identifier(_ ctx: TemplatesParser.Member_identifierContext)
	/**
	 * Enter a parse tree produced by {@link TemplatesParser#type_identifier}.
	 - Parameters:
	   - ctx: the parse tree
	 */
	func enterType_identifier(_ ctx: TemplatesParser.Type_identifierContext)
	/**
	 * Exit a parse tree produced by {@link TemplatesParser#type_identifier}.
	 - Parameters:
	   - ctx: the parse tree
	 */
	func exitType_identifier(_ ctx: TemplatesParser.Type_identifierContext)
	/**
	 * Enter a parse tree produced by {@link TemplatesParser#identifier}.
	 - Parameters:
	   - ctx: the parse tree
	 */
	func enterIdentifier(_ ctx: TemplatesParser.IdentifierContext)
	/**
	 * Exit a parse tree produced by {@link TemplatesParser#identifier}.
	 - Parameters:
	   - ctx: the parse tree
	 */
	func exitIdentifier(_ ctx: TemplatesParser.IdentifierContext)
}