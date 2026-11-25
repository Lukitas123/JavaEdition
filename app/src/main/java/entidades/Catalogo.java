package entidades;

import modelo.Posicion;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

/**
 * Catalogo de fabricas identificadas por nombre.
 *
 * @param <T> tipo de entidad que crea cada fabrica
 */
public class Catalogo<T> {
    private final Map<String, Function<Posicion, T>> fabricas = new LinkedHashMap<>();

    /** Registra una fabrica con un nombre unico. */
    public void registrar(String nombre, Function<Posicion, T> fabrica) {
        if (nombre == null || nombre.isBlank() || fabrica == null) {
            throw new IllegalArgumentException("El nombre y la fabrica son obligatorios");
        }
        if (fabricas.containsKey(nombre)) {
            throw new IllegalArgumentException("El tipo ya esta registrado: " + nombre);
        }
        fabricas.put(nombre, fabrica);
    }

    /** Crea la entidad del tipo indicado usando la posicion recibida. */
    public T crear(String nombre, Posicion posicion) {
        if (posicion == null) {
            throw new IllegalArgumentException("La posicion soesn obligatoria");
        }

        Function<Posicion, T> fabrica = fabricas.get(nombre);
        if (fabrica == null) {
            throw new IllegalArgumentException("Tipo no registrado: " + nombre);
        }

        return fabrica.apply(posicion);
    }

    /** Sortea una fabrica y crea una entidad usando la posicion recibida. */
    public T sortear(Posicion posicion, Random random) {
        if (posicion == null || random == null) {
            throw new IllegalArgumentException("La posicion y el azar son obligatorios");
        }
        if (fabricas.isEmpty()) {
            throw new IllegalStateException("El catalogo esta vacio");
        }

        int elegido = random.nextInt(fabricas.size());
        int indice = 0;

        for (Function<Posicion, T> fabrica : fabricas.values()) {
            if (indice == elegido) {
                return fabrica.apply(posicion);
            }
            indice++;
        }

        throw new IllegalStateException("No se pudo seleccionar un tipo");
    }
}