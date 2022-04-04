package mvc.model;

public class FileOutput {
    private final String name;
    private boolean done;

    public FileOutput(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public boolean equals(Object obj) {
        if (obj instanceof FileOutput) {
            return name.equals(((FileOutput) obj).name);
        }

        return false;
    }

    @Override
    public String toString() {
        if (done) return name;
        return "*"+name;
    }
}
