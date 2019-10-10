package ast;

public class ChrLiteral extends Expr {
    char c;

    ChrLiteral(char c) {
        this.c = c;
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitChrLiteral(this);
    }
}
