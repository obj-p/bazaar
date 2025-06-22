// Generated from TemplateParser.g4 by ANTLR 4.13.1
import Antlr4

/**
 * This class provides an empty implementation of {@link TemplateParserVisitor},
 * which can be extended to create a visitor which only needs to handle a subset
 * of the available methods.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
open class TemplateParserBaseVisitor<T>: AbstractParseTreeVisitor<T> {
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    open func visitTemplate(_ ctx: TemplateParser.TemplateContext) -> T? { return visitChildren(ctx) }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    open func visitView(_ ctx: TemplateParser.ViewContext) -> T? { return visitChildren(ctx) }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    open func visitIdentifier(_ ctx: TemplateParser.IdentifierContext) -> T? { return visitChildren(ctx) }
}
