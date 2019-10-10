package ast;

public interface ASTVisitor<T> {
    public T visitBaseType(BaseType bt);
    public T visitStructTypeDecl(StructTypeDecl st);
    public T visitBlock(Block b);
    public T visitFunDecl(FunDecl p);
    public T visitProgram(Program p);
    public T visitVarDecl(VarDecl vd);
    public T visitVarExpr(VarExpr v);
    public T visitBinOp(BinOp bo);
    public T visitIntLiteral(IntLiteral il);
    public T visitStrLiteral(StrLiteral sl);
    public T visitChrLiteral(ChrLiteral cl);

    // to complete ... (should have one visit method for each concrete AST node class)
}
