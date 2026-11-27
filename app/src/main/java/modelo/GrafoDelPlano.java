package modelo;

import estructuras.GrafoConPesos;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class GrafoDelPlano {
    private final Plano plano;
    private final Costo costo;
    private final GrafoConPesos<Posicion> grafo;

    private record PasoDijkstra(Posicion posicion, int costoAcumulado) implements Comparable<PasoDijkstra> {
        @Override
        public int compareTo(PasoDijkstra otro) {
            return Integer.compare(this.costoAcumulado, otro.costoAcumulado);
        }
    }

    public GrafoDelPlano(Plano plano, Costo costo) {
        //compruebo que el plano y el costo no sean nulos ni tengan valores negativos
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

    public GrafoDelPlano(Plano plano) {
        this(plano, new Costo(1, 1, 1));
    }

    private void construirGrafo() {
        //recorro todas las celdas del plano y agrego como vertices las que son transitables
        for (int f = 1; f <= plano.getFilas(); f++) {
            for (int c = 1; c <= plano.getColumnas(); c++) {
                Posicion actual = new Posicion(f, c);
                if (plano.esTransitable(actual)) {
                    grafo.agregarVertice(actual);

                    //para cada vecino transitable agrego la arista con el peso de entrar a ese terreno
                    for (Posicion vecino : plano.vecinosTransitables(actual)) {
                        int peso = costoDeTerreno(plano.terrenoEn(vecino));
                        grafo.agregarArista(actual, vecino, peso);
                    }
                }
            }
        }
    }

    private int costoDeTerreno(Terreno terreno) {
        //devuelvo el costo de energia segun el tipo de terreno de la celda
        return switch (terreno) {
            case PISO -> costo.piso();
            case AGUA -> costo.agua();
            case LAVA -> costo.lava();
            case PARED -> throw new IllegalArgumentException("Una pared no es transitable.");
        };
    }

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

    public List<Posicion> obtenerVecinos(Posicion posicion) {
        if (!contienePosicion(posicion)) {
            throw new IllegalArgumentException("La posicion no pertenece al grafo: " + posicion);
        }
        List<Posicion> vecinos = new ArrayList<>();
        for (GrafoConPesos.Arista<Posicion> arista : grafo.obtenerAdyacentes(posicion)) {
            vecinos.add(arista.getDestino().getDato());
        }
        return vecinos;
    }

    public List<Posicion> caminoMinimo(Posicion origen, Posicion destino) {
        //si alguna de las posiciones no existe en el grafo, devuelvo una lista vacia
        if (!contienePosicion(origen) || !contienePosicion(destino)) {
            return new ArrayList<>();
        }

        Map<Posicion, Integer> distancias = new HashMap<>();
        Map<Posicion, Posicion> padres = new HashMap<>();
        PriorityQueue<PasoDijkstra> cola = new PriorityQueue<>();

        //arranco desde el origen con costo acumulado 0
        distancias.put(origen, 0);
        cola.add(new PasoDijkstra(origen, 0));

        while (!cola.isEmpty()) {
            PasoDijkstra actual = cola.poll();
            Posicion posActual = actual.posicion();
            int costoActual = actual.costoAcumulado();

            //si ya llegue al destino corto el ciclo
            if (posActual.equals(destino)) {
                break;
            }

            //si este paso quedo viejo porque ya encontre un costo menor antes, lo salto
            if (costoActual > distancias.get(posActual)) {
                continue;
            }

            //recorro las aristas adyacentes y relajo los costos si encuentro un camino mas barato
            for (GrafoConPesos.Arista<Posicion> arista : grafo.obtenerAdyacentes(posActual)) {
                Posicion vecino = arista.getDestino().getDato();
                int nuevoCosto = costoActual + arista.getPeso();

                if (!distancias.containsKey(vecino) || nuevoCosto < distancias.get(vecino)) {
                    distancias.put(vecino, nuevoCosto);
                    padres.put(vecino, posActual);
                    cola.add(new PasoDijkstra(vecino, nuevoCosto));
                }
            }
        }

        //si nunca alcance el destino devuelvo lista vacia
        if (!distancias.containsKey(destino)) {
            return new ArrayList<>();
        }

        return reconstruirCamino(origen, destino, padres);
    }

    public int costoCaminoMinimo(Posicion origen, Posicion destino) {
        //si alguna posicion no esta en el grafo devuelvo -1
        if (!contienePosicion(origen) || !contienePosicion(destino)) {
            return -1;
        }
        if (origen.equals(destino)) {
            return 0;
        }

        List<Posicion> camino = caminoMinimo(origen, destino);
        if (camino.isEmpty()) {
            return -1;
        }

        //sumo el peso de cada paso del camino minimo encontrado
        int costoTotal = 0;
        for (int i = 0; i < camino.size() - 1; i++) {
            Posicion desde = camino.get(i);
            Posicion hasta = camino.get(i + 1);
            costoTotal += grafo.obtenerPeso(desde, hasta);
        }
        return costoTotal;
    }

    public List<Posicion> caminoMasCortoEnPasos(Posicion origen, Posicion destino) {
        //busco el camino con menos casillas usando BFS con una cola (ArrayDeque)
        if (!contienePosicion(origen) || !contienePosicion(destino)) {
            return new ArrayList<>();
        }

        Set<Posicion> visitados = new HashSet<>();
        Map<Posicion, Posicion> padres = new HashMap<>();
        Deque<Posicion> cola = new ArrayDeque<>();

        visitados.add(origen);
        cola.addLast(origen);

        while (!cola.isEmpty()) {
            Posicion actual = cola.pollFirst();
            if (actual.equals(destino)) {
                break;
            }

            for (GrafoConPesos.Arista<Posicion> arista : grafo.obtenerAdyacentes(actual)) {
                Posicion vecino = arista.getDestino().getDato();
                if (!visitados.contains(vecino)) {
                    visitados.add(vecino);
                    padres.put(vecino, actual);
                    cola.addLast(vecino);
                }
            }
        }

        if (!visitados.contains(destino)) {
            return new ArrayList<>();
        }

        return reconstruirCamino(origen, destino, padres);
    }

    private List<Posicion> reconstruirCamino(Posicion origen, Posicion destino,
                                             Map<Posicion, Posicion> padres) {
        //retrocedo desde el destino hasta el origen apilando cada posicion para invertir el orden
        Deque<Posicion> pila = new ArrayDeque<>();
        Posicion cursor = destino;
        while (cursor != null) {
            pila.push(cursor);
            if (cursor.equals(origen)) {
                break;
            }
            cursor = padres.get(cursor);
        }

        //desapilo y paso todo a la lista final ordenada desde origen hasta destino
        List<Posicion> camino = new ArrayList<>();
        while (!pila.isEmpty()) {
            camino.add(pila.pop());
        }
        return camino;
    }
}
