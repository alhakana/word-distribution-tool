package components;

public class Input {
    private final String name;
    private final String text;

    public Input(String name, String text) {
        this.name = name;
        this.text = text;
    }

    // poison input for queue in crunchers
    public Input() {
        name = "";
        text = "";
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }
}
