package modelo;

import estructuras.Cola;
import estructuras.ColaDePrioridad;
import estructuras.GrafoConPesos;
import estructuras.Lista;
import estructuras.Pila;
import estructuras.TablaHash;

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

    public Lista<Posicion> caminoMinimo(Posicion origen, Posicion destino) {
        //si alguna de las posiciones no existe en el grafo, devuelvo una lista vacia
        if (!contienePosicion(origen) || !contienePosicion(destino)) {
            return new Lista<>();
        }

        TablaHash<Posicion, Integer> distancias = new TablaHash<>();
        TablaHash<Posicion, Posicion> padres = new TablaHash<>();
        ColaDePrioridad<PasoDijkstra> cola = new ColaDePrioridad<>();

        //arranco desde el origen con costo acumulado 0
        distancias.insertar(origen, 0);
        cola.insertar(new PasoDijkstra(origen, 0));

        while (!cola.estaVacia()) {
            PasoDijkstra actual = cola.extraerProximo();
            Posicion posActual = actual.posicion();
            int costoActual = actual.costoAcumulado();

            //si ya llegue al destino corto el ciclo
            if (posActual.equals(destino)) {
                break;
            }

            //si este paso quedo viejo porque ya encontre un costo menor antes, lo salto
            if (costoActual > distancias.obtener(posActual)) {
                continue;
            }

            //recorro las aristas adyacentes y relajo los costos si encuentro un camino mas barato
            Lista<GrafoConPesos.Arista<Posicion>> adyacentes = grafo.obtenerAdyacentes(posActual);
            for (int i = 0; i < adyacentes.cantidad(); i++) {
                GrafoConPesos.Arista<Posicion> arista = adyacentes.obtener(i);
                Posicion vecino = arista.getDestino().getDato();
                int nuevoCosto = costoActual + arista.getPeso();

                if (!distancias.contiene(vecino) || nuevoCosto < distancias.obtener(vecino)) {
                    distancias.insertar(vecino, nuevoCosto);
                    padres.insertar(vecino, posActual);
                    cola.insertar(new PasoDijkstra(vecino, nuevoCosto));
                }
            }
        }

        //si nunca alcance el destino devuelvo lista vacia
        if (!distancias.contiene(destino)) {
            return new Lista<>();
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

        Lista<Posicion> camino = caminoMinimo(origen, destino);
        if (camino.estaVacia()) {
            return -1;
        }

        //sumo el peso de cada paso del camino minimo encontrado
        int costoTotal = 0;
        for (int i = 0; i < camino.cantidad() - 1; i++) {
            Posicion desde = camino.obtener(i);
            Posicion hasta = camino.obtener(i + 1);
            costoTotal += grafo.obtenerPeso(desde, hasta);
        }
        return costoTotal;
    }

    public Lista<Posicion> caminoMasCortoEnPasos(Posicion origen, Posicion destino) {
        //busco el camino con menos casillas usando BFS con nuestra Cola
        if (!contienePosicion(origen) || !contienePosicion(destino)) {
            return new Lista<>();
        }

        TablaHash<Posicion, Boolean> visitados = new TablaHash<>();
        TablaHash<Posicion, Posicion> padres = new TablaHash<>();
        Cola<Posicion> cola = new Cola<>();

        visitados.insertar(origen, true);
        cola.encolar(origen);

        while (!cola.estaVacia()) {
            Posicion actual = cola.desencolar();
            if (actual.equals(destino)) {
                break;
            }

            Lista<GrafoConPesos.Arista<Posicion>> adyacentes = grafo.obtenerAdyacentes(actual);
            for (int i = 0; i < adyacentes.cantidad(); i++) {
                Posicion vecino = adyacentes.obtener(i).getDestino().getDato();
                if (!visitados.contiene(vecino)) {
                    visitados.insertar(vecino, true);
                    padres.insertar(vecino, actual);
                    cola.encolar(vecino);
                }
            }
        }

        if (!visitados.contiene(destino)) {
            return new Lista<>();
        }

        return reconstruirCamino(origen, destino, padres);
    }

    private Lista<Posicion> reconstruirCamino(Posicion origen, Posicion destino,
                                              TablaHash<Posicion, Posicion> padres) {
        //retrocedo desde el destino hasta el origen apilando cada posicion para invertir el orden
        Pila<Posicion> pila = new Pila<>();
        Posicion cursor = destino;
        while (cursor != null) {
            pila.apilar(cursor);
            if (cursor.equals(origen)) {
                break;
            }
            cursor = padres.obtener(cursor);
        }

        //desapilo y paso todo a la lista final ordenada desde origen hasta destino
        Lista<Posicion> camino = new Lista<>();
        while (!pila.estaVacia()) {
            camino.agregar(pila.desapilar());
        }
        return camino;
    }
}
