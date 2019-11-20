package gen;

import ast.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.EmptyStackException;
import java.util.Stack;

public class CodeGenerator implements ASTVisitor<Register> {

    /*
     * Simple register allocator.
     */

    // contains all the free temporary registers
    private Stack<Register> freeRegs = new Stack<Register>();
    private int totalSize;              // tracks the offset from the frame pointer for the current function
    private int frameOffset;            // tracks the offset at which the next variable can be stored
    private int returnOffset;           // tracks where on the stack the return should go
    private int numWhiles = 0;          // number of while loops or if statements encountered so far
    private int numIfs = 0;
    private int numAnds = 0;
    private int numOrs = 0;
    private String currentFuncName;     // the name of the current function

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
        if (!freeRegs.contains(reg)) freeRegs.push(reg);
    }


    private PrintWriter writer;         // use this writer to output the assembly instructions
    private DataAllocation dataAlloc;   // use this pass to allocate global and local variables

    public void emitProgram(Program program, File outputFile) throws FileNotFoundException {
        writer = new PrintWriter(outputFile);
        dataAlloc = new DataAllocation();

        dataAlloc.emitProgram(program, writer);

        writer.println(".text");
        writer.println();

        visitProgram(program);
        writer.println();

        writer.close();
    }

    @Override
    // Program ::= StructTypeDecl* VarDecl* FunDecl*
    public Register visitProgram(Program p) {
        for (StructTypeDecl std : p.structTypeDecls) std.accept(this);
        // vardecls allocated in the data allocation pass
        for (FunDecl fd : p.funDecls) fd.accept(this);
        return null;
    }

    @Override
    // StructTypeDecl ::= StructType VarDecl*
    public Register visitStructTypeDecl(StructTypeDecl st) {
        return null;
    }

    @Override
    // VarDecl ::= Type String
    public Register visitVarDecl(VarDecl vd) {
        vd.offset = frameOffset;
        // increment frame offset by size of the variable's type
        int varSize = dataAlloc.getAlignedTypeSize(vd.type);
        frameOffset -= varSize;
        totalSize += varSize;
        return null;
    }

    @Override
    // FunDecl ::= Type String VarDecl* Block
    public Register visitFunDecl(FunDecl p) {
        writer.println();
        writer.printf("### entering visit fundecl %s\n", p.name);
        currentFuncName = p.name;

        if (p.name.equals("main")) {
            writer.println(".globl main");
            writer.println("main:");
        } else writer.printf("func_%s_start:\n", p.name);

        // initialise fp to the value of sp
        writer.println("move $fp,$sp");
        int paramOffset = -4;
        totalSize = 0;
        frameOffset = -4;

        // iterate through the parameters in reverse order
        for (int i = p.params.size() - 1; i >= 0; i--) {
            VarDecl vd = p.params.get(i);
            int varSize = dataAlloc.getAlignedTypeSize(vd.type);

            // increment offset by size of the type
            paramOffset += varSize;
            vd.offset = paramOffset;
        }

        // set return offset
        int returnSize = dataAlloc.getAlignedTypeSize(p.type);
        returnOffset = returnSize + 8;      // add 8 for the fp and ra, remove 4 begin from 0, net add 4
        returnOffset += paramOffset;

        // allocate local variables
        p.block.accept(this);

        // return jumps to here
        writer.printf("func_%s_end:\n", p.name);

        // deallocate local variables
        writer.printf("addi $sp,$sp,%s\n", totalSize);

        if (p.name.equals("main")) {
            writer.println("li $v0 10");
            writer.println("syscall");
        } else writer.println("jr $ra");
        return null;
    }

    // TYPES
    @Override
    public Register visitBaseType(BaseType bt) { return null; }

    @Override
    public Register visitPointerType(PointerType pt) { return null; }

    @Override
    public Register visitStructType(StructType st) { return null; }

    @Override
    public Register visitArrayType(ArrayType at) { return null; }

    // EXPR
    @Override
    // IntLiteral ::= int
    public Register visitIntLiteral(IntLiteral il) {
        Register reg = getRegister();
        writer.printf("li %s,%s\n", reg, il.i);
        return reg;
    }

    @Override
    // StrLiteral ::= String
    public Register visitStrLiteral(StrLiteral sl) {
        // returns the address of the string
        Register reg = getRegister();
        writer.printf("la %s %s\n", reg, sl.label);
        return reg;
    }

    @Override
    // ChrLiteral ::= char
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
    // FunCallExpr ::= String Expr*
    public Register visitFunCallExpr(FunCallExpr fce) {
        writer.println();
        writer.printf("### entering visit funcall expr %s\n", fce.fd.name);

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

        Register argReg;
        int argsSpace = 0;

        // 1. PRECALL

        // push space for the return value if necessary
        if (fce.fd.type instanceof StructType) writer.printf("subi $sp,$sp,%s\n", dataAlloc.getAlignedTypeSize(fce.fd.type));

        // push fp
        writer.println("addi $sp,$sp,-4");
        writer.println("sw $fp,0($sp)");

        // push ra
        writer.println("addi $sp,$sp,-4");
        writer.println("sw $ra,0($sp)");

        // push callee saves
        // TODO: this causes issues if the return type is a struct and the registers need saving
        for (Register reg : Register.tmpRegs) {
            if (!freeRegs.contains(reg)) {
                writer.println("addi $sp,$sp,-4");
                writer.printf("sw %s,0($sp)\n", reg);
            }
        }

        // push args
        for (Expr arg : fce.args) {
            argReg = arg.accept(this);

            // the register stores the address of structs
            if (arg.type instanceof StructType) {
                Register temp = getRegister();
                StructType struct = ((StructType) arg.type);
                int size = struct.std.structSize;

                // so they are copied to the stack word by word
                if (arg instanceof VarExpr && ((VarExpr) arg).vd.isGlobal) {
                    for (int offset = 0; offset <= size - 4; offset += 4) {
                        writer.printf("lw %s,%s(%s)\n", temp, offset, argReg);
                        writer.println("addi $sp,$sp,-4");
                        writer.printf("sw %s,0($sp)\n", temp);
                    }
                } else {
                    for (int offset = 0; offset >= -1 * size + 4; offset -= 4) {
                        writer.printf("lw %s,%s(%s)\n", temp, offset, argReg);
                        writer.println("addi $sp,$sp,-4");
                        writer.printf("sw %s,0($sp)\n", temp);
                    }
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

        // 2. POSTRETURN

        // pop args
        writer.printf("addi $sp,$sp,%s\n", argsSpace);

        // pop caller saves
        for (int i = Register.tmpRegs.size() - 1; i >= 0; i--) {
            Register reg = Register.tmpRegs.get(i);
            if (!freeRegs.contains(reg)) {
                writer.printf("lw %s,0($sp)\n", Register.tmpRegs.get(i));
                writer.println("subi $sp,$sp,-4");
            }
        }

        // pop ra
        writer.println("lw $ra,0($sp)");
        writer.println("subi $sp,$sp,-4");

        // pop fp
        writer.println("lw $fp,0($sp)");
        writer.println("subi $sp,$sp,-4");

        // move return value
        Register returnReg = getRegister();
        if (fce.fd.type instanceof StructType) {
            writer.printf("move %s,$sp\n", returnReg);
            writer.printf("addi %s,%s,%s\n", returnReg, returnReg, dataAlloc.getAlignedTypeSize(fce.fd.type) - 4);
        } else {
            // load the value
            writer.printf("move %s,$v0\n", returnReg);
        }

        return returnReg;
    }

    @Override
    // BinOp ::= Expr Op Expr
    public Register visitBinOp(BinOp bo) {
        Register lhsReg = bo.lhs.accept(this);
        Register rhsReg = bo.rhs.accept(this);
        Register resultReg = getRegister();

        switch (bo.op) {
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
                int n = numOrs;
                numOrs++;

                // short circuit evaluation
                // if lhs is true, whole expr is true
                writer.printf("bnez %s,or_%s_true\n", lhsReg, n);
                // if rhs is true, whole expr is true
                writer.printf("bnez %s,or_%s_true\n", rhsReg, n);
                // if neither true, the statement is false
                writer.printf("b or_%s_false\n", n);

                // code for what to in the case of true and false
                writer.printf("or_%s_true:\n", n);
                // set result to 1 and finish
                writer.printf("li %s,1\n", resultReg);
                writer.printf("b or_%s_end\n", n);

                writer.printf("or_%s_false:\n", n);
                // set result to 0 and finish
                writer.printf("li %s,0\n", resultReg);

                writer.println();
                writer.printf("or_%s_end:\n", n);
                break;
            case AND:
                n = numAnds;
                numAnds++;

                // short circuit evaluation
                // if lhs is false, whole expr is false
                writer.printf("beqz %s,and_%s_false\n", lhsReg, n);
                // if rhs is false, jump to false
                writer.printf("beqz %s,and_%s_false\n", rhsReg, n);
                // if neither false, the statement is true
                writer.printf("b and_%s_true\n", n);

                // code for what to in the case of true and false
                writer.printf("and_%s_true:\n", n);
                // set result to 1 and finish
                writer.printf("li %s,1\n", resultReg);
                writer.printf("b and_%s_end\n", n);

                writer.printf("and_%s_false:\n", n);
                // set result to 0 and finish
                writer.printf("li %s,0\n", resultReg);

                writer.println();
                writer.printf("and_%s_end:\n", n);
        }

        freeRegister(lhsReg);
        freeRegister(rhsReg);
        return resultReg;
    }

    @Override
    // ArrayAccessExpr ::= Expr Expr
    public Register visitArrayAccessExpr(ArrayAccessExpr aae) {
        writer.println("### entering array access expression");
        String loadInstruct;
        Type baseType;

        Register addrReg = getArrayAccessAddress(aae);
        Register resultReg = getRegister();

        // arr can either be an array or a pointer
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
    // FieldAccessExpr ::= Expr String
    public Register visitFieldAccessExpr(FieldAccessExpr fae) {
        writer.println("### entering field access expression");

        Register fieldAddress = getFieldAccessAddress(fae);
        Register resultReg = getRegister();
        Type fieldType = BaseType.INT;

        if (fae.struct instanceof VarExpr) {
            VarExpr ve = ((VarExpr) fae.struct);
            StructType structType = ((StructType) ve.vd.type);

            for (VarDecl vd : structType.std.varDecls) if (vd.varName.equals(fae.fieldName)) fieldType = vd.type;
        } else if (fae.struct instanceof ValueAtExpr) {
            ValueAtExpr vae = ((ValueAtExpr) fae.struct);
            VarExpr ve = ((VarExpr) vae.expr);
            PointerType pt = ((PointerType) ve.type);
            StructType structType = (StructType) pt.baseType;

            for (VarDecl vd : structType.std.varDecls) if (vd.varName.equals(fae.fieldName)) fieldType = vd.type;
        }

        // if this is an array or struct, return the address
        if (fieldType instanceof ArrayType || fieldType instanceof StructType) {
            writer.printf("la %s,(%s)\n", resultReg, fieldAddress);
            // else return the value
        } else writer.printf("lw %s,(%s)\n", resultReg, fieldAddress);

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
        int startingSize = totalSize;

        // allocate space for local variables
        for (VarDecl vd : b.varDecls) { vd.accept(this); }

        // move stack pointer before any variables are used in stmts
        // ($sp) is moved by an offset corresponding to the size of
        // all the local variables declared on the stack in this block
        writer.printf("subi $sp,$sp,%s\n", totalSize - startingSize);

        for (Stmt stmt : b.stmts) { stmt.accept(this); }

        return null;
    }

    @Override
    // While ::= Expr Stmt
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
    // If ::= Expr Stmt [Stmt]
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
    // Assign ::= Expr Expr
    public Register visitAssign(Assign a) {
        Register rhsReg = a.rhs.accept(this);

        // store the result in the variable
        if (a.lhs instanceof VarExpr) {
            // the register holds the address of the struct type
            if (a.lhs.type instanceof StructType) {
                Register addrReg = a.lhs.accept(this);
                Register temp = getRegister();
                int size = ((StructType) a.lhs.type).std.structSize;

                // so copy it over word by word
                for (int offset = 0; offset <= size; offset += 4) {
                    writer.printf("lw %s,%s(%s)\n", temp, -offset, rhsReg);
                    writer.printf("sw %s,%s(%s)\n", temp, -offset, addrReg);
                }

                freeRegister(temp);
                freeRegister(addrReg);
            } else {
                VarExpr v = ((VarExpr) a.lhs);

                if (v.vd.isGlobal) writer.printf("sw %s,%s\n", rhsReg, v.name);
                else writer.printf("sw %s,%s($fp)\n", rhsReg, v.vd.offset);
            }
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
    // Return ::= [Expr]
    public Register visitReturn(Return r) {
        if (r.expr != null) {
            // evaluate return value
            Register expReg = r.expr.accept(this);

            if (r.expr.type instanceof StructType) {
                Register temp = getRegister();
                int size = ((StructType) r.expr.type).std.structSize;

                // copy to the stack word by word
                for (int offset = 0; offset < size; offset += 4) {
                    writer.printf("lw %s,%s(%s)\n", temp, -offset, expReg);
                    writer.printf("sw %s,%s($fp)\n", temp, returnOffset-offset);
                }

                freeRegister(temp);
            } else {
                // store in the return register
                writer.printf("move $v0,%s\n", expReg);
            }

            freeRegister(expReg);
        }

        writer.printf("j func_%s_end\n", currentFuncName);
        return null;
    }

    @Override
    // ExprStmt ::= Expr
    public Register visitExprStmt(ExprStmt es) {
        es.expr.accept(this);
        return null;
    }

    @Override
    public Register visitErrorType(ErrorType et) { return null; }

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
        // the register contains the integer
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
        Register arrReg = aae.arr.accept(this);
        Register idxReg = aae.idx.accept(this);
        int typeSize;

        // TODO: endianness is wrong for struct character arrays
        if (aae.arr.type instanceof ArrayType) typeSize = dataAlloc.getTypeSize(((ArrayType) aae.arr.type).baseType);
        else typeSize = dataAlloc.getTypeSize(((PointerType) aae.arr.type).baseType);
        writer.printf("mul %s,%s,%s\n", idxReg, idxReg, typeSize);

        if (aae.arr instanceof VarExpr) {
            boolean isGlobal = (((VarExpr) aae.arr).vd.isGlobal);
            if (isGlobal) writer.printf("add %s,%s,%s\n", arrReg, arrReg, idxReg);
            else writer.printf("sub %s,%s,%s\n", arrReg, arrReg, idxReg);
        } else if (aae.arr instanceof FieldAccessExpr) {
            arrReg = getFieldAccessAddress((FieldAccessExpr) aae.arr);
            writer.printf("sub %s,%s,%s\n", arrReg, arrReg, idxReg);
        }

        freeRegister(idxReg);
        return arrReg;
    }

    private Register getFieldAccessAddress(FieldAccessExpr fae) {
        // base address of the struct
        Register structAddr = fae.struct.accept(this);
        int offset = 0;

        // find the offset from the struct for the field
        if (fae.struct instanceof VarExpr) {
            VarExpr ve = ((VarExpr) fae.struct);
            StructType structType = ((StructType) ve.vd.type);

            for (VarDecl vd : structType.std.varDecls) if (vd.varName.equals(fae.fieldName)) offset = vd.offset;

            if (ve.vd.isGlobal) writer.printf("add %s,%s,%s\n", structAddr, structAddr, offset);
                // local variables are stored on the stack which grows down so we subtract
            else writer.printf("sub %s,%s,%s\n", structAddr, structAddr, offset);
        } else if (fae.struct instanceof ValueAtExpr) {
            ValueAtExpr vae = ((ValueAtExpr) fae.struct);
            VarExpr ve = ((VarExpr) vae.expr);
            PointerType pt = ((PointerType) ve.type);
            StructType structType = (StructType) pt.baseType;

            for (VarDecl vd : structType.std.varDecls) if (vd.varName.equals(fae.fieldName)) offset = vd.offset;
            // overwrite value with address
            Register addrReg = vae.expr.accept(this);
            writer.printf("la %s,0(%s)\n", structAddr, addrReg);
            freeRegister(addrReg);
            writer.printf("sub %s,%s,%s\n", structAddr, structAddr, offset);
        }

        return structAddr;
    }
}
