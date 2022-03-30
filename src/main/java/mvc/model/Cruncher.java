package mvc.model;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class Cruncher {
	public static AtomicInteger NUMBER_OF_CRUNCHERS = new AtomicInteger(0);

	private int arity;
	private String name;
	
	public Cruncher(int arity) {
		this.arity = arity;
		name = "Counter " + NUMBER_OF_CRUNCHERS.get();
		NUMBER_OF_CRUNCHERS.incrementAndGet();
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public int getArity() {
		return arity;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		Cruncher cruncher = (Cruncher) o;
		return Objects.equals(name, cruncher.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
}
