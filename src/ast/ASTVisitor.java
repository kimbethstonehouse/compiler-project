package ast;

import java.awt.*;

public interface ASTVisitor<T> {
    public T visitProgram(Program p);
    public T visitStructTypeDecl(StructTypeDecl st);
    public T visitVarDecl(VarDecl vd);
    public T visitFunDecl(FunDecl p);

    public T visitBaseType(BaseType bt);
    public T visitPointerType(PointerType pt);
    public T visitStructType(StructType st);
    public T visitArrayType(ArrayType at);

    public T visitIntLiteral(IntLiteral il);
    public T visitStrLiteral(StrLiteral sl);
    public T visitChrLiteral(ChrLiteral cl);
    public T visitVarExpr(VarExpr v);
    public T visitFunCallExpr(FunCallExpr fce);
    public T visitBinOp(BinOp bo);
    public T visitArrayAccessExpr(ArrayAccessExpr aae);
    public T visitFieldAccessExpr(FieldAccessExpr fae);
    public T visitValueAtExpr(ValueAtExpr vae);
    public T visitSizeOfExpr(SizeOfExpr soe);
    public T visitTypecastExpr(TypecastExpr tce);

    public T visitBlock(Block b);
    public T visitWhile(While w);
    public T visitIf(If i);
    public T visitAssign(Assign a);
    public T visitReturn(Return r);
    public T visitExprStmt(ExprStmt es);

    // error
    public T visitErrorType(ErrorType et);
}
