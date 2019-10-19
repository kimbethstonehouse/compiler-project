package sem;

import ast.*;

import java.util.ArrayList;
import java.util.Arrays;

public class SemanticAnalyzer {
	
	public int analyze(ast.Program prog) {
		// List of visitors
		ArrayList<SemanticVisitor> visitors = new ArrayList<SemanticVisitor>() {{
			add(new NameAnalysisVisitor(new Scope()));
		}};
		// Error accumulator
		int errors = 0;

		// add built in library functions to program fundecls before semantic analysis
		// void print_s(char* s);
		prog.funDecls.add(new FunDecl(
				BaseType.VOID,
				"print_s",
				Arrays.asList(new VarDecl(new PointerType(BaseType.CHAR), "s")),
				new Block(new ArrayList<>(), new ArrayList<>())
		));

		// void print_i(int i);
		prog.funDecls.add(new FunDecl(
				BaseType.VOID,
				"print_i",
				Arrays.asList(new VarDecl(BaseType.INT, "i")),
				new Block(new ArrayList<>(), new ArrayList<>())
		));

		// void print_c(char c);
		prog.funDecls.add(new FunDecl(
				BaseType.VOID,
				"print_c",
				Arrays.asList(new VarDecl(BaseType.CHAR, "c")),
				new Block(new ArrayList<>(), new ArrayList<>())
		));

		// char read_c();
		prog.funDecls.add(new FunDecl(
				BaseType.CHAR,
				"read_c",
				new ArrayList<>(),
				new Block(new ArrayList<>(), new ArrayList<>())
		));

		// int read_i();
		prog.funDecls.add(new FunDecl(
				BaseType.INT,
				"read_i",
				new ArrayList<>(),
				new Block(new ArrayList<>(), new ArrayList<>())
		));

		// void* mcmalloc(int size);
		prog.funDecls.add(new FunDecl(
				new PointerType(BaseType.VOID),
				"mcmalloc",
				Arrays.asList(new VarDecl(BaseType.INT, "size")),
				new Block(new ArrayList<>(), new ArrayList<>())
		));
		
		// Apply each visitor to the AST
		for (SemanticVisitor v : visitors) {
			prog.accept(v);
			errors += v.getErrorCount();
		}
		
		// Return the number of errors.
		return errors;
	}
}
