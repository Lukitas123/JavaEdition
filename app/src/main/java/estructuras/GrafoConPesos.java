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
        private Lista<Arista<T>> adyacentes; // Asumiendo que tenés tu clase Lista propia

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
}
