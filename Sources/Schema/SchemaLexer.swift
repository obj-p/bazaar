// Generated from SchemaLexer.g4 by ANTLR 4.13.1
@preconcurrency import Antlr4

open class SchemaLexer: Lexer {
    static let _decisionToDFA: [DFA] = {
        var decisionToDFA = [DFA]()
        let length = SchemaLexer._ATN.getNumberOfDecisions()
        for i in 0 ..< length {
            decisionToDFA.append(DFA(SchemaLexer._ATN.getDecisionState(i)!, i))
        }
        return decisionToDFA
    }()

    static let _sharedContextCache = PredictionContextCache()

    public
    static let DATA = 1, EFFECT = 2, VIEW = 3, LPARANS = 4, RPARANS = 5, QUESTION = 6,
               IDENTIFIER = 7, WS = 8

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
        "DATA", "EFFECT", "VIEW", "LPARANS", "RPARANS", "QUESTION", "IDENTIFIER",
        "WS"
    ]

    private static let _LITERAL_NAMES: [String?] = [
        nil, "'data'", "'effect'", "'view'", "'('", "')'", "'?'"
    ]
    private static let _SYMBOLIC_NAMES: [String?] = [
        nil, "DATA", "EFFECT", "VIEW", "LPARANS", "RPARANS", "QUESTION", "IDENTIFIER",
        "WS"
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

    static let _serializedATN: [Int] = [
        4, 0, 8, 54, 6, -1, 2, 0, 7, 0, 2, 1, 7, 1, 2, 2, 7, 2, 2, 3, 7, 3, 2, 4, 7, 4, 2, 5, 7, 5, 2, 6, 7, 6,
        2, 7, 7, 7, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 2, 1, 2, 1,
        2, 1, 2, 1, 3, 1, 3, 1, 4, 1, 4, 1, 5, 1, 5, 1, 6, 1, 6, 5, 6, 43, 8, 6, 10, 6, 12, 6, 46, 9, 6, 1, 7,
        4, 7, 49, 8, 7, 11, 7, 12, 7, 50, 1, 7, 1, 7, 0, 0, 8, 1, 1, 3, 2, 5, 3, 7, 4, 9, 5, 11, 6, 13, 7, 15,
        8, 1, 0, 3, 3, 0, 65, 90, 95, 95, 97, 122, 4, 0, 48, 57, 65, 90, 95, 95, 97, 122, 3, 0, 9, 10, 13,
        13, 32, 32, 55, 0, 1, 1, 0, 0, 0, 0, 3, 1, 0, 0, 0, 0, 5, 1, 0, 0, 0, 0, 7, 1, 0, 0, 0, 0, 9, 1, 0, 0,
        0, 0, 11, 1, 0, 0, 0, 0, 13, 1, 0, 0, 0, 0, 15, 1, 0, 0, 0, 1, 17, 1, 0, 0, 0, 3, 22, 1, 0, 0, 0, 5, 29,
        1, 0, 0, 0, 7, 34, 1, 0, 0, 0, 9, 36, 1, 0, 0, 0, 11, 38, 1, 0, 0, 0, 13, 40, 1, 0, 0, 0, 15, 48, 1,
        0, 0, 0, 17, 18, 5, 100, 0, 0, 18, 19, 5, 97, 0, 0, 19, 20, 5, 116, 0, 0, 20, 21, 5, 97, 0, 0, 21,
        2, 1, 0, 0, 0, 22, 23, 5, 101, 0, 0, 23, 24, 5, 102, 0, 0, 24, 25, 5, 102, 0, 0, 25, 26, 5, 101,
        0, 0, 26, 27, 5, 99, 0, 0, 27, 28, 5, 116, 0, 0, 28, 4, 1, 0, 0, 0, 29, 30, 5, 118, 0, 0, 30, 31,
        5, 105, 0, 0, 31, 32, 5, 101, 0, 0, 32, 33, 5, 119, 0, 0, 33, 6, 1, 0, 0, 0, 34, 35, 5, 40, 0, 0,
        35, 8, 1, 0, 0, 0, 36, 37, 5, 41, 0, 0, 37, 10, 1, 0, 0, 0, 38, 39, 5, 63, 0, 0, 39, 12, 1, 0, 0, 0,
        40, 44, 7, 0, 0, 0, 41, 43, 7, 1, 0, 0, 42, 41, 1, 0, 0, 0, 43, 46, 1, 0, 0, 0, 44, 42, 1, 0, 0, 0,
        44, 45, 1, 0, 0, 0, 45, 14, 1, 0, 0, 0, 46, 44, 1, 0, 0, 0, 47, 49, 7, 2, 0, 0, 48, 47, 1, 0, 0, 0,
        49, 50, 1, 0, 0, 0, 50, 48, 1, 0, 0, 0, 50, 51, 1, 0, 0, 0, 51, 52, 1, 0, 0, 0, 52, 53, 6, 7, 0, 0,
        53, 16, 1, 0, 0, 0, 3, 0, 44, 50, 1, 6, 0, 0
    ]

    public
    static let _ATN: ATN = try! ATNDeserializer().deserialize(_serializedATN)
}
