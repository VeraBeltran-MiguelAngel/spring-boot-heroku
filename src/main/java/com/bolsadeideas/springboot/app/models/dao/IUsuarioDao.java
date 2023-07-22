package com.bolsadeideas.springboot.app.models.dao;

import org.springframework.data.repository.CrudRepository;

import com.bolsadeideas.springboot.app.models.entity.Usuario;

public interface IUsuarioDao extends CrudRepository<Usuario, Long> {
    // metodo personalizado (a traves del metodo (query method name) se ejecutara la
    // consulta JPQL)
    // select u from Usuario u where u.username=?1
    // el metodo findBy ya esta en crudrepository y le pasamos el nombre del atributo
    // Username de la tabla Usuario
    public Usuario findByUsername(String username);
}
