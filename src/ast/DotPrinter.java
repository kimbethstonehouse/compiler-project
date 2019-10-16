//package ast;
//
//import org.w3c.dom.DOMStringList;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.PrintWriter;
//
//public class DotPrinter implements ASTVisitor<String> {
//    PrintWriter writer;
//    int nodeCount = 0;
//
//    public DotPrinter(File f) throws IOException {
////        f = new File("C://afs/inf.ed.ac.uk/user/s16/s1615906/ast.txt");
//        writer = new PrintWriter(f);
//    }
//
//    public String visitBaseType(BaseType bt) {
//        return null;
//    }
//
//    public String visitStructTypeDecl(StructTypeDecl st) {
//        return null;
//    }
//
//    public String visitBlock(Block b) {
//        return null;
//    }
//
//    public String visitFunDecl(FunDecl p) {
//        return null;
//    }
//
//    public String visitProgram(Program p) {
//        return null;
//    }
//
//    public String visitVarDecl(VarDecl vd) {
//        return null;
//    }
//
//    public String visitVarExpr(VarExpr v) {
//        return null;
//    }
//
//    public String visitBinOp(BinOp bo) {
//        String binOpNodeId = "Node"+nodeCount++;
//        writer.println(binOpNodeId+"[label=\"BinOp\"];");
//
//        String lhsNodeId = bo.lhs.accept(this);
//        String opNodeId = "Node"+nodeCount++;
//        // what if its not a plus??
//        writer.println(opNodeId+"[label=\"+\"];");
//        String rhsNodeId = bo.rhs.accept(this);
//
//        writer.print(binOpNodeId + "->" + lhsNodeId + ";");
//        writer.print(binOpNodeId + "->" + opNodeId + ";");
//        writer.print(binOpNodeId + "->" + rhsNodeId + ";");
//
//        return binOpNodeId;
//    }
//
//    public String visitIntLiteral(IntLiteral il) {
//        nodeCount++;
//        writer.println("Node"+nodeCount+"[label=\"Cst("+il.i+")\"];");
//        return "Node"+nodeCount;
//    }
//
//    public String visitStrLiteral(StrLiteral sl) {
//        nodeCount++;
//        writer.println("Node"+nodeCount+"[label=\"Cst("+sl.s+")\"];");
//        return "Node"+nodeCount;
//    }
//
//    public String visitChrLiteral(ChrLiteral cl) {
//        nodeCount++;
//        writer.println("Node"+nodeCount+"[label=\"Cst("+cl.c+")\"];");
//        return "Node"+nodeCount;
//    }
//
//}
