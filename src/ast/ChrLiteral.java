package ast;

public class ChrLiteral extends Expr {
    public final char c;
    public boolean isEscape;

    public ChrLiteral(char c) {
        this.c = c;
        isEscape = false;
    }

    public ChrLiteral(char c, boolean isEscape) {
        this.c = c;
        this.isEscape = isEscape;
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitChrLiteral(this);
    }
}
