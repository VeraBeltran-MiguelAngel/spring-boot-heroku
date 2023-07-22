package com.bolsadeideas.springboot.app.util.paginator;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
/**
 * Clase para navegar entre paginas
 * @author Miguel
 *
 * @param <T> generico por que podemos tener lista de cliente,producto,etc
 */
public class PageRender<T> {

	private String url; // "/listar" del cliente controller
	private Page<T> page; // listado paginable de los clientes
	private int totalPaginas;
	private int numElementosPorPagina;// registros a mostra en cada pag
	private int paginaActual;
	private List<PageItem> paginas; // items que se muestran el el paginador (1,2,3,4..)

	public PageRender(String url, Page<T> page) {
		this.url = url;
		this.page = page;
		this.paginas = new ArrayList<PageItem>();
		numElementosPorPagina = page.getSize();//esta declarado en el controlador 
		totalPaginas = page.getTotalPages();
		//se debe mostrar desde 1 
		paginaActual = page.getNumber()+1;//parte desde cero (definido en el controller)

		int desde, hasta;
		if (totalPaginas <= numElementosPorPagina) {
			//muestra el paginador completo
			desde = 1; //primera pagina
			hasta = totalPaginas; //ultima pagina
		} else { //navegar por rango (inicio,medio,final)
			
			if (paginaActual <= numElementosPorPagina / 2) { //rango inicial
				//recorrer el rango (si vas avanzando entre paginas van desapareciendo el 1,2,3 a medida que llegas al final)
				desde = 1;
				hasta = numElementosPorPagina;//en la barra de navegacion mostrara solo 4 paginas
			} else if (paginaActual >= totalPaginas - numElementosPorPagina / 2) {//rango final 
				//System.out.println(numElementosPorPagina);
				//7-4=3+1=4
				desde = totalPaginas - numElementosPorPagina + 1; 
				hasta = numElementosPorPagina;
				
			} else { //rango medio(porque no se cumple el inicial ni final) 
				desde = paginaActual - numElementosPorPagina / 2; 
				hasta = numElementosPorPagina;
			}
		}
		// agregar las paginas a la coleccion 
		// va desde 0 hasta el total de paginas que se muestra en x rango dependiendo de donde 
		// tome su valor en los if
		for (int i = 0; i < hasta; i++) {
			       //obtine su valor de los if, si es verdad da un true 
			paginas.add(new PageItem(desde + i, paginaActual == desde + i));
		}
	
		/*
		 * for (PageItem t : paginas) { System.out.println(t.getNumero()); }
		 */
		
	}

	public String getUrl() {
		return url;
	}

	public int getTotalPaginas() {
		return totalPaginas;
	}

	public int getPaginaActual() {
		return paginaActual;
	}

	public List<PageItem> getPaginas() {
		return paginas;
	}
	
	public boolean isFirst() {
		return page.isFirst();
	}
	
	public boolean isLast() {
		return page.isLast();
	}
	
	public boolean isHasNext() {
		return page.hasNext();
	}
	
	public boolean isHasPrevious() {
		return page.hasPrevious();
	}
	
	

}
