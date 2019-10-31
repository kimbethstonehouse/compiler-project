package ast;

import java.util.List;

public class StructTypeDecl implements ASTNode {
    public final StructType structType;
    public final List<VarDecl> varDecls;
    public int structSize;          // memory needed for the struct, set during data allocation

    public StructTypeDecl(StructType structType, List<VarDecl> varDecls) {
        this.structType = structType;
        this.varDecls = varDecls;
        this.structSize = 0;
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitStructTypeDecl(this);
    }
}
