package com.example.mytestpeoject.Utils;

public class SharedPreferenceItem {
	private String key;
	private String value;

	public SharedPreferenceItem(String key, String value) {
		super();
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "SharedPreferenceItem [key=" + key + ", value=" + value + "]";
	}

}
