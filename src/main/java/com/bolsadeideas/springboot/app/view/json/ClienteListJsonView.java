package com.bolsadeideas.springboot.app.view.json;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.bolsadeideas.springboot.app.models.entity.Cliente;

import org.springframework.data.domain.Page;

@Component("listar.json")
public class ClienteListJsonView extends MappingJackson2JsonView {

    /**
     * para poder filtar algunos elementos del model del clientecontroller(listar)
     */
    @SuppressWarnings("unchecked")
    @Override
    protected Object filterModel(Map<String, Object> model) {

        model.remove("titulo");
        model.remove("page");

        // limpiar la informacion a mostrar del cliente
        Page<Cliente> clientes = (Page<Cliente>) model.get("clientes");
        // quitamos el clientes
        model.remove("clientes");
        // agregamos el cliente que sea de tipo list a traves del paginable getcontent
        model.put("clientes", clientes.getContent());

        // metodo heredado de la clase padre mappingJackson
        return super.filterModel(model);
    }

}
