package ast;

public class PointerType implements Type {

    public Type baseType;

    public PointerType(Type baseType) { this.baseType = baseType; }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitPointerType(this);
    }

}
