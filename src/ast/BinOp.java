package ast;

public class BinOp extends Expr {
    Op op;
    Expr lhs;
    Expr rhs;

    public BinOp(Op op, Expr lhs, Expr rhs) {
        this.op = op;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitBinOp(this);
    }
}
