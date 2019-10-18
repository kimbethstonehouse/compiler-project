package ast;

import lexer.Scanner;
import lexer.Tokeniser;
import parser.Parser;

import java.io.*;

public class DotPrinter implements ASTVisitor<String> {
    PrintWriter writer;
    int nodeCount = 0;

    public DotPrinter(File f) throws IOException {
        //FileWriter fileWriter = new FileWriter(f);
//        f = new File("C://afs/inf.ed.ac.uk/user/s16/s1615906/ast.txt");
        writer = new PrintWriter(f);
    }

    public String visitProgram(Program p) {
        nodeCount++;
        writer.println("Node" + nodeCount + "[label=\"Program\"];");
        return "Node" + nodeCount;
    }

    public String visitStructTypeDecl(StructTypeDecl st) {
        return "";
    }

    public String visitVarDecl(VarDecl vd) {
        return "";
    }

    public String visitFunDecl(FunDecl p) {
        return null;
    }

    // Type
    public String visitBaseType(BaseType bt) {
        return null;
    }

    public String visitPointerType(PointerType pt) {
        return null;
    }

    public String visitStructType(StructType st) {
        return null;
    }

    public String visitArrayType(ArrayType at) {
        return null;
    }

    // Expr
    public String visitIntLiteral(IntLiteral il) {
        nodeCount++;
        writer.println("Node" + nodeCount + "[label=\"Cst(" + il.i + ")\"];");
        return "Node" + nodeCount;
    }

    public String visitStrLiteral(StrLiteral sl) {
        nodeCount++;
        writer.println("Node" + nodeCount + "[label=\"Cst(" + sl.s + ")\"];");
        return "Node" + nodeCount;
    }

    public String visitChrLiteral(ChrLiteral cl) {
        nodeCount++;
        writer.println("Node" + nodeCount + "[label=\"Cst(" + cl.c + ")\"];");
        return "Node" + nodeCount;
    }

    public String visitVarExpr(VarExpr v) {
        return null;
    }

    public String visitFunCallExpr(FunCallExpr fce) {
        return null;
    }

    public String visitBinOp(BinOp bo) {
        String binOpNodeId = "Node" + nodeCount++;
        writer.println(binOpNodeId + "[label=\"BinOp\"];");

        String lhsNodeId = bo.lhs.accept(this);
        String opNodeId = "Node" + nodeCount++;
        // what if its not a plus??
        writer.println(opNodeId + "[label=\"+\"];");
        String rhsNodeId = bo.rhs.accept(this);

        writer.print(binOpNodeId + "->" + lhsNodeId + ";");
        writer.print(binOpNodeId + "->" + opNodeId + ";");
        writer.print(binOpNodeId + "->" + rhsNodeId + ";");

        return binOpNodeId;
    }

    public String visitArrayAccessExpr(ArrayAccessExpr aee) {
        return null;
    }

    public String visitFieldAccessExpr(FieldAccessExpr fae) {
        return null;
    }

    public String visitValueAtExpr(ValueAtExpr vae) {
        return null;
    }

    public String visitSizeOfExpr(SizeOfExpr soe) {
        return null;
    }

    public String visitTypecastExpr(TypecastExpr tce) {
        return null;
    }

    // Stmt
    public String visitBlock(Block b) {
        return null;
    }

    public String visitWhile(While w) {
        return null;
    }

    public String visitIf(If i) {
        return null;
    }

    public String visitAssign(Assign a) {
        return null;
    }

    public String visitReturn(Return r) {
        return null;
    }

    public String visitExprStmt(ExprStmt es) {
        return null;
    }

//    public static void main(String args[]) {
//        //File f = new File("C://afs/inf.ed.ac.uk/user/s16/s1615906/ast.txt");
//        //DotPrinter dp = new DotPrinter("C://afs/inf.ed.ac.uk/user/s16/s1615906/ast.txt");
//
//        File inputFile = new File(args[0]);
//        File outputFile = new File(args[1]);
//
//        Scanner scanner;
//        try {
//            scanner = new Scanner(inputFile);
//        } catch (FileNotFoundException e) {
//            System.out.println("File " + inputFile.toString() + " does not exist.");
//            return;
//        }
//
//        Tokeniser tokeniser = new Tokeniser(scanner);
//
//        Parser parser = new Parser(tokeniser);
//        Program programAst = parser.parse();
//        if (parser.getErrorCount() == 0) {
//            try {
//                programAst.accept(new DotPrinter(outputFile));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
}