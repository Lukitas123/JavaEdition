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
    private static class NodoAVL<T> {
        T valor;
        int altura;
        NodoAVL<T> izq;
        NodoAVL<T> der;

        NodoAVL(T valor) {
            this.valor = valor;
            this.altura = 1; // Todo nodo nuevo (hoja) nace con altura 1
        }
    }

    private NodoAVL<T> raiz;

    /**
     * Constructor por defecto: inicializa un árbol vacío.
     */
    public ArbolAVL() {
        this.raiz = null;
    }


    /**
     * Verifica si el árbol se encuentra vacío.
     * @return true si no contiene elementos, false en caso contrario.
     */
    public boolean estaVacio() {
        return this.raiz == null;
    }

    // ==========================================
    // MÉTODOS AUXILIARES: ALTURA Y Equilibrio
    // ==========================================

    /**
     * Retorna la altura de un nodo de manera segura (maneja nulos).
     */
    private int altura(NodoAVL<T> nodo) {
        return (nodo == null) ? 0 : nodo.altura;
    }

    /**
     * Calcula el factor de Equilibrio de un nodo (Altura Izquierda - Altura Derecha).
     */
    private int obtenerEquilibrio(NodoAVL<T> nodo) {
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
     * @param valor Elemento a insertar.
     */
    public void insertar(T valor) {
        if (valor == null) {
            throw new IllegalArgumentException("No se pueden insertar elementos nulos en el AVL.");
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
}
