package gen;

import ast.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class CodeGenerator implements ASTVisitor<Register> {

    /*
     * Simple register allocator.
     */

    // contains all the free temporary registers
    private Stack<Register> freeRegs = new Stack<Register>();
    private int currentOffset;       // tracks how much offset from the frame pointer is
                                    // needed for the local variables encountered so far

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
        // done in the data allocation pass
        //for (VarDecl vd : p.varDecls) vd.accept(this);
        for (FunDecl fd : p.funDecls) fd.accept(this);
        return null;
    }

    @Override
    public Register visitStructTypeDecl(StructTypeDecl st) {
        return null;
    }

    @Override
    public Register visitVarDecl(VarDecl vd) {
        vd.offset = currentOffset;
        // increment offset by size of the type for the next vardecl
        currentOffset -= dataAlloc.makeMultipleFour(dataAlloc.getTypeSize(vd.type));
        return null;
    }

    @Override
    // FunDecl ::= Type String VarDecl* Block
    public Register visitFunDecl(FunDecl p) {

        // TODO: maybe do this in funcall
        // initialise fp to the value of sp
        writer.println("move $fp,$sp");
        currentOffset = 0;

        // TODO: do something with the params - will need to edit varexpr
        // TODO: to to account for this

        // allocate local variables
        p.block.accept(this);

        // move the stack pointer ($sp) by an offset corresponding to the
        // size of all the local variables declared on the stack
        writer.printf("addi $sp,$sp,%s\n", currentOffset);
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
        // TODO: do this in a data pass
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
    // VarExpr ::= String
    public Register visitVarExpr(VarExpr v) {
        Register valueReg = getRegister();

        // v may be a local or a global variable
        if (v.vd.isGlobal) {
            // variable is stored at the address corresponding
            // to the label in the data directive
            writer.printf("lw %s,%s\n", valueReg, v.name);
        } else {
            // variable is stored on the stack at an offset from the fp
            writer.printf("lw %s,%s($fp)\n", valueReg, v.vd.offset);
        }

        return valueReg;
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
            // TODO: maybe change to positional encoding
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
                // short circuit evaluation
                // if lhs is true, whole expr is true
                writer.printf("bnez %s,or_true\n", lhsReg);
                // if rhs is true, whole expr is true
                writer.printf("bnez %s,or_true\n", rhsReg);
                // if neither true, the statement is false
                writer.printf("b or_false\n");

                // code for what to in the case of true and false
                writer.println("or_true:");
                // set result to 1 and finish
                writer.printf("li %s,1\n", resultReg);
                writer.printf("b or_end");

                writer.println("or_false:");
                // set result to 0 and finish
                writer.printf("li %s,0\n", resultReg);

                writer.println("or_end:");
                break;
            case AND:
                // short circuit evaluation
                // if lhs is false, whole expr is false
                writer.printf("beqz %s,and_false\n", lhsReg);
                // if rhs is false, jump to false
                writer.printf("beqz %s,and_false\n", rhsReg);
                // if neither false, the statement is true
                writer.printf("b and_true\n");

                // code for what to in the case of true and false
                writer.println("and_true:");
                // set result to 1 and finish
                writer.printf("li %s,1\n", resultReg);
                writer.printf("b and_end");

                writer.println("and_false:");
                // set result to 0 and finish
                writer.printf("li %s,0\n", resultReg);

                writer.println("and_end:");
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
    // SizeOfExpr ::= Type
    public Register visitSizeOfExpr(SizeOfExpr soe) {
        Register result = getRegister();
        int size = dataAlloc.getTypeSize(soe.sizeofType);
        writer.printf("li %s,%s\n", result, size);
        return result;
    }

    @Override
    public Register visitTypecastExpr(TypecastExpr tce) {
        return null;
    }

    // STMT
    @Override
    // Block ::= VarDecl* Stmt*
    public Register visitBlock(Block b) {

        for (VarDecl vd : b.varDecls) { vd.accept(this); }

        // TODO: what about stmts?
        for (Stmt stmt : b.stmts) { stmt.accept(this); }
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
        Register rhsReg = a.rhs.accept(this);

        // store the result in the variable
        if (a.lhs instanceof VarExpr) {
            VarExpr v = ((VarExpr) a.lhs);

            if (v.vd.isGlobal) writer.printf("sw %s,%s\n", rhsReg, v.name);
            else writer.printf("sw %s,%s($fp)\n", rhsReg, v.vd.offset);
        }

        // TODO - what if lhs is not a varexpr?
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
        // TODO: where is this called?
        writer.println("li $v0 1");
        writer.printf("move $a0 %s\n", argRegister.toString());
        writer.println("syscall");
        freeRegister(argRegister);
    }
}
