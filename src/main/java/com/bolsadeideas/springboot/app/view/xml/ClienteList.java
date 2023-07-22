package com.bolsadeideas.springboot.app.view.xml;

import java.util.List;

import com.bolsadeideas.springboot.app.models.entity.Cliente;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "clientes")
public class ClienteList {

    @XmlElement(name = "cliente")
    public List<Cliente> clientes;

    // constructor vacio
    public ClienteList() {

    }

    // constructor que asigna la lista
    public ClienteList(List<Cliente> clientes) {
        this.clientes = clientes;
    }

    public List<Cliente> getClientes() {
        return clientes;
    }

}
