package modelo;

import estructuras.Cola;
import estructuras.Lista;

/**
 * El mundo: guarda los planos (cada uno identificado por tablero y piso) y las
 * conexiones entre ellos (escaleras y pasillos)
 * Tablero y piso van de 1 a n, igual que las posiciones
 * Esta clase nunca imprime ni dibuja
 */
public class Mundo {
    /** Identifica un plano dentro del mundo (vértice del grafo de planos). */
    private record IdPlano(int tablero, int piso) {
    }

    private final int tableros;
    private final int pisos;
    private final Plano[][] planos;
    private final Lista<Conexion> conexiones = new Lista<>();

    /**
     * @param tableros cantidad de tableros, al menos 1
     * @param pisos    cantidad de pisos por tablero, al menos 1
     */
    public Mundo(int tableros, int pisos) {
        if (tableros < 1 || pisos < 1) {
            throw new IllegalArgumentException("El mundo necesita al menos un tablero y un piso");
        }
        this.tableros = tableros;
        this.pisos = pisos;
        this.planos = new Plano[tableros][pisos];
    }

    public int getTableros() {
        return tableros;
    }

    public int getPisos() {
        return pisos;
    }

    // PLANOS ---------------------------------

    /**
     * Carga un plano en el tablero y piso indicados
     *
     * @throws IllegalArgumentException si el tablero o piso no existen, si ya hay un plano
     *                                  ahí, si el plano es null o si el mundo ya tiene una salida
     *                                  en otro plano (solo uno de todo el mundo la tiene)
     */
    public void agregarPlano(int tablero, int piso, Plano plano) {
        validarIds(tablero, piso);
        if (plano == null) {
            throw new IllegalArgumentException("El plano no puede ser null");
        }
        if (planos[tablero - 1][piso - 1] != null) {
            throw new IllegalArgumentException("Ya hay un plano en tablero " + tablero + ", piso " + piso + ".");
        }
        if (plano.tieneSalida() && ubicacionDeSalida() != null) {
            throw new IllegalArgumentException("Solo un plano de todo el mundo puede tener la salida");
        }
        planos[tablero - 1][piso - 1] = plano;
    }

    /** @throws IllegalArgumentException si el tablero o piso no existen o no hay plano cargado */
    public Plano planoEn(int tablero, int piso) {
        validarIds(tablero, piso);
        Plano plano = planos[tablero - 1][piso - 1];
        if (plano == null) {
            throw new IllegalArgumentException("No hay plano cargado en tablero " + tablero + ", piso " + piso + ".");
        }
        return plano;
    }

    /** El plano donde está esa ubicación (la posición dentro del plano no se mira) */
    public Plano planoEn(Ubicacion ubicacion) {
        return planoEn(ubicacion.tablero(), ubicacion.piso());
    }

    /** Dónde empieza el héroe: la entrada del plano del tablero 1, piso 1 */
    public Ubicacion getInicio() {
        return new Ubicacion(1, 1, planoEn(1, 1).getEntrada());
    }

    /** @return dónde está la salida, o null si todavía no hay ningún plano con salida */
    public Ubicacion ubicacionDeSalida() {
        for (int t = 1; t <= tableros; t++) {
            for (int p = 1; p <= pisos; p++) {
                Plano plano = planos[t - 1][p - 1];
                if (plano != null && plano.tieneSalida()) {
                    return new Ubicacion(t, p, plano.getSalida());
                }
            }
        }
        return null;
    }

    // CONEXIONES -----------------------------------------

    public Lista<Conexion> getConexiones() {
        return conexiones;
    }

