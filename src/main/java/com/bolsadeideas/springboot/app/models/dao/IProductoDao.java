package com.bolsadeideas.springboot.app.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.bolsadeideas.springboot.app.models.entity.Producto;

public interface IProductoDao extends CrudRepository<Producto, Long>{
    
	// le damos un alias a la tabla Producto -p
   @Query("select p from Producto p where p.nombre like %?1%")
    public List<Producto> findByNombre(String term);
}
