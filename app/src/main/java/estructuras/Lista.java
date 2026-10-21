package estructuras;

/**
 * Estructura de Datos Lineal Simplemente Enlazada.
 *
 * @param <T> Tipo de elemento almacenado en la lista.
 */
public class Lista<T> {
    private Nodo<T> primero;
    private Nodo<T> ultimo;
    private int cantidad;

    /**
     * Construye una lista vacía.
     */
    public Lista() {
        this.primero = null;
        this.ultimo = null;
        this.cantidad = 0;
    }

    /**
     * Agrega un elemento al final de la lista.
     * Complejidad: O(1)
     *
     * @param elemento Elemento a agregar. Lanza IllegalArgumentException si es null.
     */
    public void agregar(T elemento) {
        if (elemento == null) {
            throw new IllegalArgumentException("No se permite agregar elementos nulos.");
        }
        Nodo<T> nuevo = new Nodo<>(elemento, null);

        if (estaVacia()) {
            this.primero = nuevo;
        } else {
            this.ultimo.siguiente = nuevo;
        }
        this.ultimo = nuevo;
        this.cantidad++;
    }

    /**
     * Obtiene el elemento almacenado en un índice específico.
     * Complejidad: O(N)
     *
     * @param indice Posición basada en cero.
     * @return El elemento en dicha posición.
     * @throws IndexOutOfBoundsException si el índice está fuera de rango.
     */
    public T obtener(int indice) {
        validarIndice(indice);
        Nodo<T> actual = this.primero;
        for (int i = 0; i < indice; i++) {
            actual = actual.siguiente;
        }
        return actual.dato;
    }

    /**
     * Remueve y retorna el elemento en el índice indicado.
     * Complejidad: O(N)
     *
     * @param indice Posición basada en cero del elemento a remover.
     * @return El elemento removido.
     * @throws IndexOutOfBoundsException si el índice está fuera de rango.
     */
    public T eliminar(int indice) {
        validarIndice(indice);
        T datoRemovido;

        if (indice == 0) {
            datoRemovido = this.primero.dato;
            this.primero = this.primero.siguiente;
            if (this.primero == null) {
                this.ultimo = null;
            }
        } else {
            Nodo<T> anterior = this.primero;
            for (int i = 0; i < indice - 1; i++) {
                anterior = anterior.siguiente;
            }
            Nodo<T> aEliminar = anterior.siguiente;
            datoRemovido = aEliminar.dato;
            anterior.siguiente = aEliminar.siguiente;
            if (esUltimo(aEliminar)) {
                this.ultimo = anterior;
            }
        }

        this.cantidad--;
        return datoRemovido;
    }

    /**
     * Remueve la primera ocurrencia del elemento especificado.
     * Complejidad: O(N)
     *
     * @param elemento Elemento a remover.
     * @return true si el elemento fue encontrado y removido, false de lo contrario.
     */
    public boolean eliminarElemento(T elemento) {
        if (elemento == null || estaVacia()) {
            return false;
        }

        if (this.primero.dato.equals(elemento)) {
            this.primero = this.primero.siguiente;

            if (this.primero == null) {
                this.ultimo = null;
            }

            this.cantidad--;
            return true;
        }

        //  En una lista simplemente enlazada no se puede volver hacia atrás.
        //  Para borrar el nodo B, hay que modificar el puntero siguiente del nodo anterior A
        Nodo<T> actual = this.primero;
        while (actual.siguiente != null && !actual.siguiente.dato.equals(elemento)) {
            actual = actual.siguiente;
        }

        if (actual.siguiente != null) {
            if (actual.siguiente == this.ultimo) {
                this.ultimo = actual;
            }
            actual.siguiente = actual.siguiente.siguiente;
            this.cantidad--;
            return true;
        }

        return false;
    }

    /**
     * Verifica si la lista contiene determinado elemento.
     * Complejidad: O(N)
     */
    public boolean contiene(T elemento) {
        if (elemento == null) return false;

        Nodo<T> actual = this.primero;

        while (actual != null) {
            if (actual.dato.equals(elemento)) {
                return true;
            }
            actual = actual.siguiente;
        }

        return false;
    }

    /**
     * Retorna si la lista no tiene elementos.
     */
    public boolean estaVacia() {
        return this.primero == null;
    }

    /**
     * Retorna el número de elementos guardados.
     */
    public int cantidad() {
        return this.cantidad;
    }

    private void validarIndice(int indice) {
        if (indice < 0 || indice >= this.cantidad) {
            throw new IndexOutOfBoundsException("Indice fuera de rango: " + indice);
        }
    }

    private boolean esUltimo(Nodo<T> nodo) {
        return nodo == this.ultimo;
    }
}
