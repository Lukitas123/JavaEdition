package generador;

import estructuras.Cola;
import estructuras.ColaDePrioridad;
import estructuras.GrafoConPesos;
import estructuras.Lista;
import estructuras.Pila;
import estructuras.TablaHash;
import modelo.Costo;
import modelo.Plano;
import modelo.Posicion;
import modelo.Terreno;

/**
 * Representa el grafo de celdas transitables de un Plano, donde cada vértice es una Posicion
 * y cada arista dirigida hacia un vecino tiene como peso el costo de energía del terreno destino.
 */
public class GrafoDelPlano {
    private final Plano plano;
    private final Costo costo;
    private final GrafoConPesos<Posicion> grafo;

    /**
     * Par auxiliar (posición y costo acumulado) para ordenar en la ColaDePrioridad de Dijkstra.
     */
    private record PasoDijkstra(Posicion posicion, int costoAcumulado) implements Comparable<PasoDijkstra> {
        @Override
        public int compareTo(PasoDijkstra otro) {
            return Integer.compare(this.costoAcumulado, otro.costoAcumulado);
        }
    }

    /**
     * Construye el grafo del plano asignando a cada arista el costo del terreno de destino.
     *
     * @param plano plano del cual extraer las celdas transitables y sus terrenos (no null).
     * @param costo costos de energía para piso, agua y lava (no null, valores >= 0).
     * @throws IllegalArgumentException si el plano o el costo son null, o si algún costo es negativo.
     */
    public GrafoDelPlano(Plano plano, Costo costo) {
        if (plano == null) {
            throw new IllegalArgumentException("El plano no puede ser null.");
        }
        if (costo == null) {
            throw new IllegalArgumentException("El costo no puede ser null.");
        }
        if (costo.piso() < 0 || costo.agua() < 0 || costo.lava() < 0) {
            throw new IllegalArgumentException("Los costos de terreno no pueden ser negativos.");
        }

        this.plano = plano;
        this.costo = costo;
        this.grafo = new GrafoConPesos<>();

        construirGrafo();
    }

    /**
     * Construye el grafo del plano con costo uniforme (1 para todos los terrenos transitables, algun fantasma o entidad que pueda transitar todas las celdas puede usarlo).
     *
     * @param plano plano del cual extraer las celdas transitables (no null).
     */
    public GrafoDelPlano(Plano plano) {
        this(plano, new Costo(1, 1, 1));
    }

    // ---------- Construcción del grafo ----------

    private void construirGrafo() {
        for (int f = 1; f <= plano.getFilas(); f++) {
            for (int c = 1; c <= plano.getColumnas(); c++) {
                Posicion actual = new Posicion(f, c);
                if (plano.esTransitable(actual)) {
                    grafo.agregarVertice(actual);

                    Lista<Posicion> vecinos = plano.vecinosTransitables(actual);
                    for (int i = 0; i < vecinos.cantidad(); i++) {
                        Posicion vecino = vecinos.obtener(i);
                        int peso = costoDeTerreno(plano.terrenoEn(vecino));
                        grafo.agregarArista(actual, vecino, peso);
                    }
                }
            }
        }
    }

    private int costoDeTerreno(Terreno terreno) {
        return switch (terreno) {
            case PISO -> costo.piso();
            case AGUA -> costo.agua();
            case LAVA -> costo.lava();
            case PARED -> throw new IllegalArgumentException("Una pared no es transitable.");
        };
    }

    // ---------- Getters del grafo ----------

    public Plano getPlano() {
        return plano;
    }

    public Costo getCosto() {
        return costo;
    }

    public int cantidadVertices() {
        return grafo.cantidadVertices();
    }

    public boolean contienePosicion(Posicion posicion) {
        return grafo.contieneVertice(posicion);
    }

    public boolean sonAdyacentes(Posicion origen, Posicion destino) {
        return grafo.contieneArista(origen, destino);
    }

    public int obtenerCostoMovimiento(Posicion origen, Posicion destino) {
        return grafo.obtenerPeso(origen, destino);
    }

    /**
     * Devuelve una lista con las posiciones vecinas transitables conectadas desde {@code posicion}.
     *
     * @throws IllegalArgumentException si la posición es null o no pertenece al grafo.
     */
    public Lista<Posicion> obtenerVecinos(Posicion posicion) {
        if (!contienePosicion(posicion)) {
            throw new IllegalArgumentException("La posicion no pertenece al grafo: " + posicion);
        }
        Lista<GrafoConPesos.Arista<Posicion>> aristas = grafo.obtenerAdyacentes(posicion);
        Lista<Posicion> vecinos = new Lista<>();
        for (int i = 0; i < aristas.cantidad(); i++) {
            vecinos.agregar(aristas.obtener(i).getDestino().getDato());
        }
        return vecinos;
    }


}
