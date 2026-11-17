package entidades;

import modelo.Posicion;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

/**
 * Catalogo de fabricas de entidades identificadas por nombre.
 *
 * @param <T> tipo de entidad que crean las fabricas (ej. monstruos, cofres)
 */
public class Catalogo <T>{
    private final Map<String, Function<Posicion, T>> fabricas = new LinkedHashMap<>();

    /**
     * Registra una fabrica con un nombre unico.
     *
     * Pre: nombre y fabrica no son null; nombre no esta vacio.
     * Post: la fabrica queda disponible para crear entidades.
     */
    public void registrar(String nombre, Function<Posicion, T> fabrica){
        if(nombre==null || nombre.isBlank() ||fabrica==null){
            throw new IllegalArgumentException("El nombre y la fabrica son obligatorios");
        }
        if(fabricas.containsKey(nombre)){
            throw new IllegalArgumentException("El tipo ya esta registrado " + nombre);
        }
        fabricas.put(nombre, fabrica);
    }

    /**
     * Crea una entidad del tipo indicado en la posicion recibida.
     *
     * Pre: el nombre esta registrado y la posicion no es null.
     * Post: devuelve la entidad creada por la fabrica.
     */
    public T crear(String nombre, Posicion posicion) {
        if (posicion == null) {
            throw new IllegalArgumentException(
                    "La posicion no puede ser null");
        }

        Function<Posicion, T> fabrica = fabricas.get(nombre);
        if (fabrica == null) {
            throw new IllegalArgumentException(
                    "Tipo no registrado: " + nombre);
        }

        return fabrica.apply(posicion);
    }

    /**
     * Sortea un tipo registrado y crea una entidad en la posicion indicada.
     *
     * Pre: posicion y random no son null; el catalogo no esta vacio.
     * Post: devuelve una entidad creada por la fabrica sorteada.
     */
    public T sortear(Posicion posicion, Random random) {
        if (posicion == null || random == null) {
            throw new IllegalArgumentException(
                    "La posicion y el generador aleatorio son obligatorios");
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
