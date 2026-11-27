package modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Grilla rectangular generica, sin reglas particulares de un juego.
 * Copia las filas recibidas, pero no clona los objetos de cada celda.
 */
public class Tablero<T> {
    private final int filas;
    private final int columnas;
    private final List<List<T>>celdas = new ArrayList<>();

    public Tablero(T[][] datos) {
        if (datos == null || datos.length == 0
                || datos[0] == null || datos[0].length == 0) {
            throw new IllegalArgumentException("La grilla no puede estar vacia");
        }

        filas = datos.length;
        columnas = datos[0].length;

        for (T[] datosFila : datos) {
            if (datosFila == null || datosFila.length != columnas) {
                throw new IllegalArgumentException(
                        "Todas las filas deben tener el mismo largo");
            }

            List<T> fila = new ArrayList<>(columnas);
            for (T dato : datosFila) {
                if (dato == null) {
                    throw new IllegalArgumentException(
                            "Las celdas no pueden ser null");
                }
                fila.add(dato);
            }
            celdas.add(fila);
        }
    }

    public int getFilas() {
        return filas;
    }

    public int getColumnas() {
        return columnas;
    }

    public final boolean estaDentro(Posicion posicion) {
        return posicion != null
                && posicion.fila() >= 1 && posicion.fila() <= filas
                && posicion.columna() >= 1 && posicion.columna() <= columnas;
    }

    public T celdaEn(Posicion posicion) {
        validarDentro(posicion);
        return celdas.get(posicion.fila() - 1)
                .get(posicion.columna() - 1);
    }

    public void cambiarCelda(Posicion posicion, T nuevo) {
        validarDentro(posicion);
        if (nuevo == null) {
            throw new IllegalArgumentException("La celda no puede ser null");
        }
        celdas.get(posicion.fila() - 1)
                .set(posicion.columna() - 1, nuevo);
    }

    protected final void validarDentro(Posicion posicion) {
        if (!estaDentro(posicion)) {
            throw new IllegalArgumentException(
                    "Posicion fuera del tablero: " + posicion);
        }
    }
}
