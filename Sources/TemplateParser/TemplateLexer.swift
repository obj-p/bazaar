// Generated from TemplateLexer.g4 by ANTLR 4.13.1
@preconcurrency import Antlr4

open class TemplateLexer: Lexer {

	internal static let _decisionToDFA: [DFA] = {
          var decisionToDFA = [DFA]()
          let length = TemplateLexer._ATN.getNumberOfDecisions()
          for i in 0..<length {
          	    decisionToDFA.append(DFA(TemplateLexer._ATN.getDecisionState(i)!, i))
          }
           return decisionToDFA
     }()

	internal static let _sharedContextCache = PredictionContextCache()

	public
	static let VIEW=1, COMMA=2, LCURLY=3, RCURLY=4, LPAREN=5, RPAREN=6, QUESTION=7, 
            IDENTIFIER=8, WS=9

	public
	static let channelNames: [String] = [
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	]

	public
	static let modeNames: [String] = [
		"DEFAULT_MODE"
	]

	public
	static let ruleNames: [String] = [
		"VIEW", "COMMA", "LCURLY", "RCURLY", "LPAREN", "RPAREN", "QUESTION", "IDENTIFIER", 
		"WS"
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
	func getVocabulary() -> Vocabulary {
		return TemplateLexer.VOCABULARY
	}

	public
	required init(_ input: CharStream) {
	    RuntimeMetaData.checkVersion("4.13.1", RuntimeMetaData.VERSION)
		super.init(input)
		_interp = LexerATNSimulator(self, TemplateLexer._ATN, TemplateLexer._decisionToDFA, TemplateLexer._sharedContextCache)
	}

	override open
	func getGrammarFileName() -> String { return "TemplateLexer.g4" }

	override open
	func getRuleNames() -> [String] { return TemplateLexer.ruleNames }

	override open
	func getSerializedATN() -> [Int] { return TemplateLexer._serializedATN }

	override open
	func getChannelNames() -> [String] { return TemplateLexer.channelNames }

	override open
	func getModeNames() -> [String] { return TemplateLexer.modeNames }

	override open
	func getATN() -> ATN { return TemplateLexer._ATN }

	static let _serializedATN:[Int] = [
		4,0,9,50,6,-1,2,0,7,0,2,1,7,1,2,2,7,2,2,3,7,3,2,4,7,4,2,5,7,5,2,6,7,6,
		2,7,7,7,2,8,7,8,1,0,1,0,1,0,1,0,1,0,1,1,1,1,1,2,1,2,1,3,1,3,1,4,1,4,1,
		5,1,5,1,6,1,6,1,7,1,7,5,7,39,8,7,10,7,12,7,42,9,7,1,8,4,8,45,8,8,11,8,
		12,8,46,1,8,1,8,0,0,9,1,1,3,2,5,3,7,4,9,5,11,6,13,7,15,8,17,9,1,0,3,3,
		0,65,90,95,95,97,122,4,0,48,57,65,90,95,95,97,122,3,0,9,10,13,13,32,32,
		51,0,1,1,0,0,0,0,3,1,0,0,0,0,5,1,0,0,0,0,7,1,0,0,0,0,9,1,0,0,0,0,11,1,
		0,0,0,0,13,1,0,0,0,0,15,1,0,0,0,0,17,1,0,0,0,1,19,1,0,0,0,3,24,1,0,0,0,
		5,26,1,0,0,0,7,28,1,0,0,0,9,30,1,0,0,0,11,32,1,0,0,0,13,34,1,0,0,0,15,
		36,1,0,0,0,17,44,1,0,0,0,19,20,5,118,0,0,20,21,5,105,0,0,21,22,5,101,0,
		0,22,23,5,119,0,0,23,2,1,0,0,0,24,25,5,44,0,0,25,4,1,0,0,0,26,27,5,123,
		0,0,27,6,1,0,0,0,28,29,5,125,0,0,29,8,1,0,0,0,30,31,5,40,0,0,31,10,1,0,
		0,0,32,33,5,41,0,0,33,12,1,0,0,0,34,35,5,63,0,0,35,14,1,0,0,0,36,40,7,
		0,0,0,37,39,7,1,0,0,38,37,1,0,0,0,39,42,1,0,0,0,40,38,1,0,0,0,40,41,1,
		0,0,0,41,16,1,0,0,0,42,40,1,0,0,0,43,45,7,2,0,0,44,43,1,0,0,0,45,46,1,
		0,0,0,46,44,1,0,0,0,46,47,1,0,0,0,47,48,1,0,0,0,48,49,6,8,0,0,49,18,1,
		0,0,0,3,0,40,46,1,6,0,0
	]

	public
	static let _ATN: ATN = try! ATNDeserializer().deserialize(_serializedATN)
}