package ast;

public class StructType implements Type {

    public final String str;

    public StructType(String str) { this.str = str; }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitStructType(this);
    }

}
