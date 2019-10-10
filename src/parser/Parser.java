package parser;

import ast.*;

import lexer.Token;
import lexer.Tokeniser;
import lexer.Token.TokenClass;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


/**
 * @author cdubach
 */
public class Parser {

    private Token token;

    // use for backtracking (useful for distinguishing decls from procs when parsing a program for instance)
    private Queue<Token> buffer = new LinkedList<>();

    private final Tokeniser tokeniser;



    public Parser(Tokeniser tokeniser) {
        this.tokeniser = tokeniser;
    }

    public Program parse() {
        // get the first token
        nextToken();

        return parseProgram();
    }

    public int getErrorCount() {
        return error;
    }

    private int error = 0;
    private Token lastErrorToken;

    private void error(TokenClass... expected) {

        if (lastErrorToken == token) {
            // skip this error, same token causing trouble
            return;
        }

        StringBuilder sb = new StringBuilder();
        String sep = "";
        for (TokenClass e : expected) {
            sb.append(sep);
            sb.append(e);
            sep = "|";
        }
        System.out.println("Parsing error: expected ("+sb+") found ("+token+") at "+token.position);

        error++;
        lastErrorToken = token;
    }

    /*
     * Look ahead the i^th element from the stream of token.
     * i should be >= 1
     */
    private Token lookAhead(int i) {
        // ensures the buffer has the element we want to look ahead
        while (buffer.size() < i)
            buffer.add(tokeniser.nextToken());
        assert buffer.size() >= i;

        int cnt=1;
        for (Token t : buffer) {
            if (cnt == i)
                return t;
            cnt++;
        }

        assert false; // should never reach this
        return null;
    }


    /*
     * Consumes the next token from the tokeniser or the buffer if not empty.
     */
    private void nextToken() {
        if (!buffer.isEmpty())
            token = buffer.remove();
        else
            token = tokeniser.nextToken();
    }

    /*
     * If the current token is equals to the expected one, then skip it, otherwise report an error.
     * Returns the expected token or null if an error occurred.
     */
    private Token expect(TokenClass... expected) {
        for (TokenClass e : expected) {
            if (e == token.tokenClass) {
                Token cur = token;
                nextToken();
                return cur;
            }
        }

        error(expected);
        return null;
    }

    /*
    * Returns true if the current token is equals to any of the expected ones.
    */
    private boolean accept(TokenClass... expected) {
        boolean result = false;
        for (TokenClass e : expected)
            result |= (e == token.tokenClass);
        return result;
    }


    private Program parseProgram() {
        parseIncludes();
        List<StructTypeDecl> stds = parseStructDecls();
        List<VarDecl> vds = parseVarDecls();
        List<FunDecl> fds = parseFunDecls();
        expect(TokenClass.EOF);
        return new Program(stds, vds, fds);
    }

    // includes are ignored, so does not need to return an AST node
    private void parseIncludes() {
        if (accept(TokenClass.INCLUDE)) {
            nextToken();
            expect(TokenClass.STRING_LITERAL);
            parseIncludes();
        }
    }

    private List<StructTypeDecl> parseStructDecls() {
        if (accept(TokenClass.STRUCT)) {
            parseStructType();
            expect(TokenClass.LBRA);

            // vardecl positive closure
            // must have one, then call kleene closure
            parseType();
            expect(TokenClass.IDENTIFIER);
            parseVarDeclRest();
            parseVarDecls();

            expect(TokenClass.RBRA);
            expect(TokenClass.SC);
            parseStructDecls();
        }

        // default
        return null;
    }

    private List<VarDecl> parseVarDecls() {
        if (accept(TokenClass.INT, TokenClass.CHAR, TokenClass.VOID, TokenClass.STRUCT)
                && (lookAhead(2).tokenClass == TokenClass.SC || lookAhead(2).tokenClass == TokenClass.LSBR
                || lookAhead(3).tokenClass == TokenClass.SC || lookAhead(3).tokenClass == TokenClass.LSBR
                || lookAhead(4).tokenClass == TokenClass.SC || lookAhead(4).tokenClass == TokenClass.LSBR)) {
            parseType();
            expect(TokenClass.IDENTIFIER);
            parseVarDeclRest();
            parseVarDecls();
        }

        // default
        return null;
    }

    private void parseVarDeclRest() {
        if (accept(TokenClass.SC)) {
            nextToken();
        } else {
            expect(TokenClass.LSBR);
            expect(TokenClass.INT_LITERAL);
            expect(TokenClass.RSBR);
            expect(TokenClass.SC);
        }
    }

