package com.bolsadeideas.springboot.app.util.paginator;
/**
 * Clase para saber si una pagina es la actual
 * @author Miguel
 *
 */
public class PageItem {
	
	private int numero;
	private boolean actual;
	
	public PageItem(int numero, boolean actual) {
		this.numero = numero;
		this.actual = actual;
	}

	public int getNumero() {
		return numero;
	}

	public boolean isActual() {
		return actual;
	}
	
	
	

}
