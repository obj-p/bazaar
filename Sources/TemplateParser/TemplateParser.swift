// Generated from TemplateParser.g4 by ANTLR 4.13.1
@preconcurrency import Antlr4

open class TemplateParser: Parser {
    static let _decisionToDFA: [DFA] = {
        var decisionToDFA = [DFA]()
        let length = TemplateParser._ATN.getNumberOfDecisions()
        for i in 0 ..< length {
            decisionToDFA.append(DFA(TemplateParser._ATN.getDecisionState(i)!, i))
        }
        return decisionToDFA
    }()

    static let _sharedContextCache = PredictionContextCache()

    public
    enum Tokens: Int {
        case EOF = -1, VIEW = 1, LPARANS = 2, RPARANS = 3, QUESTION = 4, IDENTIFIER = 5,
             WS = 6
    }

    public
    static let RULE_template = 0, RULE_view = 1, RULE_identifier = 2

    public
    static let ruleNames: [String] = [
        "template", "view", "identifier"
    ]

    private static let _LITERAL_NAMES: [String?] = [
        nil, "'view'", "'('", "')'", "'?'"
    ]
    private static let _SYMBOLIC_NAMES: [String?] = [
        nil, "VIEW", "LPARANS", "RPARANS", "QUESTION", "IDENTIFIER", "WS"
    ]
    public
    static let VOCABULARY = Vocabulary(_LITERAL_NAMES, _SYMBOLIC_NAMES)

    override open
    func getGrammarFileName() -> String { return "TemplateParser.g4" }

    override open
    func getRuleNames() -> [String] { return TemplateParser.ruleNames }

    override open
    func getSerializedATN() -> [Int] { return TemplateParser._serializedATN }

    override open
    func getATN() -> ATN { return TemplateParser._ATN }

    override open
    func getVocabulary() -> Vocabulary {
        return TemplateParser.VOCABULARY
    }

    override public
    init(_ input: TokenStream) throws {
        RuntimeMetaData.checkVersion("4.13.1", RuntimeMetaData.VERSION)
        try super.init(input)
        _interp = ParserATNSimulator(self, TemplateParser._ATN, TemplateParser._decisionToDFA, TemplateParser._sharedContextCache)
    }

    public class TemplateContext: ParserRuleContext {
        open
        func EOF() -> TerminalNode? {
            return getToken(TemplateParser.Tokens.EOF.rawValue, 0)
        }

        open
        func view() -> [ViewContext] {
            return getRuleContexts(ViewContext.self)
        }

        open
        func view(_ i: Int) -> ViewContext? {
            return getRuleContext(ViewContext.self, i)
        }

        override open
        func getRuleIndex() -> Int {
            return TemplateParser.RULE_template
        }

        override open
        func accept<T>(_ visitor: ParseTreeVisitor<T>) -> T? {
            if let visitor = visitor as? TemplateParserVisitor {
                return visitor.visitTemplate(self)
            } else if let visitor = visitor as? TemplateParserBaseVisitor {
                return visitor.visitTemplate(self)
            } else {
                return visitor.visitChildren(self)
            }
        }
    }

    @discardableResult
    open func template() throws -> TemplateContext {
        var _localctx: TemplateContext
        _localctx = TemplateContext(_ctx, getState())
        try enterRule(_localctx, 0, TemplateParser.RULE_template)
        var _la = 0
        defer {
            try! exitRule()
        }
        do {
            try enterOuterAlt(_localctx, 1)
            setState(7)
            try _errHandler.sync(self)
            _la = try _input.LA(1)
            repeat {
                setState(6)
                try view()

                setState(9)
                try _errHandler.sync(self)
                _la = try _input.LA(1)
            } while _la == TemplateParser.Tokens.VIEW.rawValue
            setState(11)
            try match(TemplateParser.Tokens.EOF.rawValue)
        } catch let ANTLRException.recognition(re) {
            _localctx.exception = re
            _errHandler.reportError(self, re)
            try _errHandler.recover(self, re)
        }

        return _localctx
    }

