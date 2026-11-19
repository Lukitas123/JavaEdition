package estructuras;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la estructura de datos Cola (FIFO).
 * Valida el comportamiento estricto del principio First In, First Out,
 * el manejo de excepciones y las pruebas de volumen.
 */
public class ColaTest {

    private Cola<String> cola;

    @BeforeEach
    public void inicializar() {
        this.cola = new Cola<>();
    }

    /**
     * Verifica que una cola recién instanciada se encuentre vacía y con tamaño cero.
     */
    @Test
    @DisplayName("Una cola recién creada debe estar vacía y tener tamaño cero")
    public void testColaNuevaEstaVacia() {
        assertTrue(this.cola.estaVacia());
        assertEquals(0, this.cola.getCantidad());
    }

    /**
     * Comprueba que al encolar un elemento el tamaño aumente a uno y el frente
     * apunte correctamente al elemento ingresado.
     */
    @Test
    @DisplayName("Encolar un elemento incrementa la cantidad y deja de estar vacía")
    public void testEncolarUnElemento() {
        this.cola.encolar("Primero");

        assertFalse(this.cola.estaVacia());
        assertEquals(1, this.cola.getCantidad());
        assertEquals("Primero", this.cola.obtenerFrente());
    }

    /**
     * Valida que intentar encolar un elemento nulo lance una IllegalArgumentException
     * y no altere el estado de la cola.
     */
    @Test
    @DisplayName("Encolar un elemento nulo debe lanzar IllegalArgumentException sin alterar la cola")
    public void testEncolarElementoNuloLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> {
            this.cola.encolar(null);
        });

        assertTrue(this.cola.estaVacia());
        assertEquals(0, this.cola.getCantidad());
    }

    /**
     * Comprueba que consultar el frente de la cola múltiples veces devuelva
     * el elemento correcto sin removerlo de la estructura.
     */
    @Test
    @DisplayName("obtenerFrente devuelve el primer elemento sin removerlo de la cola")
    public void testObtenerFrenteNoRemueveElemento() {
        this.cola.encolar("Elemento1");
        this.cola.encolar("Elemento2");

        assertEquals("Elemento1", this.cola.obtenerFrente());
        assertEquals("Elemento1", this.cola.obtenerFrente());
        assertEquals(2, this.cola.getCantidad());
    }

    /**
     * Verifica que desencolar extraiga y retorne el elemento del frente,
     * dejando la cola vacía si solo contenía uno.
     */
    @Test
    @DisplayName("Desencolar remueve y retorna el elemento del frente de la cola")
    public void testDesencolarRemueveYRetorna() {
        this.cola.encolar("Unico");

        String removido = this.cola.desencolar();

        assertEquals("Unico", removido);
        assertTrue(this.cola.estaVacia());
        assertEquals(0, this.cola.getCantidad());
    }

    /**
     * Valida el cumplimiento estricto del orden FIFO (First In, First Out)
     * al insertar y retirar múltiples elementos.
     */
    @Test
    @DisplayName("Los elementos deben salir en el mismo orden en que entraron (principio FIFO)")
    public void testComportamientoFIFO() {
        this.cola.encolar("A");
        this.cola.encolar("B");
        this.cola.encolar("C");

        assertEquals(3, this.cola.getCantidad());
        assertEquals("A", this.cola.desencolar());
        assertEquals("B", this.cola.desencolar());
        assertEquals("C", this.cola.desencolar());
        assertTrue(this.cola.estaVacia());
    }

    /**
     * Comprueba que intentar desencolar en una estructura vacía lance una NoSuchElementException.
     */
    @Test
    @DisplayName("Desencolar en una cola vacía debe lanzar NoSuchElementException")
    public void testDesencolarEnColaVaciaLanzaExcepcion() {
        assertThrows(NoSuchElementException.class, () -> {
            this.cola.desencolar();
        });
    }

    /**
     * Comprueba que consultar el frente en una estructura vacía lance una NoSuchElementException.
     */
    @Test
    @DisplayName("obtenerFrente en una cola vacía debe lanzar NoSuchElementException")
    public void testObtenerFrenteEnColaVaciaLanzaExcepcion() {
        assertThrows(NoSuchElementException.class, () -> {
            this.cola.obtenerFrente();
        });
    }

    /**
     * Valida que tras vaciar completamente la cola mediante extracciones,
     * los punteros se restablezcan y arrojen excepción si se intenta operar nuevamente en vacío.
     */
    @Test
    @DisplayName("Desencolar en una cola que fue vaciada debe lanzar NoSuchElementException")
    public void testDesencolarLuegoDeVaciarLanzaExcepcion() {
        this.cola.encolar("Temporal");
        this.cola.desencolar();

        assertTrue(this.cola.estaVacia());
        assertEquals(0, this.cola.getCantidad());

        assertThrows(NoSuchElementException.class, () -> {
            this.cola.desencolar();
        });
    }

    /**
     * Verifica la correcta reutilización de la instancia de la cola
     * después de haber sido vaciada por completo en una operación anterior.
     */
    @Test
    @DisplayName("La cola puede volver a utilizarse normalmente tras haber sido vaciada por completo")
    public void testReutilizacionTrasVaciar() {
        this.cola.encolar("Primero");
        assertEquals("Primero", this.cola.desencolar());
        assertTrue(this.cola.estaVacia());

        this.cola.encolar("Segundo");
        this.cola.encolar("Tercero");

        assertEquals(2, this.cola.getCantidad());
        assertEquals("Segundo", this.cola.obtenerFrente());
        assertEquals("Segundo", this.cola.desencolar());
        assertEquals("Tercero", this.cola.desencolar());
        assertTrue(this.cola.estaVacia());
    }

    /**
     * Realiza una prueba de estrés masiva insertando y extrayendo 1000 elementos
     * para garantizar la integridad y estabilidad de los punteros internos.
     */
    @Test
    @DisplayName("Verificar integridad de la cola con múltiples elementos (prueba de volumen)")
    public void testPruebaDeVolumen() {
        Cola<Integer> colaEnteros = new Cola<>();
        int total = 1000;

        for (int i = 0; i < total; i++) {
            colaEnteros.encolar(i);
        }

        assertEquals(total, colaEnteros.getCantidad());
        assertFalse(colaEnteros.estaVacia());

        for (int i = 0; i < total; i++) {
            assertEquals(i, colaEnteros.desencolar());
        }

        assertTrue(colaEnteros.estaVacia());
        assertEquals(0, colaEnteros.getCantidad());
    }
}
