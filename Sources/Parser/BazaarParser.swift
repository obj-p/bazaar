// Generated from BazaarParser.g4 by ANTLR 4.13.1
@preconcurrency import Antlr4

open class BazaarParser: Parser {

	internal static let _decisionToDFA: [DFA] = {
          var decisionToDFA = [DFA]()
          let length = BazaarParser._ATN.getNumberOfDecisions()
          for i in 0..<length {
            decisionToDFA.append(DFA(BazaarParser._ATN.getDecisionState(i)!, i))
           }
           return decisionToDFA
     }()

	internal static let _sharedContextCache = PredictionContextCache()

	public
	enum Tokens: Int {
		case EOF = -1, VIEW = 1, COMMA = 2, LCURLY = 3, RCURLY = 4, LPAREN = 5, 
                 RPAREN = 6, QUESTION = 7, IDENTIFIER = 8, WS = 9
	}

	public
	static let RULE_template = 0, RULE_view = 1, RULE_parameter_clause = 2, 
            RULE_parameter_list = 3, RULE_parameter = 4, RULE_type_identifier = 5, 
            RULE_identifier = 6

	public
	static let ruleNames: [String] = [
		"template", "view", "parameter_clause", "parameter_list", "parameter", 
		"type_identifier", "identifier"
	]

	private static let _LITERAL_NAMES: [String?] = [
		nil, "'view'", "','", "'{'", "'}'", "'('", "')'", "'?'"
	]
	private static let _SYMBOLIC_NAMES: [String?] = [
		nil, "VIEW", "COMMA", "LCURLY", "RCURLY", "LPAREN", "RPAREN", "QUESTION", 
		"IDENTIFIER", "WS"
	]
	public
	static let VOCABULARY = Vocabulary(_LITERAL_NAMES, _SYMBOLIC_NAMES)

	override open
	func getGrammarFileName() -> String { return "BazaarParser.g4" }

	override open
	func getRuleNames() -> [String] { return BazaarParser.ruleNames }

	override open
	func getSerializedATN() -> [Int] { return BazaarParser._serializedATN }

	override open
	func getATN() -> ATN { return BazaarParser._ATN }


	override open
	func getVocabulary() -> Vocabulary {
	    return BazaarParser.VOCABULARY
	}

	override public
	init(_ input:TokenStream) throws {
	    RuntimeMetaData.checkVersion("4.13.1", RuntimeMetaData.VERSION)
		try super.init(input)
		_interp = ParserATNSimulator(self,BazaarParser._ATN,BazaarParser._decisionToDFA, BazaarParser._sharedContextCache)
	}


	public class TemplateContext: ParserRuleContext {
			open
			func EOF() -> TerminalNode? {
				return getToken(BazaarParser.Tokens.EOF.rawValue, 0)
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
			return BazaarParser.RULE_template
		}
		override open
		func accept<T>(_ visitor: ParseTreeVisitor<T>) -> T? {
			if let visitor = visitor as? BazaarParserVisitor {
			    return visitor.visitTemplate(self)
			}
			else if let visitor = visitor as? BazaarParserBaseVisitor {
			    return visitor.visitTemplate(self)
			}
			else {
			     return visitor.visitChildren(self)
			}
		}
	}
	@discardableResult
	 open func template() throws -> TemplateContext {
		var _localctx: TemplateContext
		_localctx = TemplateContext(_ctx, getState())
		try enterRule(_localctx, 0, BazaarParser.RULE_template)
		var _la: Int = 0
		defer {
	    		try! exitRule()
	    }
		do {
		 	try enterOuterAlt(_localctx, 1)
		 	setState(15) 
		 	try _errHandler.sync(self)
		 	_la = try _input.LA(1)
		 	repeat {
		 		setState(14)
		 		try view()


		 		setState(17); 
		 		try _errHandler.sync(self)
		 		_la = try _input.LA(1)
		 	} while (_la == BazaarParser.Tokens.VIEW.rawValue)
		 	setState(19)
		 	try match(BazaarParser.Tokens.EOF.rawValue)

		}
		catch ANTLRException.recognition(let re) {
			_localctx.exception = re
			_errHandler.reportError(self, re)
			try _errHandler.recover(self, re)
		}

		return _localctx
	}

