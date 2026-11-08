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
     * Constructor por defecto: inicializa el arreglo y la cantidad en cero.
     */
    @SuppressWarnings("unchecked")
    public ColaDePrioridad() {
        this.elementos = (T[]) new Comparable[CAPACIDAD_INICIAL];
        this.cantidad = 0;
    }

    /**
     * Verifica si la cola de prioridad se encuentra vacía.
     * @return true si no contiene elementos, false en caso contrario.
     */
    public boolean estaVacia() {
        return this.cantidad == 0;
    }

    /**
     * Retorna la cantidad de elementos actuales en la estructura.
     * @return tamaño de la cola.
     */
    public int getCantidad() {
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
     * @param valor Elemento a insertar.
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
     * @return El elemento al frente de la cola.
     */
    public T obtenerProximo() {
        if (estaVacia()) {
            throw new NoSuchElementException("La cola de prioridad está vacía.");
        }
        return this.elementos[0];
    }

    /**
     * Remueve y retorna el elemento con mayor prioridad (el menor valor).
     * @return El elemento extraído de la cima.
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
}