package sem;

import ast.*;

import javax.swing.plaf.basic.BasicEditorPaneUI;
import java.lang.reflect.Field;
import java.util.List;

public class TypeCheckVisitor extends BaseSemanticVisitor<Type> {

    Type currFuncReturnType = BaseType.VOID;

    // what does it mean for two types to be equal?
    public boolean eq(Type a, Type b) {
        // arraytypes are equal if they have the
        // same length and the same basetype
        if (a instanceof ArrayType && b instanceof ArrayType) {
            return ((ArrayType) a).size == ((ArrayType) b).size
                    && eq(((ArrayType) a).baseType, ((ArrayType) b).baseType);
        }

        // pointertypes are equal if they have the same basetype
        if (a instanceof PointerType && b instanceof PointerType) {
            return eq(((PointerType) a).baseType, ((PointerType) b).baseType);
        }

        // structtypes are equal if they have the name
        // as two structs cannot be defined with the same name
        // this is enforced in name checking
        if (a instanceof StructType && b instanceof StructType) {
            return ((StructType) a).name.equals(((StructType) b).name);
        }

        // basetype
        return a == b;
    }

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
    public Type visitStructTypeDecl(StructTypeDecl std) {
        for (VarDecl vd : std.varDecls) { vd.accept(this); }
        return null;
    }

    @Override
    public Type visitVarDecl(VarDecl vd) {
        if (eq(vd.type, BaseType.VOID)) { error("Variables cannot have type VOID"); }
        return null;
    }

    @Override
    public Type visitFunDecl(FunDecl p) {
        currFuncReturnType = p.type;
        for (VarDecl vd : p.params) { vd.accept(this); }
        p.block.accept(this);
        return null;
    }

    @Override
    public Type visitBaseType(BaseType bt) {
        return bt;
    }

    @Override
    public Type visitPointerType(PointerType pt) {
        return pt;
    }

    @Override
    public Type visitStructType(StructType st) {
        return st;
    }

    @Override
    public Type visitArrayType(ArrayType at) {
        return at;
    }

    @Override
    public Type visitIntLiteral(IntLiteral il) {
        return BaseType.INT;
    }

    @Override
    public Type visitStrLiteral(StrLiteral sl) {
        return new ArrayType(BaseType.CHAR, sl.s.length() + 1);
    }

    @Override
    public Type visitChrLiteral(ChrLiteral cl) {
        return BaseType.CHAR;
    }

    @Override
    public Type visitVarExpr(VarExpr v) {
        // return if the variable has not been declared
        if (v.vd == null) return new ErrorType();

        v.type = v.vd.type;
        return v.type;
    }

    @Override
    public Type visitFunCallExpr(FunCallExpr fce) {
        // return if the function has not been declared
        if (fce.fd == null) return new ErrorType();

        List<VarDecl> params = fce.fd.params;
        List<Expr> args = fce.args;

        if (params.size() != args.size()) {
            error("Function called with the wrong number of arguments");
            return new ErrorType();
        }

        for (int i = 0; i < params.size(); i++) {
            Type paramsT = params.get(i).type;
            Type argsT = args.get(i).accept(this);

            if (!eq(paramsT, argsT)) {
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
            if (!(lhsT instanceof StructType || lhsT instanceof ArrayType || eq(lhsT, BaseType.VOID)) && eq(lhsT,  rhsT)) {
                bo.type = BaseType.INT;
                return bo.type;
            } else {
                error("Operands must be of the same type and the LHS cannot be void, a struct or an array");
            }
        } else {
            // add, sub, mul, div, mod,
            // or, and, gt, lt, ge, le
            if (eq(lhsT, BaseType.INT) && eq(rhsT, BaseType.INT)) {
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

        if (arrType instanceof ArrayType && eq(idxType, BaseType.INT)) {
            aae.type = ((ArrayType) aae.arr.type).baseType;
            return aae.type;
        }

        if (arrType instanceof PointerType && eq(idxType, BaseType.INT)) {
            aae.type = ((PointerType) aae.arr.type).baseType;
            return aae.type;
        }

        error("Array access must be of array type or pointer type" +
                "and index must be of type int");
        return new ErrorType();
    }

    @Override
    public Type visitFieldAccessExpr(FieldAccessExpr fae) {
        Type structType = fae.struct.accept(this);

        if (structType instanceof StructType) {
            // loop through the struct fields
            // to find the field being accessed
            for (VarDecl vd : ((StructType) structType).std.varDecls) {
                if (fae.fieldName.equals(vd.varName)) {
                    fae.type = vd.type;
                    return fae.type;
                }
            }
        }

        // field does not exist
        error("Field name does not exist");
        return new ErrorType();
    }

    @Override
    public Type visitValueAtExpr(ValueAtExpr vae) {
        Type exprType = vae.expr.accept(this);

        if (!(exprType instanceof PointerType)) {
            error("Value at expression must have pointer type");
            return new ErrorType();
        }

        vae.type = ((PointerType) exprType).baseType;
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
        if (eq(exprType, BaseType.CHAR) && eq(castType, BaseType.INT)) {
            tce.expr.type = BaseType.INT;
            tce.type = tce.expr.type;
            return tce.type;
        }

        // array to pointer
        if (exprType instanceof ArrayType && castType instanceof PointerType
                && eq(((ArrayType) exprType).baseType, ((PointerType) castType).baseType)) {
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

        error("Invalid cast from " + exprType + " to " + castType);
        return new ErrorType();
    }

    @Override
    public Type visitBlock(Block b) {
        for (VarDecl vd : b.varDecls) { vd.accept(this); }
        for (Stmt stmt : b.stmts) { stmt.accept(this); }
        return null;
    }

    @Override
    public Type visitWhile(While w) {
        Type exprType = w.expr.accept(this);

        if (!eq(exprType, BaseType.INT)) {
            error("Condition must be of type int");
        }

        w.stmt.accept(this);
        return null;
    }

    @Override
    public Type visitIf(If i) {
        Type exprType = i.expr.accept(this);

        if (!eq(exprType, BaseType.INT)) {
            error("Condition must be of type int");
        }

        i.stmt1.accept(this);
        // stmt2 is optional so may be null in the case of no else
        if (i.stmt2 != null) i.stmt2.accept(this);

        return null;
    }

    @Override
    public Type visitAssign(Assign a) {
        Expr lhsExpr = a.lhs;

        if (!(lhsExpr instanceof VarExpr || lhsExpr instanceof FieldAccessExpr
                || lhsExpr instanceof ArrayAccessExpr || lhsExpr instanceof ValueAtExpr)) {
            error("LHS of the assignment must be VarExpr, FieldAccessExpr," +
                    "ArrayAccessExpr or ValueAtExpr");
        }

        Type lhsType = a.lhs.accept(this);
        Type rhsType = a.rhs.accept(this);

        if (eq(lhsType, BaseType.VOID) || lhsType instanceof ArrayType) {
            error("LHS cannot be of type void or array");
        }

        if (!eq(lhsType, rhsType)) { error ("LHS and RHS must be of the same type"); }

        return null;
    }

    @Override
    public Type visitReturn(Return r) {
        // no expr has a void return type
        Type exprType = BaseType.VOID;
        if (r.expr != null) exprType = r.expr.accept(this);

        if (!eq(exprType, currFuncReturnType)) error("Return type does not match function type");
        return exprType;
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
