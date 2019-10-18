package parser;

import ast.*;

import lexer.Token;
import lexer.Tokeniser;
import lexer.Token.TokenClass;

import java.util.ArrayList;
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
        return new Token(TokenClass.INVALID, 0, 0);
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
        List<StructTypeDecl> stds = parseStructDecls(new ArrayList<>());
        List<VarDecl> vds = parseVarDecls(new ArrayList<>());
        List<FunDecl> fds = parseFunDecls(new ArrayList<>());

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

    private List<StructTypeDecl> parseStructDecls(List<StructTypeDecl> structTypeDecls) {
        if (accept(TokenClass.STRUCT) &&
                lookAhead(2).tokenClass == TokenClass.LBRA) {
            StructType structType = parseStructType();
            expect(TokenClass.LBRA);

            // vardecl positive closure
            // must have one, then call kleene closure
            Type baseType = parseType();
            Token t = expect(TokenClass.IDENTIFIER);
            Type type = parseVarDeclRest(baseType);
            List<VarDecl> varDecls = new ArrayList<>();
            varDecls.add(new VarDecl(type, t.data));
            parseVarDecls(varDecls);

            expect(TokenClass.RBRA);
            expect(TokenClass.SC);
            structTypeDecls.add(new StructTypeDecl(structType, varDecls));
            parseStructDecls(structTypeDecls);
        }

        return structTypeDecls;
    }

    private List<VarDecl> parseVarDecls(List<VarDecl> varDecls) {
        if (accept(TokenClass.INT, TokenClass.CHAR, TokenClass.VOID, TokenClass.STRUCT)
                && (lookAhead(2).tokenClass == TokenClass.SC || lookAhead(2).tokenClass == TokenClass.LSBR
                || lookAhead(3).tokenClass == TokenClass.SC || lookAhead(3).tokenClass == TokenClass.LSBR
                || lookAhead(4).tokenClass == TokenClass.SC || lookAhead(4).tokenClass == TokenClass.LSBR)) {
            Type baseType = parseType();
            Token t = expect(TokenClass.IDENTIFIER);
            Type type = parseVarDeclRest(baseType);
            varDecls.add(new VarDecl(type, t.data));
            parseVarDecls(varDecls);
        }

        return varDecls;
    }

    private Type parseVarDeclRest(Type baseType) {
        if (accept(TokenClass.SC)) {
            nextToken();
            return baseType;
        } else {
            expect(TokenClass.LSBR);
            Token t = expect(TokenClass.INT_LITERAL);
            expect(TokenClass.RSBR);
            expect(TokenClass.SC);

            // if expect threw an error, t will be an invalid token with empty data field
            if (t.tokenClass == TokenClass.INVALID) return new ArrayType(baseType, 0);
            else return new ArrayType(baseType, Integer.valueOf(t.data));
        }
    }

    private List<FunDecl> parseFunDecls(List<FunDecl> funDecls) {
        if (accept(TokenClass.INT, TokenClass.CHAR, TokenClass.VOID, TokenClass.STRUCT)
                && (lookAhead(2).tokenClass == TokenClass.LPAR ||
                    lookAhead(3).tokenClass == TokenClass.LPAR ||
                    lookAhead(4).tokenClass == TokenClass.LPAR)) {
            Type type = parseType();
            Token t = expect(TokenClass.IDENTIFIER);
            expect(TokenClass.LPAR);
            List<VarDecl> params = parseParams();
            expect(TokenClass.RPAR);
            Block block = parseBlock();
            funDecls.add(new FunDecl(type, t.data, params, block));
            parseFunDecls(funDecls);
        }

        return funDecls;
    }

    private Type parseType() {
        if (accept(TokenClass.INT)) {
            nextToken();
            return parseTypeOpt(BaseType.INT);
        } else if (accept(TokenClass.CHAR)) {
            nextToken();
            return parseTypeOpt(BaseType.CHAR);
        } else if (accept(TokenClass.VOID)) {
            nextToken();
            return parseTypeOpt(BaseType.VOID);
        } else {
            return parseTypeOpt(parseStructType());
        }
    }

    // * or e
    private Type parseTypeOpt(Type type) {
        // returns a pointer type with the base type
        if (accept(TokenClass.ASTERIX)) {
            nextToken();
            return new PointerType(type);
        }

        // just returns the base type if no pointer
        return type;
    }

    private StructType parseStructType() {
        expect(TokenClass.STRUCT);
        Token t = expect(TokenClass.IDENTIFIER);
        return new StructType(t.data);
    }

    private List<VarDecl> parseParams() {
        List<VarDecl> params = new ArrayList<>();
        if (accept(TokenClass.INT, TokenClass.CHAR, TokenClass.VOID, TokenClass.STRUCT)) {
            Type type = parseType();
            Token t = expect(TokenClass.IDENTIFIER);
            params.add(new VarDecl(type, t.data));
            parseParamsRep(params);
        }
        return params;
    }

    private void parseParamsRep(List<VarDecl> params) {
        if (accept(TokenClass.COMMA)) {
            nextToken();
            Type type = parseType();
            Token t = expect(TokenClass.IDENTIFIER);
            params.add(new VarDecl(type, t.data));
            parseParamsRep(params);
        }
    }

    private Stmt parseStmt() {
        if (accept(TokenClass.LBRA)) {
            return parseBlock();
        } else if (accept(TokenClass.WHILE)) {
            nextToken();
            expect(TokenClass.LPAR);
            Expr exp = parseExp();
            expect(TokenClass.RPAR);
            Stmt stmt = parseStmt();
            return new While(exp, stmt);
        } else if (accept(TokenClass.IF)) {
            nextToken();
            expect(TokenClass.LPAR);
            Expr exp = parseExp();
            expect(TokenClass.RPAR);
            Stmt stmt1 = parseStmt();
            Stmt stmt2 = parseElseOpt();
            return new If(exp, stmt1, stmt2);
        } else if (accept(TokenClass.RETURN)) {
            nextToken();
            Expr exp = parseExpOpt();
            expect(TokenClass.SC);
            return new Return(exp);
        } else {
            Expr lhs = parseExp();
            return parseStmtRest(lhs);
        }
    }

    private Stmt parseStmtRest(Expr lhs) {
        if (accept(TokenClass.SC)) {
            nextToken();
            return new ExprStmt(lhs);
        } else {
            expect(TokenClass.ASSIGN);
            Expr rhs = parseExp();
            expect(TokenClass.SC);
            return new Assign(lhs, rhs);
        }
    }

    private Stmt parseElseOpt() {
        if (accept(TokenClass.ELSE)) {
            nextToken();
            return parseStmt();
        }

        return null;
    }

    private Expr parseExpOpt() {
        if (accept(TokenClass.MINUS, TokenClass.SIZEOF, TokenClass.ASTERIX, TokenClass.LPAR,
                TokenClass.IDENTIFIER, TokenClass.INT_LITERAL, TokenClass.CHAR_LITERAL, TokenClass.STRING_LITERAL)) {
            return parseExp();
        }

        return null;
    }

    private Block parseBlock() {
        expect(TokenClass.LBRA);
        List<VarDecl> varDecls = parseVarDecls(new ArrayList<>());
        List<Stmt> stmts = parseStmtRep(new ArrayList<>());
        expect(TokenClass.RBRA);
        return new Block(varDecls, stmts);
    }

    private List<Stmt> parseStmtRep(List<Stmt> stmts) {
        if (accept(TokenClass.LBRA, TokenClass.WHILE, TokenClass.IF, TokenClass.RETURN,
                TokenClass.MINUS, TokenClass.SIZEOF, TokenClass.ASTERIX, TokenClass.LPAR,
                TokenClass.IDENTIFIER, TokenClass.INT_LITERAL, TokenClass.CHAR_LITERAL, TokenClass.STRING_LITERAL)) {
            stmts.add(parseStmt());
            parseStmtRep(stmts);
        }

        return stmts;
    }

    private Expr parseExp() {
        Expr lhs = parseExpA();
        return parseOpsA(lhs);
    }

    private Expr parseOpsA(Expr lhs) {
        if (accept(TokenClass.OR)) {
            nextToken();
            Expr rhs = parseExpA();
            lhs = parseOpsA(new BinOp(Op.OR, lhs, rhs));
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
            lhs = parseOpsB(new BinOp(Op.AND, lhs, rhs));
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

            if (token.tokenClass == TokenClass.EQ) op = Op.EQ;
            else op = Op.NE;

            nextToken();

            Expr rhs = parseExpC();
            lhs = parseOpsC(new BinOp(op, lhs, rhs));
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

            if (token.tokenClass == TokenClass.LT) op = Op.LT;
            else if (token.tokenClass == TokenClass.LE) op = Op.LE;
            else if (token.tokenClass == TokenClass.GT) op = Op.GT;
            else op = Op.GE;

            nextToken();

            Expr rhs = parseExpD();
            lhs = parseOpsD(new BinOp(op, lhs, rhs));
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

            if (token.tokenClass == TokenClass.PLUS) op = Op.ADD;
            else op = Op.SUB;

            nextToken();

            Expr rhs = parseExpE();
            lhs = parseOpsE(new BinOp(op, lhs, rhs));
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

            if (token.tokenClass == TokenClass.ASTERIX) op = Op.MUL;
            else if (token.tokenClass == TokenClass.DIV) op = Op.DIV;
            else op = Op.MOD;

            nextToken();

            Expr rhs = parseExpF();
            lhs = parseOpsF(new BinOp(op, lhs, rhs));
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
            Expr e = parseSizeOf();
            return parseOpsH(e);
        } else if (accept(TokenClass.ASTERIX)) {
            Expr e = parseValueAt();
            return parseOpsH(e);
        } else if (accept(TokenClass.LPAR)) {
            // (exp) or typecast (type) exp
            nextToken();
            Expr e = parseExpOrType();
            return parseOpsH(e);
        } else {
            Expr e = parseExpG();
            return parseOpsH(e);
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
            lhs = parseOpsH(new ArrayAccessExpr(lhs, idx));
        // field access . IDENT
        } else if (accept(TokenClass.DOT)) {
            nextToken();
            Token t = expect(TokenClass.IDENTIFIER);
            lhs = parseOpsH(new FieldAccessExpr(lhs, t.data));
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
            // if expect threw an error, t will be an invalid token with empty data field
            if (t.tokenClass == TokenClass.INVALID) return new IntLiteral(0);
            else return new IntLiteral((Integer.valueOf(t.data)));
        } else if (accept(TokenClass.STRING_LITERAL)) {
            Token t = expect(TokenClass.STRING_LITERAL);
            return new StrLiteral(t.data);
        } else {
            Token t = expect(TokenClass.CHAR_LITERAL);

            // if expect threw an error, t will be an invalid token with empty data field
            if (t.tokenClass == TokenClass.INVALID) return new ChrLiteral(' ');
            else return new ChrLiteral(t.data.charAt(0));
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
        List<Expr> args = new ArrayList<>();

        if (accept(TokenClass.MINUS, TokenClass.SIZEOF, TokenClass.ASTERIX, TokenClass.LPAR,
                TokenClass.IDENTIFIER, TokenClass.INT_LITERAL, TokenClass.CHAR_LITERAL, TokenClass.STRING_LITERAL)) {
            args.add(parseExp());
            parseArgRep(args);
        }

        return args;
    }

    private void parseArgRep(List<Expr> args) {
        if (accept(TokenClass.COMMA)) {
            nextToken();
            args.add(parseExp());
            parseArgRep(args);
        }
    }

    private ValueAtExpr parseValueAt() {
        expect(TokenClass.ASTERIX);
        return new ValueAtExpr(parseExp());
    }

    private SizeOfExpr parseSizeOf() {
        expect(TokenClass.SIZEOF);
        expect(TokenClass.LPAR);
        Type type = parseType();
        expect(TokenClass.RPAR);
        return new SizeOfExpr(type);
    }
}
