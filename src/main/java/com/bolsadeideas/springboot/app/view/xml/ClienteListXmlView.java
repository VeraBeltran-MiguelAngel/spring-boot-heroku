package com.bolsadeideas.springboot.app.view.xml;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.xml.MarshallingView;

import com.bolsadeideas.springboot.app.models.entity.Cliente;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component("listar.xml") // por que es una clase 'vista'
public class ClienteListXmlView extends MarshallingView {

    // creamos un constructor
    @Autowired
    public ClienteListXmlView(Jaxb2Marshaller marshaller) {
        super(marshaller);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        // hace referecnia al clientecontroller 'listar' , hay que dejar el model
        // totalmente limpio
        model.remove("titulo"); // cosas que no queremos en el XML
        model.remove("page");

        // obtenemos los clientes paginados
        Page<Cliente> clientes = (Page<Cliente>) model.get("clientes");
        model.remove("clientes");

        // guardamos el clienteList junto con el listado de clientes de forma paginada
        model.put("clienteList", new ClienteList(clientes.getContent()));// convertimos los clientes al tipo list

        // aqui esta llamando al metodo del padre
        super.renderMergedOutputModel(model, request, response);
    }

}
