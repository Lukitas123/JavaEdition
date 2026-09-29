package estructuras;

import java.util.NoSuchElementException;

/**
 * Estructura de Datos Lineal LIFO (Last In, First Out).
 *
 * @param <T> Tipo de elemento almacenado en la pila.
 */

public class Pila<T> {
    private Nodo<T> cima;
    private int cantidad;

    /**
     * Construye una pila vacía.
     */
    public Pila() {
        this.cima = null;
        this.cantidad = 0;
    }

    /**
     * Agrega un elemento en la cima de la pila.
     * Complejidad: O(1)
     *
     * @param elemento El elemento a apilar. Lanza IllegalArgumentException si es null.
     */
    public void apilar(T elemento) {
        if (elemento == null) {
            throw new IllegalArgumentException("No se permite apilar elementos nulos.");
        }
        this.cima = new Nodo<>(elemento, this.cima);
        this.cantidad++;
    }

    /**
     * Remueve y retorna el elemento ubicado en la cima de la pila.
     * Complejidad: O(1)
     *
     * @return El elemento desapilado.
     * @throws NoSuchElementException si la pila está vacía.
     */
    public T desapilar() {
        if (estaVacia()) {
            throw new NoSuchElementException("No se puede desapilar de una pila vacia.");
        }
        T dato = this.cima.getDato();
        this.cima = this.cima.getSiguiente();
        this.cantidad--;
        return dato;
    }


    /**
     * Retorna el elemento de la cima sin removerlo de la pila.
     * Complejidad: O(1)
     *
     * @return El elemento en el tope.
     * @throws NoSuchElementException si la pila está vacía.
     */
    public T obtenerCima() {
        if (estaVacia()) {
            throw new IllegalStateException("La pila esta vacía");
        }
        return this.cima.getDato();
    }

    /**
     * Indíca si la pila no contiene elementos.
     * Complejidad: O(1)
     *
     * @return true si la pila está vacía, false en caso contrario.
     */
    public boolean estaVacia() {
        return this.cima == null;
    }

    /**
     * Retorna la cantidad total de elementos en la pila.
     * Complejidad: O(1)
     *
     * @return Cantidad de elementos.
     */
    public int size() {
        return this.cantidad;
    }
}


