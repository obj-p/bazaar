// Generated from SchemaParser.g4 by ANTLR 4.13.1
@preconcurrency import Antlr4

open class SchemaParser: Parser {

	internal static let _decisionToDFA: [DFA] = {
          var decisionToDFA = [DFA]()
          let length = SchemaParser._ATN.getNumberOfDecisions()
          for i in 0..<length {
            decisionToDFA.append(DFA(SchemaParser._ATN.getDecisionState(i)!, i))
           }
           return decisionToDFA
     }()

	internal static let _sharedContextCache = PredictionContextCache()

	public
	enum Tokens: Int {
		case EOF = -1, DATA = 1, VIEW = 2, LPARANS = 3, RPARANS = 4, QUESTION = 5, 
                 IDENTIFIER = 6, WS = 7
	}

	public
	static let RULE_schema = 0, RULE_data = 1, RULE_data_body = 2, RULE_data_members = 3, 
            RULE_member = 4, RULE_member_identifier = 5, RULE_type_identifier = 6, 
            RULE_identifier = 7

	public
	static let ruleNames: [String] = [
		"schema", "data", "data_body", "data_members", "member", "member_identifier", 
		"type_identifier", "identifier"
	]

	private static let _LITERAL_NAMES: [String?] = [
		nil, "'data'", "'component'", "'('", "')'", "'?'"
	]
	private static let _SYMBOLIC_NAMES: [String?] = [
		nil, "DATA", "VIEW", "LPARANS", "RPARANS", "QUESTION", "IDENTIFIER", "WS"
	]
	public
	static let VOCABULARY = Vocabulary(_LITERAL_NAMES, _SYMBOLIC_NAMES)

	override open
	func getGrammarFileName() -> String { return "SchemaParser.g4" }

	override open
	func getRuleNames() -> [String] { return SchemaParser.ruleNames }

	override open
	func getSerializedATN() -> [Int] { return SchemaParser._serializedATN }

	override open
	func getATN() -> ATN { return SchemaParser._ATN }


	override open
	func getVocabulary() -> Vocabulary {
	    return SchemaParser.VOCABULARY
	}

	override public
	init(_ input:TokenStream) throws {
	    RuntimeMetaData.checkVersion("4.13.1", RuntimeMetaData.VERSION)
		try super.init(input)
		_interp = ParserATNSimulator(self,SchemaParser._ATN,SchemaParser._decisionToDFA, SchemaParser._sharedContextCache)
	}


	public class SchemaContext: ParserRuleContext {
			open
			func EOF() -> TerminalNode? {
				return getToken(SchemaParser.Tokens.EOF.rawValue, 0)
			}
			open
			func data() -> [DataContext] {
				return getRuleContexts(DataContext.self)
			}
			open
			func data(_ i: Int) -> DataContext? {
				return getRuleContext(DataContext.self, i)
			}
		override open
		func getRuleIndex() -> Int {
			return SchemaParser.RULE_schema
		}
		override open
		func accept<T>(_ visitor: ParseTreeVisitor<T>) -> T? {
			if let visitor = visitor as? SchemaParserVisitor {
			    return visitor.visitSchema(self)
			}
			else if let visitor = visitor as? SchemaParserBaseVisitor {
			    return visitor.visitSchema(self)
			}
			else {
			     return visitor.visitChildren(self)
			}
		}
	}
	@discardableResult
	 open func schema() throws -> SchemaContext {
		var _localctx: SchemaContext
		_localctx = SchemaContext(_ctx, getState())
		try enterRule(_localctx, 0, SchemaParser.RULE_schema)
		var _la: Int = 0
		defer {
	    		try! exitRule()
	    }
		do {
		 	try enterOuterAlt(_localctx, 1)
		 	setState(17) 
		 	try _errHandler.sync(self)
		 	_la = try _input.LA(1)
		 	repeat {
		 		setState(16)
		 		try data()


		 		setState(19); 
		 		try _errHandler.sync(self)
		 		_la = try _input.LA(1)
		 	} while (_la == SchemaParser.Tokens.DATA.rawValue)
		 	setState(21)
		 	try match(SchemaParser.Tokens.EOF.rawValue)

		}
		catch ANTLRException.recognition(let re) {
			_localctx.exception = re
			_errHandler.reportError(self, re)
			try _errHandler.recover(self, re)
		}

		return _localctx
	}

