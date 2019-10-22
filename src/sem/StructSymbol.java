package sem;

import ast.StructTypeDecl;

public class StructSymbol extends Symbol {
    public StructTypeDecl std;

    public StructSymbol(StructTypeDecl std) {
        super(std.structType.name);
        this.std = std;
    }

    public boolean isVar() { return false; }
    public boolean isFunc() { return false; }

}