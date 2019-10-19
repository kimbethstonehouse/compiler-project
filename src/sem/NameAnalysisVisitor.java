package sem;

import ast.*;

public class NameAnalysisVisitor extends BaseSemanticVisitor<Void> {

	Scope scope;
	public NameAnalysisVisitor(Scope scope) { this.scope = scope; }

	@Override
	public Void visitProgram(Program p) {
		for (StructTypeDecl std : p.structTypeDecls) std.accept(this);
		for (VarDecl vd : p.varDecls) vd.accept(this);
		for (FunDecl fd : p.funDecls) fd.accept(this);
		return null;
	}

	@Override
	public Void visitStructTypeDecl(StructTypeDecl sts) {
		// To be completed...
		return null;
	}

	@Override
	// check that variable has not been
	// already declared with the same name
	public Void visitVarDecl(VarDecl vd) {
		Symbol s = scope.lookupCurrent(vd.varName);

		if (s != null)  {
			error("Variable or function with name " + vd.varName
					+ " has already been declared in scope");
		} else scope.put(new VarSymbol(vd));

		return null;
	}

	@Override
	public Void visitFunDecl(FunDecl p) {
		Symbol s = scope.lookupCurrent(p.name);

		if (s != null)  {
			error("Variable or function with name " + p.name
					+ " has already been declared in scope");
		} else scope.put(new FuncSymbol(p));

		// fundecls introduce a new scope
		visitFunDeclBody(p);

		return null;
	}

	private void visitFunDeclBody(FunDecl p) {
		Scope oldScope = scope;
		scope = new Scope(oldScope);

		// fundecls introduce a new scope
		// for both the function parameter identifiers
		for (VarDecl vd : p.params) vd.accept(this);

		// and the block forming the body of the function
		for (VarDecl vd : p.block.varDecls) vd.accept(this);
		for (Stmt stmt : p.block.stmts) stmt.accept(this);

		scope = oldScope;
	}

	@Override
	public Void visitBaseType(BaseType bt) {
		// To be completed...
		return null;
	}

	@Override
	public Void visitPointerType(PointerType pt) {
		// To be completed...
		return null;
	}

	@Override
	public Void visitStructType(StructType st) {
		// To be completed...
		return null;
	}

	@Override
	public Void visitArrayType(ArrayType at) {
		// To be completed...
		return null;
	}

	@Override
	public Void visitIntLiteral(IntLiteral il) {
		// To be completed...
		return null;
	}

	@Override
	public Void visitStrLiteral(StrLiteral sl) {
		// To be completed...
		return null;
	}

	@Override
	public Void visitChrLiteral(ChrLiteral cl) {
		// To be completed...
		return null;
	}

	@Override
	public Void visitVarExpr(VarExpr v) {
		// check the variable has been declared
		Symbol vs = scope.lookup(v.name);

		if (vs == null) error("Variable " + v.name + " has not been declared in the scope");
		// vs could actually be a function
		else if (!vs.isVar()) error(v.name + " is a function, not a variable");
		// record vardecl in varexpr ast node
		else v.vd = ((VarSymbol) vs).vd;

		return null;
	}

	@Override
	public Void visitFunCallExpr(FunCallExpr fce) {
		// check the function has been declared
		Symbol fs = scope.lookup(fce.name);

		if (fs == null) error("Function " + fce.name + " has not been declared in the scope");
		// fs could actually be a variable
		else if (!fs.isFunc()) error(fce.name + " is a variable, not a function");
		// record funcdecl in funcall ast node
		else fce.fd = ((FuncSymbol) fs).fd;

		return null;
	}

	@Override
	public Void visitBinOp(BinOp bo) {
		// To be completed...
		return null;
	}

	@Override
	public Void visitArrayAccessExpr(ArrayAccessExpr aae) {
		// To be completed...
		return null;
	}

	@Override
	public Void visitFieldAccessExpr(FieldAccessExpr fae) {
		// To be completed...
		return null;
	}

	@Override
	public Void visitValueAtExpr(ValueAtExpr vae) {
		// To be completed...
		return null;
	}

	@Override
	public Void visitSizeOfExpr(SizeOfExpr soe) {
		// To be completed...
		return null;
	}

	@Override
	public Void visitTypecastExpr(TypecastExpr tce) {
		// To be completed...
		return null;
	}

	@Override
	// blocks create a new scope
	public Void visitBlock(Block b) {
		Scope oldScope = scope;
		scope = new Scope(oldScope);

		// visit the children
		for (VarDecl vd : b.varDecls) vd.accept(this);
		for (Stmt stmt : b.stmts) stmt.accept(this);

		scope = oldScope;
		return null;
	}

	@Override
	public Void visitWhile(While w) {
		// To be completed...
		return null;
	}

	@Override
	public Void visitIf(If i) {
		// To be completed...
		return null;
	}

	@Override
	public Void visitAssign(Assign a) {
		// To be completed...
		return null;
	}

	@Override
	public Void visitReturn(Return r) {
		// To be completed...
		return null;
	}

	@Override
	public Void visitExprStmt(ExprStmt es) {
		// To be completed...
		return null;
	}
}
