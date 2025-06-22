// Generated from Sources/Template/TemplateParser.g4 by ANTLR 4.13.1

import Antlr4


/**
 * This class provides an empty implementation of {@link TemplateParserListener},
 * which can be extended to create a listener which only needs to handle a subset
 * of the available methods.
 */
open class TemplateParserBaseListener: TemplateParserListener {
     public init() { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	open func enterSchema(_ ctx: TemplateParser.SchemaContext) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	open func exitSchema(_ ctx: TemplateParser.SchemaContext) { }

	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	open func enterData(_ ctx: TemplateParser.DataContext) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	open func exitData(_ ctx: TemplateParser.DataContext) { }

	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	open func enterData_body(_ ctx: TemplateParser.Data_bodyContext) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	open func exitData_body(_ ctx: TemplateParser.Data_bodyContext) { }

	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	open func enterData_members(_ ctx: TemplateParser.Data_membersContext) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	open func exitData_members(_ ctx: TemplateParser.Data_membersContext) { }

	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	open func enterMember(_ ctx: TemplateParser.MemberContext) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	open func exitMember(_ ctx: TemplateParser.MemberContext) { }

	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	open func enterMember_identifier(_ ctx: TemplateParser.Member_identifierContext) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	open func exitMember_identifier(_ ctx: TemplateParser.Member_identifierContext) { }

	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	open func enterType_identifier(_ ctx: TemplateParser.Type_identifierContext) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	open func exitType_identifier(_ ctx: TemplateParser.Type_identifierContext) { }

	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	open func enterIdentifier(_ ctx: TemplateParser.IdentifierContext) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	open func exitIdentifier(_ ctx: TemplateParser.IdentifierContext) { }

	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	open func enterEveryRule(_ ctx: ParserRuleContext) throws { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	open func exitEveryRule(_ ctx: ParserRuleContext) throws { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	open func visitTerminal(_ node: TerminalNode) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	open func visitErrorNode(_ node: ErrorNode) { }
}