    /**
     * Agrega una conexión (escalera o pasillo) entre dos planos distintos
     * Cada extremo debe estar sobre una celda transitable que no sea entrada, salida
     * ni cofre, y no puede tener ya otra conexión
     *
     * @throws IllegalArgumentException si alguna de esas condiciones no se cumple
     */
    public void agregarConexion(Conexion conexion) {
        if (conexion == null) {
            throw new IllegalArgumentException("La conexion no puede ser null");
        }
        Ubicacion origen = conexion.origen();
        Ubicacion destino = conexion.destino();
        if (origen.tablero() == destino.tablero() && origen.piso() == destino.piso()) {
            throw new IllegalArgumentException("Una conexion debe unir dos planos distintos");
        }
        validarExtremo(origen);
        validarExtremo(destino);
        conexiones.agregar(conexion);
    }

    /** @return la conexión que tiene un extremo en esa ubicación, o null si no hay ninguna */
    public Conexion conexionEn(Ubicacion ubicacion) {
        for (int i = 0; i < conexiones.cantidad(); i++) {
            Conexion conexion = conexiones.obtener(i);
            if (conexion.origen().equals(ubicacion) || conexion.destino().equals(ubicacion)) {
                return conexion;
            }
        }
        return null;
    }

    // ALCANZABILIDAD ----------------------------------

    /**
     * Verifica con un BFS sobre el grafo de planos (plano = vértice, conexión = arista)
     * que la salida se pueda alcanzar desde el plano de inicio
     * Alcanza con mirar planos y no celdas porque todo plano válido es conexo (R5):
     * dentro de un plano se puede llegar a cualquier celda transitable
     *
     * @return false si no hay salida cargada, si falta el plano de inicio o si no hay camino
     */
    public boolean salidaEsAlcanzable() {
        Ubicacion salida = ubicacionDeSalida();
        if (salida == null || planos[0][0] == null) {
            return false;
        }

        boolean[][] visitado = new boolean[tableros][pisos];
        Cola<IdPlano> cola = new Cola<>();
        visitado[0][0] = true;
        cola.encolar(new IdPlano(1, 1));

        while (!cola.estaVacia()) {
            IdPlano actual = cola.desencolar();
            if (actual.tablero() == salida.tablero() && actual.piso() == salida.piso()) {
                return true;
            }
            for (int i = 0; i < conexiones.cantidad(); i++) {
                IdPlano vecino = vecinoDe(conexiones.obtener(i), actual);
                if (vecino != null && !visitado[vecino.tablero() - 1][vecino.piso() - 1]) {
                    visitado[vecino.tablero() - 1][vecino.piso() - 1] = true;
                    cola.encolar(vecino);
                }
            }
        }
        return false;
    }

    /** El plano del otro extremo de la conexión si toca a `actual`, o null si no lo toca */
    private IdPlano vecinoDe(Conexion conexion, IdPlano actual) {
        IdPlano a = new IdPlano(conexion.origen().tablero(), conexion.origen().piso());
        IdPlano b = new IdPlano(conexion.destino().tablero(), conexion.destino().piso());
        if (a.equals(actual)) {
            return b;
        }
        if (b.equals(actual)) {
            return a;
        }
        return null;
    }

    // VALIDACIONES ----------------------------------------

    private void validarIds(int tablero, int piso) {
        if (tablero < 1 || tablero > tableros || piso < 1 || piso > pisos) {
            throw new IllegalArgumentException("Tablero o piso fuera del mundo: " + tablero + ", " + piso);
        }
    }

    private void validarExtremo(Ubicacion extremo) {
        Plano plano = planoEn(extremo);
        Posicion posicion = extremo.posicion();
        if (!plano.esTransitable(posicion)) {
            throw new IllegalArgumentException("El extremo de la conexion debe estar sobre una celda transitable");
        }
        if (posicion.equals(plano.getEntrada()) || posicion.equals(plano.getSalida())) {
            throw new IllegalArgumentException("El extremo de la conexion no puede ser la entrada ni la salida");
        }
        if (plano.cofreEn(posicion) != null) {
            throw new IllegalArgumentException("El extremo de la conexion no puede estar sobre un cofre");
        }
        if (conexionEn(extremo) != null) {
            throw new IllegalArgumentException("Ya hay una conexion en esa celda");
        }
    }
}