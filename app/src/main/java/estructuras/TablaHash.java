package estructuras;

/**
 * Tabla Hash genérica implementada con encadenamiento mediante listas enlazadas propias.
 *
 * @param <K> Tipo de clave. Debe implementar un hashCode y equals consistentes.
 * @param <V> Tipo de valor asociado.
 */
public class TablaHash<K, V> {

    /**
     * Par clave-valor almacenado dentro de las listas de encadenamiento.
     */
    private static class Entrada<K, V> {
        private final K clave;
        private V valor;

        public Entrada(K clave, V valor) {
            this.clave = clave;
            this.valor = valor;
        }

        public K getClave() {
            return clave;
        }

        public V getValor() {
            return valor;
        }

        public void setValor(V valor) {
            this.valor = valor;
        }
    }

    private static final int CAPACIDAD_DEFECTO = 16;
    private Lista<Entrada<K, V>>[] tabla;
    private int cantidad;

    /**
     * Inicializa una Tabla Hash con una capacidad inicial especificada.
     *
     * @param capacidadInicial Tamaño base de la tabla Hash.
     */
    public TablaHash(int capacidadInicial) {
        if (capacidadInicial <= 0) {
            throw new IllegalArgumentException("La capacidad inicial debe ser mayor a 0.");
        }

        this.tabla = (Lista<Entrada<K, V>>[]) new Lista[capacidadInicial];

        for (int i = 0; i < capacidadInicial; i++) {
            this.tabla[i] = new Lista<>();
        }
        this.cantidad = 0;
    }

    /**
     * Inicializa una Tabla Hash con la capacidad por defecto.
     */
    public TablaHash() {
        this(CAPACIDAD_DEFECTO);
    }

    /**
     * Inserta un par clave-valor. Si la clave ya existe, sobrescribe su valor.
     * Complejidad promedio: O(1)
     *
     * @param clave Clave de búsqueda (no puede ser nula).
     * @param valor Valor asociado a almacenar.
     */
    public void insertar(K clave, V valor) {
        if (clave == null) {
            throw new IllegalArgumentException("No se permiten claves nulas.");
        }
        int indice = calcularIndice(clave);
        Lista<Entrada<K, V>> balde = this.tabla[indice];

        for (int i = 0; i < balde.cantidad(); i++) {
            Entrada<K, V> entrada = balde.obtener(i);
            if (entrada.getClave().equals(clave)) {
                entrada.setValor(valor);
                return;
            }
        }

        balde.agregar(new Entrada<>(clave, valor));
        this.cantidad++;
    }

    /**
     * Obtiene el valor asociado a una clave.
     * Complejidad promedio: O(1)
     *
     * @param clave Clave a buscar.
     * @return El valor correspondiente, o null si la clave no existe.
     */
    public V obtener(K clave) {
        if (clave == null) return null;

        int indice = calcularIndice(clave);
        Lista<Entrada<K, V>> balde = this.tabla[indice];

        for (int i = 0; i < balde.cantidad(); i++) {
            Entrada<K, V> entrada = balde.obtener(i);
            if (entrada.getClave().equals(clave)) {
                return entrada.getValor();
            }
        }

        return null;
    }


    /**
     * Determina si la clave existe en la tabla Hash.
     * Complejidad promedio: O(1)
     */
    public boolean contiene(K clave) {
        return obtener(clave) != null;
    }

    /**
     * Elimina el par clave-valor correspondiente a la clave enviada.
     * Complejidad promedio: O(1)
     *
     * @param clave Clave a remover.
     * @return El valor eliminado, o null si no se encontró la clave.
     */
    public V eliminar(K clave) {
        if (clave == null) return null;
        int indice = calcularIndice(clave);
        Lista<Entrada<K, V>> balde = this.tabla[indice];

        for (int i = 0; i < balde.cantidad(); i++) {
            Entrada<K, V> entrada = balde.obtener(i);
            if (entrada.getClave().equals(clave)) {
                V valorEliminado = entrada.getValor();
                balde.eliminar(i);
                this.cantidad--;
                return valorEliminado;
            }
        }

        return null;
    }

    /**
     * Cantidad total de pares clave-valor guardados en la tabla.
     */
    public int cantidad() {
        return this.cantidad;
    }

    /**
     * Indica si la tabla Hash se encuentra vacía.
     */
    public boolean estaVacia() {
        return this.cantidad == 0;
    }

    /**
     * Mapea el hashCode() de la clave a un índice válido dentro del arreglo de baldes.
     */
    private int calcularIndice(K clave) {
        int hash = clave.hashCode();
        return Math.abs(hash) % this.tabla.length;
    }
}
