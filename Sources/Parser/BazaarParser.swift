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
		case EOF = -1, COMPONENT = 1, DATA = 2, ENUM = 3, FUNCTION = 4, TEMPLATE = 5, 
                 IMPORT = 6, IF = 7, STRING = 8, BOOL = 9, INT = 10, COMMA = 11, 
                 LCURLY = 12, RCURLY = 13, LPAREN = 14, RPAREN = 15, LBRACKET = 16, 
                 RBRACKET = 17, QUESTION = 18, DOT = 19, EQUALS = 20, SEMICOLON = 21, 
                 STRING_LITERAL = 22, IDENTIFIER = 23, WS = 24
	}

	public
	static let RULE_bzr = 0, RULE_declaration = 1, RULE_import_declaration = 2, 
            RULE_data_declaration = 3, RULE_field_list = 4, RULE_field = 5, 
            RULE_enum_declaration = 6, RULE_enum_value_list = 7, RULE_enum_value = 8, 
            RULE_component_declaration = 9, RULE_function_declaration = 10, 
            RULE_function_body = 11, RULE_statement_list = 12, RULE_statement = 13, 
            RULE_return_type = 14, RULE_template_declaration = 15, RULE_template_body = 16, 
            RULE_template_content = 17, RULE_component_instantiation = 18, 
            RULE_component_body = 19, RULE_component_content_list = 20, 
            RULE_component_content = 21, RULE_conditional_statement = 22, 
            RULE_event_handler = 23, RULE_parameter_clause = 24, RULE_parameter_list = 25, 
            RULE_parameter = 26, RULE_argument_list = 27, RULE_argument = 28, 
            RULE_expression = 29, RULE_primary_expression = 30, RULE_type_annotation = 31, 
            RULE_type_identifier = 32, RULE_string_literal = 33, RULE_identifier = 34

	public
	static let ruleNames: [String] = [
		"bzr", "declaration", "import_declaration", "data_declaration", "field_list", 
		"field", "enum_declaration", "enum_value_list", "enum_value", "component_declaration", 
		"function_declaration", "function_body", "statement_list", "statement", 
		"return_type", "template_declaration", "template_body", "template_content", 
		"component_instantiation", "component_body", "component_content_list", 
		"component_content", "conditional_statement", "event_handler", "parameter_clause", 
		"parameter_list", "parameter", "argument_list", "argument", "expression", 
		"primary_expression", "type_annotation", "type_identifier", "string_literal", 
		"identifier"
	]

	private static let _LITERAL_NAMES: [String?] = [
		nil, "'component'", "'data'", "'enum'", "'function'", "'template'", "'import'", 
		"'if'", "'String'", "'Bool'", "'Int'", "','", "'{'", "'}'", "'('", "')'", 
		"'['", "']'", "'?'", "'.'", "'='", "';'"
	]
	private static let _SYMBOLIC_NAMES: [String?] = [
		nil, "COMPONENT", "DATA", "ENUM", "FUNCTION", "TEMPLATE", "IMPORT", "IF", 
		"STRING", "BOOL", "INT", "COMMA", "LCURLY", "RCURLY", "LPAREN", "RPAREN", 
		"LBRACKET", "RBRACKET", "QUESTION", "DOT", "EQUALS", "SEMICOLON", "STRING_LITERAL", 
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


	public class BzrContext: ParserRuleContext {
			open
			func EOF() -> TerminalNode? {
				return getToken(BazaarParser.Tokens.EOF.rawValue, 0)
			}
			open
			func declaration() -> [DeclarationContext] {
				return getRuleContexts(DeclarationContext.self)
			}
			open
			func declaration(_ i: Int) -> DeclarationContext? {
				return getRuleContext(DeclarationContext.self, i)
			}
		override open
		func getRuleIndex() -> Int {
			return BazaarParser.RULE_bzr
		}
		override open
		func accept<T>(_ visitor: ParseTreeVisitor<T>) -> T? {
			if let visitor = visitor as? BazaarParserVisitor {
			    return visitor.visitBzr(self)
			}
			else if let visitor = visitor as? BazaarParserBaseVisitor {
			    return visitor.visitBzr(self)
			}
			else {
			     return visitor.visitChildren(self)
			}
		}
	}
	@discardableResult
	 open func bzr() throws -> BzrContext {
		var _localctx: BzrContext
		_localctx = BzrContext(_ctx, getState())
		try enterRule(_localctx, 0, BazaarParser.RULE_bzr)
		var _la: Int = 0
		defer {
	    		try! exitRule()
	    }
		do {
		 	try enterOuterAlt(_localctx, 1)
		 	setState(73)
		 	try _errHandler.sync(self)
		 	_la = try _input.LA(1)
		 	while (((Int64(_la) & ~0x3f) == 0 && ((Int64(1) << _la) & 126) != 0)) {
		 		setState(70)
		 		try declaration()


		 		setState(75)
		 		try _errHandler.sync(self)
		 		_la = try _input.LA(1)
		 	}
		 	setState(76)
		 	try match(BazaarParser.Tokens.EOF.rawValue)

		}
		catch ANTLRException.recognition(let re) {
			_localctx.exception = re
			_errHandler.reportError(self, re)
			try _errHandler.recover(self, re)
		}

		return _localctx
	}

	public class DeclarationContext: ParserRuleContext {
			open
			func import_declaration() -> Import_declarationContext? {
				return getRuleContext(Import_declarationContext.self, 0)
			}
			open
			func data_declaration() -> Data_declarationContext? {
				return getRuleContext(Data_declarationContext.self, 0)
			}
			open
			func enum_declaration() -> Enum_declarationContext? {
				return getRuleContext(Enum_declarationContext.self, 0)
			}
			open
			func component_declaration() -> Component_declarationContext? {
				return getRuleContext(Component_declarationContext.self, 0)
			}
			open
			func function_declaration() -> Function_declarationContext? {
				return getRuleContext(Function_declarationContext.self, 0)
			}
			open
			func template_declaration() -> Template_declarationContext? {
				return getRuleContext(Template_declarationContext.self, 0)
			}
		override open
		func getRuleIndex() -> Int {
			return BazaarParser.RULE_declaration
		}
		override open
		func accept<T>(_ visitor: ParseTreeVisitor<T>) -> T? {
			if let visitor = visitor as? BazaarParserVisitor {
			    return visitor.visitDeclaration(self)
			}
			else if let visitor = visitor as? BazaarParserBaseVisitor {
			    return visitor.visitDeclaration(self)
			}
			else {
			     return visitor.visitChildren(self)
			}
		}
	}
	@discardableResult
	 open func declaration() throws -> DeclarationContext {
		var _localctx: DeclarationContext
		_localctx = DeclarationContext(_ctx, getState())
		try enterRule(_localctx, 2, BazaarParser.RULE_declaration)
		defer {
	    		try! exitRule()
	    }
		do {
		 	setState(84)
		 	try _errHandler.sync(self)
		 	switch (BazaarParser.Tokens(rawValue: try _input.LA(1))!) {
		 	case .IMPORT:
		 		try enterOuterAlt(_localctx, 1)
		 		setState(78)
		 		try import_declaration()

		 		break

		 	case .DATA:
		 		try enterOuterAlt(_localctx, 2)
		 		setState(79)
		 		try data_declaration()

		 		break

		 	case .ENUM:
		 		try enterOuterAlt(_localctx, 3)
		 		setState(80)
		 		try enum_declaration()

		 		break

		 	case .COMPONENT:
		 		try enterOuterAlt(_localctx, 4)
		 		setState(81)
		 		try component_declaration()

		 		break

		 	case .FUNCTION:
		 		try enterOuterAlt(_localctx, 5)
		 		setState(82)
		 		try function_declaration()

		 		break

		 	case .TEMPLATE:
		 		try enterOuterAlt(_localctx, 6)
		 		setState(83)
		 		try template_declaration()

		 		break
		 	default:
		 		throw ANTLRException.recognition(e: NoViableAltException(self))
		 	}
		}
		catch ANTLRException.recognition(let re) {
			_localctx.exception = re
			_errHandler.reportError(self, re)
			try _errHandler.recover(self, re)
		}

		return _localctx
	}

	public class Import_declarationContext: ParserRuleContext {
			open
			func IMPORT() -> TerminalNode? {
				return getToken(BazaarParser.Tokens.IMPORT.rawValue, 0)
			}
			open
			func identifier() -> IdentifierContext? {
				return getRuleContext(IdentifierContext.self, 0)
			}
		override open
		func getRuleIndex() -> Int {
			return BazaarParser.RULE_import_declaration
		}
		override open
		func accept<T>(_ visitor: ParseTreeVisitor<T>) -> T? {
			if let visitor = visitor as? BazaarParserVisitor {
			    return visitor.visitImport_declaration(self)
			}
			else if let visitor = visitor as? BazaarParserBaseVisitor {
			    return visitor.visitImport_declaration(self)
			}
			else {
			     return visitor.visitChildren(self)
			}
		}
	}
	@discardableResult
	 open func import_declaration() throws -> Import_declarationContext {
		var _localctx: Import_declarationContext
		_localctx = Import_declarationContext(_ctx, getState())
		try enterRule(_localctx, 4, BazaarParser.RULE_import_declaration)
		defer {
	    		try! exitRule()
	    }
		do {
		 	try enterOuterAlt(_localctx, 1)
		 	setState(86)
		 	try match(BazaarParser.Tokens.IMPORT.rawValue)
		 	setState(87)
		 	try identifier()

		}
		catch ANTLRException.recognition(let re) {
			_localctx.exception = re
			_errHandler.reportError(self, re)
			try _errHandler.recover(self, re)
		}

		return _localctx
	}

	public class Data_declarationContext: ParserRuleContext {
			open
			func DATA() -> TerminalNode? {
				return getToken(BazaarParser.Tokens.DATA.rawValue, 0)
			}
			open
			func identifier() -> IdentifierContext? {
				return getRuleContext(IdentifierContext.self, 0)
			}
			open
			func LCURLY() -> TerminalNode? {
				return getToken(BazaarParser.Tokens.LCURLY.rawValue, 0)
			}
			open
			func RCURLY() -> TerminalNode? {
				return getToken(BazaarParser.Tokens.RCURLY.rawValue, 0)
			}
			open
			func field_list() -> Field_listContext? {
				return getRuleContext(Field_listContext.self, 0)
			}
		override open
		func getRuleIndex() -> Int {
			return BazaarParser.RULE_data_declaration
		}
		override open
		func accept<T>(_ visitor: ParseTreeVisitor<T>) -> T? {
			if let visitor = visitor as? BazaarParserVisitor {
			    return visitor.visitData_declaration(self)
			}
			else if let visitor = visitor as? BazaarParserBaseVisitor {
			    return visitor.visitData_declaration(self)
			}
			else {
			     return visitor.visitChildren(self)
			}
		}
	}
	@discardableResult
	 open func data_declaration() throws -> Data_declarationContext {
		var _localctx: Data_declarationContext
		_localctx = Data_declarationContext(_ctx, getState())
		try enterRule(_localctx, 6, BazaarParser.RULE_data_declaration)
		var _la: Int = 0
		defer {
	    		try! exitRule()
	    }
		do {
		 	try enterOuterAlt(_localctx, 1)
		 	setState(89)
		 	try match(BazaarParser.Tokens.DATA.rawValue)
		 	setState(90)
		 	try identifier()
		 	setState(91)
		 	try match(BazaarParser.Tokens.LCURLY.rawValue)
		 	setState(93)
		 	try _errHandler.sync(self)
		 	_la = try _input.LA(1)
		 	if (_la == BazaarParser.Tokens.IDENTIFIER.rawValue) {
		 		setState(92)
		 		try field_list()

		 	}

		 	setState(95)
		 	try match(BazaarParser.Tokens.RCURLY.rawValue)

		}
		catch ANTLRException.recognition(let re) {
			_localctx.exception = re
			_errHandler.reportError(self, re)
			try _errHandler.recover(self, re)
		}

		return _localctx
	}

	public class Field_listContext: ParserRuleContext {
			open
			func field() -> [FieldContext] {
				return getRuleContexts(FieldContext.self)
			}
			open
			func field(_ i: Int) -> FieldContext? {
				return getRuleContext(FieldContext.self, i)
			}
		override open
		func getRuleIndex() -> Int {
			return BazaarParser.RULE_field_list
		}
		override open
		func accept<T>(_ visitor: ParseTreeVisitor<T>) -> T? {
			if let visitor = visitor as? BazaarParserVisitor {
			    return visitor.visitField_list(self)
			}
			else if let visitor = visitor as? BazaarParserBaseVisitor {
			    return visitor.visitField_list(self)
			}
			else {
			     return visitor.visitChildren(self)
			}
		}
	}
	@discardableResult
	 open func field_list() throws -> Field_listContext {
		var _localctx: Field_listContext
		_localctx = Field_listContext(_ctx, getState())
		try enterRule(_localctx, 8, BazaarParser.RULE_field_list)
		var _la: Int = 0
		defer {
	    		try! exitRule()
	    }
		do {
		 	try enterOuterAlt(_localctx, 1)
		 	setState(97)
		 	try field()
		 	setState(101)
		 	try _errHandler.sync(self)
		 	_la = try _input.LA(1)
		 	while (_la == BazaarParser.Tokens.IDENTIFIER.rawValue) {
		 		setState(98)
		 		try field()


		 		setState(103)
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

	public class FieldContext: ParserRuleContext {
			open
			func identifier() -> IdentifierContext? {
				return getRuleContext(IdentifierContext.self, 0)
			}
			open
			func type_annotation() -> Type_annotationContext? {
				return getRuleContext(Type_annotationContext.self, 0)
			}
		override open
		func getRuleIndex() -> Int {
			return BazaarParser.RULE_field
		}
		override open
		func accept<T>(_ visitor: ParseTreeVisitor<T>) -> T? {
			if let visitor = visitor as? BazaarParserVisitor {
			    return visitor.visitField(self)
			}
			else if let visitor = visitor as? BazaarParserBaseVisitor {
			    return visitor.visitField(self)
			}
			else {
			     return visitor.visitChildren(self)
			}
		}
	}
	@discardableResult
	 open func field() throws -> FieldContext {
		var _localctx: FieldContext
		_localctx = FieldContext(_ctx, getState())
		try enterRule(_localctx, 10, BazaarParser.RULE_field)
		defer {
	    		try! exitRule()
	    }
		do {
		 	try enterOuterAlt(_localctx, 1)
		 	setState(104)
		 	try identifier()
		 	setState(105)
		 	try type_annotation()

		}
		catch ANTLRException.recognition(let re) {
			_localctx.exception = re
			_errHandler.reportError(self, re)
			try _errHandler.recover(self, re)
		}

		return _localctx
	}

	public class Enum_declarationContext: ParserRuleContext {
			open
			func ENUM() -> TerminalNode? {
				return getToken(BazaarParser.Tokens.ENUM.rawValue, 0)
			}
			open
			func identifier() -> IdentifierContext? {
				return getRuleContext(IdentifierContext.self, 0)
			}
			open
			func LCURLY() -> TerminalNode? {
				return getToken(BazaarParser.Tokens.LCURLY.rawValue, 0)
			}
			open
			func RCURLY() -> TerminalNode? {
				return getToken(BazaarParser.Tokens.RCURLY.rawValue, 0)
			}
			open
			func enum_value_list() -> Enum_value_listContext? {
				return getRuleContext(Enum_value_listContext.self, 0)
			}
		override open
		func getRuleIndex() -> Int {
			return BazaarParser.RULE_enum_declaration
		}
		override open
		func accept<T>(_ visitor: ParseTreeVisitor<T>) -> T? {
			if let visitor = visitor as? BazaarParserVisitor {
			    return visitor.visitEnum_declaration(self)
			}
			else if let visitor = visitor as? BazaarParserBaseVisitor {
			    return visitor.visitEnum_declaration(self)
			}
			else {
			     return visitor.visitChildren(self)
			}
		}
	}
	@discardableResult
	 open func enum_declaration() throws -> Enum_declarationContext {
		var _localctx: Enum_declarationContext
		_localctx = Enum_declarationContext(_ctx, getState())
		try enterRule(_localctx, 12, BazaarParser.RULE_enum_declaration)
		var _la: Int = 0
		defer {
	    		try! exitRule()
	    }
		do {
		 	try enterOuterAlt(_localctx, 1)
		 	setState(107)
		 	try match(BazaarParser.Tokens.ENUM.rawValue)
		 	setState(108)
		 	try identifier()
		 	setState(109)
		 	try match(BazaarParser.Tokens.LCURLY.rawValue)
		 	setState(111)
		 	try _errHandler.sync(self)
		 	_la = try _input.LA(1)
		 	if (_la == BazaarParser.Tokens.IDENTIFIER.rawValue) {
		 		setState(110)
		 		try enum_value_list()

		 	}

		 	setState(113)
		 	try match(BazaarParser.Tokens.RCURLY.rawValue)

		}
		catch ANTLRException.recognition(let re) {
			_localctx.exception = re
			_errHandler.reportError(self, re)
			try _errHandler.recover(self, re)
		}

		return _localctx
	}

	public class Enum_value_listContext: ParserRuleContext {
			open
			func enum_value() -> [Enum_valueContext] {
				return getRuleContexts(Enum_valueContext.self)
			}
			open
			func enum_value(_ i: Int) -> Enum_valueContext? {
				return getRuleContext(Enum_valueContext.self, i)
			}
		override open
		func getRuleIndex() -> Int {
			return BazaarParser.RULE_enum_value_list
		}
		override open
		func accept<T>(_ visitor: ParseTreeVisitor<T>) -> T? {
			if let visitor = visitor as? BazaarParserVisitor {
			    return visitor.visitEnum_value_list(self)
			}
			else if let visitor = visitor as? BazaarParserBaseVisitor {
			    return visitor.visitEnum_value_list(self)
			}
			else {
			     return visitor.visitChildren(self)
			}
		}
	}
	@discardableResult
	 open func enum_value_list() throws -> Enum_value_listContext {
		var _localctx: Enum_value_listContext
		_localctx = Enum_value_listContext(_ctx, getState())
		try enterRule(_localctx, 14, BazaarParser.RULE_enum_value_list)
		var _la: Int = 0
		defer {
	    		try! exitRule()
	    }
		do {
		 	try enterOuterAlt(_localctx, 1)
		 	setState(115)
		 	try enum_value()
		 	setState(119)
		 	try _errHandler.sync(self)
		 	_la = try _input.LA(1)
		 	while (_la == BazaarParser.Tokens.IDENTIFIER.rawValue) {
		 		setState(116)
		 		try enum_value()


		 		setState(121)
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

	public class Enum_valueContext: ParserRuleContext {
			open
			func identifier() -> IdentifierContext? {
				return getRuleContext(IdentifierContext.self, 0)
			}
		override open
		func getRuleIndex() -> Int {
			return BazaarParser.RULE_enum_value
		}
		override open
		func accept<T>(_ visitor: ParseTreeVisitor<T>) -> T? {
			if let visitor = visitor as? BazaarParserVisitor {
			    return visitor.visitEnum_value(self)
			}
			else if let visitor = visitor as? BazaarParserBaseVisitor {
			    return visitor.visitEnum_value(self)
			}
			else {
			     return visitor.visitChildren(self)
			}
		}
	}
	@discardableResult
	 open func enum_value() throws -> Enum_valueContext {
		var _localctx: Enum_valueContext
		_localctx = Enum_valueContext(_ctx, getState())
		try enterRule(_localctx, 16, BazaarParser.RULE_enum_value)
		defer {
	    		try! exitRule()
	    }
		do {
		 	try enterOuterAlt(_localctx, 1)
		 	setState(122)
		 	try identifier()

		}
		catch ANTLRException.recognition(let re) {
			_localctx.exception = re
			_errHandler.reportError(self, re)
			try _errHandler.recover(self, re)
		}

		return _localctx
	}

	public class Component_declarationContext: ParserRuleContext {
			open
			func COMPONENT() -> TerminalNode? {
				return getToken(BazaarParser.Tokens.COMPONENT.rawValue, 0)
			}
			open
			func identifier() -> IdentifierContext? {
				return getRuleContext(IdentifierContext.self, 0)
			}
			open
			func LCURLY() -> TerminalNode? {
				return getToken(BazaarParser.Tokens.LCURLY.rawValue, 0)
			}
			open
			func RCURLY() -> TerminalNode? {
				return getToken(BazaarParser.Tokens.RCURLY.rawValue, 0)
			}
			open
			func field_list() -> Field_listContext? {
				return getRuleContext(Field_listContext.self, 0)
			}
		override open
		func getRuleIndex() -> Int {
			return BazaarParser.RULE_component_declaration
		}
		override open
		func accept<T>(_ visitor: ParseTreeVisitor<T>) -> T? {
			if let visitor = visitor as? BazaarParserVisitor {
			    return visitor.visitComponent_declaration(self)
			}
			else if let visitor = visitor as? BazaarParserBaseVisitor {
			    return visitor.visitComponent_declaration(self)
			}
			else {
			     return visitor.visitChildren(self)
			}
		}
	}
	@discardableResult
	 open func component_declaration() throws -> Component_declarationContext {
		var _localctx: Component_declarationContext
		_localctx = Component_declarationContext(_ctx, getState())
		try enterRule(_localctx, 18, BazaarParser.RULE_component_declaration)
		var _la: Int = 0
		defer {
	    		try! exitRule()
	    }
		do {
		 	try enterOuterAlt(_localctx, 1)
		 	setState(124)
		 	try match(BazaarParser.Tokens.COMPONENT.rawValue)
		 	setState(125)
		 	try identifier()
		 	setState(126)
		 	try match(BazaarParser.Tokens.LCURLY.rawValue)
		 	setState(128)
		 	try _errHandler.sync(self)
		 	_la = try _input.LA(1)
		 	if (_la == BazaarParser.Tokens.IDENTIFIER.rawValue) {
		 		setState(127)
		 		try field_list()

		 	}

		 	setState(130)
		 	try match(BazaarParser.Tokens.RCURLY.rawValue)

		}
		catch ANTLRException.recognition(let re) {
			_localctx.exception = re
			_errHandler.reportError(self, re)
			try _errHandler.recover(self, re)
		}

		return _localctx
	}

	public class Function_declarationContext: ParserRuleContext {
			open
			func FUNCTION() -> TerminalNode? {
				return getToken(BazaarParser.Tokens.FUNCTION.rawValue, 0)
			}
			open
			func identifier() -> IdentifierContext? {
				return getRuleContext(IdentifierContext.self, 0)
			}
			open
			func parameter_clause() -> Parameter_clauseContext? {
				return getRuleContext(Parameter_clauseContext.self, 0)
			}
			open
			func return_type() -> Return_typeContext? {
				return getRuleContext(Return_typeContext.self, 0)
			}
			open
			func function_body() -> Function_bodyContext? {
				return getRuleContext(Function_bodyContext.self, 0)
			}
		override open
		func getRuleIndex() -> Int {
			return BazaarParser.RULE_function_declaration
		}
		override open
		func accept<T>(_ visitor: ParseTreeVisitor<T>) -> T? {
			if let visitor = visitor as? BazaarParserVisitor {
			    return visitor.visitFunction_declaration(self)
			}
			else if let visitor = visitor as? BazaarParserBaseVisitor {
			    return visitor.visitFunction_declaration(self)
			}
			else {
			     return visitor.visitChildren(self)
			}
		}
	}
	@discardableResult
	 open func function_declaration() throws -> Function_declarationContext {
		var _localctx: Function_declarationContext
		_localctx = Function_declarationContext(_ctx, getState())
		try enterRule(_localctx, 20, BazaarParser.RULE_function_declaration)
		var _la: Int = 0
		defer {
	    		try! exitRule()
	    }
		do {
		 	try enterOuterAlt(_localctx, 1)
		 	setState(132)
		 	try match(BazaarParser.Tokens.FUNCTION.rawValue)
		 	setState(133)
		 	try identifier()
		 	setState(134)
		 	try parameter_clause()
		 	setState(136)
		 	try _errHandler.sync(self)
		 	_la = try _input.LA(1)
		 	if (((Int64(_la) & ~0x3f) == 0 && ((Int64(1) << _la) & 8390400) != 0)) {
		 		setState(135)
		 		try return_type()

		 	}

		 	setState(139)
		 	try _errHandler.sync(self)
		 	_la = try _input.LA(1)
		 	if (_la == BazaarParser.Tokens.LCURLY.rawValue) {
		 		setState(138)
		 		try function_body()

		 	}


		}
		catch ANTLRException.recognition(let re) {
			_localctx.exception = re
			_errHandler.reportError(self, re)
			try _errHandler.recover(self, re)
		}

		return _localctx
	}

	public class Function_bodyContext: ParserRuleContext {
			open
			func LCURLY() -> TerminalNode? {
				return getToken(BazaarParser.Tokens.LCURLY.rawValue, 0)
			}
			open
			func RCURLY() -> TerminalNode? {
				return getToken(BazaarParser.Tokens.RCURLY.rawValue, 0)
			}
			open
			func statement_list() -> Statement_listContext? {
				return getRuleContext(Statement_listContext.self, 0)
			}
		override open
		func getRuleIndex() -> Int {
			return BazaarParser.RULE_function_body
		}
		override open
		func accept<T>(_ visitor: ParseTreeVisitor<T>) -> T? {
			if let visitor = visitor as? BazaarParserVisitor {
			    return visitor.visitFunction_body(self)
			}
			else if let visitor = visitor as? BazaarParserBaseVisitor {
			    return visitor.visitFunction_body(self)
			}
			else {
			     return visitor.visitChildren(self)
			}
		}
	}
	@discardableResult
	 open func function_body() throws -> Function_bodyContext {
		var _localctx: Function_bodyContext
		_localctx = Function_bodyContext(_ctx, getState())
		try enterRule(_localctx, 22, BazaarParser.RULE_function_body)
		var _la: Int = 0
		defer {
	    		try! exitRule()
	    }
		do {
		 	try enterOuterAlt(_localctx, 1)
		 	setState(141)
		 	try match(BazaarParser.Tokens.LCURLY.rawValue)
		 	setState(143)
		 	try _errHandler.sync(self)
		 	_la = try _input.LA(1)
		 	if (((Int64(_la) & ~0x3f) == 0 && ((Int64(1) << _la) & 13107200) != 0)) {
		 		setState(142)
		 		try statement_list()

		 	}

		 	setState(145)
		 	try match(BazaarParser.Tokens.RCURLY.rawValue)

		}
		catch ANTLRException.recognition(let re) {
			_localctx.exception = re
			_errHandler.reportError(self, re)
			try _errHandler.recover(self, re)
		}

		return _localctx
	}

	public class Statement_listContext: ParserRuleContext {
			open
			func statement() -> [StatementContext] {
				return getRuleContexts(StatementContext.self)
			}
			open
			func statement(_ i: Int) -> StatementContext? {
				return getRuleContext(StatementContext.self, i)
			}
		override open
		func getRuleIndex() -> Int {
			return BazaarParser.RULE_statement_list
		}
		override open
		func accept<T>(_ visitor: ParseTreeVisitor<T>) -> T? {
			if let visitor = visitor as? BazaarParserVisitor {
			    return visitor.visitStatement_list(self)
			}
			else if let visitor = visitor as? BazaarParserBaseVisitor {
			    return visitor.visitStatement_list(self)
			}
			else {
			     return visitor.visitChildren(self)
			}
		}
	}
	@discardableResult
	 open func statement_list() throws -> Statement_listContext {
		var _localctx: Statement_listContext
		_localctx = Statement_listContext(_ctx, getState())
		try enterRule(_localctx, 24, BazaarParser.RULE_statement_list)
		var _la: Int = 0
		defer {
	    		try! exitRule()
	    }
		do {
		 	try enterOuterAlt(_localctx, 1)
		 	setState(147)
		 	try statement()
		 	setState(151)
		 	try _errHandler.sync(self)
		 	_la = try _input.LA(1)
		 	while (((Int64(_la) & ~0x3f) == 0 && ((Int64(1) << _la) & 13107200) != 0)) {
		 		setState(148)
		 		try statement()


		 		setState(153)
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

	public class StatementContext: ParserRuleContext {
			open
			func expression() -> ExpressionContext? {
				return getRuleContext(ExpressionContext.self, 0)
			}
		override open
		func getRuleIndex() -> Int {
			return BazaarParser.RULE_statement
		}
		override open
		func accept<T>(_ visitor: ParseTreeVisitor<T>) -> T? {
			if let visitor = visitor as? BazaarParserVisitor {
			    return visitor.visitStatement(self)
			}
			else if let visitor = visitor as? BazaarParserBaseVisitor {
			    return visitor.visitStatement(self)
			}
			else {
			     return visitor.visitChildren(self)
			}
		}
	}
	@discardableResult
	 open func statement() throws -> StatementContext {
		var _localctx: StatementContext
		_localctx = StatementContext(_ctx, getState())
		try enterRule(_localctx, 26, BazaarParser.RULE_statement)
		defer {
	    		try! exitRule()
	    }
		do {
		 	try enterOuterAlt(_localctx, 1)
		 	setState(154)
		 	try expression()

		}
		catch ANTLRException.recognition(let re) {
			_localctx.exception = re
			_errHandler.reportError(self, re)
			try _errHandler.recover(self, re)
		}

		return _localctx
	}

	public class Return_typeContext: ParserRuleContext {
			open
			func type_annotation() -> Type_annotationContext? {
				return getRuleContext(Type_annotationContext.self, 0)
			}
		override open
		func getRuleIndex() -> Int {
			return BazaarParser.RULE_return_type
		}
		override open
		func accept<T>(_ visitor: ParseTreeVisitor<T>) -> T? {
			if let visitor = visitor as? BazaarParserVisitor {
			    return visitor.visitReturn_type(self)
			}
			else if let visitor = visitor as? BazaarParserBaseVisitor {
			    return visitor.visitReturn_type(self)
			}
			else {
			     return visitor.visitChildren(self)
			}
		}
	}
	@discardableResult
	 open func return_type() throws -> Return_typeContext {
		var _localctx: Return_typeContext
		_localctx = Return_typeContext(_ctx, getState())
		try enterRule(_localctx, 28, BazaarParser.RULE_return_type)
		defer {
	    		try! exitRule()
	    }
		do {
		 	try enterOuterAlt(_localctx, 1)
		 	setState(156)
		 	try type_annotation()

		}
		catch ANTLRException.recognition(let re) {
			_localctx.exception = re
			_errHandler.reportError(self, re)
			try _errHandler.recover(self, re)
		}

		return _localctx
	}

	public class Template_declarationContext: ParserRuleContext {
			open
			func TEMPLATE() -> TerminalNode? {
				return getToken(BazaarParser.Tokens.TEMPLATE.rawValue, 0)
			}
			open
			func identifier() -> IdentifierContext? {
				return getRuleContext(IdentifierContext.self, 0)
			}
			open
			func parameter_clause() -> Parameter_clauseContext? {
				return getRuleContext(Parameter_clauseContext.self, 0)
			}
			open
			func template_body() -> Template_bodyContext? {
				return getRuleContext(Template_bodyContext.self, 0)
			}
			open
			func return_type() -> Return_typeContext? {
				return getRuleContext(Return_typeContext.self, 0)
			}
		override open
		func getRuleIndex() -> Int {
			return BazaarParser.RULE_template_declaration
		}
		override open
		func accept<T>(_ visitor: ParseTreeVisitor<T>) -> T? {
			if let visitor = visitor as? BazaarParserVisitor {
			    return visitor.visitTemplate_declaration(self)
			}
			else if let visitor = visitor as? BazaarParserBaseVisitor {
			    return visitor.visitTemplate_declaration(self)
			}
			else {
			     return visitor.visitChildren(self)
			}
		}
	}
	@discardableResult
	 open func template_declaration() throws -> Template_declarationContext {
		var _localctx: Template_declarationContext
		_localctx = Template_declarationContext(_ctx, getState())
		try enterRule(_localctx, 30, BazaarParser.RULE_template_declaration)
		var _la: Int = 0
		defer {
	    		try! exitRule()
	    }
		do {
		 	try enterOuterAlt(_localctx, 1)
		 	setState(158)
		 	try match(BazaarParser.Tokens.TEMPLATE.rawValue)
		 	setState(159)
		 	try identifier()
		 	setState(160)
		 	try parameter_clause()
		 	setState(162)
		 	try _errHandler.sync(self)
		 	_la = try _input.LA(1)
		 	if (((Int64(_la) & ~0x3f) == 0 && ((Int64(1) << _la) & 8390400) != 0)) {
		 		setState(161)
		 		try return_type()

		 	}

		 	setState(164)
		 	try template_body()

		}
		catch ANTLRException.recognition(let re) {
			_localctx.exception = re
			_errHandler.reportError(self, re)
			try _errHandler.recover(self, re)
		}

		return _localctx
	}

	public class Template_bodyContext: ParserRuleContext {
			open
			func LCURLY() -> TerminalNode? {
				return getToken(BazaarParser.Tokens.LCURLY.rawValue, 0)
			}
			open
			func template_content() -> Template_contentContext? {
				return getRuleContext(Template_contentContext.self, 0)
			}
			open
			func RCURLY() -> TerminalNode? {
				return getToken(BazaarParser.Tokens.RCURLY.rawValue, 0)
			}
		override open
		func getRuleIndex() -> Int {
			return BazaarParser.RULE_template_body
		}
		override open
		func accept<T>(_ visitor: ParseTreeVisitor<T>) -> T? {
			if let visitor = visitor as? BazaarParserVisitor {
			    return visitor.visitTemplate_body(self)
			}
			else if let visitor = visitor as? BazaarParserBaseVisitor {
			    return visitor.visitTemplate_body(self)
			}
			else {
			     return visitor.visitChildren(self)
			}
		}
	}
	@discardableResult
	 open func template_body() throws -> Template_bodyContext {
		var _localctx: Template_bodyContext
		_localctx = Template_bodyContext(_ctx, getState())
		try enterRule(_localctx, 32, BazaarParser.RULE_template_body)
		defer {
	    		try! exitRule()
	    }
		do {
		 	try enterOuterAlt(_localctx, 1)
		 	setState(166)
		 	try match(BazaarParser.Tokens.LCURLY.rawValue)
		 	setState(167)
		 	try template_content()
		 	setState(168)
		 	try match(BazaarParser.Tokens.RCURLY.rawValue)

		}
		catch ANTLRException.recognition(let re) {
			_localctx.exception = re
			_errHandler.reportError(self, re)
			try _errHandler.recover(self, re)
		}

		return _localctx
	}

	public class Template_contentContext: ParserRuleContext {
			open
			func component_instantiation() -> Component_instantiationContext? {
				return getRuleContext(Component_instantiationContext.self, 0)
			}
		override open
		func getRuleIndex() -> Int {
			return BazaarParser.RULE_template_content
		}
		override open
		func accept<T>(_ visitor: ParseTreeVisitor<T>) -> T? {
			if let visitor = visitor as? BazaarParserVisitor {
			    return visitor.visitTemplate_content(self)
			}
			else if let visitor = visitor as? BazaarParserBaseVisitor {
			    return visitor.visitTemplate_content(self)
			}
			else {
			     return visitor.visitChildren(self)
			}
		}
	}
	@discardableResult
	 open func template_content() throws -> Template_contentContext {
		var _localctx: Template_contentContext
		_localctx = Template_contentContext(_ctx, getState())
		try enterRule(_localctx, 34, BazaarParser.RULE_template_content)
		defer {
	    		try! exitRule()
	    }
		do {
		 	try enterOuterAlt(_localctx, 1)
		 	setState(170)
		 	try component_instantiation()

		}
		catch ANTLRException.recognition(let re) {
			_localctx.exception = re
			_errHandler.reportError(self, re)
			try _errHandler.recover(self, re)
		}

		return _localctx
	}

	public class Component_instantiationContext: ParserRuleContext {
			open
			func identifier() -> IdentifierContext? {
				return getRuleContext(IdentifierContext.self, 0)
			}
			open
			func LPAREN() -> TerminalNode? {
				return getToken(BazaarParser.Tokens.LPAREN.rawValue, 0)
			}
			open
			func RPAREN() -> TerminalNode? {
				return getToken(BazaarParser.Tokens.RPAREN.rawValue, 0)
			}
			open
			func component_body() -> Component_bodyContext? {
				return getRuleContext(Component_bodyContext.self, 0)
			}
			open
			func argument_list() -> Argument_listContext? {
				return getRuleContext(Argument_listContext.self, 0)
			}
		override open
		func getRuleIndex() -> Int {
			return BazaarParser.RULE_component_instantiation
		}
		override open
		func accept<T>(_ visitor: ParseTreeVisitor<T>) -> T? {
			if let visitor = visitor as? BazaarParserVisitor {
			    return visitor.visitComponent_instantiation(self)
			}
			else if let visitor = visitor as? BazaarParserBaseVisitor {
			    return visitor.visitComponent_instantiation(self)
			}
			else {
			     return visitor.visitChildren(self)
			}
		}
	}
	@discardableResult
	 open func component_instantiation() throws -> Component_instantiationContext {
		var _localctx: Component_instantiationContext
		_localctx = Component_instantiationContext(_ctx, getState())
		try enterRule(_localctx, 36, BazaarParser.RULE_component_instantiation)
		var _la: Int = 0
		defer {
	    		try! exitRule()
	    }
		do {
		 	try enterOuterAlt(_localctx, 1)
		 	setState(172)
		 	try identifier()
		 	setState(178)
		 	try _errHandler.sync(self)
		 	_la = try _input.LA(1)
		 	if (_la == BazaarParser.Tokens.LPAREN.rawValue) {
		 		setState(173)
		 		try match(BazaarParser.Tokens.LPAREN.rawValue)
		 		setState(175)
		 		try _errHandler.sync(self)
		 		_la = try _input.LA(1)
		 		if (((Int64(_la) & ~0x3f) == 0 && ((Int64(1) << _la) & 13107200) != 0)) {
		 			setState(174)
		 			try argument_list()

		 		}

		 		setState(177)
		 		try match(BazaarParser.Tokens.RPAREN.rawValue)

		 	}

		 	setState(181)
		 	try _errHandler.sync(self)
		 	_la = try _input.LA(1)
		 	if (_la == BazaarParser.Tokens.LCURLY.rawValue) {
		 		setState(180)
		 		try component_body()

		 	}


		}
		catch ANTLRException.recognition(let re) {
			_localctx.exception = re
			_errHandler.reportError(self, re)
			try _errHandler.recover(self, re)
		}

		return _localctx
	}

	public class Component_bodyContext: ParserRuleContext {
			open
			func LCURLY() -> TerminalNode? {
				return getToken(BazaarParser.Tokens.LCURLY.rawValue, 0)
			}
			open
			func RCURLY() -> TerminalNode? {
				return getToken(BazaarParser.Tokens.RCURLY.rawValue, 0)
			}
			open
			func component_content_list() -> Component_content_listContext? {
				return getRuleContext(Component_content_listContext.self, 0)
			}
			open
			func event_handler() -> [Event_handlerContext] {
				return getRuleContexts(Event_handlerContext.self)
			}
			open
			func event_handler(_ i: Int) -> Event_handlerContext? {
				return getRuleContext(Event_handlerContext.self, i)
			}
		override open
		func getRuleIndex() -> Int {
			return BazaarParser.RULE_component_body
		}
		override open
		func accept<T>(_ visitor: ParseTreeVisitor<T>) -> T? {
			if let visitor = visitor as? BazaarParserVisitor {
			    return visitor.visitComponent_body(self)
			}
			else if let visitor = visitor as? BazaarParserBaseVisitor {
			    return visitor.visitComponent_body(self)
			}
			else {
			     return visitor.visitChildren(self)
			}
		}
	}
	@discardableResult
	 open func component_body() throws -> Component_bodyContext {
		var _localctx: Component_bodyContext
		_localctx = Component_bodyContext(_ctx, getState())
		try enterRule(_localctx, 38, BazaarParser.RULE_component_body)
		var _la: Int = 0
		defer {
	    		try! exitRule()
	    }
		do {
			var _alt:Int
		 	try enterOuterAlt(_localctx, 1)
		 	setState(183)
		 	try match(BazaarParser.Tokens.LCURLY.rawValue)
		 	setState(185)
		 	try _errHandler.sync(self)
		 	_la = try _input.LA(1)
		 	if (_la == BazaarParser.Tokens.IF.rawValue || _la == BazaarParser.Tokens.IDENTIFIER.rawValue) {
		 		setState(184)
		 		try component_content_list()

		 	}

		 	setState(187)
		 	try match(BazaarParser.Tokens.RCURLY.rawValue)
		 	setState(191)
		 	try _errHandler.sync(self)
		 	_alt = try getInterpreter().adaptivePredict(_input,16,_ctx)
		 	while (_alt != 2 && _alt != ATN.INVALID_ALT_NUMBER) {
		 		if ( _alt==1 ) {
		 			setState(188)
		 			try event_handler()

		 	 
		 		}
		 		setState(193)
		 		try _errHandler.sync(self)
		 		_alt = try getInterpreter().adaptivePredict(_input,16,_ctx)
		 	}

		}
		catch ANTLRException.recognition(let re) {
			_localctx.exception = re
			_errHandler.reportError(self, re)
			try _errHandler.recover(self, re)
		}

		return _localctx
	}

	public class Component_content_listContext: ParserRuleContext {
			open
			func component_content() -> [Component_contentContext] {
				return getRuleContexts(Component_contentContext.self)
			}
			open
			func component_content(_ i: Int) -> Component_contentContext? {
				return getRuleContext(Component_contentContext.self, i)
			}
		override open
		func getRuleIndex() -> Int {
			return BazaarParser.RULE_component_content_list
		}
		override open
		func accept<T>(_ visitor: ParseTreeVisitor<T>) -> T? {
			if let visitor = visitor as? BazaarParserVisitor {
			    return visitor.visitComponent_content_list(self)
			}
			else if let visitor = visitor as? BazaarParserBaseVisitor {
			    return visitor.visitComponent_content_list(self)
			}
			else {
			     return visitor.visitChildren(self)
			}
		}
	}
	@discardableResult
	 open func component_content_list() throws -> Component_content_listContext {
		var _localctx: Component_content_listContext
		_localctx = Component_content_listContext(_ctx, getState())
		try enterRule(_localctx, 40, BazaarParser.RULE_component_content_list)
		var _la: Int = 0
		defer {
	    		try! exitRule()
	    }
		do {
		 	try enterOuterAlt(_localctx, 1)
		 	setState(194)
		 	try component_content()
		 	setState(198)
		 	try _errHandler.sync(self)
		 	_la = try _input.LA(1)
		 	while (_la == BazaarParser.Tokens.IF.rawValue || _la == BazaarParser.Tokens.IDENTIFIER.rawValue) {
		 		setState(195)
		 		try component_content()


		 		setState(200)
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

	public class Component_contentContext: ParserRuleContext {
			open
			func component_instantiation() -> Component_instantiationContext? {
				return getRuleContext(Component_instantiationContext.self, 0)
			}
			open
			func conditional_statement() -> Conditional_statementContext? {
				return getRuleContext(Conditional_statementContext.self, 0)
			}
		override open
		func getRuleIndex() -> Int {
			return BazaarParser.RULE_component_content
		}
		override open
		func accept<T>(_ visitor: ParseTreeVisitor<T>) -> T? {
			if let visitor = visitor as? BazaarParserVisitor {
			    return visitor.visitComponent_content(self)
			}
			else if let visitor = visitor as? BazaarParserBaseVisitor {
			    return visitor.visitComponent_content(self)
			}
			else {
			     return visitor.visitChildren(self)
			}
		}
	}
	@discardableResult
	 open func component_content() throws -> Component_contentContext {
		var _localctx: Component_contentContext
		_localctx = Component_contentContext(_ctx, getState())
		try enterRule(_localctx, 42, BazaarParser.RULE_component_content)
		defer {
	    		try! exitRule()
	    }
		do {
		 	setState(203)
		 	try _errHandler.sync(self)
		 	switch (BazaarParser.Tokens(rawValue: try _input.LA(1))!) {
		 	case .IDENTIFIER:
		 		try enterOuterAlt(_localctx, 1)
		 		setState(201)
		 		try component_instantiation()

		 		break

		 	case .IF:
		 		try enterOuterAlt(_localctx, 2)
		 		setState(202)
		 		try conditional_statement()

		 		break
		 	default:
		 		throw ANTLRException.recognition(e: NoViableAltException(self))
		 	}
		}
		catch ANTLRException.recognition(let re) {
			_localctx.exception = re
			_errHandler.reportError(self, re)
			try _errHandler.recover(self, re)
		}

		return _localctx
	}

	public class Conditional_statementContext: ParserRuleContext {
			open
			func IF() -> TerminalNode? {
				return getToken(BazaarParser.Tokens.IF.rawValue, 0)
			}
			open
			func LPAREN() -> TerminalNode? {
				return getToken(BazaarParser.Tokens.LPAREN.rawValue, 0)
			}
			open
			func expression() -> ExpressionContext? {
				return getRuleContext(ExpressionContext.self, 0)
			}
			open
			func RPAREN() -> TerminalNode? {
				return getToken(BazaarParser.Tokens.RPAREN.rawValue, 0)
			}
			open
			func LCURLY() -> TerminalNode? {
				return getToken(BazaarParser.Tokens.LCURLY.rawValue, 0)
			}
			open
			func RCURLY() -> TerminalNode? {
				return getToken(BazaarParser.Tokens.RCURLY.rawValue, 0)
			}
			open
			func component_content_list() -> Component_content_listContext? {
				return getRuleContext(Component_content_listContext.self, 0)
			}
		override open
		func getRuleIndex() -> Int {
			return BazaarParser.RULE_conditional_statement
		}
		override open
		func accept<T>(_ visitor: ParseTreeVisitor<T>) -> T? {
			if let visitor = visitor as? BazaarParserVisitor {
			    return visitor.visitConditional_statement(self)
			}
			else if let visitor = visitor as? BazaarParserBaseVisitor {
			    return visitor.visitConditional_statement(self)
			}
			else {
			     return visitor.visitChildren(self)
			}
		}
	}
	@discardableResult
	 open func conditional_statement() throws -> Conditional_statementContext {
		var _localctx: Conditional_statementContext
		_localctx = Conditional_statementContext(_ctx, getState())
		try enterRule(_localctx, 44, BazaarParser.RULE_conditional_statement)
		var _la: Int = 0
		defer {
	    		try! exitRule()
	    }
		do {
		 	try enterOuterAlt(_localctx, 1)
		 	setState(205)
		 	try match(BazaarParser.Tokens.IF.rawValue)
		 	setState(206)
		 	try match(BazaarParser.Tokens.LPAREN.rawValue)
		 	setState(207)
		 	try expression()
		 	setState(208)
		 	try match(BazaarParser.Tokens.RPAREN.rawValue)
		 	setState(209)
		 	try match(BazaarParser.Tokens.LCURLY.rawValue)
		 	setState(211)
		 	try _errHandler.sync(self)
		 	_la = try _input.LA(1)
		 	if (_la == BazaarParser.Tokens.IF.rawValue || _la == BazaarParser.Tokens.IDENTIFIER.rawValue) {
		 		setState(210)
		 		try component_content_list()

		 	}

		 	setState(213)
		 	try match(BazaarParser.Tokens.RCURLY.rawValue)

		}
		catch ANTLRException.recognition(let re) {
			_localctx.exception = re
			_errHandler.reportError(self, re)
			try _errHandler.recover(self, re)
		}

		return _localctx
	}

	public class Event_handlerContext: ParserRuleContext {
			open
			func identifier() -> IdentifierContext? {
				return getRuleContext(IdentifierContext.self, 0)
			}
			open
			func LCURLY() -> TerminalNode? {
				return getToken(BazaarParser.Tokens.LCURLY.rawValue, 0)
			}
			open
			func RCURLY() -> TerminalNode? {
				return getToken(BazaarParser.Tokens.RCURLY.rawValue, 0)
			}
			open
			func statement_list() -> Statement_listContext? {
				return getRuleContext(Statement_listContext.self, 0)
			}
		override open
		func getRuleIndex() -> Int {
			return BazaarParser.RULE_event_handler
		}
		override open
		func accept<T>(_ visitor: ParseTreeVisitor<T>) -> T? {
			if let visitor = visitor as? BazaarParserVisitor {
			    return visitor.visitEvent_handler(self)
			}
			else if let visitor = visitor as? BazaarParserBaseVisitor {
			    return visitor.visitEvent_handler(self)
			}
			else {
			     return visitor.visitChildren(self)
			}
		}
	}
	@discardableResult
	 open func event_handler() throws -> Event_handlerContext {
		var _localctx: Event_handlerContext
		_localctx = Event_handlerContext(_ctx, getState())
		try enterRule(_localctx, 46, BazaarParser.RULE_event_handler)
		var _la: Int = 0
		defer {
	    		try! exitRule()
	    }
		do {
		 	try enterOuterAlt(_localctx, 1)
		 	setState(215)
		 	try identifier()
		 	setState(216)
		 	try match(BazaarParser.Tokens.LCURLY.rawValue)
		 	setState(218)
		 	try _errHandler.sync(self)
		 	_la = try _input.LA(1)
		 	if (((Int64(_la) & ~0x3f) == 0 && ((Int64(1) << _la) & 13107200) != 0)) {
		 		setState(217)
		 		try statement_list()

		 	}

		 	setState(220)
		 	try match(BazaarParser.Tokens.RCURLY.rawValue)

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
		try enterRule(_localctx, 48, BazaarParser.RULE_parameter_clause)
		var _la: Int = 0
		defer {
	    		try! exitRule()
	    }
		do {
		 	try enterOuterAlt(_localctx, 1)
		 	setState(222)
		 	try match(BazaarParser.Tokens.LPAREN.rawValue)
		 	setState(224)
		 	try _errHandler.sync(self)
		 	_la = try _input.LA(1)
		 	if (_la == BazaarParser.Tokens.IDENTIFIER.rawValue) {
		 		setState(223)
		 		try parameter_list()

		 	}

		 	setState(226)
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
		try enterRule(_localctx, 50, BazaarParser.RULE_parameter_list)
		var _la: Int = 0
		defer {
	    		try! exitRule()
	    }
		do {
		 	try enterOuterAlt(_localctx, 1)
		 	setState(228)
		 	try parameter()
		 	setState(233)
		 	try _errHandler.sync(self)
		 	_la = try _input.LA(1)
		 	while (_la == BazaarParser.Tokens.COMMA.rawValue) {
		 		setState(229)
		 		try match(BazaarParser.Tokens.COMMA.rawValue)
		 		setState(230)
		 		try parameter()


		 		setState(235)
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
			func type_annotation() -> Type_annotationContext? {
				return getRuleContext(Type_annotationContext.self, 0)
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
		try enterRule(_localctx, 52, BazaarParser.RULE_parameter)
		defer {
	    		try! exitRule()
	    }
		do {
		 	try enterOuterAlt(_localctx, 1)
		 	setState(236)
		 	try identifier()
		 	setState(237)
		 	try type_annotation()

		}
		catch ANTLRException.recognition(let re) {
			_localctx.exception = re
			_errHandler.reportError(self, re)
			try _errHandler.recover(self, re)
		}

		return _localctx
	}

	public class Argument_listContext: ParserRuleContext {
			open
			func argument() -> [ArgumentContext] {
				return getRuleContexts(ArgumentContext.self)
			}
			open
			func argument(_ i: Int) -> ArgumentContext? {
				return getRuleContext(ArgumentContext.self, i)
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
			return BazaarParser.RULE_argument_list
		}
		override open
		func accept<T>(_ visitor: ParseTreeVisitor<T>) -> T? {
			if let visitor = visitor as? BazaarParserVisitor {
			    return visitor.visitArgument_list(self)
			}
			else if let visitor = visitor as? BazaarParserBaseVisitor {
			    return visitor.visitArgument_list(self)
			}
			else {
			     return visitor.visitChildren(self)
			}
		}
	}
	@discardableResult
	 open func argument_list() throws -> Argument_listContext {
		var _localctx: Argument_listContext
		_localctx = Argument_listContext(_ctx, getState())
		try enterRule(_localctx, 54, BazaarParser.RULE_argument_list)
		var _la: Int = 0
		defer {
	    		try! exitRule()
	    }
		do {
		 	try enterOuterAlt(_localctx, 1)
		 	setState(239)
		 	try argument()
		 	setState(244)
		 	try _errHandler.sync(self)
		 	_la = try _input.LA(1)
		 	while (_la == BazaarParser.Tokens.COMMA.rawValue) {
		 		setState(240)
		 		try match(BazaarParser.Tokens.COMMA.rawValue)
		 		setState(241)
		 		try argument()


		 		setState(246)
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

	public class ArgumentContext: ParserRuleContext {
			open
			func expression() -> ExpressionContext? {
				return getRuleContext(ExpressionContext.self, 0)
			}
			open
			func identifier() -> IdentifierContext? {
				return getRuleContext(IdentifierContext.self, 0)
			}
			open
			func EQUALS() -> TerminalNode? {
				return getToken(BazaarParser.Tokens.EQUALS.rawValue, 0)
			}
		override open
		func getRuleIndex() -> Int {
			return BazaarParser.RULE_argument
		}
		override open
		func accept<T>(_ visitor: ParseTreeVisitor<T>) -> T? {
			if let visitor = visitor as? BazaarParserVisitor {
			    return visitor.visitArgument(self)
			}
			else if let visitor = visitor as? BazaarParserBaseVisitor {
			    return visitor.visitArgument(self)
			}
			else {
			     return visitor.visitChildren(self)
			}
		}
	}
	@discardableResult
	 open func argument() throws -> ArgumentContext {
		var _localctx: ArgumentContext
		_localctx = ArgumentContext(_ctx, getState())
		try enterRule(_localctx, 56, BazaarParser.RULE_argument)
		defer {
	    		try! exitRule()
	    }
		do {
		 	try enterOuterAlt(_localctx, 1)
		 	setState(250)
		 	try _errHandler.sync(self)
		 	switch (try getInterpreter().adaptivePredict(_input,24,_ctx)) {
		 	case 1:
		 		setState(247)
		 		try identifier()
		 		setState(248)
		 		try match(BazaarParser.Tokens.EQUALS.rawValue)

		 		break
		 	default: break
		 	}
		 	setState(252)
		 	try expression()

		}
		catch ANTLRException.recognition(let re) {
			_localctx.exception = re
			_errHandler.reportError(self, re)
			try _errHandler.recover(self, re)
		}

		return _localctx
	}

	public class ExpressionContext: ParserRuleContext {
			open
			func primary_expression() -> Primary_expressionContext? {
				return getRuleContext(Primary_expressionContext.self, 0)
			}
			open
			func DOT() -> [TerminalNode] {
				return getTokens(BazaarParser.Tokens.DOT.rawValue)
			}
			open
			func DOT(_ i:Int) -> TerminalNode? {
				return getToken(BazaarParser.Tokens.DOT.rawValue, i)
			}
			open
			func identifier() -> [IdentifierContext] {
				return getRuleContexts(IdentifierContext.self)
			}
			open
			func identifier(_ i: Int) -> IdentifierContext? {
				return getRuleContext(IdentifierContext.self, i)
			}
		override open
		func getRuleIndex() -> Int {
			return BazaarParser.RULE_expression
		}
		override open
		func accept<T>(_ visitor: ParseTreeVisitor<T>) -> T? {
			if let visitor = visitor as? BazaarParserVisitor {
			    return visitor.visitExpression(self)
			}
			else if let visitor = visitor as? BazaarParserBaseVisitor {
			    return visitor.visitExpression(self)
			}
			else {
			     return visitor.visitChildren(self)
			}
		}
	}
	@discardableResult
	 open func expression() throws -> ExpressionContext {
		var _localctx: ExpressionContext
		_localctx = ExpressionContext(_ctx, getState())
		try enterRule(_localctx, 58, BazaarParser.RULE_expression)
		defer {
	    		try! exitRule()
	    }
		do {
			var _alt:Int
		 	try enterOuterAlt(_localctx, 1)
		 	setState(254)
		 	try primary_expression()
		 	setState(259)
		 	try _errHandler.sync(self)
		 	_alt = try getInterpreter().adaptivePredict(_input,25,_ctx)
		 	while (_alt != 2 && _alt != ATN.INVALID_ALT_NUMBER) {
		 		if ( _alt==1 ) {
		 			setState(255)
		 			try match(BazaarParser.Tokens.DOT.rawValue)
		 			setState(256)
		 			try identifier()

		 	 
		 		}
		 		setState(261)
		 		try _errHandler.sync(self)
		 		_alt = try getInterpreter().adaptivePredict(_input,25,_ctx)
		 	}

		}
		catch ANTLRException.recognition(let re) {
			_localctx.exception = re
			_errHandler.reportError(self, re)
			try _errHandler.recover(self, re)
		}

		return _localctx
	}

	public class Primary_expressionContext: ParserRuleContext {
			open
			func identifier() -> IdentifierContext? {
				return getRuleContext(IdentifierContext.self, 0)
			}
			open
			func LPAREN() -> TerminalNode? {
				return getToken(BazaarParser.Tokens.LPAREN.rawValue, 0)
			}
			open
			func RPAREN() -> TerminalNode? {
				return getToken(BazaarParser.Tokens.RPAREN.rawValue, 0)
			}
			open
			func argument_list() -> Argument_listContext? {
				return getRuleContext(Argument_listContext.self, 0)
			}
			open
			func string_literal() -> String_literalContext? {
				return getRuleContext(String_literalContext.self, 0)
			}
			open
			func DOT() -> TerminalNode? {
				return getToken(BazaarParser.Tokens.DOT.rawValue, 0)
			}
		override open
		func getRuleIndex() -> Int {
			return BazaarParser.RULE_primary_expression
		}
		override open
		func accept<T>(_ visitor: ParseTreeVisitor<T>) -> T? {
			if let visitor = visitor as? BazaarParserVisitor {
			    return visitor.visitPrimary_expression(self)
			}
			else if let visitor = visitor as? BazaarParserBaseVisitor {
			    return visitor.visitPrimary_expression(self)
			}
			else {
			     return visitor.visitChildren(self)
			}
		}
	}
	@discardableResult
	 open func primary_expression() throws -> Primary_expressionContext {
		var _localctx: Primary_expressionContext
		_localctx = Primary_expressionContext(_ctx, getState())
		try enterRule(_localctx, 60, BazaarParser.RULE_primary_expression)
		var _la: Int = 0
		defer {
	    		try! exitRule()
	    }
		do {
		 	setState(273)
		 	try _errHandler.sync(self)
		 	switch (BazaarParser.Tokens(rawValue: try _input.LA(1))!) {
		 	case .IDENTIFIER:
		 		try enterOuterAlt(_localctx, 1)
		 		setState(262)
		 		try identifier()
		 		setState(268)
		 		try _errHandler.sync(self)
		 		_la = try _input.LA(1)
		 		if (_la == BazaarParser.Tokens.LPAREN.rawValue) {
		 			setState(263)
		 			try match(BazaarParser.Tokens.LPAREN.rawValue)
		 			setState(265)
		 			try _errHandler.sync(self)
		 			_la = try _input.LA(1)
		 			if (((Int64(_la) & ~0x3f) == 0 && ((Int64(1) << _la) & 13107200) != 0)) {
		 				setState(264)
		 				try argument_list()

		 			}

		 			setState(267)
		 			try match(BazaarParser.Tokens.RPAREN.rawValue)

		 		}


		 		break

		 	case .STRING_LITERAL:
		 		try enterOuterAlt(_localctx, 2)
		 		setState(270)
		 		try string_literal()

		 		break

		 	case .DOT:
		 		try enterOuterAlt(_localctx, 3)
		 		setState(271)
		 		try match(BazaarParser.Tokens.DOT.rawValue)
		 		setState(272)
		 		try identifier()

		 		break
		 	default:
		 		throw ANTLRException.recognition(e: NoViableAltException(self))
		 	}
		}
		catch ANTLRException.recognition(let re) {
			_localctx.exception = re
			_errHandler.reportError(self, re)
			try _errHandler.recover(self, re)
		}

		return _localctx
	}

	public class Type_annotationContext: ParserRuleContext {
			open
			func type_identifier() -> Type_identifierContext? {
				return getRuleContext(Type_identifierContext.self, 0)
			}
			open
			func QUESTION() -> TerminalNode? {
				return getToken(BazaarParser.Tokens.QUESTION.rawValue, 0)
			}
			open
			func LBRACKET() -> TerminalNode? {
				return getToken(BazaarParser.Tokens.LBRACKET.rawValue, 0)
			}
			open
			func RBRACKET() -> TerminalNode? {
				return getToken(BazaarParser.Tokens.RBRACKET.rawValue, 0)
			}
		override open
		func getRuleIndex() -> Int {
			return BazaarParser.RULE_type_annotation
		}
		override open
		func accept<T>(_ visitor: ParseTreeVisitor<T>) -> T? {
			if let visitor = visitor as? BazaarParserVisitor {
			    return visitor.visitType_annotation(self)
			}
			else if let visitor = visitor as? BazaarParserBaseVisitor {
			    return visitor.visitType_annotation(self)
			}
			else {
			     return visitor.visitChildren(self)
			}
		}
	}
	@discardableResult
	 open func type_annotation() throws -> Type_annotationContext {
		var _localctx: Type_annotationContext
		_localctx = Type_annotationContext(_ctx, getState())
		try enterRule(_localctx, 62, BazaarParser.RULE_type_annotation)
		var _la: Int = 0
		defer {
	    		try! exitRule()
	    }
		do {
		 	setState(283)
		 	try _errHandler.sync(self)
		 	switch(try getInterpreter().adaptivePredict(_input,30, _ctx)) {
		 	case 1:
		 		try enterOuterAlt(_localctx, 1)
		 		setState(275)
		 		try type_identifier()
		 		setState(277)
		 		try _errHandler.sync(self)
		 		_la = try _input.LA(1)
		 		if (_la == BazaarParser.Tokens.QUESTION.rawValue) {
		 			setState(276)
		 			try match(BazaarParser.Tokens.QUESTION.rawValue)

		 		}


		 		break
		 	case 2:
		 		try enterOuterAlt(_localctx, 2)
		 		setState(279)
		 		try type_identifier()
		 		setState(280)
		 		try match(BazaarParser.Tokens.LBRACKET.rawValue)
		 		setState(281)
		 		try match(BazaarParser.Tokens.RBRACKET.rawValue)

		 		break
		 	default: break
		 	}
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
			open
			func STRING() -> TerminalNode? {
				return getToken(BazaarParser.Tokens.STRING.rawValue, 0)
			}
			open
			func BOOL() -> TerminalNode? {
				return getToken(BazaarParser.Tokens.BOOL.rawValue, 0)
			}
			open
			func INT() -> TerminalNode? {
				return getToken(BazaarParser.Tokens.INT.rawValue, 0)
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
		try enterRule(_localctx, 64, BazaarParser.RULE_type_identifier)
		defer {
	    		try! exitRule()
	    }
		do {
		 	setState(289)
		 	try _errHandler.sync(self)
		 	switch (BazaarParser.Tokens(rawValue: try _input.LA(1))!) {
		 	case .IDENTIFIER:
		 		try enterOuterAlt(_localctx, 1)
		 		setState(285)
		 		try identifier()

		 		break

		 	case .STRING:
		 		try enterOuterAlt(_localctx, 2)
		 		setState(286)
		 		try match(BazaarParser.Tokens.STRING.rawValue)

		 		break

		 	case .BOOL:
		 		try enterOuterAlt(_localctx, 3)
		 		setState(287)
		 		try match(BazaarParser.Tokens.BOOL.rawValue)

		 		break

		 	case .INT:
		 		try enterOuterAlt(_localctx, 4)
		 		setState(288)
		 		try match(BazaarParser.Tokens.INT.rawValue)

		 		break
		 	default:
		 		throw ANTLRException.recognition(e: NoViableAltException(self))
		 	}
		}
		catch ANTLRException.recognition(let re) {
			_localctx.exception = re
			_errHandler.reportError(self, re)
			try _errHandler.recover(self, re)
		}

		return _localctx
	}

	public class String_literalContext: ParserRuleContext {
			open
			func STRING_LITERAL() -> TerminalNode? {
				return getToken(BazaarParser.Tokens.STRING_LITERAL.rawValue, 0)
			}
		override open
		func getRuleIndex() -> Int {
			return BazaarParser.RULE_string_literal
		}
		override open
		func accept<T>(_ visitor: ParseTreeVisitor<T>) -> T? {
			if let visitor = visitor as? BazaarParserVisitor {
			    return visitor.visitString_literal(self)
			}
			else if let visitor = visitor as? BazaarParserBaseVisitor {
			    return visitor.visitString_literal(self)
			}
			else {
			     return visitor.visitChildren(self)
			}
		}
	}
	@discardableResult
	 open func string_literal() throws -> String_literalContext {
		var _localctx: String_literalContext
		_localctx = String_literalContext(_ctx, getState())
		try enterRule(_localctx, 66, BazaarParser.RULE_string_literal)
		defer {
	    		try! exitRule()
	    }
		do {
		 	try enterOuterAlt(_localctx, 1)
		 	setState(291)
		 	try match(BazaarParser.Tokens.STRING_LITERAL.rawValue)

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
		try enterRule(_localctx, 68, BazaarParser.RULE_identifier)
		defer {
	    		try! exitRule()
	    }
		do {
		 	try enterOuterAlt(_localctx, 1)
		 	setState(293)
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
		4,1,24,296,2,0,7,0,2,1,7,1,2,2,7,2,2,3,7,3,2,4,7,4,2,5,7,5,2,6,7,6,2,7,
		7,7,2,8,7,8,2,9,7,9,2,10,7,10,2,11,7,11,2,12,7,12,2,13,7,13,2,14,7,14,
		2,15,7,15,2,16,7,16,2,17,7,17,2,18,7,18,2,19,7,19,2,20,7,20,2,21,7,21,
		2,22,7,22,2,23,7,23,2,24,7,24,2,25,7,25,2,26,7,26,2,27,7,27,2,28,7,28,
		2,29,7,29,2,30,7,30,2,31,7,31,2,32,7,32,2,33,7,33,2,34,7,34,1,0,5,0,72,
		8,0,10,0,12,0,75,9,0,1,0,1,0,1,1,1,1,1,1,1,1,1,1,1,1,3,1,85,8,1,1,2,1,
		2,1,2,1,3,1,3,1,3,1,3,3,3,94,8,3,1,3,1,3,1,4,1,4,5,4,100,8,4,10,4,12,4,
		103,9,4,1,5,1,5,1,5,1,6,1,6,1,6,1,6,3,6,112,8,6,1,6,1,6,1,7,1,7,5,7,118,
		8,7,10,7,12,7,121,9,7,1,8,1,8,1,9,1,9,1,9,1,9,3,9,129,8,9,1,9,1,9,1,10,
		1,10,1,10,1,10,3,10,137,8,10,1,10,3,10,140,8,10,1,11,1,11,3,11,144,8,11,
		1,11,1,11,1,12,1,12,5,12,150,8,12,10,12,12,12,153,9,12,1,13,1,13,1,14,
		1,14,1,15,1,15,1,15,1,15,3,15,163,8,15,1,15,1,15,1,16,1,16,1,16,1,16,1,
		17,1,17,1,18,1,18,1,18,3,18,176,8,18,1,18,3,18,179,8,18,1,18,3,18,182,
		8,18,1,19,1,19,3,19,186,8,19,1,19,1,19,5,19,190,8,19,10,19,12,19,193,9,
		19,1,20,1,20,5,20,197,8,20,10,20,12,20,200,9,20,1,21,1,21,3,21,204,8,21,
		1,22,1,22,1,22,1,22,1,22,1,22,3,22,212,8,22,1,22,1,22,1,23,1,23,1,23,3,
		23,219,8,23,1,23,1,23,1,24,1,24,3,24,225,8,24,1,24,1,24,1,25,1,25,1,25,
		5,25,232,8,25,10,25,12,25,235,9,25,1,26,1,26,1,26,1,27,1,27,1,27,5,27,
		243,8,27,10,27,12,27,246,9,27,1,28,1,28,1,28,3,28,251,8,28,1,28,1,28,1,
		29,1,29,1,29,5,29,258,8,29,10,29,12,29,261,9,29,1,30,1,30,1,30,3,30,266,
		8,30,1,30,3,30,269,8,30,1,30,1,30,1,30,3,30,274,8,30,1,31,1,31,3,31,278,
		8,31,1,31,1,31,1,31,1,31,3,31,284,8,31,1,32,1,32,1,32,1,32,3,32,290,8,
		32,1,33,1,33,1,34,1,34,1,34,0,0,35,0,2,4,6,8,10,12,14,16,18,20,22,24,26,
		28,30,32,34,36,38,40,42,44,46,48,50,52,54,56,58,60,62,64,66,68,0,0,299,
		0,73,1,0,0,0,2,84,1,0,0,0,4,86,1,0,0,0,6,89,1,0,0,0,8,97,1,0,0,0,10,104,
		1,0,0,0,12,107,1,0,0,0,14,115,1,0,0,0,16,122,1,0,0,0,18,124,1,0,0,0,20,
		132,1,0,0,0,22,141,1,0,0,0,24,147,1,0,0,0,26,154,1,0,0,0,28,156,1,0,0,
		0,30,158,1,0,0,0,32,166,1,0,0,0,34,170,1,0,0,0,36,172,1,0,0,0,38,183,1,
		0,0,0,40,194,1,0,0,0,42,203,1,0,0,0,44,205,1,0,0,0,46,215,1,0,0,0,48,222,
		1,0,0,0,50,228,1,0,0,0,52,236,1,0,0,0,54,239,1,0,0,0,56,250,1,0,0,0,58,
		254,1,0,0,0,60,273,1,0,0,0,62,283,1,0,0,0,64,289,1,0,0,0,66,291,1,0,0,
		0,68,293,1,0,0,0,70,72,3,2,1,0,71,70,1,0,0,0,72,75,1,0,0,0,73,71,1,0,0,
		0,73,74,1,0,0,0,74,76,1,0,0,0,75,73,1,0,0,0,76,77,5,0,0,1,77,1,1,0,0,0,
		78,85,3,4,2,0,79,85,3,6,3,0,80,85,3,12,6,0,81,85,3,18,9,0,82,85,3,20,10,
		0,83,85,3,30,15,0,84,78,1,0,0,0,84,79,1,0,0,0,84,80,1,0,0,0,84,81,1,0,
		0,0,84,82,1,0,0,0,84,83,1,0,0,0,85,3,1,0,0,0,86,87,5,6,0,0,87,88,3,68,
		34,0,88,5,1,0,0,0,89,90,5,2,0,0,90,91,3,68,34,0,91,93,5,12,0,0,92,94,3,
		8,4,0,93,92,1,0,0,0,93,94,1,0,0,0,94,95,1,0,0,0,95,96,5,13,0,0,96,7,1,
		0,0,0,97,101,3,10,5,0,98,100,3,10,5,0,99,98,1,0,0,0,100,103,1,0,0,0,101,
		99,1,0,0,0,101,102,1,0,0,0,102,9,1,0,0,0,103,101,1,0,0,0,104,105,3,68,
		34,0,105,106,3,62,31,0,106,11,1,0,0,0,107,108,5,3,0,0,108,109,3,68,34,
		0,109,111,5,12,0,0,110,112,3,14,7,0,111,110,1,0,0,0,111,112,1,0,0,0,112,
		113,1,0,0,0,113,114,5,13,0,0,114,13,1,0,0,0,115,119,3,16,8,0,116,118,3,
		16,8,0,117,116,1,0,0,0,118,121,1,0,0,0,119,117,1,0,0,0,119,120,1,0,0,0,
		120,15,1,0,0,0,121,119,1,0,0,0,122,123,3,68,34,0,123,17,1,0,0,0,124,125,
		5,1,0,0,125,126,3,68,34,0,126,128,5,12,0,0,127,129,3,8,4,0,128,127,1,0,
		0,0,128,129,1,0,0,0,129,130,1,0,0,0,130,131,5,13,0,0,131,19,1,0,0,0,132,
		133,5,4,0,0,133,134,3,68,34,0,134,136,3,48,24,0,135,137,3,28,14,0,136,
		135,1,0,0,0,136,137,1,0,0,0,137,139,1,0,0,0,138,140,3,22,11,0,139,138,
		1,0,0,0,139,140,1,0,0,0,140,21,1,0,0,0,141,143,5,12,0,0,142,144,3,24,12,
		0,143,142,1,0,0,0,143,144,1,0,0,0,144,145,1,0,0,0,145,146,5,13,0,0,146,
		23,1,0,0,0,147,151,3,26,13,0,148,150,3,26,13,0,149,148,1,0,0,0,150,153,
		1,0,0,0,151,149,1,0,0,0,151,152,1,0,0,0,152,25,1,0,0,0,153,151,1,0,0,0,
		154,155,3,58,29,0,155,27,1,0,0,0,156,157,3,62,31,0,157,29,1,0,0,0,158,
		159,5,5,0,0,159,160,3,68,34,0,160,162,3,48,24,0,161,163,3,28,14,0,162,
		161,1,0,0,0,162,163,1,0,0,0,163,164,1,0,0,0,164,165,3,32,16,0,165,31,1,
		0,0,0,166,167,5,12,0,0,167,168,3,34,17,0,168,169,5,13,0,0,169,33,1,0,0,
		0,170,171,3,36,18,0,171,35,1,0,0,0,172,178,3,68,34,0,173,175,5,14,0,0,
		174,176,3,54,27,0,175,174,1,0,0,0,175,176,1,0,0,0,176,177,1,0,0,0,177,
		179,5,15,0,0,178,173,1,0,0,0,178,179,1,0,0,0,179,181,1,0,0,0,180,182,3,
		38,19,0,181,180,1,0,0,0,181,182,1,0,0,0,182,37,1,0,0,0,183,185,5,12,0,
		0,184,186,3,40,20,0,185,184,1,0,0,0,185,186,1,0,0,0,186,187,1,0,0,0,187,
		191,5,13,0,0,188,190,3,46,23,0,189,188,1,0,0,0,190,193,1,0,0,0,191,189,
		1,0,0,0,191,192,1,0,0,0,192,39,1,0,0,0,193,191,1,0,0,0,194,198,3,42,21,
		0,195,197,3,42,21,0,196,195,1,0,0,0,197,200,1,0,0,0,198,196,1,0,0,0,198,
		199,1,0,0,0,199,41,1,0,0,0,200,198,1,0,0,0,201,204,3,36,18,0,202,204,3,
		44,22,0,203,201,1,0,0,0,203,202,1,0,0,0,204,43,1,0,0,0,205,206,5,7,0,0,
		206,207,5,14,0,0,207,208,3,58,29,0,208,209,5,15,0,0,209,211,5,12,0,0,210,
		212,3,40,20,0,211,210,1,0,0,0,211,212,1,0,0,0,212,213,1,0,0,0,213,214,
		5,13,0,0,214,45,1,0,0,0,215,216,3,68,34,0,216,218,5,12,0,0,217,219,3,24,
		12,0,218,217,1,0,0,0,218,219,1,0,0,0,219,220,1,0,0,0,220,221,5,13,0,0,
		221,47,1,0,0,0,222,224,5,14,0,0,223,225,3,50,25,0,224,223,1,0,0,0,224,
		225,1,0,0,0,225,226,1,0,0,0,226,227,5,15,0,0,227,49,1,0,0,0,228,233,3,
		52,26,0,229,230,5,11,0,0,230,232,3,52,26,0,231,229,1,0,0,0,232,235,1,0,
		0,0,233,231,1,0,0,0,233,234,1,0,0,0,234,51,1,0,0,0,235,233,1,0,0,0,236,
		237,3,68,34,0,237,238,3,62,31,0,238,53,1,0,0,0,239,244,3,56,28,0,240,241,
		5,11,0,0,241,243,3,56,28,0,242,240,1,0,0,0,243,246,1,0,0,0,244,242,1,0,
		0,0,244,245,1,0,0,0,245,55,1,0,0,0,246,244,1,0,0,0,247,248,3,68,34,0,248,
		249,5,20,0,0,249,251,1,0,0,0,250,247,1,0,0,0,250,251,1,0,0,0,251,252,1,
		0,0,0,252,253,3,58,29,0,253,57,1,0,0,0,254,259,3,60,30,0,255,256,5,19,
		0,0,256,258,3,68,34,0,257,255,1,0,0,0,258,261,1,0,0,0,259,257,1,0,0,0,
		259,260,1,0,0,0,260,59,1,0,0,0,261,259,1,0,0,0,262,268,3,68,34,0,263,265,
		5,14,0,0,264,266,3,54,27,0,265,264,1,0,0,0,265,266,1,0,0,0,266,267,1,0,
		0,0,267,269,5,15,0,0,268,263,1,0,0,0,268,269,1,0,0,0,269,274,1,0,0,0,270,
		274,3,66,33,0,271,272,5,19,0,0,272,274,3,68,34,0,273,262,1,0,0,0,273,270,
		1,0,0,0,273,271,1,0,0,0,274,61,1,0,0,0,275,277,3,64,32,0,276,278,5,18,
		0,0,277,276,1,0,0,0,277,278,1,0,0,0,278,284,1,0,0,0,279,280,3,64,32,0,
		280,281,5,16,0,0,281,282,5,17,0,0,282,284,1,0,0,0,283,275,1,0,0,0,283,
		279,1,0,0,0,284,63,1,0,0,0,285,290,3,68,34,0,286,290,5,8,0,0,287,290,5,
		9,0,0,288,290,5,10,0,0,289,285,1,0,0,0,289,286,1,0,0,0,289,287,1,0,0,0,
		289,288,1,0,0,0,290,65,1,0,0,0,291,292,5,22,0,0,292,67,1,0,0,0,293,294,
		5,23,0,0,294,69,1,0,0,0,32,73,84,93,101,111,119,128,136,139,143,151,162,
		175,178,181,185,191,198,203,211,218,224,233,244,250,259,265,268,273,277,
		283,289
	]

	public
	static let _ATN = try! ATNDeserializer().deserialize(_serializedATN)
}