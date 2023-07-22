package com.bolsadeideas.springboot.app.view.xlsx;

import java.util.Map;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractXlsxView;

import com.bolsadeideas.springboot.app.models.entity.Factura;
import com.bolsadeideas.springboot.app.models.entity.ItemFactura;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component("factura/ver.xlsx") // para diferenciar el nombre de facturapdfview
public class FacturaXlsxView extends AbstractXlsxView {

    /**
     * @param model    para enviar datos a la vista
     * @param workbook es el libro excel
     */
    @Override
    protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        // cambiar el nombre del archivo (nombre del atributo del header,nombre del
        // archivo(tiene que llevar comillas))
        response.setHeader("Content-Disposition", "attachment; filename=\"factura_view.xlsx\"");

        // obtenemos el objeto factura del controlador
        Factura factura = (Factura) model.get("factura");

        // creamos la hoja
        Sheet sheet = workbook.createSheet("Factura Spring");

        // traduciendo los textos
        MessageSourceAccessor mensajes = getMessageSourceAccessor();

        // primera fila primera columna
        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        // froma traducida hace referencia a messages_es.properties
        cell.setCellValue(mensajes.getMessage("text.factura.ver.datos.cliente"));

        // fila 2 columna 0
        row = sheet.createRow(1);
        cell = row.createCell(0);
        cell.setCellValue(factura.getCliente().getNombre() + " " + factura.getCliente().getApellido());

        // fila 3 columna 0
        row = sheet.createRow(2);
        cell = row.createCell(0);
        cell.setCellValue(factura.getCliente().getEmail());

        // fila 5 (la 4 esta en blanco para dar espacio)
        sheet.createRow(4).createCell(0).setCellValue(mensajes.getMessage("text.factura.ver.datos.factura"));
        // 6
        sheet.createRow(5).createCell(0)
                .setCellValue(mensajes.getMessage("text.cliente.factura.folio") + ": " + factura.getId());
        // 7
        sheet.createRow(6).createCell(0).setCellValue(
                mensajes.getMessage("text.cliente.factura.descripcion") + ": " + factura.getDescripcion());
        // 8
        sheet.createRow(7).createCell(0)
                .setCellValue(mensajes.getMessage("text.cliente.factura.fecha") + ": " + factura.getCreateAt());

        // DETALLE DE LA FACTURA
        CellStyle theaderStyle = workbook.createCellStyle();
        theaderStyle.setBorderBottom(BorderStyle.MEDIUM);
        theaderStyle.setBorderTop(BorderStyle.MEDIUM);
        theaderStyle.setBorderRight(BorderStyle.MEDIUM);
        theaderStyle.setBorderLeft(BorderStyle.MEDIUM);
        // para obtener el index del color de fondo
        theaderStyle.setFillForegroundColor(IndexedColors.GOLD.index);
        // tipo de patron solido
        theaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle tbodyStyle = workbook.createCellStyle();
        tbodyStyle.setBorderBottom(BorderStyle.THIN);
        tbodyStyle.setBorderTop(BorderStyle.THIN);
        tbodyStyle.setBorderRight(BorderStyle.THIN);
        tbodyStyle.setBorderLeft(BorderStyle.THIN);

        Row header = sheet.createRow(9);
        header.createCell(0).setCellValue(mensajes.getMessage("text.factura.form.item.nombre"));
        header.createCell(1).setCellValue(mensajes.getMessage("text.factura.form.item.precio"));
        header.createCell(2).setCellValue(mensajes.getMessage("text.factura.form.item.cantidad"));
        header.createCell(3).setCellValue(mensajes.getMessage("text.factura.form.item.total"));

        header.getCell(0).setCellStyle(theaderStyle); // estilo de celda Producto
        header.getCell(1).setCellStyle(theaderStyle); // ""precio
        header.getCell(2).setCellStyle(theaderStyle);
        header.getCell(3).setCellStyle(theaderStyle);
        int rownum = 10; // por que ya tenemos nueve filas ocupadas
        for (ItemFactura item : factura.getItems()) {

            Row fila = sheet.createRow(rownum++);
            // la columna 0 corresponde al nombre del producto y añadimos estilo
            cell = fila.createCell(0);
            cell.setCellValue(item.getProducto().getNombre());
            cell.setCellStyle(tbodyStyle);
            // columna 1
            cell = fila.createCell(1);
            cell.setCellValue(item.getProducto().getPrecio());
            cell.setCellStyle(tbodyStyle);

            cell = fila.createCell(2);
            cell.setCellValue(item.getCantidad());
            cell.setCellStyle(tbodyStyle);

            cell = fila.createCell(3);
            cell.setCellValue(item.calcularImporte());
            cell.setCellStyle(tbodyStyle);

        }
        // se crea la fila en el valor del contador despues de acabar el for
        // añadimos estilos
        Row filatotal = sheet.createRow(rownum);
        cell = filatotal.createCell(2);
        cell.setCellValue(mensajes.getMessage("text.factura.form.total") + ": ");
        cell.setCellStyle(tbodyStyle);

        cell = filatotal.createCell(3);
        cell.setCellValue(factura.getTotal());
        cell.setCellStyle(tbodyStyle);

    }

}
