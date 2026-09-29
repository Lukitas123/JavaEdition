package estructuras;

/**
 * Nodo genérico para las estructuras enlazadas propias (Pila, Cola, Lista).
 */
class Nodo<T> {
    T dato;
    Nodo<T> siguiente;

    Nodo(T dato, Nodo<T> siguiente) {
        this.dato = dato;
        this.siguiente = siguiente;
    }

     Nodo(T dato) {
        this(dato, null);
    }


}