	public class ViewContext: ParserRuleContext {
			open
			func VIEW() -> TerminalNode? {
				return getToken(BazaarParser.Tokens.VIEW.rawValue, 0)
			}
			open
			func identifier() -> IdentifierContext? {
				return getRuleContext(IdentifierContext.self, 0)
			}
			open
			func parameter_clause() -> Parameter_clauseContext? {
				return getRuleContext(Parameter_clauseContext.self, 0)
			}
		override open
		func getRuleIndex() -> Int {
			return BazaarParser.RULE_view
		}
		override open
		func accept<T>(_ visitor: ParseTreeVisitor<T>) -> T? {
			if let visitor = visitor as? BazaarParserVisitor {
			    return visitor.visitView(self)
			}
			else if let visitor = visitor as? BazaarParserBaseVisitor {
			    return visitor.visitView(self)
			}
			else {
			     return visitor.visitChildren(self)
			}
		}
	}
	@discardableResult
	 open func view() throws -> ViewContext {
		var _localctx: ViewContext
		_localctx = ViewContext(_ctx, getState())
		try enterRule(_localctx, 2, BazaarParser.RULE_view)
		var _la: Int = 0
		defer {
	    		try! exitRule()
	    }
		do {
		 	try enterOuterAlt(_localctx, 1)
		 	setState(21)
		 	try match(BazaarParser.Tokens.VIEW.rawValue)
		 	setState(22)
		 	try identifier()
		 	setState(24)
		 	try _errHandler.sync(self)
		 	_la = try _input.LA(1)
		 	if (_la == BazaarParser.Tokens.LPAREN.rawValue) {
		 		setState(23)
		 		try parameter_clause()

		 	}


		}
		catch ANTLRException.recognition(let re) {
			_localctx.exception = re
			_errHandler.reportError(self, re)
			try _errHandler.recover(self, re)
		}

		return _localctx
	}

	public class Parameter_clauseContext: ParserRuleContext {
			open
			func LPAREN() -> TerminalNode? {
				return getToken(BazaarParser.Tokens.LPAREN.rawValue, 0)
			}
			open
			func RPAREN() -> TerminalNode? {
				return getToken(BazaarParser.Tokens.RPAREN.rawValue, 0)
			}
			open
			func parameter_list() -> Parameter_listContext? {
				return getRuleContext(Parameter_listContext.self, 0)
			}
		override open
		func getRuleIndex() -> Int {
			return BazaarParser.RULE_parameter_clause
		}
		override open
		func accept<T>(_ visitor: ParseTreeVisitor<T>) -> T? {
			if let visitor = visitor as? BazaarParserVisitor {
			    return visitor.visitParameter_clause(self)
			}
			else if let visitor = visitor as? BazaarParserBaseVisitor {
			    return visitor.visitParameter_clause(self)
			}
			else {
			     return visitor.visitChildren(self)
			}
		}
	}
	@discardableResult
	 open func parameter_clause() throws -> Parameter_clauseContext {
		var _localctx: Parameter_clauseContext
		_localctx = Parameter_clauseContext(_ctx, getState())
		try enterRule(_localctx, 4, BazaarParser.RULE_parameter_clause)
		var _la: Int = 0
		defer {
	    		try! exitRule()
	    }
		do {
		 	try enterOuterAlt(_localctx, 1)
		 	setState(26)
		 	try match(BazaarParser.Tokens.LPAREN.rawValue)
		 	setState(28)
		 	try _errHandler.sync(self)
		 	_la = try _input.LA(1)
		 	if (_la == BazaarParser.Tokens.IDENTIFIER.rawValue) {
		 		setState(27)
		 		try parameter_list()

		 	}

		 	setState(30)
		 	try match(BazaarParser.Tokens.RPAREN.rawValue)

		}
		catch ANTLRException.recognition(let re) {
			_localctx.exception = re
			_errHandler.reportError(self, re)
			try _errHandler.recover(self, re)
		}

		return _localctx
	}

