package ast;

public class ArrayAccessExpr extends Expr {
    public final Expr arr;
    public final Expr idx;

    public ArrayAccessExpr(Expr arr, Expr idx) {
        this.arr = arr;
        this.idx = idx;
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitArrayAccessExpr(this);
    }

}
