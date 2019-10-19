package sem;

import java.util.Map;

public class Scope {
	private Scope outer;
	// map identifier x to symbol which is either procedure or funciton
	private Map<String, Symbol> symbolTable;
	
	public Scope(Scope outer) { 
		this.outer = outer; 
	}
	
	public Scope() { this(null); }

	// given a name, lookup the symbol corresponding to this name
	public Symbol lookup(String name) {
		Symbol s = symbolTable.get(name);

		// symbol exists
		if (s != null) return s;

		// symbol does not exist, so check outer
		if (outer != null) return outer.lookup(name);

		// identifier is unknown
		return null;
	}

	// same, but only in current scope
	// this is useful for finding if a variable
	// has already been declared only in current -
	// this allows for shadowing
	public Symbol lookupCurrent(String name) {
		return symbolTable.get(name);
	}
	
	public void put(Symbol sym) {
		symbolTable.put(sym.name, sym);
	}
}