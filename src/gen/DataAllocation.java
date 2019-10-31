package gen;

import ast.*;

import java.io.PrintWriter;

public class DataAllocation implements ASTVisitor<Void> {

    private PrintWriter writer; // use this writer to output the assembly instructions
    private int numStrings = 0;

    public void emitProgram(Program program, PrintWriter writer) {
        this.writer = writer;
        writer.println(".data");
        visitProgram(program);
        writer.println();
    }

    // Program ::= StructTypeDecl* VarDecl* FunDecl*
    public Void visitProgram(Program p) {
        for (StructTypeDecl std : p.structTypeDecls) std.accept(this);
        for (VarDecl vd : p.varDecls) vd.accept(this);
        for (FunDecl fd : p.funDecls) fd.accept(this);
        return null;
    }

    @Override
    // StructTypeDecl ::= StructType VarDecl*
    public Void visitStructTypeDecl(StructTypeDecl st) {
        for (VarDecl vd : st.varDecls) { st.structSize += getTypeSize(vd.type); }
        return null;
    }

    @Override
    // VarDecl ::= Type String
    public Void visitVarDecl(VarDecl vd) {
        // global variables go in the static storage area (.data)
        int size = getTypeSize(vd.type);
        writer.printf("%s: .space %s\n", vd.varName, size);
        return null;
    }

    @Override
    // FunDecl ::= Type String VarDecl* Block
    public Void visitFunDecl(FunDecl p) {
        // fundecl vardecls are handled in the code generation pass as they are not statically allocated
        p.block.accept(this);
        return null;
    }

    @Override
    // BaseType ::= INT | CHAR | VOID
    public Void visitBaseType(BaseType bt) {
        return null;
    }

    @Override
    // PointerType ::= Type
    public Void visitPointerType(PointerType pt) {
        return null;
    }

    @Override
    // StructType ::= String
    public Void visitStructType(StructType st) {
        return null;
    }

    @Override
    // ArrayType ::= Type int
    public Void visitArrayType(ArrayType at) {
        return null;
    }

    @Override
    // IntLiteral ::= int
    public Void visitIntLiteral(IntLiteral il) {
        return null;
    }

    @Override
    // StrLiteral ::= String
    public Void visitStrLiteral(StrLiteral sl) {
        sl.label = String.format("str%s", numStrings);
        numStrings++;
        writer.printf("%s: .asciiz \"%s\"\n", sl.label, sl.s);
        return null;
    }

    @Override
    // ChrLiteral ::= char
    public Void visitChrLiteral(ChrLiteral cl) {
        return null;
    }

    @Override
    // VarExpr ::= String
    public Void visitVarExpr(VarExpr v) {
        return null;
    }

    @Override
    // FunCallExpr ::= String Expr*
    public Void visitFunCallExpr(FunCallExpr fce) {
        return null;
    }

    @Override
    // BinOp ::= Expr Op Expr
    public Void visitBinOp(BinOp bo) {
        bo.lhs.accept(this);
        bo.rhs.accept(this);
        return null;
    }

    @Override
    // ArrayAccessExpr ::= Expr Expr
    public Void visitArrayAccessExpr(ArrayAccessExpr aae) {
        aae.arr.accept(this);
        aae.idx.accept(this);
        return null;
    }

    @Override
    // FieldAccessExpr ::= Expr String
    public Void visitFieldAccessExpr(FieldAccessExpr fae) {
        fae.struct.accept(this);
        return null;
    }

    @Override
    // ValueAtExpr ::= Expr
    public Void visitValueAtExpr(ValueAtExpr vae) {
        vae.expr.accept(this);
        return null;
    }

    @Override
    // SizeOfExpr ::= Type
    public Void visitSizeOfExpr(SizeOfExpr soe) {
        return null;
    }

    @Override
    // TypecastExpr ::= Type Expr
    public Void visitTypecastExpr(TypecastExpr tce) {
        tce.expr.accept(this);
        return null;
    }

    // STMT
    @Override
    // Block ::= VarDecl* Stmt*
    public Void visitBlock(Block b) {
        // block vardecls are handled in the code generation pass as they are
        // within functions and therefore not statically allocated
        for (Stmt stmt : b.stmts) { stmt.accept(this); }
        return null;
    }

    @Override
    // While ::= Expr Stmt
    public Void visitWhile(While w) {
        w.expr.accept(this);
        return null;
    }

    @Override
    // If ::= Expr Stmt [Stmt]
    public Void visitIf(If i) {
        i.expr.accept(this);
        i.stmt1.accept(this);
        // stmt2 is optional so may be null in the case of no else
        if (i.stmt2 != null) i.stmt2.accept(this);
        return null;
    }

    @Override
    // Assign ::= Expr Expr
    public Void visitAssign(Assign a) {
        a.lhs.accept(this);
        a.rhs.accept(this);
        return null;
    }

    @Override
    // Return ::= [Expr]
    public Void visitReturn(Return r) {
        if (r.expr != null) r.expr.accept(this);
        return null;
    }

    @Override
    // ExprStmt ::= Expr
    public Void visitExprStmt(ExprStmt es) {
        es.expr.accept(this);
        return null;
    }

    @Override
    public Void visitErrorType(ErrorType et) {
        return null;
    }

    // HELPER FUNCTIONS
    private int getTypeSize(Type type) {
        if (type instanceof StructType) {
            // struct size was determined at declaration and stored in the std ast node
            return ((StructType) type).std.structSize;
        } else if (type instanceof ArrayType) {
            ArrayType arrayType = (ArrayType) type;
            return (arrayType.size * getTypeSize(arrayType.baseType));
        } else {
            // chars, ints and pointers need 4 bytes
            return 4;
        }
    }
}
