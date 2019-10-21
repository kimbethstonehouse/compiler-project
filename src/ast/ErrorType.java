package ast;

public class ErrorType implements Type {
    public <T> T accept(ASTVisitor<T> v) { return v.visitErrorType(this); }
}
