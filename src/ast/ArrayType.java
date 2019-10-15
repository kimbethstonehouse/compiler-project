package ast;

public class ArrayType implements Type {

    public final Type type;
    public final int size;

    public ArrayType(Type type, int size) {
        this.type = type;
        this.size = size;
    }

    public <T> T accept(ASTVisitor<T> v) { return v.visitArrayType(this); }

}
