package estructuras;

import java.util.NoSuchElementException;

/**
 * Cola de Prioridad implementada mediante un Min-Heap (Montículo Mínimo)
 * utilizando un arreglo nativo redimensionable desde cero.
 *
 * @param <T> Tipo de elemento almacenado, que debe implementar Comparable.
 */
public class ColaDePrioridad<T extends Comparable<T>> {
    private static final int CAPACIDAD_INICIAL = 10;
    private T[] elementos;
    private int cantidad;

    /**
     * Inicializa una cola de prioridad con una capacidad inicial especificada.
     *
     * @param capacidadInicial Capacidad base del arreglo interno (debe ser mayor a 0).
     * @throws IllegalArgumentException si capacidadInicial es menor o igual a 0.
     */
    @SuppressWarnings("unchecked")
    public ColaDePrioridad(int capacidadInicial) {
        if (capacidadInicial <= 0) {
            throw new IllegalArgumentException("La capacidad inicial debe ser mayor a 0.");
        }
        this.elementos = (T[]) new Comparable[capacidadInicial];
        this.cantidad = 0;
    }

    /**
     * Constructor por defecto: inicializa el arreglo con la capacidad estándar (10)
     * y la cantidad en cero.
     */
    public ColaDePrioridad() {
        this(CAPACIDAD_INICIAL);
    }

    /**
     * Verifica si la cola de prioridad se encuentra vacía.
     * Complejidad: O(1)
     *
     * @return true si no contiene elementos, false en caso contrario.
     */
    public boolean estaVacia() {
        return this.cantidad == 0;
    }

    /**
     * Retorna la cantidad de elementos actuales en la estructura.
     * Complejidad: O(1)
     *
     * @return tamaño de la cola.
     */
    public int getCantidad() {
        return this.cantidad;
    }

    /**
     * Alias de getCantidad() para consistencia con el resto de las estructuras del proyecto.
     * Complejidad: O(1)
     *
     * @return tamaño de la cola.
     */
    public int cantidad() {
        return this.cantidad;
    }


    // GESTIÓN DINÁMICA DEL ARREGLO

    @SuppressWarnings("unchecked")
    private void redimensionar() {
        T[] nuevoArreglo = (T[]) new Comparable[this.elementos.length * 2];
        for (int i = 0; i < this.cantidad; i++) {
            nuevoArreglo[i] = this.elementos[i];
        }
        this.elementos = nuevoArreglo;
    }


    // FÓRMULAS DE ÍNDICES DEL ÁRBOL EN ARRAY

    private int padre(int i) {
        return (i - 1) / 2;
    }

    private int hijoIzquierda(int i) {
        return 2 * i + 1;
    }

    private int hijoDerecha(int i) {
        return 2 * i + 2;
    }

    private void intercambiar(int i, int j) {
        T temp = this.elementos[i];
        this.elementos[i] = this.elementos[j];
        this.elementos[j] = temp;
    }


    // INSERCIÓN Y FLOTADO (Sift-Up)

    /**
     * Inserta un nuevo elemento en la cola de prioridad respetando la propiedad de Min-Heap.
     * Complejidad: O(log N)
     *
     * @param valor Elemento a insertar.
     * @throws IllegalArgumentException si el valor es nulo.
     */
    public void insertar(T valor) {
        if (valor == null) {
            throw new IllegalArgumentException("No se pueden insertar elementos nulos en la cola de prioridad.");
        }

        // Si el arreglo está lleno, duplicamos su capacidad
        if (this.cantidad == this.elementos.length) {
            redimensionar();
        }

        // Agregamos al final del arreglo lógicamente
        this.elementos[this.cantidad] = valor;
        this.cantidad++;

        // Hacemos flotar el elemento hacia arriba
        flotar(this.cantidad - 1);
    }

    private void flotar(int i) {
        while (i > 0 && this.elementos[i].compareTo(this.elementos[padre(i)]) < 0) {
            intercambiar(i, padre(i));
            i = padre(i);
        }
    }


    // EXTRACCIÓN Y HUNDIDO (Sift-Down)

    /**
     * Retorna el elemento con mayor prioridad (el menor valor) sin removerlo.
     * Complejidad: O(1)
     *
     * @return El elemento al frente de la cola.
     * @throws NoSuchElementException si la cola está vacía.
     */
    public T obtenerProximo() {
        if (estaVacia()) {
            throw new NoSuchElementException("La cola de prioridad está vacía.");
        }
        return this.elementos[0];
    }

    /**
     * Remueve y retorna el elemento con mayor prioridad (el menor valor).
     * Complejidad: O(log N)
     *
     * @return El elemento extraído de la cima.
     * @throws NoSuchElementException si la cola está vacía.
     */
    public T extraerProximo() {
        if (estaVacia()) {
            throw new NoSuchElementException("La cola de prioridad está vacía.");
        }

        T proximo = this.elementos[0];

        // Tomamos el último elemento válido y lo ubicamos temporalmente en la raíz
        this.cantidad--;
        T ultimo = this.elementos[this.cantidad];
        this.elementos[this.cantidad] = null; // Evitamos loitering (fuga de memoria)

        if (!estaVacia()) {
            this.elementos[0] = ultimo;
            hundir(0);
        }

        return proximo;
    }

    private void hundir(int i) {
        int menor = i;
        int izq = hijoIzquierda(i);
        int der = hijoDerecha(i);

        if (izq < this.cantidad && this.elementos[izq].compareTo(this.elementos[menor]) < 0) {
            menor = izq;
        }

        if (der < this.cantidad && this.elementos[der].compareTo(this.elementos[menor]) < 0) {
            menor = der;
        }

        if (menor != i) {
            intercambiar(i, menor);
            hundir(menor);
        }
    }

    /**
     * Verifica que el arreglo interno cumpla estrictamente la propiedad de Min-Heap
     * (cada nodo padre es menor o igual a sus hijos izquierdo y derecho).
     * Método auxiliar de prueba a nivel paquete.
     *
     * @return true si cumple la invariante de Min-Heap, false en caso contrario.
     */
    boolean esMinHeapValido() {
        for (int i = 0; i < this.cantidad; i++) {
            int izq = hijoIzquierda(i);
            int der = hijoDerecha(i);

            if (izq < this.cantidad && this.elementos[i].compareTo(this.elementos[izq]) > 0) {
                return false;
            }
            if (der < this.cantidad && this.elementos[i].compareTo(this.elementos[der]) > 0) {
                return false;
            }
        }
        return true;
    }
}
