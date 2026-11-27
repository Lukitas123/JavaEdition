package modelo;

import entidades.cofres.Cofre;
import entidades.monstruos.Monstruo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Una grilla de terreno con una entrada, una salida opcional, las celdas visitadas
 * y los monstruos y cofres que viven en ella.
 * El plano solo conoce las abstracciones Monstruo y Cofre, no sus tipos concretos.
 * Las posiciones van de 1 a n. Esta clase nunca imprime ni dibuja.
 */
public class Plano extends Tablero<Terreno> {
    private final Set<Posicion> visitadas = new HashSet<>();
    private final Posicion entrada;
    private final Posicion salida;
    private final List<Monstruo> monstruos = new LinkedList<>();
    private final List<Cofre> cofres = new LinkedList<>();
    private final EfectosPlano efectos;

    public Plano(Terreno[][] terreno, Posicion entrada, Posicion salida) {
        super(terreno);

        efectos = new EfectosPlano(this);

        if (entrada == null || !esTransitable(entrada)) {
            throw new IllegalArgumentException(
                    "La entrada debe ser una celda transitable del plano");
        }
        if (salida != null && !esTransitable(salida)) {
            throw new IllegalArgumentException(
                    "La salida debe ser una celda transitable del plano");
        }

        this.entrada = entrada;
        this.salida = salida;
    }

    public Posicion getEntrada() {
        return entrada;
    }

    /** @return la posición de la salida, o null si este plano no la tiene. */
    public Posicion getSalida() {
        return salida;
    }

    public boolean tieneSalida() {
        return salida != null;
    }

    // ---------- Terreno ----------

    @Override
    public Terreno celdaEn(Posicion posicion) {
        return efectos.terrenoVisible(posicion);
    }

    public Terreno terrenoEn(Posicion posicion) {
        return celdaEn(posicion);
    }

    public Terreno terrenoBaseEn(Posicion posicion) {
        return super.celdaEn(posicion);
    }

    @Override
    public void cambiarCelda(Posicion posicion, Terreno nuevo) {
        super.cambiarCelda(posicion, nuevo);
        efectos.cancelarEn(posicion);
    }

    public void cambiarTerreno(Posicion posicion, Terreno nuevo) {
        cambiarCelda(posicion, nuevo);
    }

    /** Transitable es todo lo que no es pared. Fuera del plano no es transitable. */
    public boolean esTransitable(Posicion posicion) {
        return estaDentro(posicion) && terrenoEn(posicion) != Terreno.PARED;
    }

    /** Vecinos (arriba, abajo, izquierda, derecha) que son transitables. */
    public List<Posicion> vecinosTransitables(Posicion posicion) {
        List<Posicion> resultado = new ArrayList<>();
        for (Direccion direccion : Direccion.values()) {
            Posicion vecino = posicion.mover(direccion);
            if (esTransitable(vecino)) {
                resultado.add(vecino);
            }
        }
        return resultado;
    }

    // ---------- Visitadas ----------

    public void marcarVisitada(Posicion posicion) {
        validarDentro(posicion);
        visitadas.add(posicion);
    }

    public boolean estaVisitada(Posicion posicion) {
        validarDentro(posicion);
        return visitadas.contains(posicion);
    }

    // ---------- Monstruos ----------

    public List<Monstruo> getMonstruos() {
        return monstruos;
    }

    /**
     * @throws IllegalArgumentException si el monstruo es null o está sobre una celda no transitable.
     */
    public void agregarMonstruo(Monstruo monstruo) {
        if (monstruo == null) {
            throw new IllegalArgumentException("El monstruo no puede ser null.");
        }
        if (!esTransitable(monstruo.getPosicion())) {
            throw new IllegalArgumentException("El monstruo debe estar sobre una celda transitable.");
        }
        monstruos.add(monstruo);
    }

    /** @return true si el monstruo estaba en el plano y se quitó. */
    public boolean quitarMonstruo(Monstruo monstruo) {
        return monstruos.remove(monstruo);
    }

    /** @return el monstruo en esa posición, o null si no hay ninguno. */
    public Monstruo monstruoEn(Posicion posicion) {
        for (Monstruo monstruo : monstruos) {
            if (monstruo.getPosicion().equals(posicion)) {
                return monstruo;
            }
        }
        return null;
    }

    // ---------- Cofres ----------

    public List<Cofre> getCofres() {
        return cofres;
    }

    /**
     * @throws IllegalArgumentException si el cofre es null o está sobre una celda no transitable.
     */
    public void agregarCofre(Cofre cofre) {
        if (cofre == null) {
            throw new IllegalArgumentException("El cofre no puede ser null.");
        }
        if (!esTransitable(cofre.getPosicion())) {
            throw new IllegalArgumentException("El cofre debe estar sobre una celda transitable.");
        }
        cofres.add(cofre);
    }

    /** @return true si el cofre estaba en el plano y se quitó. */
    public boolean quitarCofre(Cofre cofre) {
        return cofres.remove(cofre);
    }

    /** @return el cofre en esa posición, o null si no hay ninguno. */
    public Cofre cofreEn(Posicion posicion) {
        for (Cofre cofre : cofres) {
            if (cofre.getPosicion().equals(posicion)) {
                return cofre;
            }
        }
        return null;
    }

    public EfectosPlano getEfectos() {
        return efectos;
    }
}
