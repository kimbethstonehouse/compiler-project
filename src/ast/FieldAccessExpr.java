package ast;

public class FieldAccessExpr extends Expr {

    public final Expr struct;
    public final String fieldName;

    public FieldAccessExpr(Expr struct, String fieldName) {
        this.struct = struct;
        this.fieldName = fieldName;
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitFieldAccessExpr(this);
    }

}
