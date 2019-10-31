package ast;

public class StrLiteral extends Expr {

    public final String s;
    public String label;        // set during data allocation

    public StrLiteral(String s) {
        this.s = s;
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitStrLiteral(this);
    }

}

