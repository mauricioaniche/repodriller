package br.com.metricminer2.metric.java8.methods;

public class Method {

	private String name;
	private int lines;
	public Method(String name, int lines) {
		this.name = name;
		this.lines = lines;
	}
	public String getName() {
		return name;
	}
	public int getLines() {
		return lines;
	}
	public boolean isGetter() {
		return name.startsWith("get");
	}
	public boolean isSetter() {
		return name.startsWith("set");
	}
	
	public boolean isGetterOrSetter() {
		return isGetter() || isSetter();
	}
	
	
	
	
}
