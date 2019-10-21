package sem;

import ast.*;

import javax.swing.plaf.basic.BasicEditorPaneUI;
import java.util.List;

public class TypeCheckVisitor extends BaseSemanticVisitor<Type> {

	@Override
	public Type visitProgram(Program p) {
		for (StructTypeDecl std : p.structTypeDecls) std.accept(this);
		for (VarDecl vd : p.varDecls) vd.accept(this);
		for (FunDecl fd : p.funDecls) fd.accept(this);

		// do not forget to set types  !!
		// every expr should have a type
		return null;
	}

	@Override
	public Type visitStructTypeDecl(StructTypeDecl sts) {
		// To be completed...
		return null;
	}

	@Override
	public Type visitVarDecl(VarDecl vd) {
		if (vd.type == BaseType.VOID) {
			error("Variables cannot have type VOID");
		}

		// only expressions have types, vardecl is a statement
		return null;
	}

	@Override
	public Type visitFunDecl(FunDecl p) {

		for (VarDecl vd : p.params) { vd.accept(this); }

		Type returnType = p.block.accept(this);
		//if (returnType != p.type) {
		//	error("Return statement does not match return type");
	//		return new ErrorType();
	//	}
		// TODO check against return type

		return null;
	}

	@Override
	public Type visitBaseType(BaseType bt) {
		// To be completed...
		return null;
	}

	@Override
	public Type visitPointerType(PointerType pt) {
		// To be completed...
		return null;
	}

	@Override
	public Type visitStructType(StructType st) {
		// To be completed...
		return null;
	}

	@Override
	public Type visitArrayType(ArrayType at) {
		// To be completed...
		return null;
	}

	@Override
	public Type visitIntLiteral(IntLiteral il) {
		return BaseType.INT;
	}

	@Override
	public Type visitStrLiteral(StrLiteral sl) {
		return new ArrayType(BaseType.CHAR, sl.s.length()+1);
	}

	@Override
	public Type visitChrLiteral(ChrLiteral cl) {
		return BaseType.CHAR;
	}

	@Override
	public Type visitVarExpr(VarExpr v) {
		// the type of the variable is the type of the declaration
		v.type = v.vd.type;
		return v.type;
	}

	@Override
	public Type visitFunCallExpr(FunCallExpr fce) {
		List<VarDecl> params = fce.fd.params;
		List<Expr> args = fce.args;

		if (params.size() != args.size()) {
			error("Function called with the wrong number of arguments");
		}

		for (int i = 0; i < params.size(); i++) {
			Type paramsT = params.get(i).type;
			Type argsT = args.get(i).accept(this);

			if (paramsT != argsT) {
				error("Parameter and argument types do not match");
				return new ErrorType();
			}
		}

		fce.type = fce.fd.type;
		return fce.type;
	}

	@Override
	public Type visitBinOp(BinOp bo) {
		Type lhsT = bo.lhs.accept(this);
		Type rhsT = bo.rhs.accept(this);

		if (bo.op == Op.NE || bo.op == Op.EQ) {
			if (!(lhsT instanceof StructType || lhsT instanceof ArrayType || lhsT == BaseType.VOID)
					&& lhsT.getClass() == rhsT.getClass()) {
				bo.type = lhsT;
				return bo.type;
			} else {
				error("Operands must be of the same type and the LHS cannot be void, a struct or an array");
			}
		} else {
			// add, sub, mul, div, mod,
			// or, and, gt, lt, ge, le
			if (lhsT == BaseType.INT && rhsT == BaseType.INT) {
				bo.type = BaseType.INT;
				return bo.type;
			} else {
				error("Addition requires two integer operands");
			}
		}

		return new ErrorType();
	}

	@Override
	public Type visitArrayAccessExpr(ArrayAccessExpr aae) {
		// do we support arrays of arrays, pointers to pointers, structs of structs?
		Type arrType = aae.arr.accept(this);
		Type idxType = aae.idx.accept(this);

		if ((arrType instanceof ArrayType || arrType instanceof PointerType)
					&& idxType == BaseType.INT) {
			aae.type = aae.arr.type;
			return aae.type;
		}

		error("Array access must be of array type or pointer type" +
				"and index must be of type int");
		return new ErrorType();
	}

	@Override
	public Type visitFieldAccessExpr(FieldAccessExpr fae) {
		// TODO
		return null;
	}

	@Override
	public Type visitValueAtExpr(ValueAtExpr vae) {
		Type exprType = vae.expr.accept(this);

		if (!(exprType instanceof PointerType)) {
			error("Value at expression must have pointer type");
			return new ErrorType();
		}

		vae.type = exprType;
 		return vae.type;
	}

	@Override
	public Type visitSizeOfExpr(SizeOfExpr soe) {
		soe.type = BaseType.INT;
		return soe.type;
	}

	@Override
	public Type visitTypecastExpr(TypecastExpr tce) {
		Type exprType = tce.expr.accept(this);
		Type castType = tce.castType;

		// char to int
		if (exprType == BaseType.CHAR && castType == BaseType.INT) {
			tce.expr.type = BaseType.INT;
			tce.type = tce.expr.type;
			return tce.type;
		}

		// array to pointer
		if (exprType instanceof ArrayType && castType instanceof PointerType) {
			tce.expr.type = new PointerType(((ArrayType) exprType).baseType);
			tce.type = tce.expr.type;
			return tce.type;
		}

		// pointer to pointer
		if (exprType instanceof PointerType && castType instanceof PointerType) {
			tce.expr.type = new PointerType(((PointerType) castType).baseType);
			tce.type = tce.expr.type;
			return tce.type;
		}

		return new ErrorType();
	}

	@Override
	public Type visitBlock(Block b) {
		Type returnType = null;

		for (VarDecl vd : b.varDecls) { vd.accept(this); }
		for (Stmt stmt : b.stmts) {
			if (stmt instanceof Return) {
				returnType = stmt.accept(this);
			} else stmt.accept(this);
		}

		// TODO how to return the return type
		// what about nesting?
		// null if no return
		return returnType;
	}

	@Override
	public Type visitWhile(While w) {
		Type exprType = w.expr.accept(this);

		if (exprType != BaseType.INT) {
			error("Condition must be of type int");
			return new ErrorType();
		}

		return null;
	}

	@Override
	public Type visitIf(If i) {
		// TODO what if expr2 is null
		// TODO if no else if else distinction?

		Type exprType = i.expr.accept(this);

		// no else
		if (exprType != BaseType.INT) {
			error("Condition must be of type int");
			return new ErrorType();
		}

		return null;
	}

	@Override
	public Type visitAssign(Assign a) {
		Type lhsType = a.lhs.accept(this);
		Type rhsType = a.rhs.accept(this);

		if (lhsType == BaseType.VOID || lhsType instanceof ArrayType
						|| lhsType.getClass() != rhsType.getClass()) {
			error("LHS and RHS must be of the same type and" +
					"cannot be of type void or array");
		}

		return null;
	}

	@Override
	public Type visitReturn(Return r) {

		if (r.expr != null) {
			Type exprType = r.expr.accept(this);
			return exprType;
		} else return BaseType.VOID; // should be return void
	}

	@Override
	public Type visitExprStmt(ExprStmt es) {
		es.expr.accept(this);
		return null;
	}

	@Override
	public Type visitErrorType(ErrorType et) {
		return null;
	}


}
