package parser;

import lexer.Token;
import lexer.Tokeniser;
import lexer.Token.TokenClass;

import java.util.LinkedList;
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

    public void parse() {
        // get the first token
        nextToken();

        parseProgram();
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


    private void parseProgram() {
        parseIncludes();
        parseStructDecls();
        parseVarDecls();
        parseFunDecls();
        expect(TokenClass.EOF);
    }

    // includes are ignored, so does not need to return an AST node
    private void parseIncludes() {
        if (accept(TokenClass.INCLUDE)) {
            nextToken();
            expect(TokenClass.STRING_LITERAL);
            parseIncludes();
        }
    }

    private void parseStructDecls() {
        if (accept(TokenClass.STRUCT)) {
            parseStructType();
            expect(TokenClass.LBRA);
            parseVarDeclPosClosure();
            expect(TokenClass.RBRA);
            expect(TokenClass.SC);
            parseStructDecls();
        }
    }

    private void parseVarDecls() {
        if (accept(TokenClass.INT,TokenClass.CHAR, TokenClass.VOID, TokenClass.STRUCT)) {
            parseType();
            expect(TokenClass.IDENTIFIER);
            parseVarDeclRest();
            parseVarDecls();
        }
    }

    private void parseVarDeclPosClosure() {

    }

    private void parseVarDeclRest() {
        if (accept(TokenClass.SC)) {
            nextToken();
        } else {
            parseType();
            expect(TokenClass.IDENTIFIER);
            expect(TokenClass.LSBR);
            expect(TokenClass.INT_LITERAL);
            expect(TokenClass.RSBR);
            expect(TokenClass.SC);
        }
    }

    private void parseFunDecls() {
        if (accept(TokenClass.INT,TokenClass.CHAR, TokenClass.VOID, TokenClass.STRUCT)) {
            parseType();
            expect(TokenClass.IDENTIFIER);
            expect(TokenClass.LPAR);
            parseParams();
            expect(TokenClass.RPAR);
            parseBlock();
            parseFunDecls();
        }
    }

    private void parseType() {
        if (accept(TokenClass.INT)) {
            nextToken();
            parseTypeOpt();
        } else if (accept(TokenClass.CHAR)) {
            nextToken();
            parseTypeOpt();
        } else if (accept(TokenClass.VOID)) {
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
        if (accept(TokenClass.STRUCT)) {
            expect(TokenClass.STRUCT);
            expect(TokenClass.IDENTIFIER);
        }
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
            parseStmtOpt1();
        } else if (accept(TokenClass.RETURN)) {
            nextToken();
            parseStmtOpt2();
            expect(TokenClass.SC);
        } else {
            nextToken();
            parseStmtRest();
        }
    }

    private void parseStmtRest() {
        if (accept(TokenClass.SC)) {
            nextToken();
        } else {
            parseExp();
            expect(TokenClass.SC);
        }
    }

    private void parseStmtOpt1() {
        if (accept(TokenClass.ELSE)) {
            nextToken();
            parseStmt();
        }
    }

    private void parseStmtOpt2() {
        if (accept(TokenClass.LBRA, TokenClass.WHILE, TokenClass.IF, TokenClass.RETURN,
                TokenClass.MINUS, TokenClass.SIZEOF, TokenClass.ASTERIX,
                TokenClass.LPAR, TokenClass.LSBR, TokenClass.DOT, TokenClass.IDENTIFIER,
                TokenClass.INT_LITERAL, TokenClass.CHAR_LITERAL, TokenClass.STRING_LITERAL)) {
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
        if (accept(TokenClass.MINUS, TokenClass.SIZEOF, TokenClass.ASTERIX,
                TokenClass.LPAR, TokenClass.LSBR, TokenClass.DOT, TokenClass.IDENTIFIER,
                TokenClass.INT_LITERAL, TokenClass.CHAR_LITERAL, TokenClass.STRING_LITERAL)) {
            parseStmt();
            parseStmtRep();
        }
    }

    private void parseExp() {
        parseExpA();
        parseOpsA();
    }

    private void parseOpsA() {
        if (accept(TokenClass.OR)) {
            nextToken();
            parseExpA();
            parseOpsA();
        }
    }

    private void parseExpA() {
        parseExpB();
        parseOpsB();
    }

    private void parseOpsB() {
        if (accept(TokenClass.AND)) {
            nextToken();
            parseExpB();
            parseOpsB();
        }
    }

    private void parseExpB() {
        parseExpC();
        parseOpsC();
    }

    private void parseOpsC() {
        if (accept(TokenClass.EQ, TokenClass.NE)) {
            nextToken();
            parseExpC();
            parseOpsC();
        }
    }

    private void parseExpC() {
        parseExpD();
        parseOpsD();
    }

    private void parseOpsD() {
        if (accept(TokenClass.LT, TokenClass.LE, TokenClass.GT, TokenClass.GE)) {
            nextToken();
            parseExpD();
            parseOpsD();
        }
    }

    private void parseExpD() {
        parseExpE();
        parseOpsE();
    }

    private void parseOpsE() {
        if (accept(TokenClass.PLUS, TokenClass.MINUS)) {
            nextToken();
            parseExpE();
            parseOpsE();
        }
    }

    private void parseExpE() {
        parseExpF();
        parseOpsF();
    }

    private void parseOpsF() {
        if (accept(TokenClass.ASTERIX, TokenClass.DIV, TokenClass.REM)) {
            nextToken();
            parseExpF();
            parseOpsF();
        }
    }

    private void parseExpF() {
        if (accept(TokenClass.MINUS)) {
            nextToken();
            parseExpF();
        } else if (accept(TokenClass.SIZEOF)) {
            parseSizeOf();
        } else if (accept(TokenClass.ASTERIX)) {
            parseValueAt();
        } else if (accept(TokenClass.LPAR)) {
            parseTypeCast();
        } else {
            parseExpG();
        }
    }

    public void parseExpG() {
        if (accept(TokenClass.LSBR)) {
            nextToken();
            parseExpH();
            expect(TokenClass.RSBR);
        } else if (accept(TokenClass.DOT)) {
            nextToken();
            expect(TokenClass.IDENTIFIER);
        } else if (accept(TokenClass.IDENTIFIER)) {
            parseFunCall();
        } else {
            parseExpH();
        }
    }

    public void parseExpH() {
        if (accept(TokenClass.LPAR)) {
            nextToken();
            parseExpOrType();
        } else {
            nextToken();
        }
    }

    public void parseExpOrType() {
        if (accept(TokenClass.INT, TokenClass.CHAR, TokenClass.VOID, TokenClass.STRUCT)) {
            parseType();
            expect(TokenClass.RPAR);
            parseExp();
        } else {
            parseExp();
            expect(TokenClass.RPAR);
        }
    }

    public void parseValueAt() {
        expect(TokenClass.ASTERIX);
        parseExp();
    }

    public void parseSizeOf() {
        expect(TokenClass.SIZEOF);
        expect(TokenClass.LPAR);
        parseType();
        expect(TokenClass.RPAR);
    }

    public void parseTypeCast() {
        expect(TokenClass.LPAR);
        parseType();
        expect(TokenClass.RPAR);
        parseExp();
    }

    public void parseFunCall() {
        if (accept(TokenClass.IDENTIFIER)) {
            nextToken();
            expect(TokenClass.LPAR);
            parseArgList();
            expect(TokenClass.RPAR);
        }
    }

    public void parseArgList() {
        if (accept(TokenClass.MINUS, TokenClass.SIZEOF, TokenClass.ASTERIX,
                TokenClass.LPAR, TokenClass.LSBR, TokenClass.DOT, TokenClass.IDENTIFIER,
                TokenClass.INT_LITERAL, TokenClass.CHAR_LITERAL, TokenClass.STRING_LITERAL)) {
            parseExp();
            parseArgRep();
        }
    }

    public void parseArgRep() {
        if (accept(TokenClass.COMMA)) {
            nextToken();
            parseExp();
            parseArgRep();
        }
    }
}
