package com.bolsadeideas.springboot.app.models.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.bolsadeideas.springboot.app.models.entity.Factura;

public interface IFacturaDao extends CrudRepository<Factura, Long> {

    /**
     * *Consulta a la clase entity Factura con joins
     * *c alias del atributo cliente en Factura.java
     * *l (linea) alias del atributo items en Factura.java
     * @param id ID de la factura 
     * @return
     */
    @Query("select f from Factura f join fetch f.cliente c join fetch f.items l join fetch l.producto where f.id=?1")
    public Factura fetchByIdWithClienteWithItemFacturaWithProducto(Long id);

}
