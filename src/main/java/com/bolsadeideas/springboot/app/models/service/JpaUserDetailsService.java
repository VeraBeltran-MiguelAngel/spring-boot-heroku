package com.bolsadeideas.springboot.app.models.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bolsadeideas.springboot.app.models.dao.IUsuarioDao;
import com.bolsadeideas.springboot.app.models.entity.Role;
import com.bolsadeideas.springboot.app.models.entity.Usuario;

/**
 * Aqui se encuentra el usuario autenticado,no necesitamos crear(implmentar) una
 * interface propia, ya que la interfacela
 * provee spring security
 */
@Service("jpaUserDetailsService")
public class JpaUserDetailsService implements UserDetailsService {

    @Autowired
    private IUsuarioDao usuarioDao;

    private Logger logger = LoggerFactory.getLogger(JpaUserDetailsService.class);

    @Override
    @Transactional(readOnly = true) // bajo la misma transaccion vamos a realizar la consulta del usuario y ademas
    // vamos a obtener los roles
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // obtenemos el usuario
        Usuario usuario = usuarioDao.findByUsername(username);

        if (usuario == null) {
            logger.error("Error login: no existe el usuario '" + username + "'");
            throw new UsernameNotFoundException("Username " + username + " no existe en el sistema!");
        }
        // obtener los roles del usuario uno por uno y registar estos roles en una lista
        // de grantedAuthority
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

        for (Role role : usuario.getRoles()) {
            logger.info("Role: ".concat(role.getAuthority()));// mostrar en consola el nombre del rol
            authorities.add(new SimpleGrantedAuthority(role.getAuthority()));
        }

        if (authorities.isEmpty()) {
            logger.error("Error login: usuario '" + username + "' no tiene roles asignado!");
            throw new UsernameNotFoundException("Error login: usuario '" + username + "' no tiene roles asignado!");
        }

        // retornar el objeto UserDetails(usuario autenticado) pero retornamos la
        // implementacion
        return new User(usuario.getUsername(), usuario.getPassword(),
                usuario.getEnabled(), true, true, true,
                authorities);
    }

}
