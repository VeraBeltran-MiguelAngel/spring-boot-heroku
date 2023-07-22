package com.bolsadeideas.springboot.app.models.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bolsadeideas.springboot.app.models.entity.Cliente;
// como la interfaz JPAREpository ya es un componente de spring no hay que 
//colocar ninguna anotacion
public interface IClienteDao extends JpaRepository<Cliente, Long>{
	
// podriamos crear metodos con consultas personalizadas  que no cubran los metodos default de la interfaz
/**
 * Si usas un inner join y no existen registros en la tabla factura no mostrara los clientes , usamos
 * left join , este permite mostrar los clientes (tabla izq) aunque no existan registros en la tabla der
 * facturas
 */
	@Query("select c from Cliente c left join fetch c.facturas f where c.id=?1")
	public Cliente fecthByIdWithFacturas(Long id);
}
