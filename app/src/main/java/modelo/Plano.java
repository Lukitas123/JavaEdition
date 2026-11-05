package modelo;

import entidades.cofres.Cofre;
import entidades.monstruos.Monstruo;
import estructuras.Lista;

/**
 * Una grilla de terreno con una entrada, una salida opcional, las celdas visitadas
 * y los monstruos y cofres que viven en ella.
 * El plano solo conoce las abstracciones Monstruo y Cofre, no sus tipos concretos.
 * Las posiciones van de 1 a n. Esta clase nunca imprime ni dibuja.
 */
public class Plano {
    private final int filas;
    private final int columnas;
    private final Terreno[][] celdas;
    private final boolean[][] visitadas;
    private final Posicion entrada;
    private final Posicion salida; // null si este plano no tiene la salida
    private final Lista<Monstruo> monstruos = new Lista<>();
    private final Lista<Cofre> cofres = new Lista<>();

    /**
     * @param terreno grilla rectangular de terreno (se copia).
     * @param entrada donde aparece el héroe; debe ser una celda transitable.
     * @param salida  posición de la salida, o null si el plano no la tiene.
     * @throws IllegalArgumentException si la grilla es inválida o la entrada/salida no son válidas.
     */
    public Plano(Terreno[][] terreno, Posicion entrada, Posicion salida) {
        if (terreno == null || terreno.length == 0 || terreno[0].length == 0) {
            throw new IllegalArgumentException("El terreno no puede estar vacio.");
        }
        this.filas = terreno.length;
        this.columnas = terreno[0].length;
        this.celdas = new Terreno[filas][columnas];
        for (int f = 0; f < filas; f++) {
            if (terreno[f].length != columnas) {
                throw new IllegalArgumentException("Todas las filas deben tener el mismo largo.");
            }
            for (int c = 0; c < columnas; c++) {
                if (terreno[f][c] == null) {
                    throw new IllegalArgumentException("Celda nula en " + (f + 1) + "," + (c + 1));
                }
                this.celdas[f][c] = terreno[f][c];
            }
        }
        this.visitadas = new boolean[filas][columnas];

        if (entrada == null || !esTransitable(entrada)) {
            throw new IllegalArgumentException("La entrada debe ser una celda transitable del plano.");
        }
        if (salida != null && !esTransitable(salida)) {
            throw new IllegalArgumentException("La salida debe ser una celda transitable del plano.");
        }
        this.entrada = entrada;
        this.salida = salida;
    }

    public int getFilas() {
        return filas;
    }

    public int getColumnas() {
        return columnas;
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

    /** @return true si la posición está dentro de la grilla (1..filas, 1..columnas). */
    public boolean estaDentro(Posicion posicion) {
        return posicion.fila() >= 1 && posicion.fila() <= filas
                && posicion.columna() >= 1 && posicion.columna() <= columnas;
    }

    /** @throws IllegalArgumentException si la posición está fuera del plano. */
    public Terreno terrenoEn(Posicion posicion) {
        validarDentro(posicion);
        return celdas[posicion.fila() - 1][posicion.columna() - 1];
    }

    /** Cambia el terreno de una celda (lo usan Puente, EfectoDeLava, escaleras). */
    public void cambiarTerreno(Posicion posicion, Terreno nuevo) {
        validarDentro(posicion);
        if (nuevo == null) {
            throw new IllegalArgumentException("El terreno no puede ser null.");
        }
        celdas[posicion.fila() - 1][posicion.columna() - 1] = nuevo;
    }

    /** Transitable es todo lo que no es pared. Fuera del plano no es transitable. */
    public boolean esTransitable(Posicion posicion) {
        return estaDentro(posicion) && terrenoEn(posicion) != Terreno.PARED;
    }

    /** Vecinos (arriba, abajo, izquierda, derecha) que son transitables. */
    public Lista<Posicion> vecinosTransitables(Posicion posicion) {
        Lista<Posicion> resultado = new Lista<>();
        for (Direccion direccion : Direccion.values()) {
            Posicion vecino = posicion.mover(direccion);
            if (esTransitable(vecino)) {
                resultado.agregar(vecino);
            }
        }
        return resultado;
    }

    // ---------- Visitadas ----------

    public void marcarVisitada(Posicion posicion) {
        validarDentro(posicion);
        visitadas[posicion.fila() - 1][posicion.columna() - 1] = true;
    }

    public boolean estaVisitada(Posicion posicion) {
        validarDentro(posicion);
        return visitadas[posicion.fila() - 1][posicion.columna() - 1];
    }

    // ---------- Monstruos ----------

    public Lista<Monstruo> getMonstruos() {
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
        monstruos.agregar(monstruo);
    }

    /** @return true si el monstruo estaba en el plano y se quitó. */
    public boolean quitarMonstruo(Monstruo monstruo) {
        for (int i = 0; i < monstruos.cantidad(); i++) {
            if (monstruos.obtener(i) == monstruo) {
                monstruos.eliminar(i);
                return true;
            }
        }
        return false;
    }

    /** @return el monstruo en esa posición, o null si no hay ninguno. */
    public Monstruo monstruoEn(Posicion posicion) {
        for (int i = 0; i < monstruos.cantidad(); i++) {
            Monstruo monstruo = monstruos.obtener(i);
            if (monstruo.getPosicion().equals(posicion)) {
                return monstruo;
            }
        }
        return null;
    }

    // ---------- Cofres ----------

    public Lista<Cofre> getCofres() {
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
        cofres.agregar(cofre);
    }

    /** @return true si el cofre estaba en el plano y se quitó. */
    public boolean quitarCofre(Cofre cofre) {
        for (int i = 0; i < cofres.cantidad(); i++) {
            if (cofres.obtener(i) == cofre) {
                cofres.eliminar(i);
                return true;
            }
        }
        return false;
    }

    /** @return el cofre en esa posición, o null si no hay ninguno. */
    public Cofre cofreEn(Posicion posicion) {
        for (int i = 0; i < cofres.cantidad(); i++) {
            Cofre cofre = cofres.obtener(i);
            if (cofre.getPosicion().equals(posicion)) {
                return cofre;
            }
        }
        return null;
    }

    private void validarDentro(Posicion posicion) {
        if (!estaDentro(posicion)) {
            throw new IllegalArgumentException("Posicion fuera del plano: " + posicion);
        }
    }
}
