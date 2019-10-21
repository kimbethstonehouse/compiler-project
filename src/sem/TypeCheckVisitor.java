package sem;

import ast.*;

import java.util.List;

public class TypeCheckVisitor extends BaseSemanticVisitor<Type> {

	@Override
	public Type visitProgram(Program p) {
		// To be completed...
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
		// To be completed...
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
		// To be completed...
		return null;
	}

	@Override
	public Type visitStrLiteral(StrLiteral sl) {
		// To be completed...
		return null;
	}

	@Override
	public Type visitChrLiteral(ChrLiteral cl) {
		// To be completed...
		return null;
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
		// To be completed...
		// do we support arrays of arrays, pointers to pointers, structs of structs?
		return null;
	}

	@Override
	public Type visitFieldAccessExpr(FieldAccessExpr fae) {
		// To be completed...
		return null;
	}

	@Override
	public Type visitValueAtExpr(ValueAtExpr vae) {
		// To be completed...
		return null;
	}

	@Override
	public Type visitSizeOfExpr(SizeOfExpr soe) {
		// To be completed...
		return null;
	}

	@Override
	public Type visitTypecastExpr(TypecastExpr tce) {
		// To be completed...
		return null;
	}

	@Override
	public Type visitBlock(Block b) {
		// To be completed...
		return null;
	}

	@Override
	public Type visitWhile(While w) {
		// To be completed...
		return null;
	}

	@Override
	public Type visitIf(If i) {
		// To be completed...
		return null;
	}

	@Override
	public Type visitAssign(Assign a) {
		// To be completed...
		return null;
	}

	@Override
	public Type visitReturn(Return r) {
		// To be completed...
		return null;
	}

	@Override
	public Type visitExprStmt(ExprStmt es) {
		// To be completed...
		return null;
	}

	@Override
	public Type visitErrorType(ErrorType et) {
		return null;
	}


}
