package estructuras;

/**
 * Representación de un Grafo con Pesos utilizando estructuras 100% propias
 *
 * @param <T> Tipo de dato almacenado en los vértices (ej: Posicion, Celda).
 */
public class GrafoConPesos<T> {

    // TablaHash para acceder a los vértices en O(1)
    private TablaHash<T, Vertice<T>> vertices;

    /**
     * Clase interna que representa un Vértice del grafo.
     */
    public static class Vertice<T> {
        private T dato;
        private Lista<Arista<T>> adyacentes;

        public Vertice(T dato) {
            this.dato = dato;
            this.adyacentes = new Lista<>();
        }

        public T getDato() {
            return this.dato;
        }

        public Lista<Arista<T>> getAdyacentes() {
            return this.adyacentes;
        }
    }

    /**
     * Clase interna que representa una Arista con peso (conexión dirigida).
     */
    public static class Arista<T> {
        private Vertice<T> destino;
        private int peso;

        public Arista(Vertice<T> destino, int peso) {
            this.destino = destino;
            this.peso = peso;
        }

        public Vertice<T> getDestino() {
            return this.destino;
        }

        public int getPeso() {
            return this.peso;
        }
    }

    /**
     * Constructor por defecto: inicializa el grafo con la TablaHash propia.
     */
    public GrafoConPesos() {
        this.vertices = new TablaHash<>();
    }

    /**
     * Agrega un nuevo vértice al grafo si no existe previamente.
     * @param dato Valor del vértice.
     */
    public void agregarVertice(T dato) {
        if (dato == null) {
            throw new IllegalArgumentException("El vértice no puede ser nulo.");
        }
        // Verificamos si ya existe en la TablaHash antes de agregarlo
        if (!this.vertices.contiene(dato)) {
            this.vertices.insertar(dato, new Vertice<>(dato));
        }
    }

    /**
     * Agrega una arista dirigida con un peso específico entre dos vértices.
     * Si los vértices no existen, los crea automáticamente.
     *
     * @param origen Vértice de salida.
     * @param destino Vértice de llegada.
     * @param peso Costo de la transición.
     */
    public void agregarArista(T origen, T destino, int peso) {
        if (peso < 0) {
            throw new IllegalArgumentException("El peso de la arista no puede ser negativo.");
        }

        // Aseguramos que ambos vértices existan en el grafo
        agregarVertice(origen);
        agregarVertice(destino);

        Vertice<T> vOrigen = this.vertices.obtener(origen);
        Vertice<T> vDestino = this.vertices.obtener(destino);

        vOrigen.getAdyacentes().agregar(new Arista<>(vDestino, peso));
    }

    /**
     * Agrega una arista no dirigida (doble mano / simétrica).
     */
    public void agregarAristaNoDirigida(T a, T b, int peso) {
        agregarArista(a, b, peso);
        agregarArista(b, a, peso);
    }

    /**
     * Retorna el vértice asociado a un valor determinado usando la TablaHash.
     */
    public Vertice<T> obtenerVertice(T dato) {
        return this.vertices.obtener(dato);
    }

    /**
     * Verifica si el grafo se encuentra vacío.
     */
    public boolean estaVacio() {
        return this.vertices.estaVacia();
    }
}
