package ast;

public class TypecastExpr extends Expr {

    public final Type castType;
    public final Expr expr;

    public TypecastExpr(Type castType, Expr expr) {
        this.castType = castType;
        this.expr = expr;
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitTypecastExpr(this);
    }

}
