package sem;

import java.util.HashMap;
import java.util.Map;

public class Scope {
	private Scope outer;
	// map identifier x to a symbol which
	// is either a variable or a function
	private Map<String, Symbol> symbolTable;
	
	public Scope(Scope outer) {
		this.outer = outer;
		symbolTable = new HashMap<>();
	}
	
	public Scope() {
		this(null);
		symbolTable = new HashMap<>();
	}

	// given a name, lookup the symbol corresponding to this name
	public Symbol lookup(String name) {
		Symbol s = symbolTable.get(name);

		// symbol exists
		if (s != null) return s;

		// symbol does not exist, so check outer scope
		if (outer != null) return outer.lookup(name);

		// identifier is unknown
		return null;
	}

	// same, but only in the current scope
	// useful for shadowing
	public Symbol lookupCurrent(String name) {
		return symbolTable.get(name);
	}
	
	public void put(Symbol sym) {
		symbolTable.put(sym.name, sym);
	}
}