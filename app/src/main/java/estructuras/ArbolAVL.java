package estructuras;

/**
 * Implementación de un Árbol AVL genérico auto-balanceado.
 * Garantiza operaciones de inserción, búsqueda y recorrido en O(log N).
 *
 * @param <T> Tipo de dato almacenado que debe implementar Comparable.
 */
public class ArbolAVL<T extends Comparable<T>> {

    /**
     * Nodo interno del Árbol AVL.
     */
    static class NodoAVL<T> {
        T valor;
        int altura;
        NodoAVL<T> izq;
        NodoAVL<T> der;

        NodoAVL(T valor) {
            this.valor = valor;
            this.altura = 1; // Todo nodo nuevo (hoja) nace con altura 1
        }
    }

    NodoAVL<T> raiz;
    private int cantidad;

    /**
     * Constructor por defecto: inicializa un árbol vacío.
     */
    public ArbolAVL() {
        this.raiz = null;
        this.cantidad = 0;
    }

    /**
     * Verifica si el árbol se encuentra vacío.
     * Complejidad: O(1)
     *
     * @return true si no contiene elementos, false en caso contrario.
     */
    public boolean estaVacio() {
        return this.raiz == null;
    }

    /**
     * Retorna la cantidad total de elementos almacenados en el árbol.
     * Complejidad: O(1)
     *
     * @return Cantidad de elementos únicos en el árbol.
     */
    public int cantidad() {
        return this.cantidad;
    }

    /**
     * Retorna la altura total del árbol AVL.
     * Un árbol vacío tiene altura 0, y un árbol con solo la raíz tiene altura 1.
     * Complejidad: O(1)
     *
     * @return Altura del árbol.
     */
    public int altura() {
        return altura(this.raiz);
    }

    /**
     * Retorna el elemento almacenado en la raíz del árbol.
     *
     * @return Valor en la raíz, o null si el árbol está vacío.
     */
    public T obtenerRaiz() {
        return (this.raiz == null) ? null : this.raiz.valor;
    }

    /**
     * Determina si un elemento se encuentra presente en el árbol AVL.
     * Complejidad: O(log N)
     *
     * @param valor Elemento a buscar.
     * @return true si el elemento existe en el árbol, false en caso contrario.
     */
    public boolean contiene(T valor) {
        if (valor == null) {
            return false;
        }
        return contieneRec(this.raiz, valor);
    }

    private boolean contieneRec(NodoAVL<T> nodo, T valor) {
        if (nodo == null) {
            return false;
        }
        int comparacion = valor.compareTo(nodo.valor);
        if (comparacion < 0) {
            return contieneRec(nodo.izq, valor);
        } else if (comparacion > 0) {
            return contieneRec(nodo.der, valor);
        } else {
            return true;
        }
    }

    /**
     * Realiza un recorrido Inorden (Izquierda - Raíz - Derecha) y retorna los
     * elementos ordenados ascendentemente en una Lista propia.
     * Complejidad: O(N)
     *
     * @return Lista enlazada con los elementos en orden ascendente.
     */
    public Lista<T> inorden() {
        Lista<T> resultado = new Lista<>();
        inordenRec(this.raiz, resultado);
        return resultado;
    }

    private void inordenRec(NodoAVL<T> nodo, Lista<T> lista) {
        if (nodo != null) {
            inordenRec(nodo.izq, lista);
            lista.agregar(nodo.valor);
            inordenRec(nodo.der, lista);
        }
    }

    // ==========================================
    // MÉTODOS AUXILIARES: ALTURA Y EQUILIBRIO
    // ==========================================

    /**
     * Retorna la altura de un nodo de manera segura (maneja nulos).
     */
    int altura(NodoAVL<T> nodo) {
        return (nodo == null) ? 0 : nodo.altura;
    }

    /**
     * Calcula el factor de equilibrio de un nodo (Altura Izquierda - Altura Derecha).
     */
    int obtenerEquilibrio(NodoAVL<T> nodo) {
        return nodo == null ? 0 : altura(nodo.izq) - altura(nodo.der);
    }

    /**
     * Realiza una rotación simple a la derecha (Caso LL).
     */
    private NodoAVL<T> rotacionDerecha(NodoAVL<T> y) {
        NodoAVL<T> x = y.izq;
        NodoAVL<T> t2 = x.der;

        // Rotación
        x.der = y;
        y.izq = t2;

        // Actualizar alturas
        y.altura = Math.max(altura(y.izq), altura(y.der)) + 1;
        x.altura = Math.max(altura(x.izq), altura(x.der)) + 1;

        return x; // Nueva raíz del subárbol
    }

