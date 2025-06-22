// Generated from SchemaParser.g4 by ANTLR 4.13.1
import Antlr4

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link SchemaParser}.
 */
public protocol SchemaParserListener: ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link SchemaParser#schema}.
     - Parameters:
       - ctx: the parse tree
     */
    func enterSchema(_ ctx: SchemaParser.SchemaContext)
    /**
     * Exit a parse tree produced by {@link SchemaParser#schema}.
     - Parameters:
       - ctx: the parse tree
     */
    func exitSchema(_ ctx: SchemaParser.SchemaContext)
    /**
     * Enter a parse tree produced by {@link SchemaParser#data}.
     - Parameters:
       - ctx: the parse tree
     */
    func enterData(_ ctx: SchemaParser.DataContext)
    /**
     * Exit a parse tree produced by {@link SchemaParser#data}.
     - Parameters:
       - ctx: the parse tree
     */
    func exitData(_ ctx: SchemaParser.DataContext)
    /**
     * Enter a parse tree produced by {@link SchemaParser#data_body}.
     - Parameters:
       - ctx: the parse tree
     */
    func enterData_body(_ ctx: SchemaParser.Data_bodyContext)
    /**
     * Exit a parse tree produced by {@link SchemaParser#data_body}.
     - Parameters:
       - ctx: the parse tree
     */
    func exitData_body(_ ctx: SchemaParser.Data_bodyContext)
    /**
     * Enter a parse tree produced by {@link SchemaParser#data_members}.
     - Parameters:
       - ctx: the parse tree
     */
    func enterData_members(_ ctx: SchemaParser.Data_membersContext)
    /**
     * Exit a parse tree produced by {@link SchemaParser#data_members}.
     - Parameters:
       - ctx: the parse tree
     */
    func exitData_members(_ ctx: SchemaParser.Data_membersContext)
    /**
     * Enter a parse tree produced by {@link SchemaParser#member}.
     - Parameters:
       - ctx: the parse tree
     */
    func enterMember(_ ctx: SchemaParser.MemberContext)
    /**
     * Exit a parse tree produced by {@link SchemaParser#member}.
     - Parameters:
       - ctx: the parse tree
     */
    func exitMember(_ ctx: SchemaParser.MemberContext)
    /**
     * Enter a parse tree produced by {@link SchemaParser#member_identifier}.
     - Parameters:
       - ctx: the parse tree
     */
    func enterMember_identifier(_ ctx: SchemaParser.Member_identifierContext)
    /**
     * Exit a parse tree produced by {@link SchemaParser#member_identifier}.
     - Parameters:
       - ctx: the parse tree
     */
    func exitMember_identifier(_ ctx: SchemaParser.Member_identifierContext)
    /**
     * Enter a parse tree produced by {@link SchemaParser#type_identifier}.
     - Parameters:
       - ctx: the parse tree
     */
    func enterType_identifier(_ ctx: SchemaParser.Type_identifierContext)
    /**
     * Exit a parse tree produced by {@link SchemaParser#type_identifier}.
     - Parameters:
       - ctx: the parse tree
     */
    func exitType_identifier(_ ctx: SchemaParser.Type_identifierContext)
    /**
     * Enter a parse tree produced by {@link SchemaParser#identifier}.
     - Parameters:
       - ctx: the parse tree
     */
    func enterIdentifier(_ ctx: SchemaParser.IdentifierContext)
    /**
     * Exit a parse tree produced by {@link SchemaParser#identifier}.
     - Parameters:
       - ctx: the parse tree
     */
    func exitIdentifier(_ ctx: SchemaParser.IdentifierContext)
}
