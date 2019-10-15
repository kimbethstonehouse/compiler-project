package ast;

public class BinOp extends Expr {

    public final Op op;
    public final Expr lhs;
    public final Expr rhs;

    public BinOp(Op op, Expr lhs, Expr rhs) {
        this.op = op;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public <T> T accept(ASTVisitor<T> v) { return v.visitBinOp(this); }

}
