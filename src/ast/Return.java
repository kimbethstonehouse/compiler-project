package ast;

public class Return extends Stmt {

    public final Expr expr;

    public Return(Expr expr) {

        // expr is optional, so we explicitly set to null if not supplied
        if (expr != null) {
            this.expr = expr;
        } else {
            this.expr = null;
        }
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitReturn(this);
    }

}
