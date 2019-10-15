package ast;

import java.util.List;

public class Block extends Stmt {

    public final List<VarDecl> varDeclList;
    public final List<Stmt> stmtList;

    public Block(List<VarDecl> varDeclList, List<Stmt> stmtList) {
        this.varDeclList = varDeclList;
        this.stmtList = stmtList;
    }

    public <T> T accept(ASTVisitor<T> v) {
	    return v.visitBlock(this);
    }
}
