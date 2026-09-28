package estructuras;

/**
 * Nodo genérico para las estructuras enlazadas propias (Pila, Cola, Lista).
 */
class Nodo<T> {
    T dato;
    Nodo<T> siguiente;

    public Nodo(T dato) {
        this.dato = dato;
        this.siguiente = null;
    }
}
