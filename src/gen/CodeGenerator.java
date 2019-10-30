package gen;

import ast.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.EmptyStackException;
import java.util.Stack;

public class CodeGenerator implements ASTVisitor<Register> {

    /*
     * Simple register allocator.
     */

    // contains all the free temporary registers
    private Stack<Register> freeRegs = new Stack<Register>();

    public CodeGenerator() {
        freeRegs.addAll(Register.tmpRegs);
    }

    private class RegisterAllocationError extends Error {}

    private Register getRegister() {
        try {
            return freeRegs.pop();
        } catch (EmptyStackException ese) {
            throw new RegisterAllocationError(); // no more free registers, bad luck!
        }
    }

    private void freeRegister(Register reg) {
        freeRegs.push(reg);
    }


    private PrintWriter writer; // use this writer to output the assembly instructions
    private DataAllocation dataAlloc; // use this pass to allocate global and local variables

    public void emitProgram(Program program, File outputFile) throws FileNotFoundException {
        writer = new PrintWriter(outputFile);
        dataAlloc = new DataAllocation();

        dataAlloc.emitProgram(program, writer);

        writer.println(".text");
        writer.println("main:");

        visitProgram(program);

        writer.println("li $v0 10");
        writer.println("syscall");

        writer.close();
    }

    @Override
    // Program ::= StructTypeDecl* VarDecl* FunDecl*
    public Register visitProgram(Program p) {
        for (StructTypeDecl std : p.structTypeDecls) std.accept(this);
        for (VarDecl vd : p.varDecls) vd.accept(this);
        for (FunDecl fd : p.funDecls) fd.accept(this);
        return null;
    }

    @Override
    public Register visitStructTypeDecl(StructTypeDecl st) {
        return null;
    }

    @Override
    public Register visitVarDecl(VarDecl vd) {
        // TODO: to complete
        return null;
    }

    @Override
    public Register visitFunDecl(FunDecl p) {
        // TODO: to complete
        return null;
    }

    // TYPES
    @Override
    public Register visitBaseType(BaseType bt) {
        return null;
    }

    @Override
    public Register visitPointerType(PointerType pt) {
        return null;
    }

    @Override
    public Register visitStructType(StructType st) {
        return null;
    }

    @Override
    public Register visitArrayType(ArrayType at) {
        return null;
    }

    // EXPR
    @Override
    public Register visitIntLiteral(IntLiteral il) {
        Register reg = getRegister();
        writer.printf("li %s,%s\n", reg, il.i);
        return reg;
    }

    @Override
    public Register visitStrLiteral(StrLiteral sl) {
        // do this in a data pass
//        Register reg = getRegister();
//        writer.printf("li %s %s\n", reg, sl.s);
//        return reg;
        return null;
    }

    @Override
    public Register visitChrLiteral(ChrLiteral cl) {
        Register reg = getRegister();
        writer.printf("li %s,'%s'\n", reg, cl.c);
        return reg;
    }

    @Override
    public Register visitVarExpr(VarExpr v) {
        // TODO: to complete
        // the variable will be stored at a memory address
        // then want to load the address
        // then get the result and return
        // see slides
        return null;
    }

    @Override
    public Register visitFunCallExpr(FunCallExpr fce) {
//        List<Register> argRegisters = new ArrayList<>();
//
//        for (Expr arg : fce.args) {
//            // null pointer if expr returns null
//            argRegisters.add(arg.accept(this));
//        }
//
//        if (fce.name.equals("print_i")) {
//            for (Register argRegister : argRegisters) {
//                writer.println("li $v0 1");
//                writer.println("mv $a0 " + argRegister);
//                writer.println("syscall");
//
//                freeRegister(argRegister);
//            }
//
//        }
//
//        writer.println("");
        return null;
    }

    @Override
    public Register visitBinOp(BinOp bo) {
        Register lhsReg = bo.lhs.accept(this);
        Register rhsReg = bo.rhs.accept(this);
        Register resultReg = getRegister();

        switch(bo.op) {
            case ADD:
                writer.printf("add %s %s %s\n", resultReg, lhsReg, rhsReg);
                break;
            case SUB:
                writer.printf("sub %s %s %s\n", resultReg, lhsReg, rhsReg);
                break;
            case MUL:
                writer.printf("mul %s,%s,%s\n", resultReg, lhsReg, rhsReg);
                break;
            case DIV:
                writer.printf("div %s,%s\n", lhsReg, rhsReg);
                writer.printf("mflo %s\n", resultReg);
                break;
            case MOD:
                writer.printf("div %s,%s\n", lhsReg, rhsReg);
                writer.printf("mfhi %s\n", resultReg);
                break;
            case GT:
                writer.printf("sgt %s,%s,gt\n", resultReg, lhsReg, rhsReg);
                break;
            case LT:
                writer.printf("slt %s,%s,gt\n", resultReg, lhsReg, rhsReg);
                break;
            case GE:
                writer.printf("sge %s,%s,gt\n", resultReg, lhsReg, rhsReg);
                break;
            case LE:
                writer.printf("sle %s,%s,gt\n", resultReg, lhsReg, rhsReg);
                break;
            case NE:
                writer.printf("sne %s,%s,gt\n", resultReg, lhsReg, rhsReg);
                break;
            case EQ:
                writer.printf("seq %s,%s,gt\n", resultReg, lhsReg, rhsReg);
                break;
            case OR:
                writer.printf("or %s,%s,%s\n", resultReg, lhsReg, rhsReg);
                break;
            case AND:
                writer.printf("and %s,%s,%s\n", resultReg, lhsReg, rhsReg);
        }

        freeRegister(lhsReg);
        freeRegister(rhsReg);
        return resultReg;
    }

    @Override
    public Register visitArrayAccessExpr(ArrayAccessExpr aae) {
        return null;
    }

    @Override
    public Register visitFieldAccessExpr(FieldAccessExpr fae) {
        return null;
    }

    @Override
    public Register visitValueAtExpr(ValueAtExpr vae) {
        return null;
    }

    @Override
    public Register visitSizeOfExpr(SizeOfExpr soe) {
        return null;
    }

    @Override
    public Register visitTypecastExpr(TypecastExpr tce) {
        return null;
    }

    // STMT
    @Override
    public Register visitBlock(Block b) {
        // TODO: to complete
        return null;
    }

    @Override
    public Register visitWhile(While w) {
        return null;
    }

    @Override
    public Register visitIf(If i) {
        return null;
    }

    @Override
    public Register visitAssign(Assign a) {
        return null;
    }

    @Override
    public Register visitReturn(Return r) {
        return null;
    }

    @Override
    public Register visitExprStmt(ExprStmt es) {
        return null;
    }

    @Override
    public Register visitErrorType(ErrorType et) {
        return null;
    }

    // HELPER FUNCTIONS
    public void print_i(Register argRegister) {
        writer.println("li $v0 1");
        writer.printf("move $a0 %s\n", argRegister.toString());
        writer.println("syscall");
        freeRegister(argRegister);
    }
}
