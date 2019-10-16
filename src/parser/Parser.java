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
            nextToken();
            Expr rhs = parseExpA();
            parseOpsA(new BinOp(Op.OR, lhs, rhs));
        }

        return lhs;
    }

    private Expr parseExpA() {
        Expr lhs = parseExpB();
        return parseOpsB(lhs);
    }

    private Expr parseOpsB(Expr lhs) {
        if (accept(TokenClass.AND)) {
            nextToken();
            Expr rhs = parseExpB();
            parseOpsB(new BinOp(Op.AND, lhs, rhs));
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

    private Expr parseExpF() {
        // unary minus
        if (accept(TokenClass.MINUS)) {
            nextToken();
            Expr rhs = parseExpF();
            return new BinOp(Op.SUB, new IntLiteral(0), rhs);
        } else if (accept(TokenClass.SIZEOF)) {
            return parseSizeOf();
        } else if (accept(TokenClass.ASTERIX)) {
            return parseValueAt();
        } else if (accept(TokenClass.LPAR)) {
            // (exp) or typecast (type) exp
            nextToken();
            return parseExpOrType();
        } else {
            return parseExpG();
        }
    }

    private Expr parseExpG() {
        Expr lhs = parseExpH();
        return parseOpsH(lhs);
    }

    private Expr parseOpsH(Expr lhs) {
        // array access [exp]
        if (accept(TokenClass.LSBR)) {
            nextToken();
            Expr idx = parseExp();
            expect(TokenClass.RSBR);
            return new ArrayAccessExpr(lhs, idx);
        // field access . IDENT
        } else if (accept(TokenClass.DOT)) {
            nextToken();
            Token t = expect(TokenClass.IDENTIFIER);
            return new FieldAccessExpr(lhs, t.data);
        }

        return lhs;
    }

    private Expr parseExpH() {
        // identifier or funcall IDENT ( arglist )
        if (accept(TokenClass.IDENTIFIER)) {
            Token t = expect(TokenClass.IDENTIFIER);
            return parseFunCallOrIdent(t.data);
        } else if (accept(TokenClass.INT_LITERAL)) {
            Token t = expect(TokenClass.INT_LITERAL);
            return new IntLiteral(Integer.valueOf(t.data));
        } else if (accept(TokenClass.STRING_LITERAL)) {
            Token t = expect(TokenClass.STRING_LITERAL);
            return new StrLiteral(t.data);
        } else {
            Token t = expect(TokenClass.CHAR_LITERAL);
            return new ChrLiteral(t.data.charAt(0));
        }
    }

    private Expr parseExpOrType() {
        // typecast (type) exp
        if (accept(TokenClass.INT, TokenClass.CHAR, TokenClass.VOID, TokenClass.STRUCT)) {
            Type type = parseType();
            expect(TokenClass.RPAR);
            Expr exp = parseExp();
            return new TypecastExpr(type, exp);
        // (exp)
        } else {
            Expr exp = parseExp();
            expect(TokenClass.RPAR);
            return exp;
        }
    }

    private Expr parseFunCallOrIdent(String name) {
        // function call
        if (accept(TokenClass.LPAR)) {
            nextToken();
            List<Expr> args = parseArgList();
            expect(TokenClass.RPAR);
            return new FunCallExpr(name, args);
        }

        // or identifier
        return new VarExpr(name);
    }

    private List<Expr> parseArgList() {
        if (accept(TokenClass.MINUS, TokenClass.SIZEOF, TokenClass.ASTERIX, TokenClass.LPAR,
                TokenClass.IDENTIFIER, TokenClass.INT_LITERAL, TokenClass.CHAR_LITERAL, TokenClass.STRING_LITERAL)) {
            parseExp();
            parseArgRep();
        }

        // return to this
        return null;
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