    private List<FunDecl> parseFunDecls() {
        if (accept(TokenClass.INT, TokenClass.CHAR, TokenClass.VOID, TokenClass.STRUCT)
                && (lookAhead(2).tokenClass == TokenClass.LPAR ||
                    lookAhead(3).tokenClass == TokenClass.LPAR ||
                    lookAhead(4).tokenClass == TokenClass.LPAR)) {
            parseType();
            expect(TokenClass.IDENTIFIER);
            expect(TokenClass.LPAR);
            parseParams();
            expect(TokenClass.RPAR);
            parseBlock();
            parseFunDecls();
        }

        // default
        return null;
    }

    private void parseType() {
        if (accept(TokenClass.INT, TokenClass.CHAR, TokenClass.VOID)) {
            nextToken();
            parseTypeOpt();
        } else {
            parseStructType();
            parseTypeOpt();
        }
    }

    private void parseTypeOpt() {
        if (accept(TokenClass.ASTERIX)) {
            nextToken();
        }
    }

    private void parseStructType() {
        expect(TokenClass.STRUCT);
        expect(TokenClass.IDENTIFIER);
    }

    private void parseParams() {
        if (accept(TokenClass.INT, TokenClass.CHAR, TokenClass.VOID, TokenClass.STRUCT)) {
            parseType();
            expect(TokenClass.IDENTIFIER);
            parseParamsRep();
        }
    }

    private void parseParamsRep() {
        if (accept(TokenClass.COMMA)) {
            nextToken();
            parseType();
            expect(TokenClass.IDENTIFIER);
            parseParamsRep();
        }
    }

    private void parseStmt() {
        if (accept(TokenClass.LBRA)) {
            parseBlock();
        } else if (accept(TokenClass.WHILE)) {
            nextToken();
            expect(TokenClass.LPAR);
            parseExp();
            expect(TokenClass.RPAR);
            parseStmt();
        } else if (accept(TokenClass.IF)) {
            nextToken();
            expect(TokenClass.LPAR);
            parseExp();
            expect(TokenClass.RPAR);
            parseStmt();
            parseElseOpt();
        } else if (accept(TokenClass.RETURN)) {
            nextToken();
            parseExpOpt();
            expect(TokenClass.SC);
        } else {
            parseExp();
            parseStmtRest();
        }
    }

    private void parseStmtRest() {
        if (accept(TokenClass.SC)) {
            nextToken();
        } else {
            expect(TokenClass.ASSIGN);
            parseExp();
            expect(TokenClass.SC);
        }
    }

    private void parseElseOpt() {
        if (accept(TokenClass.ELSE)) {
            nextToken();
            parseStmt();
        }
    }

    private void parseExpOpt() {
        if (accept(TokenClass.MINUS, TokenClass.SIZEOF, TokenClass.ASTERIX, TokenClass.LPAR,
                TokenClass.IDENTIFIER, TokenClass.INT_LITERAL, TokenClass.CHAR_LITERAL, TokenClass.STRING_LITERAL)) {
            parseExp();
        }
    }

    private void parseBlock() {
        expect(TokenClass.LBRA);
        parseVarDecls();
        parseStmtRep();
        expect(TokenClass.RBRA);
    }

    private void parseStmtRep() {
        if (accept(TokenClass.LBRA, TokenClass.WHILE, TokenClass.IF, TokenClass.RETURN,
                TokenClass.MINUS, TokenClass.SIZEOF, TokenClass.ASTERIX, TokenClass.LPAR,
                TokenClass.IDENTIFIER, TokenClass.INT_LITERAL, TokenClass.CHAR_LITERAL, TokenClass.STRING_LITERAL)) {
            parseStmt();
            parseStmtRep();
        }
    }

    private Expr parseExp() {
        Expr lhs = parseExpA();
        return parseOpsA(lhs);
    }

    private Expr parseOpsA(Expr lhs) {
        if (accept(TokenClass.OR)) {
            Op op = Op.OR;
            nextToken();
            Expr rhs = parseExpA();
            parseOpsA(new BinOp(op, lhs, rhs));
        }

        return lhs;
    }

    private Expr parseExpA() {
        Expr lhs = parseExpB();
        return parseOpsB(lhs);
    }

    private Expr parseOpsB(Expr lhs) {
        if (accept(TokenClass.AND)) {
            Op op = Op.AND;
            nextToken();
            Expr rhs = parseExpB();
            parseOpsB(new BinOp(op, lhs, rhs));
        }

        return lhs;
    }

    private Expr parseExpB() {
        Expr lhs = parseExpC();
        return parseOpsC(lhs);
    }

