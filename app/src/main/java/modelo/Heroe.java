package modelo;

import estructuras.Lista;
import estructuras.TablaHash;

import java.util.HashMap;
import java.util.Map;

/**
 * héroe: guarda su energia, donde esta, cuantos cofres abrio,
 * su inventario y los efectos que tiene activos
 * No dibuja ni imprime nada
 */
public class Heroe {
    private int energia;
    private Ubicacion ubicacion;
    private int cofresAbiertos;
    private final Map<String, Integer> inventario = new HashMap<>();
    private final TablaHash<String, Boolean> efectosActivos = new TablaHash<>();

    /**
     * @param energiaInicial debe ser mayor a 0
     * @param ubicacion donde empieza, no puede ser null
     */
    public Heroe(int energiaInicial, Ubicacion ubicacion) {
        if (energiaInicial <= 0) {
            throw new IllegalArgumentException("La energia inicial debe ser positiva");
        }
        if (ubicacion == null) {
            throw new IllegalArgumentException("La ubicacion no puede ser null");
        }
        this.energia = energiaInicial;
        this.ubicacion = ubicacion;
        this.cofresAbiertos = 0;
    }

    public int getEnergia() {
        return energia;
    }

    public Ubicacion getUbicacion() {
        return ubicacion;
    }

    public int getCofresAbiertos() {
        return cofresAbiertos;
    }

    /**
     * Devuelve una copia no modificable del inventario.
     * Permite consultarlo sin alterar directamente el estado del heroe.
     */
    public Map<String, Integer> getInventario() {
        return Map.copyOf(inventario);
    }

    /** Resta energía. Nunca baja de 0. */
    public void perderEnergia(int cantidad) {
        validarNoNegativa(cantidad);
        energia = Math.max(0, energia - cantidad);
    }

    public void ganarEnergia(int cantidad) {
        validarNoNegativa(cantidad);
        energia += cantidad;
    }

    public boolean estaSinEnergia() {
        return energia == 0;
    }

    public void irA(Ubicacion destino) {
        if (destino == null) {
            throw new IllegalArgumentException("El destino no puede ser null.");
        }
        this.ubicacion = destino;
    }

    public void registrarCofreAbierto() {
        cofresAbiertos++;
    }

    // INVENTARIO --------------------------------------------------------

    /**
     * Agrega una unidad del objeto al inventario.
     *
     * Pre: el identificador no es null ni vacio.
     * Post: la cantidad de ese objeto aumenta en uno.
     */
    public void agregarObjeto(String idObjeto) {
        validarId(idObjeto);
        int cantidadActual = inventario.getOrDefault(idObjeto, 0);
        inventario.put(idObjeto, cantidadActual + 1);
    }

    /** Saca una unidad del objeto si existe

     * Pre: el identificador no es null ni vacio.
     * Post: si lo tenia, su cantidad disminuye en uno.
     * Si era la ultima unidad, elimina la entrada del inventario.
     *
     * @return true si quito una unidad; false si no tenia el objeto
     * */
    public boolean quitarObjeto(String idObjeto) {
        validarId(idObjeto);
        int cantidadActual = inventario.getOrDefault(idObjeto, 0);

        if (cantidadActual == 0) {
            return false;
        }

        if (cantidadActual == 1) {
            inventario.remove(idObjeto);
        } else {
            inventario.put(idObjeto, cantidadActual - 1);
        }

        return true;
    }
    /**
     * Indica si el heroe tiene al menos una unidad del objeto.
     *
     * Pre: el identificador no es null ni vacio.
     */
    public boolean tieneObjeto(String idObjeto) {
        validarId(idObjeto);
        return inventario.containsKey(idObjeto);
    }

    /**
     * Devuelve la cantidad de unidades de un objeto.
     *
     * Pre: el identificador no es null ni vacio.
     * @return cantidad disponible, o cero si no tiene el objeto
     */
    public int cantidadDe(String idObjeto) {
        validarId(idObjeto);
        return inventario.getOrDefault(idObjeto, 0);
    }
    public int cantidadDeObjetos() {
        int total = 0;

        for (int cantidad : inventario.values()) {
            total += cantidad;
        }

        return total;
    }

    // EFECTOS ACTIVOS -----------------------------------------------

    public void activarEfecto(String idEfecto) {
        validarId(idEfecto);
        efectosActivos.insertar(idEfecto, true);
    }

    public void desactivarEfecto(String idEfecto) {
        efectosActivos.eliminar(idEfecto);
    }

    public boolean tieneEfecto(String idEfecto) {
        return efectosActivos.contiene(idEfecto);
    }

    private void validarNoNegativa(int cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa");
        }
    }

    private void validarId(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El identificador no puede ser nulo ni vacio");
        }
    }
    //Devuelve un listado de los efectos activos
    public Lista<String> getListaEfectos(){
        return efectosActivos.claves();
    }
}