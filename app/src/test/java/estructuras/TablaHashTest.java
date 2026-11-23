package estructuras;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la estructura de datos Tabla Hash con encadenamiento.
 * Valida las operaciones de inserción, actualización de valores por clave existente,
 * consulta, eliminación, resolución de colisiones y pruebas de estrés.
 */
public class TablaHashTest {

    private TablaHash<String, Integer> tabla;

    /**
     * Clase auxiliar para simular colisiones controladas forzando el mismo hashCode.
     */
    private static class ClaveColision {
        private final String valor;
        private final int hashFijo;

        public ClaveColision(String valor, int hashFijo) {
            this.valor = valor;
            this.hashFijo = hashFijo;
        }

        @Override
        public int hashCode() {
            return this.hashFijo;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            ClaveColision otra = (ClaveColision) obj;
            return this.valor.equals(otra.valor);
        }
    }

    @BeforeEach
    public void inicializar() {
        this.tabla = new TablaHash<>();
    }

    /**
     * Verifica que una tabla hash recién creada esté vacía y con cantidad cero.
     */
    @Test
    @DisplayName("Una tabla hash recién creada debe estar vacía y tener cantidad cero")
    public void testTablaNuevaEstaVacia() {
        assertTrue(this.tabla.estaVacia());
        assertEquals(0, this.tabla.cantidad());
    }

