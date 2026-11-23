package estructuras;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la estructura de datos Lista Simplemente Enlazada.
 * Valida las operaciones de inserción, acceso por índice, eliminación por posición
 * y por valor, búsqueda de elementos, casos de borde y pruebas de volumen.
 */
public class ListaTest {

    private Lista<String> lista;

    @BeforeEach
    public void inicializar() {
        this.lista = new Lista<>();
    }

    /**
     * Verifica que una lista recién instanciada se encuentre vacía y con tamaño cero.
     */
    @Test
    @DisplayName("Una lista recién creada debe estar vacía y tener cantidad cero")
    public void testListaNuevaEstaVacia() {
        assertTrue(this.lista.estaVacia());
        assertEquals(0, this.lista.cantidad());
    }

    /**
     * Comprueba que al agregar un elemento el tamaño aumente a uno y deje de estar vacía.
     */
    @Test
    @DisplayName("Agregar un elemento incrementa la cantidad y deja de estar vacía")
    public void testAgregarUnElemento() {
        this.lista.agregar("Primero");

        assertFalse(this.lista.estaVacia());
        assertEquals(1, this.lista.cantidad());
        assertEquals("Primero", this.lista.obtener(0));
    }

    /**
     * Valida que al agregar múltiples elementos se preserve el orden de inserción secuencial.
     */
    @Test
    @DisplayName("Agregar múltiples elementos conserva el orden de inserción")
    public void testAgregarMultiplesElementosConservaOrden() {
        this.lista.agregar("A");
        this.lista.agregar("B");
        this.lista.agregar("C");

        assertEquals(3, this.lista.cantidad());
        assertEquals("A", this.lista.obtener(0));
        assertEquals("B", this.lista.obtener(1));
        assertEquals("C", this.lista.obtener(2));
    }

