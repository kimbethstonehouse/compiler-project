package ast;

public class SizeOfExpr extends Expr {

    public final Type sizeofType;

    public SizeOfExpr(Type sizeofType) { this.sizeofType = sizeofType; }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitSizeOfExpr(this);
    }

}
