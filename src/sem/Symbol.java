package sem;

public abstract class Symbol {
	public String name;

	public abstract boolean isVar();
	public abstract boolean isFunc();

	public Symbol(String name) {
		this.name = name;
	}
}