    public class ViewContext: ParserRuleContext {
        open
        func VIEW() -> TerminalNode? {
            return getToken(TemplateParser.Tokens.VIEW.rawValue, 0)
        }

        open
        func identifier() -> IdentifierContext? {
            return getRuleContext(IdentifierContext.self, 0)
        }

        override open
        func getRuleIndex() -> Int {
            return TemplateParser.RULE_view
        }

        override open
        func accept<T>(_ visitor: ParseTreeVisitor<T>) -> T? {
            if let visitor = visitor as? TemplateParserVisitor {
                return visitor.visitView(self)
            } else if let visitor = visitor as? TemplateParserBaseVisitor {
                return visitor.visitView(self)
            } else {
                return visitor.visitChildren(self)
            }
        }
    }

    @discardableResult
    open func view() throws -> ViewContext {
        var _localctx: ViewContext
        _localctx = ViewContext(_ctx, getState())
        try enterRule(_localctx, 2, TemplateParser.RULE_view)
        defer {
            try! exitRule()
        }
        do {
            try enterOuterAlt(_localctx, 1)
            setState(13)
            try match(TemplateParser.Tokens.VIEW.rawValue)
            setState(14)
            try identifier()
        } catch let ANTLRException.recognition(re) {
            _localctx.exception = re
            _errHandler.reportError(self, re)
            try _errHandler.recover(self, re)
        }

        return _localctx
    }

    public class IdentifierContext: ParserRuleContext {
        open
        func IDENTIFIER() -> TerminalNode? {
            return getToken(TemplateParser.Tokens.IDENTIFIER.rawValue, 0)
        }

        override open
        func getRuleIndex() -> Int {
            return TemplateParser.RULE_identifier
        }

        override open
        func accept<T>(_ visitor: ParseTreeVisitor<T>) -> T? {
            if let visitor = visitor as? TemplateParserVisitor {
                return visitor.visitIdentifier(self)
            } else if let visitor = visitor as? TemplateParserBaseVisitor {
                return visitor.visitIdentifier(self)
            } else {
                return visitor.visitChildren(self)
            }
        }
    }

    @discardableResult
    open func identifier() throws -> IdentifierContext {
        var _localctx: IdentifierContext
        _localctx = IdentifierContext(_ctx, getState())
        try enterRule(_localctx, 4, TemplateParser.RULE_identifier)
        defer {
            try! exitRule()
        }
        do {
            try enterOuterAlt(_localctx, 1)
            setState(16)
            try match(TemplateParser.Tokens.IDENTIFIER.rawValue)
        } catch let ANTLRException.recognition(re) {
            _localctx.exception = re
            _errHandler.reportError(self, re)
            try _errHandler.recover(self, re)
        }

        return _localctx
    }

    static let _serializedATN: [Int] = [
        4, 1, 6, 19, 2, 0, 7, 0, 2, 1, 7, 1, 2, 2, 7, 2, 1, 0, 4, 0, 8, 8, 0, 11, 0, 12, 0, 9, 1, 0, 1, 0, 1, 1,
        1, 1, 1, 1, 1, 2, 1, 2, 1, 2, 0, 0, 3, 0, 2, 4, 0, 0, 16, 0, 7, 1, 0, 0, 0, 2, 13, 1, 0, 0, 0, 4, 16, 1,
        0, 0, 0, 6, 8, 3, 2, 1, 0, 7, 6, 1, 0, 0, 0, 8, 9, 1, 0, 0, 0, 9, 7, 1, 0, 0, 0, 9, 10, 1, 0, 0, 0, 10,
        11, 1, 0, 0, 0, 11, 12, 5, 0, 0, 1, 12, 1, 1, 0, 0, 0, 13, 14, 5, 1, 0, 0, 14, 15, 3, 4, 2, 0, 15, 3,
        1, 0, 0, 0, 16, 17, 5, 5, 0, 0, 17, 5, 1, 0, 0, 0, 1, 9
    ]

    public
    static let _ATN = try! ATNDeserializer().deserialize(_serializedATN)
}