    /**
     * Valida que intentar agregar un elemento nulo lance IllegalArgumentException sin alterar la lista.
     */
    @Test
    @DisplayName("Agregar un elemento nulo debe lanzar IllegalArgumentException sin alterar la lista")
    public void testAgregarElementoNuloLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> {
            this.lista.agregar(null);
        });

        assertTrue(this.lista.estaVacia());
        assertEquals(0, this.lista.cantidad());
    }

    /**
     * Verifica que intentar obtener un elemento con un índice fuera de rango lance IndexOutOfBoundsException.
     */
    @Test
    @DisplayName("Obtener con índices inválidos (negativos, iguales o mayores a la cantidad) debe lanzar excepción")
    public void testObtenerConIndiceInvalidoLanzaExcepcion() {
        // En lista vacía
        assertThrows(IndexOutOfBoundsException.class, () -> {
            this.lista.obtener(0);
        });

        this.lista.agregar("Elemento");

        // Índice negativo
        assertThrows(IndexOutOfBoundsException.class, () -> {
            this.lista.obtener(-1);
        });

        // Índice igual al tamaño
        assertThrows(IndexOutOfBoundsException.class, () -> {
            this.lista.obtener(1);
        });

        // Índice mayor al tamaño
        assertThrows(IndexOutOfBoundsException.class, () -> {
            this.lista.obtener(10);
        });
    }

    /**
     * Comprueba la eliminación del primer elemento (índice 0 / cabeza) en una lista con varios elementos.
     */
    @Test
    @DisplayName("Eliminar por índice en la cabeza remueve el primer elemento y reajusta la lista")
    public void testEliminarPorIndiceCabeza() {
        this.lista.agregar("A");
        this.lista.agregar("B");
        this.lista.agregar("C");

        String removido = this.lista.eliminar(0);

        assertEquals("A", removido);
        assertEquals(2, this.lista.cantidad());
        assertEquals("B", this.lista.obtener(0));
        assertEquals("C", this.lista.obtener(1));
    }

    /**
     * Comprueba la eliminación de un elemento intermedio mediante su índice.
     */
    @Test
    @DisplayName("Eliminar por índice en una posición intermedia conecta los nodos vecinos")
    public void testEliminarPorIndiceMedio() {
        this.lista.agregar("A");
        this.lista.agregar("B");
        this.lista.agregar("C");

        String removido = this.lista.eliminar(1);

        assertEquals("B", removido);
        assertEquals(2, this.lista.cantidad());
        assertEquals("A", this.lista.obtener(0));
        assertEquals("C", this.lista.obtener(1));
    }

    /**
     * Comprueba la eliminación del último elemento por índice y verifica que el puntero 'ultimo' se actualice.
     */
    @Test
    @DisplayName("Eliminar por índice el último elemento actualiza el puntero final correctamente")
    public void testEliminarPorIndiceUltimo() {
        this.lista.agregar("A");
        this.lista.agregar("B");
        this.lista.agregar("C");

        String removido = this.lista.eliminar(2);

        assertEquals("C", removido);
        assertEquals(2, this.lista.cantidad());
        assertEquals("A", this.lista.obtener(0));
        assertEquals("B", this.lista.obtener(1));

        // Verificamos que 'ultimo' se haya actualizado agregando un nuevo elemento
        this.lista.agregar("D");
        assertEquals(3, this.lista.cantidad());
        assertEquals("D", this.lista.obtener(2));
    }

    /**
     * Comprueba que eliminar el único elemento de una lista la deje completamente vacía.
     */
    @Test
    @DisplayName("Eliminar el único elemento de la lista la deja vacía con punteros limpios")
    public void testEliminarUnicoElementoDejaListaVacia() {
        this.lista.agregar("Unico");

        String removido = this.lista.eliminar(0);

        assertEquals("Unico", removido);
        assertTrue(this.lista.estaVacia());
        assertEquals(0, this.lista.cantidad());
    }

    /**
     * Verifica que intentar eliminar con un índice fuera de rango lance IndexOutOfBoundsException.
     */
    @Test
    @DisplayName("Eliminar con índice inválido debe lanzar IndexOutOfBoundsException")
    public void testEliminarConIndiceInvalidoLanzaExcepcion() {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            this.lista.eliminar(0);
        });

        this.lista.agregar("Elemento");

        assertThrows(IndexOutOfBoundsException.class, () -> {
            this.lista.eliminar(-1);
        });

        assertThrows(IndexOutOfBoundsException.class, () -> {
            this.lista.eliminar(1);
        });
    }

    /**
     * Valida la eliminación por valor cuando el elemento se encuentra en la cabeza de la lista.
     */
    @Test
    @DisplayName("eliminarElemento remueve correctamente el elemento si está en la cabeza")
    public void testEliminarElementoPorValorCabeza() {
        this.lista.agregar("A");
        this.lista.agregar("B");

        boolean eliminado = this.lista.eliminarElemento("A");

        assertTrue(eliminado);
        assertEquals(1, this.lista.cantidad());
        assertEquals("B", this.lista.obtener(0));
    }

    /**
     * Valida la eliminación por valor cuando el elemento se encuentra en una posición intermedia.
     */
    @Test
    @DisplayName("eliminarElemento remueve correctamente un elemento en el medio")
    public void testEliminarElementoPorValorMedio() {
        this.lista.agregar("A");
        this.lista.agregar("B");
        this.lista.agregar("C");

        boolean eliminado = this.lista.eliminarElemento("B");

        assertTrue(eliminado);
        assertEquals(2, this.lista.cantidad());
        assertEquals("A", this.lista.obtener(0));
        assertEquals("C", this.lista.obtener(1));
    }

    /**
     * Valida la eliminación por valor cuando el elemento se encuentra al final de la lista.
     */
    @Test
    @DisplayName("eliminarElemento remueve el último elemento y actualiza el puntero 'ultimo'")
    public void testEliminarElementoPorValorUltimo() {
        this.lista.agregar("A");
        this.lista.agregar("B");

        boolean eliminado = this.lista.eliminarElemento("B");

        assertTrue(eliminado);
        assertEquals(1, this.lista.cantidad());
        assertEquals("A", this.lista.obtener(0));

        // Verificamos que se pueda volver a agregar luego de remover el ultimo
        this.lista.agregar("C");
        assertEquals(2, this.lista.cantidad());
        assertEquals("C", this.lista.obtener(1));
    }

    /**
     * Valida que eliminarElemento retorne false si el elemento no existe, es null o la lista está vacía.
     */
    @Test
    @DisplayName("eliminarElemento retorna false si el elemento no existe, es null o la lista está vacía")
    public void testEliminarElementoInexistenteONuloRetornaFalse() {
        assertFalse(this.lista.eliminarElemento("Inexistente"));
        assertFalse(this.lista.eliminarElemento(null));

        this.lista.agregar("A");

        assertFalse(this.lista.eliminarElemento("Z"));
        assertFalse(this.lista.eliminarElemento(null));
        assertEquals(1, this.lista.cantidad());
    }

    /**
     * Comprueba que si hay elementos duplicados, eliminarElemento sólo remueva la primera ocurrencia.
     */
    @Test
    @DisplayName("eliminarElemento sólo remueve la primera ocurrencia ante duplicados")
    public void testEliminarElementoSoloRemuevePrimeraOcurrencia() {
        this.lista.agregar("A");
        this.lista.agregar("B");
        this.lista.agregar("A");

        boolean eliminado = this.lista.eliminarElemento("A");

        assertTrue(eliminado);
        assertEquals(2, this.lista.cantidad());
        assertEquals("B", this.lista.obtener(0));
        assertEquals("A", this.lista.obtener(1));
    }

    /**
     * Comprueba el funcionamiento de la búsqueda con el método contiene().
     */
    @Test
    @DisplayName("contiene retorna true si el elemento existe y false en caso contrario o con null")
    public void testContieneElemento() {
        assertFalse(this.lista.contiene("A"));
        assertFalse(this.lista.contiene(null));

        this.lista.agregar("A");
        this.lista.agregar("B");

        assertTrue(this.lista.contiene("A"));
        assertTrue(this.lista.contiene("B"));
        assertFalse(this.lista.contiene("C"));
        assertFalse(this.lista.contiene(null));
    }

    /**
     * Verifica la correcta reutilización de la lista luego de haber sido vaciada completamente.
     */
    @Test
    @DisplayName("La lista puede reutilizarse normalmente tras vaciarla por completo")
    public void testReutilizacionTrasVaciar() {
        this.lista.agregar("Temporal");
        this.lista.eliminar(0);
        assertTrue(this.lista.estaVacia());

        this.lista.agregar("Nuevo1");
        this.lista.agregar("Nuevo2");

        assertEquals(2, this.lista.cantidad());
        assertEquals("Nuevo1", this.lista.obtener(0));
        assertEquals("Nuevo2", this.lista.obtener(1));
    }

    /**
     * Realiza una prueba de estrés agregando y verificando 1000 elementos secuenciales.
     */
    @Test
    @DisplayName("Verificar integridad de la lista con múltiples elementos (prueba de volumen)")
    public void testPruebaDeVolumen() {
        Lista<Integer> listaEnteros = new Lista<>();
        int total = 1000;

        for (int i = 0; i < total; i++) {
            listaEnteros.agregar(i);
        }

        assertEquals(total, listaEnteros.cantidad());
        assertFalse(listaEnteros.estaVacia());

        for (int i = 0; i < total; i++) {
            assertEquals(i, listaEnteros.obtener(i));
        }

        // Eliminamos la mitad y verificamos consistencia
        for (int i = 0; i < 500; i++) {
            listaEnteros.eliminar(0);
        }

        assertEquals(500, listaEnteros.cantidad());
        assertEquals(500, listaEnteros.obtener(0));
    }
}
