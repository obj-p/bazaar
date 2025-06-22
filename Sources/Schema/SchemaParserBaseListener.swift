// Generated from SchemaParser.g4 by ANTLR 4.13.1

import Antlr4

/**
 * This class provides an empty implementation of {@link SchemaParserListener},
 * which can be extended to create a listener which only needs to handle a subset
 * of the available methods.
 */
open class SchemaParserBaseListener: SchemaParserListener {
    public init() {}
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    open func enterSchema(_: SchemaParser.SchemaContext) {}
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    open func exitSchema(_: SchemaParser.SchemaContext) {}

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    open func enterData(_: SchemaParser.DataContext) {}
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    open func exitData(_: SchemaParser.DataContext) {}

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    open func enterData_body(_: SchemaParser.Data_bodyContext) {}
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    open func exitData_body(_: SchemaParser.Data_bodyContext) {}

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    open func enterData_members(_: SchemaParser.Data_membersContext) {}
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    open func exitData_members(_: SchemaParser.Data_membersContext) {}

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    open func enterMember(_: SchemaParser.MemberContext) {}
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    open func exitMember(_: SchemaParser.MemberContext) {}

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    open func enterMember_identifier(_: SchemaParser.Member_identifierContext) {}
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    open func exitMember_identifier(_: SchemaParser.Member_identifierContext) {}

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    open func enterType_identifier(_: SchemaParser.Type_identifierContext) {}
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    open func exitType_identifier(_: SchemaParser.Type_identifierContext) {}

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    open func enterIdentifier(_: SchemaParser.IdentifierContext) {}
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    open func exitIdentifier(_: SchemaParser.IdentifierContext) {}

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    open func enterEveryRule(_: ParserRuleContext) throws {}
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    open func exitEveryRule(_: ParserRuleContext) throws {}
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    open func visitTerminal(_: TerminalNode) {}
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    open func visitErrorNode(_: ErrorNode) {}
}
