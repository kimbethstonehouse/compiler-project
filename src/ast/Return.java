package ast;

public class Return extends Stmt {

    public final Expr expr;

    public Return(Expr expr) {

        // expr is optional so may be null
        this.expr = expr;
    }

    public <T> T accept(ASTVisitor<T> v) { return v.visitReturn(this); }

}