	public class DataContext: ParserRuleContext {
			open
			func DATA() -> TerminalNode? {
				return getToken(SchemaParser.Tokens.DATA.rawValue, 0)
			}
			open
			func identifier() -> IdentifierContext? {
				return getRuleContext(IdentifierContext.self, 0)
			}
			open
			func data_body() -> Data_bodyContext? {
				return getRuleContext(Data_bodyContext.self, 0)
			}
		override open
		func getRuleIndex() -> Int {
			return SchemaParser.RULE_data
		}
		override open
		func accept<T>(_ visitor: ParseTreeVisitor<T>) -> T? {
			if let visitor = visitor as? SchemaParserVisitor {
			    return visitor.visitData(self)
			}
			else if let visitor = visitor as? SchemaParserBaseVisitor {
			    return visitor.visitData(self)
			}
			else {
			     return visitor.visitChildren(self)
			}
		}
	}
	@discardableResult
	 open func data() throws -> DataContext {
		var _localctx: DataContext
		_localctx = DataContext(_ctx, getState())
		try enterRule(_localctx, 2, SchemaParser.RULE_data)
		defer {
	    		try! exitRule()
	    }
		do {
		 	try enterOuterAlt(_localctx, 1)
		 	setState(23)
		 	try match(SchemaParser.Tokens.DATA.rawValue)
		 	setState(24)
		 	try identifier()
		 	setState(25)
		 	try data_body()

		}
		catch ANTLRException.recognition(let re) {
			_localctx.exception = re
			_errHandler.reportError(self, re)
			try _errHandler.recover(self, re)
		}

		return _localctx
	}

	public class Data_bodyContext: ParserRuleContext {
			open
			func LPARANS() -> TerminalNode? {
				return getToken(SchemaParser.Tokens.LPARANS.rawValue, 0)
			}
			open
			func data_members() -> Data_membersContext? {
				return getRuleContext(Data_membersContext.self, 0)
			}
			open
			func RPARANS() -> TerminalNode? {
				return getToken(SchemaParser.Tokens.RPARANS.rawValue, 0)
			}
		override open
		func getRuleIndex() -> Int {
			return SchemaParser.RULE_data_body
		}
		override open
		func accept<T>(_ visitor: ParseTreeVisitor<T>) -> T? {
			if let visitor = visitor as? SchemaParserVisitor {
			    return visitor.visitData_body(self)
			}
			else if let visitor = visitor as? SchemaParserBaseVisitor {
			    return visitor.visitData_body(self)
			}
			else {
			     return visitor.visitChildren(self)
			}
		}
	}
	@discardableResult
	 open func data_body() throws -> Data_bodyContext {
		var _localctx: Data_bodyContext
		_localctx = Data_bodyContext(_ctx, getState())
		try enterRule(_localctx, 4, SchemaParser.RULE_data_body)
		defer {
	    		try! exitRule()
	    }
		do {
		 	try enterOuterAlt(_localctx, 1)
		 	setState(27)
		 	try match(SchemaParser.Tokens.LPARANS.rawValue)
		 	setState(28)
		 	try data_members()
		 	setState(29)
		 	try match(SchemaParser.Tokens.RPARANS.rawValue)

		}
		catch ANTLRException.recognition(let re) {
			_localctx.exception = re
			_errHandler.reportError(self, re)
			try _errHandler.recover(self, re)
		}

		return _localctx
	}

