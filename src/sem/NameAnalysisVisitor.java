package sem;

import ast.*;

import java.util.ArrayList;
import java.util.Arrays;

public class NameAnalysisVisitor extends BaseSemanticVisitor<Void> {

	Scope scope;
	Scope structScope;

	public NameAnalysisVisitor() {
		this.scope = new Scope();
		this.structScope = new Scope();
	}

	@Override
	// Program ::= StructTypeDecl* VarDecl* FunDecl*
	public Void visitProgram(Program p) {
        for (StructTypeDecl std : p.structTypeDecls) std.accept(this);
        for (VarDecl vd : p.varDecls) vd.accept(this);
        addLibraryFunctions();
        for (FunDecl fd : p.funDecls) fd.accept(this);
		return null;
	}

	@Override
	// StructTypeDecl ::= StructType VarDecl*
	public Void visitStructTypeDecl(StructTypeDecl std) {
		Symbol structSymbol = structScope.lookupCurrent(std.structType.name);

		if (structSymbol != null) error("Struct with name %s has already been declared in this scope\n", std.structType.name);
		else structScope.put(new StructSymbol(std));

		// don't switch to a new scope, but create one in parallel instead
		// if we didn't do this, a vardecl with the same name as a structtype
		// would overwrite the structtypedecl with the vardecl
		Scope newScope = new Scope(structScope);

		// visit vardecls declared in struct
		for (VarDecl vd : std.varDecls) {
			Symbol varSymbol = newScope.lookupCurrent(vd.varName);

			// this is because new vardecls need to be checked in the new scope
			if (varSymbol != null) error("Variable with name %s has already been declared in this scope\n", vd.varName);
			else newScope.put(new VarSymbol(vd));

			// but the type needs to be checked in the current scope
			vd.type.accept(this);
		}

		return null;
	}

	@Override
	// VarDecl ::= Type String
	public Void visitVarDecl(VarDecl vd) {
		// vd could be of type int, char, void, pointertype, arraytype or structtype
		// in the last case. we need to check if the structtype exists
		vd.type.accept(this);

		Symbol s = scope.lookupCurrent(vd.varName);
		if (s != null) error("Variable or function with name %s has already been declared in this scope\n", vd.varName);
		else scope.put(new VarSymbol(vd));
		return null;
	}

	@Override
	// FunDecl ::= Type String VarDecl* Block
	public Void visitFunDecl(FunDecl fd) {
		// fd could be of type int, char, void, pointertype, arraytype or structtype
		// in the last case. we need to check if the structtype exists
		fd.type.accept(this);

		Symbol s = scope.lookupCurrent(fd.name);
		if (s != null)  error("Variable or function with name %s has already been declared in this scope\n", fd.name);
		else scope.put(new FuncSymbol(fd));

		// visit the children
		visitFunDeclBody(fd);
		return null;
	}

	private void visitFunDeclBody(FunDecl fd) {
		Scope oldScope = scope;
		scope = new Scope(oldScope);

		// fundecls introduce a new scope
		// for both the function parameter identifiers
		for (VarDecl vd : fd.params) vd.accept(this);

		// and the block forming the body of the function
		for (VarDecl vd : fd.block.varDecls) vd.accept(this);
		for (Stmt stmt : fd.block.stmts) stmt.accept(this);

		scope = oldScope;
	}

	@Override
	// BaseType ::= INT | CHAR | VOID
	public Void visitBaseType(BaseType bt) {
		return null;
	}

	@Override
	// PointerType ::= Type
	public Void visitPointerType(PointerType pt) {
		pt.baseType.accept(this);
		return null;
	}

	@Override
	// StructType ::= String
	public Void visitStructType(StructType st) {
		// check the struct has been declared
		Symbol s = structScope.lookup(st.name);

		if (s == null) error("Struct with name %s has not been declared in this scope\n", st.name);
		else st.std = ((StructSymbol) s).std;

		return null;
	}

	@Override





	// ArrayType ::= Type int
	public Void visitArrayType(ArrayType at) {
		at.baseType.accept(this);
		return null;
	}

	@Override
	// IntLiteral ::= int
	public Void visitIntLiteral(IntLiteral il) {
		return null;
	}

	@Override
	// StrLiteral ::= String
	public Void visitStrLiteral(StrLiteral sl) {
		return null;
	}

	@Override
	// ChrLiteral ::= char
	public Void visitChrLiteral(ChrLiteral cl) {
		return null;
	}

	@Override
	// VarExpr ::= String
	public Void visitVarExpr(VarExpr ve) {
		// check the variable has been declared
		Symbol vs = scope.lookup(ve.name);

		if (vs == null) error("Variable %s has not been declared in this scope\n", ve.name);
		// vs could actually be a function
		else if (!vs.isVar()) error("%s is not a variable\n", ve.name);
		// record vardecl in varexpr ast node
		else ve.vd = ((VarSymbol) vs).vd;

		return null;
	}

	@Override
	// FunCallExpr ::= String Expr*
	public Void visitFunCallExpr(FunCallExpr fce) {
		// check the function has been declared
		Symbol fs = scope.lookup(fce.name);

		if (fs == null) error("Function %s has not been declared in this scope\n", fce.name);
		// fs could actually be a variable
		else if (!fs.isFunc()) error("%s is not a function\n", fce.name);
		// record funcdecl in funcall ast node
		else fce.fd = ((FuncSymbol) fs).fd;

		// check the arguments have been declared
		for (Expr e : fce.args) e.accept(this);
		return null;
	}

	@Override
	// BinOp ::= Expr Op Expr
	public Void visitBinOp(BinOp bo) {
		bo.lhs.accept(this);
		bo.rhs.accept(this);
		return null;
	}

	@Override
	// ArrayAccessExpr ::= Expr Expr
	public Void visitArrayAccessExpr(ArrayAccessExpr aae) {
		aae.arr.accept(this);
		aae.idx.accept(this);
		return null;
	}

	@Override
	// FieldAccessExpr ::= Expr String
	public Void visitFieldAccessExpr(FieldAccessExpr fae) {
		fae.struct.accept(this);
		return null;
	}

	@Override
	// ValueAtExpr ::= Expr
	public Void visitValueAtExpr(ValueAtExpr vae) {
		vae.expr.accept(this);
		return null;
	}

	@Override
	// SizeOfExpr ::= Type
	public Void visitSizeOfExpr(SizeOfExpr soe) {
		soe.sizeofType.accept(this);
		return null;
	}

	@Override
	// TypecastExpr ::= Type Expr
	public Void visitTypecastExpr(TypecastExpr tce) {
		tce.castType.accept(this);
		tce.expr.accept(this);
		return null;
	}

	@Override
	// Block ::= VarDecl* Stmt*
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
	// While ::= Expr Stmt
	public Void visitWhile(While w) {
		w.expr.accept(this);
		w.stmt.accept(this);
		return null;
	}

	@Override
	// If ::= Expr Stmt [Stmt]
	public Void visitIf(If i) {
		i.expr.accept(this);
		i.stmt1.accept(this);
		// stmt2 is optional so may be null
		if (i.stmt2 != null) i.stmt2.accept(this);
		return null;
	}

	@Override
	// Assign ::= Expr Expr
	public Void visitAssign(Assign a) {
		a.lhs.accept(this);
		a.rhs.accept(this);
		return null;
	}

	@Override
	// Return ::= [Expr]
	public Void visitReturn(Return r) {
		// expr may be null in the case of a void return
		if (r.expr != null) r.expr.accept(this);
		return null;
	}

	@Override
	// ExprStmt ::= Expr
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
