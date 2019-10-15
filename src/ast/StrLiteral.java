package ast;

public class StrLiteral extends Expr {
    String s;

    public StrLiteral(String s) {
        this.s = s;
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitStrLiteral(this);
    }
}

