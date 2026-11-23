package estructuras;

/**
 * Representación de un Grafo con Pesos utilizando estructuras 100% propias (TablaHash y Lista).
 * Soporta aristas dirigidas y no dirigidas con pesos no negativos.
 *
 * @param <T> Tipo de dato almacenado en los vértices (ej: Posicion, Celda, String).
 */
public class GrafoConPesos<T> {

    // TablaHash para acceder a los vértices por su valor en O(1)
    private TablaHash<T, Vertice<T>> vertices;
    // Lista para poder iterar sobre todos los vértices del grafo en O(N)
    private Lista<Vertice<T>> listaVertices;

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

        public int cantidadAdyacentes() {
            return this.adyacentes.cantidad();
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

        public void setPeso(int peso) {
            if (peso < 0) {
                throw new IllegalArgumentException("El peso de la arista no puede ser negativo.");
            }
            this.peso = peso;
        }
    }

    /**
     * Constructor por defecto: inicializa el grafo con estructuras propias.
     */
    public GrafoConPesos() {
        this.vertices = new TablaHash<>();
        this.listaVertices = new Lista<>();
    }

    /**
     * Agrega un nuevo vértice al grafo si no existe previamente.
     * Complejidad: O(1) promedio
     *
     * @param dato Valor del vértice (no nulo).
     * @throws IllegalArgumentException si dato es nulo.
     */
    public void agregarVertice(T dato) {
        if (dato == null) {
            throw new IllegalArgumentException("El vértice no puede ser nulo.");
        }
        if (!this.vertices.contiene(dato)) {
            Vertice<T> nuevoVertice = new Vertice<>(dato);
            this.vertices.insertar(dato, nuevoVertice);
            this.listaVertices.agregar(nuevoVertice);
        }
    }

    /**
     * Determina si un vértice se encuentra registrado en el grafo.
     * Complejidad: O(1) promedio
     *
     * @param dato Valor del vértice a consultar.
     * @return true si el vértice existe, false en caso contrario.
     */
    public boolean contieneVertice(T dato) {
        if (dato == null) {
            return false;
        }
        return this.vertices.contiene(dato);
    }

    /**
     * Retorna la cantidad total de vértices en el grafo.
     * Complejidad: O(1)
     *
     * @return Número de vértices.
     */
    public int cantidadVertices() {
        return this.listaVertices.cantidad();
    }

    /**
     * Alias de cantidadVertices() para consistencia con los demás TDAs.
     * Complejidad: O(1)
     *
     * @return Número de vértices.
     */
    public int cantidad() {
        return this.cantidadVertices();
    }

    /**
     * Agrega una arista dirigida con un peso específico entre dos vértices.
     * Si los vértices no existen en el grafo, se crean automáticamente.
     * Si ya existía una arista entre ambos, actualiza su peso.
     * Complejidad: O(1) promedio + O(G) donde G es el grado de salida del origen.
     *
     * @param origen Vértice de partida (no nulo).
     * @param destino Vértice de llegada (no nulo).
     * @param peso Costo de la transición (debe ser mayor o igual a 0).
     * @throws IllegalArgumentException si origen o destino son nulos, o peso es negativo.
     */
    public void agregarArista(T origen, T destino, int peso) {
        if (origen == null || destino == null) {
            throw new IllegalArgumentException("Los vértices de origen y destino no pueden ser nulos.");
        }
        if (peso < 0) {
            throw new IllegalArgumentException("El peso de la arista no puede ser negativo.");
        }

        // Aseguramos que ambos vértices existan en el grafo
        agregarVertice(origen);
        agregarVertice(destino);

        Vertice<T> vOrigen = this.vertices.obtener(origen);
        Vertice<T> vDestino = this.vertices.obtener(destino);

        // Si ya existe la arista, actualizamos su peso
        Lista<Arista<T>> ady = vOrigen.getAdyacentes();
        for (int i = 0; i < ady.cantidad(); i++) {
            Arista<T> arista = ady.obtener(i);
            if (arista.getDestino().getDato().equals(destino)) {
                arista.setPeso(peso);
                return;
            }
        }

        vOrigen.getAdyacentes().agregar(new Arista<>(vDestino, peso));
    }

