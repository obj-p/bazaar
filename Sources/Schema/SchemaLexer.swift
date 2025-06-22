// Generated from SchemaLexer.g4 by ANTLR 4.13.1
@preconcurrency import Antlr4

open class SchemaLexer: Lexer {

	internal static let _decisionToDFA: [DFA] = {
          var decisionToDFA = [DFA]()
          let length = SchemaLexer._ATN.getNumberOfDecisions()
          for i in 0..<length {
          	    decisionToDFA.append(DFA(SchemaLexer._ATN.getDecisionState(i)!, i))
          }
           return decisionToDFA
     }()

	internal static let _sharedContextCache = PredictionContextCache()

	public
	static let DATA=1, VIEW=2, LPARANS=3, RPARANS=4, QUESTION=5, IDENTIFIER=6, 
            WS=7

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
		"DATA", "VIEW", "LPARANS", "RPARANS", "QUESTION", "IDENTIFIER", "WS"
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
	func getVocabulary() -> Vocabulary {
		return SchemaLexer.VOCABULARY
	}

	public
	required init(_ input: CharStream) {
	    RuntimeMetaData.checkVersion("4.13.1", RuntimeMetaData.VERSION)
		super.init(input)
		_interp = LexerATNSimulator(self, SchemaLexer._ATN, SchemaLexer._decisionToDFA, SchemaLexer._sharedContextCache)
	}

	override open
	func getGrammarFileName() -> String { return "SchemaLexer.g4" }

	override open
	func getRuleNames() -> [String] { return SchemaLexer.ruleNames }

	override open
	func getSerializedATN() -> [Int] { return SchemaLexer._serializedATN }

	override open
	func getChannelNames() -> [String] { return SchemaLexer.channelNames }

	override open
	func getModeNames() -> [String] { return SchemaLexer.modeNames }

	override open
	func getATN() -> ATN { return SchemaLexer._ATN }

	static let _serializedATN:[Int] = [
		4,0,7,50,6,-1,2,0,7,0,2,1,7,1,2,2,7,2,2,3,7,3,2,4,7,4,2,5,7,5,2,6,7,6,
		1,0,1,0,1,0,1,0,1,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,2,1,2,1,
		3,1,3,1,4,1,4,1,5,1,5,5,5,39,8,5,10,5,12,5,42,9,5,1,6,4,6,45,8,6,11,6,
		12,6,46,1,6,1,6,0,0,7,1,1,3,2,5,3,7,4,9,5,11,6,13,7,1,0,3,3,0,65,90,95,
		95,97,122,4,0,48,57,65,90,95,95,97,122,3,0,9,10,13,13,32,32,51,0,1,1,0,
		0,0,0,3,1,0,0,0,0,5,1,0,0,0,0,7,1,0,0,0,0,9,1,0,0,0,0,11,1,0,0,0,0,13,
		1,0,0,0,1,15,1,0,0,0,3,20,1,0,0,0,5,30,1,0,0,0,7,32,1,0,0,0,9,34,1,0,0,
		0,11,36,1,0,0,0,13,44,1,0,0,0,15,16,5,100,0,0,16,17,5,97,0,0,17,18,5,116,
		0,0,18,19,5,97,0,0,19,2,1,0,0,0,20,21,5,99,0,0,21,22,5,111,0,0,22,23,5,
		109,0,0,23,24,5,112,0,0,24,25,5,111,0,0,25,26,5,110,0,0,26,27,5,101,0,
		0,27,28,5,110,0,0,28,29,5,116,0,0,29,4,1,0,0,0,30,31,5,40,0,0,31,6,1,0,
		0,0,32,33,5,41,0,0,33,8,1,0,0,0,34,35,5,63,0,0,35,10,1,0,0,0,36,40,7,0,
		0,0,37,39,7,1,0,0,38,37,1,0,0,0,39,42,1,0,0,0,40,38,1,0,0,0,40,41,1,0,
		0,0,41,12,1,0,0,0,42,40,1,0,0,0,43,45,7,2,0,0,44,43,1,0,0,0,45,46,1,0,
		0,0,46,44,1,0,0,0,46,47,1,0,0,0,47,48,1,0,0,0,48,49,6,6,0,0,49,14,1,0,
		0,0,3,0,40,46,1,6,0,0
	]

	public
	static let _ATN: ATN = try! ATNDeserializer().deserialize(_serializedATN)
}