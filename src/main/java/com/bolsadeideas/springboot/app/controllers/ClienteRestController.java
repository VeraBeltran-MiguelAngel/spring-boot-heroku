package com.bolsadeideas.springboot.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bolsadeideas.springboot.app.models.service.IClienteService;
import com.bolsadeideas.springboot.app.view.xml.ClienteList;

@RestController // solo devuelve vistas tipos REST 
@RequestMapping("/api/clientes")
public class ClienteRestController {

    @Autowired // siempre se inyecta la interfaz para hacerlo generico
	private IClienteService clienteService;
    
    /**si deseas que regrese XML y JSON  http://localhost:8080/listar-rest?format=xml/json*/
    @GetMapping(value = "/listar")
    public ClienteList listar() { // de esta manera devolvemos XML y JSON
		return new ClienteList(clienteService.findAll());
	}
}