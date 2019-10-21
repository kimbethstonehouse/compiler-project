package sem;

import ast.*;

import java.util.ArrayList;
import java.util.Arrays;

public class NameAnalysisVisitor extends BaseSemanticVisitor<Void> {

	Scope scope;
	public NameAnalysisVisitor(Scope scope) { this.scope = scope; }

	@Override
	public Void visitProgram(Program p) {
		for (StructTypeDecl std : p.structTypeDecls) std.accept(this);
		for (VarDecl vd : p.varDecls) vd.accept(this);

		addLibraryFunctions();
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

		// check the parameters to the funcall exist
		for (Expr e : fce.args) e.accept(this);

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
		vae.expr.accept(this);
		return null;
	}

	@Override
	public Void visitSizeOfExpr(SizeOfExpr soe) {
		// To be completed...
		return null;
	}

	@Override
	public Void visitTypecastExpr(TypecastExpr tce) {
		tce.expr.accept(this);
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
		w.expr.accept(this);
		return null;
	}

	@Override
	public Void visitIf(If i) {
		i.expr.accept(this);
		return null;
	}

	@Override
	public Void visitAssign(Assign a) {
		a.lhs.accept(this);
		a.rhs.accept(this);
		return null;
	}

	@Override
	public Void visitReturn(Return r) {
		r.expr.accept(this);
		return null;
	}

	@Override
	public Void visitExprStmt(ExprStmt es) {
		es.expr.accept(this);
		return null;
	}

	@Override
    public Void visitErrorType(ErrorType et) {
	    return null;
    }

	// HELPER FUNCTIONS

	private void addLibraryFunctions() {
		// void print_s(char* s);
		scope.put(new FuncSymbol(
				new FunDecl(
						BaseType.VOID,
						"print_s",
						Arrays.asList(new VarDecl(new PointerType(BaseType.CHAR), "s")),
						new Block(new ArrayList<>(), new ArrayList<>()))));

		// void print_i(int i);
		scope.put(new FuncSymbol(
				new FunDecl(
						BaseType.VOID,
						"print_i",
						Arrays.asList(new VarDecl(BaseType.INT, "i")),
						new Block(new ArrayList<>(), new ArrayList<>()))));

		// void print_c(char c);
		scope.put(new FuncSymbol(
				new FunDecl(
						BaseType.VOID,
						"print_c",
						Arrays.asList(new VarDecl(BaseType.CHAR, "c")),
						new Block(new ArrayList<>(), new ArrayList<>()))));

		// char read_c();
		scope.put(new FuncSymbol(
				new FunDecl(
						BaseType.CHAR,
						"read_c",
						new ArrayList<>(),
						new Block(new ArrayList<>(), new ArrayList<>()))));

		// int read_i();
		scope.put(new FuncSymbol(
				new FunDecl(
						BaseType.INT,
						"read_i",
						new ArrayList<>(),
						new Block(new ArrayList<>(), new ArrayList<>()))));

		// void* mcmalloc(int size);
		scope.put(new FuncSymbol(
				new FunDecl(
						new PointerType(BaseType.VOID),
						"mcmalloc",
						Arrays.asList(new VarDecl(BaseType.INT, "size")),
						new Block(new ArrayList<>(), new ArrayList<>()))));
	}
}