    /**
     * Comprueba que instanciar una tabla con capacidad menor o igual a cero lance IllegalArgumentException.
     */
    @Test
    @DisplayName("Inicializar la tabla con capacidad menor o igual a cero debe lanzar IllegalArgumentException")
    public void testConstructorConCapacidadInvalidaLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> {
            new TablaHash<String, Integer>(0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new TablaHash<String, Integer>(-5);
        });
    }

    /**
     * Valida la inicialización y funcionamiento correcto con una capacidad personalizada válida.
     */
    @Test
    @DisplayName("Una tabla hash con capacidad personalizada inicializa y opera correctamente")
    public void testConstructorConCapacidadPersonalizada() {
        TablaHash<String, String> tablaChica = new TablaHash<>(4);

        assertTrue(tablaChica.estaVacia());
        tablaChica.insertar("clave", "valor");
        assertEquals(1, tablaChica.cantidad());
        assertEquals("valor", tablaChica.obtener("clave"));
    }

    /**
     * Comprueba la inserción de un elemento en la tabla hash.
     */
    @Test
    @DisplayName("Insertar un par clave-valor incrementa la cantidad y permite su obtención")
    public void testInsertarUnElemento() {
        this.tabla.insertar("pocion", 5);

        assertFalse(this.tabla.estaVacia());
        assertEquals(1, this.tabla.cantidad());
        assertEquals(5, this.tabla.obtener("pocion"));
        assertTrue(this.tabla.contiene("pocion"));
    }

    /**
     * Valida que intentar insertar una clave nula lance IllegalArgumentException sin alterar la tabla.
     */
    @Test
    @DisplayName("Insertar una clave nula debe lanzar IllegalArgumentException")
    public void testInsertarClaveNulaLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> {
            this.tabla.insertar(null, 10);
        });

        assertTrue(this.tabla.estaVacia());
        assertEquals(0, this.tabla.cantidad());
    }

    /**
     * Verifica que al insertar una clave ya existente se sobrescriba su valor sin duplicar la cantidad.
     */
    @Test
    @DisplayName("Insertar una clave ya existente sobrescribe el valor y mantiene la cantidad")
    public void testInsertarSobrescribeValorExistente() {
        this.tabla.insertar("monedas", 50);
        assertEquals(1, this.tabla.cantidad());
        assertEquals(50, this.tabla.obtener("monedas"));

        // Sobrescritura
        this.tabla.insertar("monedas", 150);
        assertEquals(1, this.tabla.cantidad());
        assertEquals(150, this.tabla.obtener("monedas"));
    }

    /**
     * Comprueba que buscar una clave inexistente o pasar una clave nula a obtener() retorne null de forma segura.
     */
    @Test
    @DisplayName("obtener con clave inexistente o null debe retornar null")
    public void testObtenerClaveInexistenteONulaRetornaNull() {
        assertNull(this.tabla.obtener("inexistente"));
        assertNull(this.tabla.obtener(null));

        this.tabla.insertar("existente", 1);
        assertNull(this.tabla.obtener("otra"));
        assertNull(this.tabla.obtener(null));
    }

    /**
     * Valida el funcionamiento del método contiene() ante claves presentes, ausentes o nulas.
     */
    @Test
    @DisplayName("contiene retorna true para claves presentes y false para ausentes o nulas")
    public void testContieneClave() {
        assertFalse(this.tabla.contiene("espada"));
        assertFalse(this.tabla.contiene(null));

        this.tabla.insertar("espada", 1);

        assertTrue(this.tabla.contiene("espada"));
        assertFalse(this.tabla.contiene("escudo"));
        assertFalse(this.tabla.contiene(null));
    }

    /**
     * Verifica que eliminar una clave existente retorne su valor asociado, decremente la cantidad
     * y remueva el par de la tabla.
     */
    @Test
    @DisplayName("Eliminar una clave existente retorna el valor eliminado y reduce la cantidad")
    public void testEliminarClaveExistente() {
        this.tabla.insertar("antorcha", 3);

        Integer eliminado = this.tabla.eliminar("antorcha");

        assertEquals(3, eliminado);
        assertEquals(0, this.tabla.cantidad());
        assertTrue(this.tabla.estaVacia());
        assertFalse(this.tabla.contiene("antorcha"));
        assertNull(this.tabla.obtener("antorcha"));
    }

    /**
     * Comprueba que eliminar una clave inexistente o nula retorne null sin producir efectos colaterales.
     */
    @Test
    @DisplayName("Eliminar una clave inexistente o null retorna null sin alterar la tabla")
    public void testEliminarClaveInexistenteONulaRetornaNull() {
        assertNull(this.tabla.eliminar("no_esta"));
        assertNull(this.tabla.eliminar(null));

        this.tabla.insertar("llave", 1);

        assertNull(this.tabla.eliminar("no_esta"));
        assertNull(this.tabla.eliminar(null));
        assertEquals(1, this.tabla.cantidad());
    }

    /**
     * Comprueba la resolución de colisiones mediante encadenamiento abierto (separating chaining).
     * Dos claves con el mismo hashCode caen en el mismo bucket pero se distinguen por equals.
     */
    @Test
    @DisplayName("Manejo de colisiones: claves con igual hashCode coexisten y se gestionan independientemente")
    public void testManejoDeColisiones() {
        TablaHash<ClaveColision, String> tablaColisiones = new TablaHash<>(4);

        // Ambas claves fuerzan el mismo hashCode (42), garantizando colisión en el mismo balde
        ClaveColision claveA = new ClaveColision("A", 42);
        ClaveColision claveB = new ClaveColision("B", 42);

        tablaColisiones.insertar(claveA, "ValorA");
        tablaColisiones.insertar(claveB, "ValorB");

        assertEquals(2, tablaColisiones.cantidad());
        assertEquals("ValorA", tablaColisiones.obtener(claveA));
        assertEquals("ValorB", tablaColisiones.obtener(claveB));

        // Eliminar una de las claves colisionadas no debe afectar a la otra
        String removido = tablaColisiones.eliminar(claveA);
        assertEquals("ValorA", removido);
        assertEquals(1, tablaColisiones.cantidad());
        assertFalse(tablaColisiones.contiene(claveA));
        assertTrue(tablaColisiones.contiene(claveB));
        assertEquals("ValorB", tablaColisiones.obtener(claveB));
    }

    /**
     * Verifica que claves con hashCode negativo o extremo (como Integer.MIN_VALUE)
     * se mapeen a un índice válido sin lanzar ArrayIndexOutOfBoundsException.
     */
    @Test
    @DisplayName("Claves con hashCode negativo extremo se mapean a índices válidos sin desbordar")
    public void testClaveConHashCodeNegativo() {
        TablaHash<ClaveColision, String> tablaExtrema = new TablaHash<>(10);

        ClaveColision claveNegativa = new ClaveColision("Negativa", -12345);
        ClaveColision claveMinima = new ClaveColision("Minima", Integer.MIN_VALUE);

        assertDoesNotThrow(() -> {
            tablaExtrema.insertar(claveNegativa, "ValorNeg");
            tablaExtrema.insertar(claveMinima, "ValorMin");
        });

        assertEquals(2, tablaExtrema.cantidad());
        assertEquals("ValorNeg", tablaExtrema.obtener(claveNegativa));
        assertEquals("ValorMin", tablaExtrema.obtener(claveMinima));
    }

    /**
     * Valida que la tabla pueda vaciarse por completo y reutilizarse normalmente para nuevas inserciones.
     */
    @Test
    @DisplayName("La tabla hash puede reutilizarse normalmente tras haber sido vaciada")
    public void testReutilizacionTrasVaciar() {
        this.tabla.insertar("temp1", 10);
        this.tabla.insertar("temp2", 20);

        assertEquals(10, this.tabla.eliminar("temp1"));
        assertEquals(20, this.tabla.eliminar("temp2"));
        assertTrue(this.tabla.estaVacia());

        // Reutilización
        this.tabla.insertar("nuevo", 100);
        assertEquals(1, this.tabla.cantidad());
        assertEquals(100, this.tabla.obtener("nuevo"));
    }

    /**
     * Realiza una prueba de estrés insertando 500 pares, sobrescribiendo parte de ellos
     * y eliminando la mitad para asegurar la consistencia y estabilidad del direccionamiento.
     */
    @Test
    @DisplayName("Verificar integridad de la tabla hash bajo prueba de volumen y estrés")
    public void testPruebaDeVolumen() {
        int total = 500;

        for (int i = 0; i < total; i++) {
            this.tabla.insertar("clave_" + i, i);
        }

        assertEquals(total, this.tabla.cantidad());
        assertFalse(this.tabla.estaVacia());

        // Verificamos que todos los elementos existan
        for (int i = 0; i < total; i++) {
            assertEquals(i, this.tabla.obtener("clave_" + i));
            assertTrue(this.tabla.contiene("clave_" + i));
        }

        // Sobrescribimos los primeros 100 (el tamaño total no debe cambiar)
        for (int i = 0; i < 100; i++) {
            this.tabla.insertar("clave_" + i, i * 10);
        }
        assertEquals(total, this.tabla.cantidad());
        for (int i = 0; i < 100; i++) {
            assertEquals(i * 10, this.tabla.obtener("clave_" + i));
        }

        // Eliminamos la mitad (los pares pares del 0 al 498)
        int eliminados = 0;
        for (int i = 0; i < total; i += 2) {
            this.tabla.eliminar("clave_" + i);
            eliminados++;
        }

        assertEquals(total - eliminados, this.tabla.cantidad());

        // Verificamos que los impares sigan presentes
        for (int i = 1; i < total; i += 2) {
            assertTrue(this.tabla.contiene("clave_" + i));
        }
    }
}
