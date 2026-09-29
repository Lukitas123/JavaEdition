package estructuras;

/**
 * Nodo genérico para las estructuras enlazadas propias (Pila, Cola, Lista).
 */
public class Nodo<T> {
    private final T dato;
    private Nodo<T> siguiente;

    public Nodo(T dato, Nodo<T> siguiente) {
        this.dato = dato;
        this.siguiente = siguiente;
    }

    public T getDato() {
        return dato;
    }
    public Nodo<T> getSiguiente() {
        return siguiente;
    }
}

