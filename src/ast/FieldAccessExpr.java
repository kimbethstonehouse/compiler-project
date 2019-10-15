package ast;

public class FieldAccessExpr extends Expr {
    public final Expr structure;
    public final String name;

    public FieldAccessExpr(Expr structure, String name) {
        this.structure = structure;
        this.name = name;
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitFieldAccessExpr(this);
    }

}
