package model.representation;

public class Stat {

	private String field = null;
	private String text = null;
	private int value;
	private boolean relativeValue = true;

	public Stat(String field, String text, int value) {
		this.field = field;
		this.text = text;
		this.value = value;
	}

    public Stat(String field, int value, boolean relativeValue) {
        this.field = field;
        this.text = text;
        this.value = value;
        this.relativeValue = relativeValue;
    }

    public Stat(String field, int value) {
		this.field = field;
		this.value = value;
	}

	public String getField() {
		return field;
	}

	public String getText() {
		return text;
	}

	public int getValue() {
		return value;
	}

	@Override
	public String toString() {
		String aux = field + ": " + value;
		if(relativeValue)
		    aux += "%";
		if(text != null)
			return aux + "\n" + text;
		else
			return aux;
	}
}
