package mvc.model;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class FileInput {
	public static AtomicInteger NUMBER_OF_INPUTS = new AtomicInteger(0);

	private Disk disk;
	private String name;
	
	public FileInput(Disk disk) {
		this.disk = disk;
		this.name = "File input " + NUMBER_OF_INPUTS.get();
		NUMBER_OF_INPUTS.incrementAndGet();
	}
	
	public Disk getDisk() {
		return disk;
	}

	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		FileInput fileInput = (FileInput) o;
		return Objects.equals(name, fileInput.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
}
