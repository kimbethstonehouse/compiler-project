package ast;

public class VarDecl implements ASTNode {

    public final Type type;
    public final String varName;
    public boolean isGlobal;        // records whether the variable is local or global
    public int offset;              // records where a local variable is stored in relation to the frame pointer

    public VarDecl(Type type, String varName) {
	    this.type = type;
	    this.varName = varName;
	    isGlobal = false;
	    offset = 0;
    }

     public <T> T accept(ASTVisitor<T> v) {
	return v.visitVarDecl(this);
    }

}