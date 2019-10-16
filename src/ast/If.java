package ast;

public class If extends Stmt {

    public final Expr expr;
    public final Stmt stmt1;
    public final Stmt stmt2;

    public If(Expr expr, Stmt stmt1, Stmt stmt2) {
        this.expr = expr;
        this.stmt1 = stmt1;

        // stmt2 is optional, so we explicitly set to null if not supplied
        if (stmt2 != null) {
            this.stmt2 = stmt2;
        } else {
            this.stmt2 = null;
        }
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitIf(this);
    }

}