    private Expr parseOpsC(Expr lhs) {
        if (accept(TokenClass.EQ, TokenClass.NE)) {
            Op op;
            switch (token.tokenClass) {
                case EQ: op = Op.EQ;
                default: op = Op.NE;
            }
            nextToken();

            Expr rhs = parseExpC();
            parseOpsC(new BinOp(op, lhs, rhs));
        }

        return lhs;
    }

    private Expr parseExpC() {
        Expr lhs = parseExpD();
        return parseOpsD(lhs);
    }

    private Expr parseOpsD(Expr lhs) {
        if (accept(TokenClass.LT, TokenClass.LE, TokenClass.GT, TokenClass.GE)) {
            Op op;
            switch(token.tokenClass) {
                case LT: op = Op.LT;
                case LE: op = Op.LE;
                case GT: op = Op.GT;
                default: op = Op.GE;
            }
            nextToken();

            Expr rhs = parseExpD();
            parseOpsD(new BinOp(op, lhs, rhs));
        }

        return lhs;
    }

    private Expr parseExpD() {
        Expr lhs = parseExpE();
        return parseOpsE(lhs);
    }

    private Expr parseOpsE(Expr lhs) {
        if (accept(TokenClass.PLUS, TokenClass.MINUS)) {
            Op op;
            switch (token.tokenClass) {
                case PLUS: op = Op.ADD;
                default: op = Op.SUB;
            }
            nextToken();

            Expr rhs = parseExpE();
            parseOpsE(new BinOp(op, lhs, rhs));
        }

        return lhs;
    }

    private Expr parseExpE() {
        Expr lhs = parseExpF();
        return parseOpsF(lhs);
    }

    private Expr parseOpsF(Expr lhs) {
        if (accept(TokenClass.ASTERIX, TokenClass.DIV, TokenClass.REM)) {
            Op op;
            switch (token.tokenClass) {
                case ASTERIX: op = Op.MUL;
                case DIV: op = Op.DIV;
                default: op = Op.MOD;
            }
            nextToken();

            Expr rhs = parseExpF();
            parseOpsF(new BinOp(op, lhs, rhs));
        }

        return lhs;
    }

    // how is -exp represented?
    private Expr parseExpF() {
        if (accept(TokenClass.MINUS)) {
            nextToken();
            parseExpF();
        } else if (accept(TokenClass.SIZEOF)) {
            parseSizeOf();
        } else if (accept(TokenClass.ASTERIX)) {
            parseValueAt();
        } else if (accept(TokenClass.LPAR)) {
            nextToken();
            parseExpOrType();
        } else {
            parseExpG();
        }

        return null;

    }

    private Expr parseExpG() {
        Expr lhs = parseExpH();
        parseOpsH();

        return null;

    }

    private void parseOpsH() {
        if (accept(TokenClass.LSBR)) {
            nextToken();
            parseExp();
            expect(TokenClass.RSBR);
        } else if (accept(TokenClass.DOT)) {
            nextToken();
            expect(TokenClass.IDENTIFIER);
        }
    }

    private Expr parseExpH() {
        if (accept(TokenClass.IDENTIFIER)) {
            nextToken();
            parseFunCallOrIdent();
        } else {
            nextToken();
        }

        return null;
    }

    private void parseExpOrType() {
        if (accept(TokenClass.INT, TokenClass.CHAR, TokenClass.VOID, TokenClass.STRUCT)) {
            parseType();
            expect(TokenClass.RPAR);
            parseExp();
        } else {
            parseExp();
            expect(TokenClass.RPAR);
        }
    }

    private void parseFunCallOrIdent() {
        if (accept(TokenClass.LPAR)) {
            nextToken();
            parseArgList();
            expect(TokenClass.RPAR);
        }
    }

    private void parseArgList() {
        if (accept(TokenClass.MINUS, TokenClass.SIZEOF, TokenClass.ASTERIX, TokenClass.LPAR,
                TokenClass.IDENTIFIER, TokenClass.INT_LITERAL, TokenClass.CHAR_LITERAL, TokenClass.STRING_LITERAL)) {
            parseExp();
            parseArgRep();
        }
    }

    private void parseArgRep() {
        if (accept(TokenClass.COMMA)) {
            nextToken();
            parseExp();
            parseArgRep();
        }
    }

    private void parseValueAt() {
        expect(TokenClass.ASTERIX);
        parseExp();
    }

    private void parseSizeOf() {
        expect(TokenClass.SIZEOF);
        expect(TokenClass.LPAR);
        parseType();
        expect(TokenClass.RPAR);
    }
}
