package ar.uba.fi.cb100.tp2.estructuras;

/**
 * TAD Pila (LIFO) implementado con Nodos dinámicos.
 * SIN usar java.util.Stack según restricciones del TP2.
 */
public class Pila<T> {
    private Nodo<T> cima;
    private int cantidad;

    public Pila() {
        this.cima = null;
        this.cantidad = 0;
    }

    public void apilar(T elemento) {
        Nodo<T> nuevo = new Nodo<>(elemento);
        nuevo.siguiente = this.cima;
        this.cima = nuevo;
        this.cantidad++;
    }

    public T desapilar() {
        if (estaVacia()) {
            throw new IllegalStateException("La pila esta vacia");
        }
        T dato = this.cima.dato;
        this.cima = this.cima.siguiente;
        this.cantidad--;
        return dato;
    }

    public T verTope() {
        if (estaVacia()) {
            throw new IllegalStateException("La pila esta vacia");
        }
        return this.cima.dato;
    }

    public boolean estaVacia() {
        return this.cima == null;
    }

    public int size() {
        return this.cantidad;
    }
}