    /**
     * Realiza una rotación simple a la izquierda (Caso RR).
     */
    private NodoAVL<T> rotacionIzquierda(NodoAVL<T> x) {
        NodoAVL<T> y = x.der;
        NodoAVL<T> t2 = y.izq;

        // Rotación
        y.izq = x;
        x.der = t2;

        // Actualizar alturas
        x.altura = Math.max(altura(x.izq), altura(x.der)) + 1;
        y.altura = Math.max(altura(y.izq), altura(y.der)) + 1;

        return y; // Nueva raíz del subárbol
    }

    // ==========================================
    // INSERCIÓN
    // ==========================================

    /**
     * Inserta un elemento en el árbol AVL manteniendo la propiedad de equilibrio.
     * Complejidad: O(log N)
     *
     * @param valor Elemento a insertar.
     * @throws IllegalArgumentException si el valor es nulo.
     */
    public void insertar(T valor) {
        if (valor == null) {
            throw new IllegalArgumentException("No se pueden insertar elementos nulos en el AVL.");
        }
        if (!contiene(valor)) {
            this.cantidad++;
        }
        this.raiz = insertarRec(this.raiz, valor);
    }

    /**
     * Método recursivo para insertar y reequilibrar el árbol.
     */
    private NodoAVL<T> insertarRec(NodoAVL<T> nodo, T valor) {
        // Inserción estándar de un ABB
        if (nodo == null) {
            return new NodoAVL<>(valor);
        }

        int comparacion = valor.compareTo(nodo.valor);
        if (comparacion < 0) {
            nodo.izq = insertarRec(nodo.izq, valor);
        } else if (comparacion > 0) {
            nodo.der = insertarRec(nodo.der, valor);
        } else {
            return nodo; // Los valores duplicados no se reinsertan
        }

        // Actualizar la altura del nodo actual
        nodo.altura = 1 + Math.max(altura(nodo.izq), altura(nodo.der));

        // Obtener el factor de balance para comprobar si desequilibró el nodo
        int balance = obtenerEquilibrio(nodo);

        // Aplicar rotaciones según los 4 casos posibles de desequilibrio

        // Caso Izquierda-Izquierda (LL)
        if (balance > 1 && valor.compareTo(nodo.izq.valor) < 0) {
            return rotacionDerecha(nodo);
        }

        // Caso Derecha-Derecha (RR)
        if (balance < -1 && valor.compareTo(nodo.der.valor) > 0) {
            return rotacionIzquierda(nodo);
        }

        // Caso Izquierda-Derecha (LR)
        if (balance > 1 && valor.compareTo(nodo.izq.valor) > 0) {
            nodo.izq = rotacionIzquierda(nodo.izq);
            return rotacionDerecha(nodo);
        }

        // Caso Derecha-Izquierda (RL)
        if (balance < -1 && valor.compareTo(nodo.der.valor) < 0) {
            nodo.der = rotacionDerecha(nodo.der);
            return rotacionIzquierda(nodo);
        }

        // Retornar el nodo sin cambios si ya estaba equilibrado
        return nodo;
    }

    /**
     * Valida si el árbol cumple estrictamente todas las propiedades de un Árbol AVL:
     * 1. Propiedad de búsqueda binaria (ABB).
     * 2. Propiedad de balance (|altura(izq) - altura(der)| <= 1 para todo nodo).
     * 3. Alturas correctamente calculadas en cada nodo.
     *
     * @return true si es un AVL válido, false si alguna propiedad se viola.
     */
    boolean esAVLValido() {
        return esAVLValidoRec(this.raiz);
    }

    private boolean esAVLValidoRec(NodoAVL<T> nodo) {
        if (nodo == null) {
            return true;
        }

        int balance = obtenerEquilibrio(nodo);
        if (balance < -1 || balance > 1) {
            return false;
        }

        if (nodo.altura != 1 + Math.max(altura(nodo.izq), altura(nodo.der))) {
            return false;
        }

        if (nodo.izq != null && nodo.izq.valor.compareTo(nodo.valor) >= 0) {
            return false;
        }

        if (nodo.der != null && nodo.der.valor.compareTo(nodo.valor) <= 0) {
            return false;
        }

        return esAVLValidoRec(nodo.izq) && esAVLValidoRec(nodo.der);
    }
}
