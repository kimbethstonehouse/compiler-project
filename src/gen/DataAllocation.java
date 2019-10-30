package gen;

import ast.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class DataAllocation implements ASTVisitor<Void> {

    private PrintWriter writer; // use this writer to output the assembly instructions

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
        int size = 0;
        for (VarDecl vd : st.varDecls) {
            size += returnTypeSize(vd.type);
        }
        st.structSize = size;
        return null;
    }

    @Override
    // VarDecl ::= Type String
    public Void visitVarDecl(VarDecl vd) {
        // global variables go in the static storage area (.data)
        int size = returnTypeSize(vd.type);
        writer.printf("%s: .space %s\n", vd.varName, size);
        return null;
    }

    @Override
    public Void visitFunDecl(FunDecl p) {
        return null;
    }

    @Override
    public Void visitBaseType(BaseType bt) {
        return null;
    }

    @Override
    public Void visitPointerType(PointerType pt) {
        return null;
    }

    @Override
    public Void visitStructType(StructType st) {
        return null;
    }

    @Override
    public Void visitArrayType(ArrayType at) {
        return null;
    }

    @Override
    public Void visitIntLiteral(IntLiteral il) {
        return null;
    }

    @Override
    public Void visitStrLiteral(StrLiteral sl) {
        // TODO: handle str literal
//        writer.printf("%s: .asciiz \"%s\"\n", "string", sl.s);
        return null;
    }

    @Override
    public Void visitChrLiteral(ChrLiteral cl) {
        return null;
    }

    @Override
    public Void visitVarExpr(VarExpr v) {
        return null;
    }

    @Override
    public Void visitFunCallExpr(FunCallExpr fce) {
        return null;
    }

    @Override
    public Void visitBinOp(BinOp bo) {
        return null;
    }

    @Override
    public Void visitArrayAccessExpr(ArrayAccessExpr aae) {
        return null;
    }

    @Override
    public Void visitFieldAccessExpr(FieldAccessExpr fae) {
        return null;
    }

    @Override
    public Void visitValueAtExpr(ValueAtExpr vae) {
        return null;
    }

    @Override
    public Void visitSizeOfExpr(SizeOfExpr soe) {
        return null;
    }

    @Override
    public Void visitTypecastExpr(TypecastExpr tce) {
        return null;
    }

    @Override
    public Void visitBlock(Block b) {
        return null;
    }

    @Override
    public Void visitWhile(While w) {
        return null;
    }

    @Override
    public Void visitIf(If i) {
        return null;
    }

    @Override
    public Void visitAssign(Assign a) {
        return null;
    }

    @Override
    public Void visitReturn(Return r) {
        return null;
    }

    @Override
    public Void visitExprStmt(ExprStmt es) {
        return null;
    }

    @Override
    public Void visitErrorType(ErrorType et) {
        return null;
    }

    // HELPER FUNCTIONS
    private int returnTypeSize(Type type) {
        if (type instanceof StructType) {
            // struct size was determined at declaration
            // and stored in the std ast node
            return ((StructType) type).std.structSize;
        } else if (type instanceof ArrayType) {
            ArrayType arrayType = (ArrayType) type;
            return (arrayType.size * returnTypeSize(arrayType.baseType));
        } else if (type == BaseType.CHAR) {
            // chars need one byte
            return 1;
        } else {
            // ints and pointers need 4 bytes
            return 4;
        }
    }
}
