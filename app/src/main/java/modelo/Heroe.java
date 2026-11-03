package modelo;

import estructuras.Lista;
import estructuras.TablaHash;

/**
 * héroe: guarda su energia, donde esta, cuantos cofres abrio,
 * su inventario y los efectos que tiene activos
 * No dibuja ni imprime nada
 */
public class Heroe {
    private int energia;
    private Ubicacion ubicacion;
    private int cofresAbiertos;
    private final Lista<String> inventario = new Lista<>();
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

    public void agregarObjeto(String idObjeto) {
        validarId(idObjeto);
        inventario.agregar(idObjeto);
    }

    /** Saca una unidad del objeto, retorna true si lo tenía */
    public boolean quitarObjeto(String idObjeto) {
        return inventario.eliminarElemento(idObjeto);
    }

    public boolean tieneObjeto(String idObjeto) {
        return inventario.contiene(idObjeto);
    }

    public int cantidadDeObjetos() {
        return inventario.cantidad();
    }

    /** objeto en el indice dado (base 0), sin sacarlo, sirve para listar el inventario */
    public String objetoEn(int indice) {
        return inventario.obtener(indice);
    }

    /** saca y devuelve el objeto del indice dado (lo usa el Ladrón) */
    public String quitarObjetoEn(int indice) {
        return inventario.eliminar(indice);
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
}