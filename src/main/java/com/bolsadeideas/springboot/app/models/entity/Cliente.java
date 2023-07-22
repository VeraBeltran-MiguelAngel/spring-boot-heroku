package com.bolsadeideas.springboot.app.models.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "clientes")
public class Cliente implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // como se crea la llave primaria(autoincremental)
	private Long id;

	@NotEmpty
	private String nombre;
	@NotEmpty
	private String apellido;
	@NotEmpty
	@Email
	private String email;

	@NotNull
	@Column(name = "create_at")
	// *solo guarda fecha sin horas ni minutos
	@Temporal(TemporalType.DATE) // solo para fechas(indica el formato de la fecha que se va a guardar en la BD)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") // para mostrar bien la fecha en el json
	private Date createAt;

	// un cliente - muchas facturas
	// todas las operaciones delete y persist se hacen en cadena
	// crea de forma automatica la llave foranea (clienteId) en la tabla facturas
	@OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	/**
	 * lo agergamos para que al crear el archivo json no tenga problemas con la
	 * relacion bidireccional y cree un ciclo infiniito cliente-factura, necesitamos
	 * mostrar de una forma direccional
	 * esta anotacion se usa en la tabla padre
	 */
	@JsonManagedReference // se muestra en el json
	private List<Factura> facturas;

	public Cliente() {
		facturas = new ArrayList<Factura>();
	}

	private String foto; // guardara el nombre de la imagen

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}

	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public List<Factura> getFacturas() {
		return facturas;
	}

	/**
	 * Metodo para guardar un conjunto de facturas
	 * 
	 * @param facturas
	 */
	public void setFacturas(List<Factura> facturas) {
		this.facturas = facturas;
	}

	/**
	 * Metodo para guardar una sola factura
	 * 
	 * @param factura
	 */
	public void addFactura(Factura factura) {
		facturas.add(factura);
	}

	/**
	 * Metodo para imprimir el nombre y el apellido
	 */
	@Override
	public String toString() {
		return nombre + " " + apellido;
	}

	private static final long serialVersionUID = 1L;

}
