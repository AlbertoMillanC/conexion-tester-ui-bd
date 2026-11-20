/**
 * ErrorDeDatosException.java
 */
package edu.iudigital.ea3.datos;

public class ErrorDeDatosException extends Exception {

    public ErrorDeDatosException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
