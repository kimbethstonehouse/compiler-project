package gen;

import ast.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
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
    private int numWhiles = 0;       // number of while loops or if statements encountered
    private int numIfs = 0;          // so far, used for generating unique labels
    private String currentFuncName;  // the name of the function currently in

    public CodeGenerator() {
        freeRegs.addAll(Register.tmpRegs);
    }

    private class RegisterAllocationError extends Error {
    }

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
        writer.println();

        visitProgram(program);
        writer.println();

        //writer.println("li $v0 10");
        //writer.println("syscall");
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
        // increment offset by size of the type
        currentOffset -= dataAlloc.getAlignedTypeSize(vd.type);
        vd.offset = currentOffset;
        return null;
    }

    @Override
    // FunDecl ::= Type String VarDecl* Block
    public Register visitFunDecl(FunDecl p) {
        writer.println();
        writer.println("### entering visit fundecl");
        currentFuncName = p.name;

        if (p.name.equals("main")) {
            writer.println(".globl main");
            writer.println("main:");
        } else writer.printf("func_%s_start:\n", p.name);

        // initialise fp to the value of sp
        writer.println("move $fp,$sp");
        int paramOffset = 0;
        currentOffset = 0;

        // iterate through the parameters in reverse order
        for (int i = p.params.size() - 1; i >= 0; i--) {
            VarDecl vd = p.params.get(i);

            vd.offset = paramOffset;
            // increment offset by size of the type for the next vardecl
            paramOffset += dataAlloc.getAlignedTypeSize(vd.type);
        }

//        for (VarDecl vd : p.params) {
//            vd.offset = paramOffset;
//            // increment offset by size of the type for the next vardecl
//            paramOffset += dataAlloc.getAlignedTypeSize(vd.type);
//        }

        // allocate local variables
        p.block.accept(this);

        // push return

        // end
        writer.printf("func_%s_end:\n", p.name);
        // deallocate local variables
        writer.printf("subi $sp,$sp,%s\n", currentOffset);

        if (p.name.equals("main")) {
            writer.println("li $v0 10");
            writer.println("syscall");
        } else writer.println("jr $ra");
        return null;
    }

    // TYPES
    @Override
    public Register visitBaseType(BaseType bt) {
        return null;
    }

    @Override
    public Register visitPointerType(PointerType pt) {
        // TODO return address for array access expr - maybe this is
        // TODO handled in varexpr and maybe it doesnt even make sense
        return null;
    }

    @Override
    public Register visitStructType(StructType st) {
        return null;
    }

    @Override
    public Register visitArrayType(ArrayType at) {

        // TODO-maybe covered in varexpr, return address for array access expr
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
    // TODO: is this right?
    public Register visitStrLiteral(StrLiteral sl) {
        // returns the address of the string
        Register reg = getRegister();
        writer.printf("la %s %s\n", reg, sl.label);
        return reg;
    }

    @Override
    public Register visitChrLiteral(ChrLiteral cl) {
        Register reg = getRegister();
        if (cl.isEscape) writer.printf("li %s,'\\%s'\n", reg, cl.c);
        else writer.printf("li %s,'%s'\n", reg, cl.c);
        return reg;
    }

    @Override
    // VarExpr ::= String
    public Register visitVarExpr(VarExpr v) {
        Register valueReg = getRegister();
        String loadInstr = "";

        // return the address in these cases
        if (v.vd.type instanceof ArrayType || v.vd.type instanceof StructType) loadInstr = "la";
        else loadInstr = "lw";

        // v may be a local or a global variable
        if (v.vd.isGlobal) {
            writer.printf("%s %s,%s\n", loadInstr, valueReg, v.name);
        } else {
            // variable is stored on the stack at an offset from the fp
            writer.printf("%s %s,%s($fp)\n", loadInstr, valueReg, v.vd.offset);
        }

        return valueReg;
    }

    @Override
    // TODO: stack isn't being returned to the order it was in before call
    // something is not being allocated or deallocated correctly
    // check funcall.fundecl and local block allocations
    public Register visitFunCallExpr(FunCallExpr fce) {
        writer.println();
        writer.println("### entering visit funcall expr");

        // library functions
        if (fce.name.equals("print_s")) {
            print_s(fce.args.get(0).accept(this));
            return null;
        } else if (fce.name.equals("print_i")) {
            print_i(fce.args.get(0).accept(this));
            return null;
        } else if(fce.name.equals("print_c")) {
            print_c(fce.args.get(0).accept(this));
            return null;
        } else if (fce.name.equals("read_c")) {
            return read_c();
        } else if (fce.name.equals("read_i")) {
            return read_i();
        } else if (fce.name.equals("mcmalloc")) {
            return mcmalloc(fce.args.get(0).accept(this));
        }

        Register returnReg = getRegister();
        Register argReg;
        int argsSpace = 0;

        // 1. PRECALL

        // push fp
        writer.println("addi $sp,$sp,-4");
        writer.println("sw $fp,0($sp)");

        // push ra
        writer.println("addi $sp,$sp,-4");
        writer.println("sw $ra,0($sp)");

        // TODO: push caller saves

        // push all args onto stack
        for (Expr arg : fce.args) {
            // if the arg is an int, pointer or char the register
            // will store the value, else it will store the address
            // TODO: what if expr returns null?
            argReg = arg.accept(this);

            // the register stores the address of arrays or structs
            if (arg.type instanceof ArrayType || arg.type instanceof StructType) {
                Register temp = getRegister();
                int size;

                if (arg.type instanceof ArrayType) size = ((ArrayType) arg.type).size;
                else size = ((StructType) arg.type).std.structSize;

                // so they are copied to the stack word by word
                for (int offset = 0; offset <= size; offset += 4) {
                    writer.printf("lw %s,%s(%s)\n", temp, offset, argReg);
                    writer.println("addi $sp,$sp,-4");
                    writer.printf("sw %s,0($sp)\n", temp);
                }

                argsSpace += size;
                freeRegister(temp);
            } else {
                // the register stores the value of ints, pointers and chars
                writer.println("addi $sp,$sp,-4");
                writer.printf("sw %s,0($sp)\n", argReg);
                argsSpace += 4;
            }

            freeRegister(argReg);
        }

        // callee
        writer.printf("jal func_%s_start\n", fce.name);

        // pop return
        // TODO: if return is larger than a reg, returning an address - is this correct?
        if (fce.type instanceof ArrayType || fce.type instanceof StructType) {
            // return the address of the array or struct on the stack
            writer.printf("mv %s,$sp\n", returnReg);
            // TODO: in this case, the ra fp will be in the wrong place
        } else if (fce.type == BaseType.CHAR || fce.type == BaseType.INT
                || fce.type instanceof PointerType) {
            // load the value and decrement the stack pointer
            writer.printf("lw %s,0($sp)\n", returnReg);
            writer.println("subi $sp,$sp,-4");
        }

        // pop args
        writer.printf("addi $sp,$sp,%s\n", argsSpace);

        // TODO: pop caller saves

        // pop ra
        writer.println("lw $ra,0($sp)");
        writer.println("subi $sp,$sp,-4");

        // pop fp
        writer.println("lw $fp,0($sp)");
        writer.println("subi $sp,$sp,-4");

        return returnReg;
    }

    @Override
    // BinOp ::= Expr Op Expr
    public Register visitBinOp(BinOp bo) {
        Register lhsReg = bo.lhs.accept(this);
        Register rhsReg = bo.rhs.accept(this);
        Register resultReg = getRegister();

        switch (bo.op) {
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
                writer.printf("sgt %s,%s,%s\n", resultReg, lhsReg, rhsReg);
                break;
            case LT:
                writer.printf("slt %s,%s,%s\n", resultReg, lhsReg, rhsReg);
                break;
            case GE:
                writer.printf("sge %s,%s,%s\n", resultReg, lhsReg, rhsReg);
                break;
            case LE:
                writer.printf("sle %s,%s,%s\n", resultReg, lhsReg, rhsReg);
                break;
            case NE:
                writer.printf("sne %s,%s,%s\n", resultReg, lhsReg, rhsReg);
                break;
            case EQ:
                writer.printf("seq %s,%s,%s\n", resultReg, lhsReg, rhsReg);
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

                writer.println();
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

                writer.println();
                writer.println("and_end:");
        }

        freeRegister(lhsReg);
        freeRegister(rhsReg);
        return resultReg;
    }

    @Override
    // TODO: check this
    public Register visitArrayAccessExpr(ArrayAccessExpr aae) {
        writer.println();
        String loadInstruct;
        Type baseType;

        Register addrReg = getArrayAccessAddress(aae);
        Register resultReg = getRegister();

        // arr can be either an array or a pointer
        if (aae.arr.type instanceof ArrayType) baseType = (((ArrayType) aae.arr.type).baseType);
        else baseType = (((PointerType) aae.arr.type).baseType);

        if (baseType == BaseType.CHAR) loadInstruct = "lb";
        else loadInstruct = "lw";

        writer.printf("%s %s,(%s)\n", loadInstruct, resultReg, addrReg);

        freeRegister(addrReg);
        writer.println();
        return resultReg;
    }

    @Override
    // TODO: check this
    public Register visitFieldAccessExpr(FieldAccessExpr fae) {
        writer.println();

        Register fieldAddress = getFieldAccessAddress(fae);
        Register resultReg = getRegister();

        writer.printf("lw %s,(%s)\n", resultReg, fieldAddress);

        freeRegister(fieldAddress);
        writer.println();
        return resultReg;
    }

    @Override
    // ValueAtExpr ::= Expr
    public Register visitValueAtExpr(ValueAtExpr vae) {
        Register valueReg = getRegister();
        Register addrReg = vae.expr.accept(this);
        writer.printf("lw %s,0(%s)\n", valueReg, addrReg);
        freeRegister(addrReg);
        return valueReg;
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
    // TypecastExpr ::= Type Expr
    public Register visitTypecastExpr(TypecastExpr tce) {
        // mips doesn't care about the type of something
        // this has been handled in the typechecking
        // so just return the relevant value or address
        return tce.expr.accept(this);
    }

    // STMT
    @Override
    // Block ::= VarDecl* Stmt*
    public Register visitBlock(Block b) {
        // vardecl allocates space for all variables so current offset will be changed
        int startingOffset = currentOffset;

        // allocate space for local variables
        for (VarDecl vd : b.varDecls) { vd.accept(this); }

        // move stack pointer before any variables are used in stmts
        // ($sp) is moved by an offset corresponding to the size of
        // all the local variables declared on the stack in this block
        int blockOffset = currentOffset - startingOffset;
        writer.printf("addi $sp,$sp,%s\n", blockOffset);

        for (Stmt stmt : b.stmts) { stmt.accept(this); }

//        // move sp back after variable scope ends
//        writer.printf("subi $sp,$sp,%s\n", blockOffset);
//        currentOffset -= blockOffset;
        return null;
    }

    @Override
    public Register visitWhile(While w) {
        // CONTROL FLOW
        // PRE TEST
        // 1. evaluate condition
        // 2. skip loop if not true

        // LOOP BODY
        // 3. execute body of loop
        // 4. reevaluate condition

        // POST TEST
        // 5. end if not true
        // 6. or jump back to body

        writer.println();
        writer.println("### entering visit while");
        int n = numWhiles;
        numWhiles++;

        // 1. evaluate condition
        Register expReg = w.expr.accept(this);
        // 2. skip loop if not true
        writer.printf("beqz %s,while_end%s\n", expReg, n);
        // no longer needed, condition has been evaluated
        freeRegister(expReg);

        // 3. execute body of loop
        writer.printf("while_body%s:\n", n);
        w.stmt.accept(this);
        // 4. reevaluate condition
        expReg = w.expr.accept(this);

        // 5. end if not true
        writer.printf("beqz %s,while_end%s\n", expReg, n);
        freeRegister(expReg);
        // 6. or jump back to body
        writer.printf("j while_body%s\n", n);

        writer.printf("while_end%s:\n", n);
        // increment number of while loops encountered so far
        writer.println();
        return null;
    }

    @Override
    public Register visitIf(If i) {
        // CONTROL FLOW
        // 1. evaluate condition
        // 2. go to else if not true
        // 3. execute stmt if true and end
        // 4. execute else
        // 5. end

        writer.println();
        writer.println("### entering visit if");
        int n = numIfs;
        numIfs++;

        // 1. evaluate condition
        Register expReg = i.expr.accept(this);
        // 2. go to else if not true
        writer.printf("beqz %s,if_else%s\n", expReg, n);
        freeRegister(expReg);

        // 3. execute stmt if true and end
        i.stmt1.accept(this);
        writer.printf("j if_end%s\n", n);
        writer.printf("if_else%s:\n", n);

        // 4. execute else
        if (i.stmt2 != null) {
            i.stmt2.accept(this);
        }

        // 5. end
        writer.printf("if_end%s:\n", n);
        return null;
    }

    @Override
    // TODO: check this
    public Register visitAssign(Assign a) {
        Register rhsReg = a.rhs.accept(this);

        // store the result in the variable
        if (a.lhs instanceof VarExpr) {
            VarExpr v = ((VarExpr) a.lhs);

            if (v.vd.isGlobal) writer.printf("sw %s,%s\n", rhsReg, v.name);
            else writer.printf("sw %s,%s($fp)\n", rhsReg, v.vd.offset);
        // store the result at the address the pointer points to
        } else if (a.lhs instanceof ValueAtExpr) {
            ValueAtExpr vae = ((ValueAtExpr) a.lhs);

            Register addrReg = vae.expr.accept(this);
            writer.printf("sw %s,0(%s)\n", rhsReg, addrReg);
            freeRegister(addrReg);
        } else if (a.lhs instanceof ArrayAccessExpr) {
            ArrayAccessExpr aae = ((ArrayAccessExpr) a.lhs);

            String storeInstruct;
            Type baseType;

            Register addrReg = getArrayAccessAddress(aae);

            // arr can be either an array or a pointer
            if (aae.arr.type instanceof ArrayType) baseType = (((ArrayType) aae.arr.type).baseType);
            else baseType = (((PointerType) aae.arr.type).baseType);

            if (baseType == BaseType.CHAR) storeInstruct = "sb";
            else storeInstruct = "sw";

            writer.printf("%s %s,(%s)\n", storeInstruct, rhsReg, addrReg);
            freeRegister(addrReg);
        } else if (a.lhs instanceof FieldAccessExpr) {
            FieldAccessExpr fae = ((FieldAccessExpr) a.lhs);
            Register addrReg = getFieldAccessAddress(fae);
            writer.printf("sw %s,(%s)\n", rhsReg, addrReg);
            freeRegister(addrReg);
        }

        freeRegister(rhsReg);
        return null;
    }

    @Override
    public Register visitReturn(Return r) {
        if (r.expr != null) {
            // evaluate return value
            Register expReg = r.expr.accept(this);

            // deallocate local variables
            writer.printf("subi $sp,$sp,%s\n", currentOffset);

            // the register stores the address of arrays or structs
            if (r.expr.type instanceof ArrayType || r.expr.type instanceof StructType) {
                Register temp = getRegister();
                int size;

                if (r.expr.type instanceof ArrayType) size = ((ArrayType) r.expr.type).size;
                else size = ((StructType) r.expr.type).std.structSize;

                // so they are copied to the stack word by word
                for (int offset = 0; offset <= size; offset += 4) {
                    writer.printf("lw %s,%s(%s)\n", temp, offset, expReg);
                    writer.println("addi $sp,$sp,-4");
                    writer.printf("sw %s,0($sp)\n", temp);
                }

                freeRegister(temp);
            } else {
                // the register stores the value of ints, pointers and chars
                writer.println("addi $sp,$sp,-4");
                writer.printf("sw %s,0($sp)\n", expReg);
            }

            freeRegister(expReg);
        } else {
            // deallocate local variables
            writer.printf("subi $sp,$sp,%s\n", currentOffset);
        }

        if (currentFuncName.equals("main")) {
            writer.println("li $v0,10");
            writer.println("syscall");
        } else writer.println("jr $ra");
        return null;
    }

    @Override
    public Register visitExprStmt(ExprStmt es) {
        es.expr.accept(this);
        return null;
    }

    @Override
    public Register visitErrorType(ErrorType et) {
        return null;
    }

    // the register contains the integer

    // HELPER FUNCTIONS
    // void print_s(const char* s)
    private void print_s(Register addrRegister) {
        // the register contains the address of the string
        writer.println("li $v0,4");
        writer.printf("move $a0,%s\n", addrRegister);
        writer.println("syscall");
        freeRegister(addrRegister);
    }

    // void print_i(int i)
    private void print_i(Register argRegister) {
        writer.println("li $v0,1");
        writer.printf("move $a0,%s\n", argRegister);
        writer.println("syscall");
        freeRegister(argRegister);
    }

    // void print_c(char c)
    private void print_c(Register argRegister) {
        // the register contains the character
        writer.println("li $v0,11");
        writer.printf("move $a0,%s\n", argRegister);
        writer.println("syscall");
        freeRegister(argRegister);
    }

    // char read_c()
    private Register read_c() {
        Register result = getRegister();
        writer.println("li $v0,12");
        writer.println("syscall");
        writer.printf("move %s,$v0\n", result);
        return result;
    }

    // int read_i()
    private Register read_i() {
        Register result = getRegister();
        writer.println("li $v0,5");
        writer.println("syscall");
        writer.printf("move %s,$v0\n", result);
        return result;
    }

    // void* mcmalloc(int size)
    private Register mcmalloc(Register size) {
        // the register argument contains the size to malloc
        Register addr = getRegister();
        writer.println("li $v0,9");
        writer.printf("move $a0,%s\n", size);
        freeRegister(size);
        writer.println("syscall");
        writer.printf("move %s,$v0\n", addr);
        return addr;
    }

    private Register getArrayAccessAddress(ArrayAccessExpr aae) {
        Register arrReg;
        Register idxReg = aae.idx.accept(this);
        int typeSize = dataAlloc.getTypeSize(((ArrayType) aae.arr.type).baseType);
        // all structs are global so this is initially assumed true
        boolean isGlobal = true;


        // TODO: what about field access?

        // TODO: value at ?

        // TOOD: do we allow pointers to arrays? if so, how?

        if (aae.arr instanceof FieldAccessExpr) arrReg = getFieldAccessAddress((FieldAccessExpr) aae.arr);
        else arrReg = aae.arr.accept(this);

        writer.printf("mul %s,%s,%s\n", idxReg, idxReg, typeSize);
        // if the array is global the address will be stored in the data segment
        if (aae.arr instanceof VarExpr) isGlobal = (((VarExpr) aae.arr).vd.isGlobal);

        if (isGlobal) writer.printf("add %s,%s,%s\n", arrReg, arrReg, idxReg);
        else writer.printf("sub %s,%s,%s\n", arrReg, arrReg, idxReg);

        freeRegister(idxReg);
        return arrReg;
    }

    private Register getFieldAccessAddress(FieldAccessExpr fae) {
        Register structReg = fae.struct.accept(this);
        int offset = 0;

        // find the offset from the struct for the field
        for (VarDecl vd : ((StructType) (((VarExpr) fae.struct).vd.type)).std.varDecls) {
            if (vd.varName.equals(fae.fieldName)) offset = vd.offset;
        }

        if (((VarExpr) fae.struct).vd.isGlobal) writer.printf("add %s,%s,%s\n", structReg, structReg, offset);
            // local variables are stored on the stack which grows down so we subtract
        else writer.printf("sub %s,%s,%s\n", structReg, structReg, offset);

        return structReg;
    }
}