    /**
     * Agrega una arista no dirigida (bidireccional / simétrica) entre dos vértices con el mismo peso.
     *
     * @param a Primer vértice.
     * @param b Segundo vértice.
     * @param peso Costo de la conexión en ambos sentidos.
     */
    public void agregarAristaNoDirigida(T a, T b, int peso) {
        agregarArista(a, b, peso);
        agregarArista(b, a, peso);
    }

    /**
     * Verifica si existe una arista dirigida desde el vértice origen hacia el vértice destino.
     * Complejidad: O(G) donde G es el grado de salida del vértice origen.
     *
     * @param origen Vértice de salida.
     * @param destino Vértice de llegada.
     * @return true si la arista existe, false en caso contrario.
     */
    public boolean contieneArista(T origen, T destino) {
        Vertice<T> vOrigen = obtenerVertice(origen);
        if (vOrigen == null || destino == null) {
            return false;
        }

        Lista<Arista<T>> ady = vOrigen.getAdyacentes();
        for (int i = 0; i < ady.cantidad(); i++) {
            if (ady.obtener(i).getDestino().getDato().equals(destino)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Obtiene el peso de la arista dirigida entre dos vértices.
     *
     * @param origen Vértice de salida.
     * @param destino Vértice de llegada.
     * @return Peso de la arista.
     * @throws IllegalArgumentException si algún vértice no existe o si no hay arista entre ellos.
     */
    public int obtenerPeso(T origen, T destino) {
        Vertice<T> vOrigen = obtenerVertice(origen);
        if (vOrigen == null) {
            throw new IllegalArgumentException("El vértice de origen no existe en el grafo: " + origen);
        }

        Lista<Arista<T>> ady = vOrigen.getAdyacentes();
        for (int i = 0; i < ady.cantidad(); i++) {
            Arista<T> arista = ady.obtener(i);
            if (arista.getDestino().getDato().equals(destino)) {
                return arista.getPeso();
            }
        }

        throw new IllegalArgumentException("No existe arista dirigida desde " + origen + " hacia " + destino);
    }

    /**
     * Retorna el objeto Vertice asociado a un valor determinado usando la TablaHash interna.
     * Complejidad: O(1) promedio
     *
     * @param dato Valor del vértice a buscar.
     * @return Vertice correspondiente, o null si no se encuentra en el grafo.
     */
    public Vertice<T> obtenerVertice(T dato) {
        if (dato == null) {
            return null;
        }
        return this.vertices.obtener(dato);
    }

    /**
     * Retorna la lista de aristas salientes (adyacentes) de un vértice.
     * Complejidad: O(1) promedio
     *
     * @param dato Valor del vértice.
     * @return Lista de aristas, o null si el vértice no existe en el grafo.
     */
    public Lista<Arista<T>> obtenerAdyacentes(T dato) {
        Vertice<T> v = obtenerVertice(dato);
        return (v == null) ? null : v.getAdyacentes();
    }

    /**
     * Retorna la lista con todos los vértices presentes en el grafo.
     * Permite iterar sobre el grafo para algoritmos globales como Dijkstra o recorrido en anchura.
     * Complejidad: O(1)
     *
     * @return Lista de vértices del grafo.
     */
    public Lista<Vertice<T>> obtenerVertices() {
        return this.listaVertices;
    }

    /**
     * Verifica si el grafo se encuentra vacío (sin vértices).
     * Complejidad: O(1)
     *
     * @return true si no contiene vértices, false en caso contrario.
     */
    public boolean estaVacio() {
        return this.vertices.estaVacia();
    }
}
