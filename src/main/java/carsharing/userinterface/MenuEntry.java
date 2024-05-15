package carsharing.userinterface;

public class MenuEntry {
    private int number;
    private String text;
    private Runnable method;

    public MenuEntry(int number, String text, Runnable method) {
        this.number = number;
        this.text = text;
        this.method = method;
    }

    public int getNumber() {
        return number;
    }

    public String getText() {
        return text;
    }

    public Runnable getMethod() {
        return method;
    }

    public void execute() {
        this.method.run();
    }
}
