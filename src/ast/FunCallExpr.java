package ast;

import java.util.List;

public class FunCallExpr extends Expr {
    public final String str;
    public final List<Expr> exprList;

    public FunCallExpr(String str, List<Expr> exprList) {
        this.str = str;
        this.exprList = exprList;
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitFunCallExpr(this);
    }
}
