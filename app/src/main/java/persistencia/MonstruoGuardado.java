package persistencia;

import modelo.Posicion;

/** Datos de un monstruo (simplificado) */
public record MonstruoGuardado(String tipo, Posicion posicion, Posicion posicionInicial, boolean activo, String estado){
}
