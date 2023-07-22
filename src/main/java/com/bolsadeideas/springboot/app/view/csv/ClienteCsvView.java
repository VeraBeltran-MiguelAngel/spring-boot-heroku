package com.bolsadeideas.springboot.app.view.csv;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.AbstractView;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.bolsadeideas.springboot.app.models.entity.Cliente;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component("listar") // hace referencia al archivo html y al clientecontrolller en el metodo listar ,
                     // si tuvieramos mas vistas como pdf xml excel tendriamos que agregar la
                     // extension .csv al nombre del bean
public class ClienteCsvView extends AbstractView {

    // constructor
    public ClienteCsvView() {
        setContentType("text/csv");
    }

    // como es un archivo que se desacarga necesitamos este metodo
    @Override
    protected boolean generatesDownloadContent() {

        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        response.setHeader("Content-Disposition", "attachment; filename=\"clientes.csv\"");
        // pasar el contenttype a la respuesta(este contenttype es de la clase
        // abstractview que estamos heredando)
        response.setContentType(getContentType());
        // obtener el listado de clientesque hace referecnia a clientecontroller al
        // metodo listar model.addAttribute("clientes", clientes);
        
        Page<Cliente> clientes = (Page<Cliente>) model.get("clientes");

        // almacenar el archivo plano en el response, config por defecto de nuestro
        // archivo plano(configura que sea separado por comas y tambien saltos de linea)
        ICsvBeanWriter beanWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);

        // arreglo de string que tiene los atributos de nuestra clase a onvertir en csv
        String[] header = { "id", "nombre", "apellido", "email", "createAt" };
        //escribir una linea para el header
        beanWriter.writeHeader(header);

        //iterar atraves de los clientes y guardamos cada objeto en el archivo plano 
        for (Cliente cliente : clientes) {
            beanWriter.write(cliente, header);
            
        }
        //cerramos el bean writer
        beanWriter.close();

    }

}
