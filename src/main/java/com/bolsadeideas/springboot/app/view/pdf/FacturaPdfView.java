package com.bolsadeideas.springboot.app.view.pdf;

import java.awt.Color;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.view.document.AbstractPdfView;

import com.bolsadeideas.springboot.app.models.entity.Factura;
import com.bolsadeideas.springboot.app.models.entity.ItemFactura;
import com.lowagie.text.Document;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component("factura/ver") // para que se pueda renderizar le damos el mismo nombre del return de 'ver' en
                          // facturacontroller, para que en vez de mostrar el contenido en una pagina html
                          // lo muestre en un PDF
public class FacturaPdfView extends AbstractPdfView {

    //primera forma de traducir 
    @Autowired
    private MessageSource messageSource;

    @Autowired
    private LocaleResolver localeResolver;

    /**
     * @param model    guarda datos a la vista
     * @param document representa al PDF
     */
    @Override
    protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        // obtenenmos la factura a traves de los datos que manda la vista (model), se
        // tiene que llamar igual al objeto que se guarda en 'ver ' de facturacontroller
        Factura factura = (Factura) model.get("factura");

        Locale locale = localeResolver.resolveLocale(request);

        // segunda forma de traducir (este objeto tiene el locale y el messageSource por
        // debajo)
        MessageSourceAccessor mensajes = getMessageSourceAccessor();

        // tabla con una columna y tres filas
        PdfPTable tabla = new PdfPTable(1);
        // espacio despues de la tabla
        tabla.setSpacingAfter(20);

        PdfPCell cell = null;
        // constructor: ponemos la frase que tendra la celda(forma sin traducir)
        // cell = new PdfPCell(new Phrase("Datos del cliente"));
        // traducido
        cell = new PdfPCell(new Phrase(messageSource.getMessage("text.factura.ver.datos.cliente", null, locale)));
        // asignar color rgb
        cell.setBackgroundColor(new Color(184, 218, 255));
        cell.setPadding(8f);
        tabla.addCell(cell);

        tabla.addCell(factura.getCliente().getNombre() + "" + factura.getCliente().getApellido());
        tabla.addCell(factura.getCliente().getEmail());

        // tabla 2
        PdfPTable tabla2 = new PdfPTable(1);
        tabla2.setSpacingAfter(20);

        cell = new PdfPCell(new Phrase(messageSource.getMessage("text.factura.ver.datos.factura", null, locale)));
        // asignar color rgb
        cell.setBackgroundColor(new Color(195, 230, 203));
        cell.setPadding(8f);

        tabla2.addCell(cell);
        //2° forma de traducir 
        tabla2.addCell(mensajes.getMessage("text.cliente.factura.folio") + ": " + factura.getId());
        tabla2.addCell(mensajes.getMessage("text.cliente.factura.descripcion") + ": " + factura.getDescripcion());
        tabla2.addCell(mensajes.getMessage("text.cliente.factura.fecha") + ": " + factura.getCreateAt());

        document.add(tabla);
        document.add(tabla2);

        // tabla 3

        PdfPTable tabla3 = new PdfPTable(4);
        // cambiar el tamaño de 'producto'(3.5) usamos un arrelog de float para tener
        // mayor presicion , donde tenemos las medidas de cada columna
        tabla3.setWidths(new float[] { 3.5f, 1, 1, 1 });

        tabla3.addCell(mensajes.getMessage("text.factura.form.item.nombre"));
        tabla3.addCell(mensajes.getMessage("text.factura.form.item.precio"));
        tabla3.addCell(mensajes.getMessage("text.factura.form.item.cantidad"));
        tabla3.addCell(mensajes.getMessage("text.factura.form.item.total"));

        for (ItemFactura item : factura.getItems()) {
            tabla3.addCell(item.getProducto().getNombre());
            tabla3.addCell(item.getProducto().getPrecio().toString());

            cell = new PdfPCell(new Phrase(item.getCantidad().toString()));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            tabla3.addCell(cell);
            tabla3.addCell(item.calcularImporte().toString());
        }

        // creamos un objeto celda
        cell = new PdfPCell(new Phrase(mensajes.getMessage("text.factura.form.total") + ": "));
        // que ocupe tres columnas
        cell.setColspan(3);
        // alinear a la derecha
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        tabla3.addCell(cell);
        tabla3.addCell(factura.getTotal().toString());

        document.add(tabla3);
    }

}
