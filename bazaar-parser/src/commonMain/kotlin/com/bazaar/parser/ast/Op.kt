package com.bazaar.parser.ast

enum class BinaryOp {
    ADD,
    SUB,
    MUL,
    DIV,
    MOD,
    POW,
    EQ,
    NEQ,
    LT,
    LTE,
    GT,
    GTE,
    AND,
    OR,
    COALESCE,
}

enum class UnaryOp { NOT, NEGATE }

enum class AssignOp {
    ASSIGN,
    ADD_ASSIGN,
    SUB_ASSIGN,
    MUL_ASSIGN,
    DIV_ASSIGN,
    MOD_ASSIGN,
}
