package sem;

import ast.FunDecl;

public class FuncSymbol extends Symbol {
    public FunDecl f;

    public FuncSymbol(FunDecl f) {
        super(f.name);
        this.f = f;
    }
}
