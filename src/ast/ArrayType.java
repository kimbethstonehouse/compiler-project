package ast;

public class ArrayType implements Type {

    public final Type type;
    public final int i;

    public ArrayType(Type type, int i) {
        this.type = type;
        this.i = i;
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitArrayType(this);
    }

}
