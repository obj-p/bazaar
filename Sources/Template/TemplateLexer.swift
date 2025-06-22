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
	static let VIEW=1, LPARANS=2, RPARANS=3, QUESTION=4, IDENTIFIER=5, WS=6

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
		"VIEW", "LPARANS", "RPARANS", "QUESTION", "IDENTIFIER", "WS"
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
		4,0,6,38,6,-1,2,0,7,0,2,1,7,1,2,2,7,2,2,3,7,3,2,4,7,4,2,5,7,5,1,0,1,0,
		1,0,1,0,1,0,1,1,1,1,1,2,1,2,1,3,1,3,1,4,1,4,5,4,27,8,4,10,4,12,4,30,9,
		4,1,5,4,5,33,8,5,11,5,12,5,34,1,5,1,5,0,0,6,1,1,3,2,5,3,7,4,9,5,11,6,1,
		0,3,3,0,65,90,95,95,97,122,4,0,48,57,65,90,95,95,97,122,3,0,9,10,13,13,
		32,32,39,0,1,1,0,0,0,0,3,1,0,0,0,0,5,1,0,0,0,0,7,1,0,0,0,0,9,1,0,0,0,0,
		11,1,0,0,0,1,13,1,0,0,0,3,18,1,0,0,0,5,20,1,0,0,0,7,22,1,0,0,0,9,24,1,
		0,0,0,11,32,1,0,0,0,13,14,5,118,0,0,14,15,5,105,0,0,15,16,5,101,0,0,16,
		17,5,119,0,0,17,2,1,0,0,0,18,19,5,40,0,0,19,4,1,0,0,0,20,21,5,41,0,0,21,
		6,1,0,0,0,22,23,5,63,0,0,23,8,1,0,0,0,24,28,7,0,0,0,25,27,7,1,0,0,26,25,
		1,0,0,0,27,30,1,0,0,0,28,26,1,0,0,0,28,29,1,0,0,0,29,10,1,0,0,0,30,28,
		1,0,0,0,31,33,7,2,0,0,32,31,1,0,0,0,33,34,1,0,0,0,34,32,1,0,0,0,34,35,
		1,0,0,0,35,36,1,0,0,0,36,37,6,5,0,0,37,12,1,0,0,0,3,0,28,34,1,6,0,0
	]

	public
	static let _ATN: ATN = try! ATNDeserializer().deserialize(_serializedATN)
}