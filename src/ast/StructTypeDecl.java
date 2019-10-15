package ast;

import java.util.List;

public class StructTypeDecl implements ASTNode {
    public StructType st;
    public final List<VarDecl> varDecls;

    public StructTypeDecl(StructType st, List<VarDecl> varDecls) {
        this.st = st;
        this.varDecls = varDecls;
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitStructTypeDecl(this);
    }
}
