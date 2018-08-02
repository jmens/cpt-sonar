package de.jmens;

public class Main {

	public static void main(String[] args) throws Exception {
		System.out.println("Starting server");
		new Server().startup(10000);
	}
}
