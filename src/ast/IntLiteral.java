package ast;

public class IntLiteral extends Expr {
    int i;

    IntLiteral(int i) {
        this.i = i;
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitIntLiteral(this);
    }
}