	public class Data_membersContext: ParserRuleContext {
			open
			func member() -> [MemberContext] {
				return getRuleContexts(MemberContext.self)
			}
			open
			func member(_ i: Int) -> MemberContext? {
				return getRuleContext(MemberContext.self, i)
			}
		override open
		func getRuleIndex() -> Int {
			return SchemaParser.RULE_data_members
		}
		override open
		func accept<T>(_ visitor: ParseTreeVisitor<T>) -> T? {
			if let visitor = visitor as? SchemaParserVisitor {
			    return visitor.visitData_members(self)
			}
			else if let visitor = visitor as? SchemaParserBaseVisitor {
			    return visitor.visitData_members(self)
			}
			else {
			     return visitor.visitChildren(self)
			}
		}
	}
	@discardableResult
	 open func data_members() throws -> Data_membersContext {
		var _localctx: Data_membersContext
		_localctx = Data_membersContext(_ctx, getState())
		try enterRule(_localctx, 6, SchemaParser.RULE_data_members)
		var _la: Int = 0
		defer {
	    		try! exitRule()
	    }
		do {
		 	try enterOuterAlt(_localctx, 1)
		 	setState(34)
		 	try _errHandler.sync(self)
		 	_la = try _input.LA(1)
		 	while (_la == SchemaParser.Tokens.IDENTIFIER.rawValue) {
		 		setState(31)
		 		try member()


		 		setState(36)
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

	public class MemberContext: ParserRuleContext {
			open
			func member_identifier() -> Member_identifierContext? {
				return getRuleContext(Member_identifierContext.self, 0)
			}
			open
			func type_identifier() -> Type_identifierContext? {
				return getRuleContext(Type_identifierContext.self, 0)
			}
			open
			func QUESTION() -> TerminalNode? {
				return getToken(SchemaParser.Tokens.QUESTION.rawValue, 0)
			}
		override open
		func getRuleIndex() -> Int {
			return SchemaParser.RULE_member
		}
		override open
		func accept<T>(_ visitor: ParseTreeVisitor<T>) -> T? {
			if let visitor = visitor as? SchemaParserVisitor {
			    return visitor.visitMember(self)
			}
			else if let visitor = visitor as? SchemaParserBaseVisitor {
			    return visitor.visitMember(self)
			}
			else {
			     return visitor.visitChildren(self)
			}
		}
	}
	@discardableResult
	 open func member() throws -> MemberContext {
		var _localctx: MemberContext
		_localctx = MemberContext(_ctx, getState())
		try enterRule(_localctx, 8, SchemaParser.RULE_member)
		var _la: Int = 0
		defer {
	    		try! exitRule()
	    }
		do {
		 	try enterOuterAlt(_localctx, 1)
		 	setState(37)
		 	try member_identifier()
		 	setState(39)
		 	try _errHandler.sync(self)
		 	_la = try _input.LA(1)
		 	if (_la == SchemaParser.Tokens.QUESTION.rawValue) {
		 		setState(38)
		 		try match(SchemaParser.Tokens.QUESTION.rawValue)

		 	}

		 	setState(41)
		 	try type_identifier()

		}
		catch ANTLRException.recognition(let re) {
			_localctx.exception = re
			_errHandler.reportError(self, re)
			try _errHandler.recover(self, re)
		}

		return _localctx
	}

	public class Member_identifierContext: ParserRuleContext {
			open
			func identifier() -> IdentifierContext? {
				return getRuleContext(IdentifierContext.self, 0)
			}
		override open
		func getRuleIndex() -> Int {
			return SchemaParser.RULE_member_identifier
		}
		override open
		func accept<T>(_ visitor: ParseTreeVisitor<T>) -> T? {
			if let visitor = visitor as? SchemaParserVisitor {
			    return visitor.visitMember_identifier(self)
			}
			else if let visitor = visitor as? SchemaParserBaseVisitor {
			    return visitor.visitMember_identifier(self)
			}
			else {
			     return visitor.visitChildren(self)
			}
		}
	}
	@discardableResult
	 open func member_identifier() throws -> Member_identifierContext {
		var _localctx: Member_identifierContext
		_localctx = Member_identifierContext(_ctx, getState())
		try enterRule(_localctx, 10, SchemaParser.RULE_member_identifier)
		defer {
	    		try! exitRule()
	    }
		do {
		 	try enterOuterAlt(_localctx, 1)
		 	setState(43)
		 	try identifier()

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
			return SchemaParser.RULE_type_identifier
		}
		override open
		func accept<T>(_ visitor: ParseTreeVisitor<T>) -> T? {
			if let visitor = visitor as? SchemaParserVisitor {
			    return visitor.visitType_identifier(self)
			}
			else if let visitor = visitor as? SchemaParserBaseVisitor {
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
		try enterRule(_localctx, 12, SchemaParser.RULE_type_identifier)
		defer {
	    		try! exitRule()
	    }
		do {
		 	try enterOuterAlt(_localctx, 1)
		 	setState(45)
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
				return getToken(SchemaParser.Tokens.IDENTIFIER.rawValue, 0)
			}
		override open
		func getRuleIndex() -> Int {
			return SchemaParser.RULE_identifier
		}
		override open
		func accept<T>(_ visitor: ParseTreeVisitor<T>) -> T? {
			if let visitor = visitor as? SchemaParserVisitor {
			    return visitor.visitIdentifier(self)
			}
			else if let visitor = visitor as? SchemaParserBaseVisitor {
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
		try enterRule(_localctx, 14, SchemaParser.RULE_identifier)
		defer {
	    		try! exitRule()
	    }
		do {
		 	try enterOuterAlt(_localctx, 1)
		 	setState(47)
		 	try match(SchemaParser.Tokens.IDENTIFIER.rawValue)

		}
		catch ANTLRException.recognition(let re) {
			_localctx.exception = re
			_errHandler.reportError(self, re)
			try _errHandler.recover(self, re)
		}

		return _localctx
	}

	static let _serializedATN:[Int] = [
		4,1,7,50,2,0,7,0,2,1,7,1,2,2,7,2,2,3,7,3,2,4,7,4,2,5,7,5,2,6,7,6,2,7,7,
		7,1,0,4,0,18,8,0,11,0,12,0,19,1,0,1,0,1,1,1,1,1,1,1,1,1,2,1,2,1,2,1,2,
		1,3,5,3,33,8,3,10,3,12,3,36,9,3,1,4,1,4,3,4,40,8,4,1,4,1,4,1,5,1,5,1,6,
		1,6,1,7,1,7,1,7,0,0,8,0,2,4,6,8,10,12,14,0,0,44,0,17,1,0,0,0,2,23,1,0,
		0,0,4,27,1,0,0,0,6,34,1,0,0,0,8,37,1,0,0,0,10,43,1,0,0,0,12,45,1,0,0,0,
		14,47,1,0,0,0,16,18,3,2,1,0,17,16,1,0,0,0,18,19,1,0,0,0,19,17,1,0,0,0,
		19,20,1,0,0,0,20,21,1,0,0,0,21,22,5,0,0,1,22,1,1,0,0,0,23,24,5,1,0,0,24,
		25,3,14,7,0,25,26,3,4,2,0,26,3,1,0,0,0,27,28,5,3,0,0,28,29,3,6,3,0,29,
		30,5,4,0,0,30,5,1,0,0,0,31,33,3,8,4,0,32,31,1,0,0,0,33,36,1,0,0,0,34,32,
		1,0,0,0,34,35,1,0,0,0,35,7,1,0,0,0,36,34,1,0,0,0,37,39,3,10,5,0,38,40,
		5,5,0,0,39,38,1,0,0,0,39,40,1,0,0,0,40,41,1,0,0,0,41,42,3,12,6,0,42,9,
		1,0,0,0,43,44,3,14,7,0,44,11,1,0,0,0,45,46,3,14,7,0,46,13,1,0,0,0,47,48,
		5,6,0,0,48,15,1,0,0,0,3,19,34,39
	]

	public
	static let _ATN = try! ATNDeserializer().deserialize(_serializedATN)
}