	public class Parameter_listContext: ParserRuleContext {
			open
			func parameter() -> [ParameterContext] {
				return getRuleContexts(ParameterContext.self)
			}
			open
			func parameter(_ i: Int) -> ParameterContext? {
				return getRuleContext(ParameterContext.self, i)
			}
			open
			func COMMA() -> [TerminalNode] {
				return getTokens(BazaarParser.Tokens.COMMA.rawValue)
			}
			open
			func COMMA(_ i:Int) -> TerminalNode? {
				return getToken(BazaarParser.Tokens.COMMA.rawValue, i)
			}
		override open
		func getRuleIndex() -> Int {
			return BazaarParser.RULE_parameter_list
		}
		override open
		func accept<T>(_ visitor: ParseTreeVisitor<T>) -> T? {
			if let visitor = visitor as? BazaarParserVisitor {
			    return visitor.visitParameter_list(self)
			}
			else if let visitor = visitor as? BazaarParserBaseVisitor {
			    return visitor.visitParameter_list(self)
			}
			else {
			     return visitor.visitChildren(self)
			}
		}
	}
	@discardableResult
	 open func parameter_list() throws -> Parameter_listContext {
		var _localctx: Parameter_listContext
		_localctx = Parameter_listContext(_ctx, getState())
		try enterRule(_localctx, 6, BazaarParser.RULE_parameter_list)
		var _la: Int = 0
		defer {
	    		try! exitRule()
	    }
		do {
		 	try enterOuterAlt(_localctx, 1)
		 	setState(32)
		 	try parameter()
		 	setState(37)
		 	try _errHandler.sync(self)
		 	_la = try _input.LA(1)
		 	while (_la == BazaarParser.Tokens.COMMA.rawValue) {
		 		setState(33)
		 		try match(BazaarParser.Tokens.COMMA.rawValue)
		 		setState(34)
		 		try parameter()


		 		setState(39)
		 		try _errHandler.sync(self)
		 		_la = try _input.LA(1)
		 	}

		}
		catch ANTLRException.recognition(let re) {
			_localctx.exception = re
			_errHandler.reportError(self, re)
			try _errHandler.recover(self, re)
		}

		return _localctx
	}

	public class ParameterContext: ParserRuleContext {
			open
			func identifier() -> IdentifierContext? {
				return getRuleContext(IdentifierContext.self, 0)
			}
			open
			func type_identifier() -> Type_identifierContext? {
				return getRuleContext(Type_identifierContext.self, 0)
			}
			open
			func QUESTION() -> TerminalNode? {
				return getToken(BazaarParser.Tokens.QUESTION.rawValue, 0)
			}
		override open
		func getRuleIndex() -> Int {
			return BazaarParser.RULE_parameter
		}
		override open
		func accept<T>(_ visitor: ParseTreeVisitor<T>) -> T? {
			if let visitor = visitor as? BazaarParserVisitor {
			    return visitor.visitParameter(self)
			}
			else if let visitor = visitor as? BazaarParserBaseVisitor {
			    return visitor.visitParameter(self)
			}
			else {
			     return visitor.visitChildren(self)
			}
		}
	}
	@discardableResult
	 open func parameter() throws -> ParameterContext {
		var _localctx: ParameterContext
		_localctx = ParameterContext(_ctx, getState())
		try enterRule(_localctx, 8, BazaarParser.RULE_parameter)
		var _la: Int = 0
		defer {
	    		try! exitRule()
	    }
		do {
		 	try enterOuterAlt(_localctx, 1)
		 	setState(40)
		 	try identifier()
		 	setState(42)
		 	try _errHandler.sync(self)
		 	_la = try _input.LA(1)
		 	if (_la == BazaarParser.Tokens.QUESTION.rawValue) {
		 		setState(41)
		 		try match(BazaarParser.Tokens.QUESTION.rawValue)

		 	}

		 	setState(44)
		 	try type_identifier()

		}
		catch ANTLRException.recognition(let re) {
			_localctx.exception = re
			_errHandler.reportError(self, re)
			try _errHandler.recover(self, re)
		}

		return _localctx
	}

	public class Type_identifierContext: ParserRuleContext {
			open
			func identifier() -> IdentifierContext? {
				return getRuleContext(IdentifierContext.self, 0)
			}
		override open
		func getRuleIndex() -> Int {
			return BazaarParser.RULE_type_identifier
		}
		override open
		func accept<T>(_ visitor: ParseTreeVisitor<T>) -> T? {
			if let visitor = visitor as? BazaarParserVisitor {
			    return visitor.visitType_identifier(self)
			}
			else if let visitor = visitor as? BazaarParserBaseVisitor {
			    return visitor.visitType_identifier(self)
			}
			else {
			     return visitor.visitChildren(self)
			}
		}
	}
	@discardableResult
	 open func type_identifier() throws -> Type_identifierContext {
		var _localctx: Type_identifierContext
		_localctx = Type_identifierContext(_ctx, getState())
		try enterRule(_localctx, 10, BazaarParser.RULE_type_identifier)
		defer {
	    		try! exitRule()
	    }
		do {
		 	try enterOuterAlt(_localctx, 1)
		 	setState(46)
		 	try identifier()

		}
		catch ANTLRException.recognition(let re) {
			_localctx.exception = re
			_errHandler.reportError(self, re)
			try _errHandler.recover(self, re)
		}

		return _localctx
	}

