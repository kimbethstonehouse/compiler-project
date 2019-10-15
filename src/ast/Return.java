package ast;

public class Return extends Stmt {

    public final Expr expr;

    // expr is optional, so we explicitly set to null if not supplied
    public Return() {
        this.expr = null;
    }

    public Return(Expr expr) {
        this.expr = expr;
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitReturn(this);
    }

}
