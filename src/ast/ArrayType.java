package ast;

public class ArrayType implements Type {

    public final Type baseType;
    public final int size;

    public ArrayType(Type baseType, int size) {
        this.baseType = baseType;
        this.size = size;
    }

    public <T> T accept(ASTVisitor<T> v) { return v.visitArrayType(this); }

}
