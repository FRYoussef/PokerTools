package com.example.youss.pokertools.model.representation;

import android.os.Parcel;
import android.os.Parcelable;

public class Stat implements Parcelable{

	private String field = null;
	private String text = "";
	private int value;
	private boolean relativeValue = true;

	public Stat(String field, String text, int value) {
		this.field = field;
		this.text = text;
		this.value = value;
	}

    public Stat(String field, int value, boolean relativeValue) {
        this.field = field;
        this.value = value;
        this.relativeValue = relativeValue;
    }

    public Stat(String field, int value) {
		this.field = field;
		this.value = value;
	}

	public Stat(Parcel in){
		readFromParcel(in);
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

	public boolean isRelativeValue() {
		return relativeValue;
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

	public static final Creator<Stat> CREATOR = new Creator<Stat>() {
		@Override
		public Stat createFromParcel(Parcel in) {
			return new Stat(in);
		}

		@Override
		public Stat[] newArray(int size) {
			return new Stat[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeString(field);
		parcel.writeString(text);
		parcel.writeInt(value);
		parcel.writeBooleanArray(new boolean[]{relativeValue});
	}

	private void readFromParcel(Parcel in) {
		field = in.readString();
		text = in.readString();
		value = in.readInt();
		boolean[] temp = new boolean[1];
		in.readBooleanArray(temp);
		relativeValue = temp[0];
	}
}
