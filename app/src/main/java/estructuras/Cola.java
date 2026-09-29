package estructuras;

import java.util.NoSuchElementException;

/**
 * Estructura de Datos Lineal FIFO (First In, First Out).
 *
 * @param <T> Tipo de elemento almacenado en la cola.
 */
public class Cola<T> {
    private Nodo<T> frente;
    private Nodo<T> ultimo;
    private int cantidad;

    /**
     * Construye una cola vacía.
     */
    public Cola() {
        this.frente = null;
        this.ultimo = null;
        this.cantidad = 0;
    }

    /**
     * Inserta un elemento al final de la cola.
     * Complejidad: O(1)
     *
     * @param elemento El elemento a encolar. Lanza IllegalArgumentException si es null.
     */
    public void encolar(T elemento) {
        if (elemento == null) {
            throw new IllegalArgumentException("No se permite encolar elementos nulos.");
        }

        Nodo<T> nuevoNodo = new Nodo<>(elemento);

        if (estaVacia()) {
            this.frente = nuevoNodo;
        } else {
            this.ultimo.siguiente = nuevoNodo;
        }

        this.ultimo = nuevoNodo;
        this.cantidad++;
    }

    /**
     * Remueve y retorna el elemento ubicado al frente de la cola.
     * Complejidad: O(1)
     *
     * @return El elemento removido.
     * @throws NoSuchElementException si la cola está vacía.
     */
    public T desencolar() {
        if (estaVacia()) {
            throw new NoSuchElementException("No se puede desencolar de una cola vacia.");
        }

        T dato = this.frente.dato;
        this.frente = this.frente.siguiente;

        if (this.frente == null) {
            this.ultimo = null;
        }

        this.cantidad--;
        return dato;
    }

    /**
     * Retorna el elemento del frente sin removerlo de la cola.
     * Complejidad: O(1)
     *
     * @return El elemento en el frente.
     * @throws NoSuchElementException si la cola está vacía.
     */
    public T obtenerFrente() {
        if (estaVacia()) {
            throw new NoSuchElementException("La cola esta vacia.");
        }
        return this.frente.dato;
    }

    /**
     * Indica si la cola no contiene elementos.
     * Complejidad: O(1)
     *
     * @return true si la cola está vacía, false en caso contrario.
     */
    public boolean estaVacia() {
        return this.frente == null;
    }

    /**
     * Retorna la cantidad total de elementos en la cola.
     * Complejidad: O(1)
     *
     * @return Cantidad de elementos.
     */
    public int getCantidad() {
        return this.cantidad;
    }
}