	public class IdentifierContext: ParserRuleContext {
			open
			func IDENTIFIER() -> TerminalNode? {
				return getToken(BazaarParser.Tokens.IDENTIFIER.rawValue, 0)
			}
		override open
		func getRuleIndex() -> Int {
			return BazaarParser.RULE_identifier
		}
		override open
		func accept<T>(_ visitor: ParseTreeVisitor<T>) -> T? {
			if let visitor = visitor as? BazaarParserVisitor {
			    return visitor.visitIdentifier(self)
			}
			else if let visitor = visitor as? BazaarParserBaseVisitor {
			    return visitor.visitIdentifier(self)
			}
			else {
			     return visitor.visitChildren(self)
			}
		}
	}
	@discardableResult
	 open func identifier() throws -> IdentifierContext {
		var _localctx: IdentifierContext
		_localctx = IdentifierContext(_ctx, getState())
		try enterRule(_localctx, 12, BazaarParser.RULE_identifier)
		defer {
	    		try! exitRule()
	    }
		do {
		 	try enterOuterAlt(_localctx, 1)
		 	setState(48)
		 	try match(BazaarParser.Tokens.IDENTIFIER.rawValue)

		}
		catch ANTLRException.recognition(let re) {
			_localctx.exception = re
			_errHandler.reportError(self, re)
			try _errHandler.recover(self, re)
		}

		return _localctx
	}

	static let _serializedATN:[Int] = [
		4,1,9,51,2,0,7,0,2,1,7,1,2,2,7,2,2,3,7,3,2,4,7,4,2,5,7,5,2,6,7,6,1,0,4,
		0,16,8,0,11,0,12,0,17,1,0,1,0,1,1,1,1,1,1,3,1,25,8,1,1,2,1,2,3,2,29,8,
		2,1,2,1,2,1,3,1,3,1,3,5,3,36,8,3,10,3,12,3,39,9,3,1,4,1,4,3,4,43,8,4,1,
		4,1,4,1,5,1,5,1,6,1,6,1,6,0,0,7,0,2,4,6,8,10,12,0,0,48,0,15,1,0,0,0,2,
		21,1,0,0,0,4,26,1,0,0,0,6,32,1,0,0,0,8,40,1,0,0,0,10,46,1,0,0,0,12,48,
		1,0,0,0,14,16,3,2,1,0,15,14,1,0,0,0,16,17,1,0,0,0,17,15,1,0,0,0,17,18,
		1,0,0,0,18,19,1,0,0,0,19,20,5,0,0,1,20,1,1,0,0,0,21,22,5,1,0,0,22,24,3,
		12,6,0,23,25,3,4,2,0,24,23,1,0,0,0,24,25,1,0,0,0,25,3,1,0,0,0,26,28,5,
		5,0,0,27,29,3,6,3,0,28,27,1,0,0,0,28,29,1,0,0,0,29,30,1,0,0,0,30,31,5,
		6,0,0,31,5,1,0,0,0,32,37,3,8,4,0,33,34,5,2,0,0,34,36,3,8,4,0,35,33,1,0,
		0,0,36,39,1,0,0,0,37,35,1,0,0,0,37,38,1,0,0,0,38,7,1,0,0,0,39,37,1,0,0,
		0,40,42,3,12,6,0,41,43,5,7,0,0,42,41,1,0,0,0,42,43,1,0,0,0,43,44,1,0,0,
		0,44,45,3,10,5,0,45,9,1,0,0,0,46,47,3,12,6,0,47,11,1,0,0,0,48,49,5,8,0,
		0,49,13,1,0,0,0,5,17,24,28,37,42
	]

	public
	static let _ATN = try! ATNDeserializer().deserialize(_serializedATN)
}