/**
 * ExportadorCsv.java
 */
package edu.iudigital.ea3.util;

import edu.iudigital.ea3.modelo.Producto;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

public class ExportadorCsv {

    private static final String CABECERA = "id,nombre,categoria,cantidad,precio,valor_en_bodega";

    public int exportar(List<Producto> productos, Path destino) throws IOException {
        try (BufferedWriter escritor = Files.newBufferedWriter(destino, StandardCharsets.UTF_8)) {
            escritor.write(CABECERA);
            escritor.newLine();
            for (Producto producto : productos) {
                escritor.write(linea(producto));
                escritor.newLine();
            }
        }
        return productos.size();
    }

    private String linea(Producto producto) {
        return producto.id()
                + "," + proteger(producto.nombre())
                + "," + proteger(producto.categoria())
                + "," + producto.cantidad()
                + "," + String.format(Locale.US, "%.2f", producto.precio())
                + "," + String.format(Locale.US, "%.2f", producto.valorEnBodega());
    }

    private String proteger(String valor) {
        if (valor == null) {
            return "";
        }
        if (valor.contains(",") || valor.contains("\"")) {
            return "\"" + valor.replace("\"", "\"\"") + "\"";
        }
        return valor;
    }
}
