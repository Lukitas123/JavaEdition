package estructuras;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la estructura de datos Pila (LIFO).
 * Valida el comportamiento estricto del principio Last In, First Out,
 * el manejo de excepciones y las pruebas de volumen.
 */
public class PilaTest {

    private Pila<String> pila;

    @BeforeEach
    public void inicializar() {
        this.pila = new Pila<>();
    }

    /**
     * Verifica que una pila recién instanciada se encuentre vacía y con tamaño cero.
     */
    @Test
    @DisplayName("Una pila recién creada debe estar vacía y tener tamaño cero")
    public void testPilaNuevaEstaVacia() {
        assertTrue(this.pila.estaVacia());
        assertEquals(0, this.pila.size());
        assertEquals(0, this.pila.getCantidad());
    }

    /**
     * Comprueba que al apilar un elemento el tamaño aumente a uno y la cima
     * apunte correctamente al elemento ingresado.
     */
    @Test
    @DisplayName("Apilar un elemento incrementa la cantidad y deja de estar vacía")
    public void testApilarUnElemento() {
        this.pila.apilar("Primero");

        assertFalse(this.pila.estaVacia());
        assertEquals(1, this.pila.size());
        assertEquals("Primero", this.pila.obtenerCima());
    }

    /**
     * Valida que intentar apilar un elemento nulo lance una IllegalArgumentException
     * y no altere el estado de la pila.
     */
    @Test
    @DisplayName("Apilar un elemento nulo debe lanzar IllegalArgumentException sin alterar la pila")
    public void testApilarElementoNuloLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> {
            this.pila.apilar(null);
        });

        assertTrue(this.pila.estaVacia());
        assertEquals(0, this.pila.size());
    }

    /**
     * Comprueba que consultar la cima de la pila múltiples veces devuelva
     * el elemento correcto sin removerlo de la estructura.
     */
    @Test
    @DisplayName("obtenerCima devuelve el elemento en el tope sin removerlo de la pila")
    public void testObtenerCimaNoRemueveElemento() {
        this.pila.apilar("Elemento1");
        this.pila.apilar("Elemento2");

        assertEquals("Elemento2", this.pila.obtenerCima());
        assertEquals("Elemento2", this.pila.obtenerCima());
        assertEquals(2, this.pila.size());
    }

    /**
     * Verifica que desapilar extraiga y retorne el elemento de la cima,
     * dejando la pila vacía si solo contenía uno.
     */
    @Test
    @DisplayName("Desapilar remueve y retorna el elemento de la cima de la pila")
    public void testDesapilarRemueveYRetorna() {
        this.pila.apilar("Unico");

        String removido = this.pila.desapilar();

        assertEquals("Unico", removido);
        assertTrue(this.pila.estaVacia());
        assertEquals(0, this.pila.size());
    }

    /**
     * Valida el cumplimiento estricto del orden LIFO (Last In, First Out)
     * al insertar y retirar múltiples elementos.
     */
    @Test
    @DisplayName("Los elementos deben salir en orden inverso al que entraron (principio LIFO)")
    public void testComportamientoLIFO() {
        this.pila.apilar("A");
        this.pila.apilar("B");
        this.pila.apilar("C");

        assertEquals(3, this.pila.size());
        // El último en entrar ("C") debe ser el primero en salir
        assertEquals("C", this.pila.desapilar());
        assertEquals("B", this.pila.desapilar());
        assertEquals("A", this.pila.desapilar());
        assertTrue(this.pila.estaVacia());
    }

    /**
     * Comprueba que intentar desapilar en una estructura vacía lance una NoSuchElementException.
     */
    @Test
    @DisplayName("Desapilar en una pila vacía debe lanzar NoSuchElementException")
    public void testDesapilarEnPilaVaciaLanzaExcepcion() {
        assertThrows(NoSuchElementException.class, () -> {
            this.pila.desapilar();
        });
    }

    /**
     * Comprueba que consultar la cima en una estructura vacía lance una NoSuchElementException.
     */
    @Test
    @DisplayName("obtenerCima en una pila vacía debe lanzar NoSuchElementException")
    public void testObtenerCimaEnPilaVaciaLanzaExcepcion() {
        assertThrows(NoSuchElementException.class, () -> {
            this.pila.obtenerCima();
        });
    }

    /**
     * Valida que tras vaciar completamente la pila mediante extracciones,
     * los punteros se restablezcan y arrojen excepción si se intenta operar nuevamente en vacío.
     */
    @Test
    @DisplayName("Desapilar en una pila que fue vaciada debe lanzar NoSuchElementException")
    public void testDesapilarLuegoDeVaciarLanzaExcepcion() {
        this.pila.apilar("Temporal");
        this.pila.desapilar();

        assertTrue(this.pila.estaVacia());
        assertEquals(0, this.pila.size());

        assertThrows(NoSuchElementException.class, () -> {
            this.pila.desapilar();
        });
    }

    /**
     * Verifica la correcta reutilización de la instancia de la pila
     * después de haber sido vaciada por completo en una operación anterior.
     */
    @Test
    @DisplayName("La pila puede volver a utilizarse normalmente tras haber sido vaciada por completo")
    public void testReutilizacionTrasVaciar() {
        this.pila.apilar("Primero");
        assertEquals("Primero", this.pila.desapilar());
        assertTrue(this.pila.estaVacia());

        this.pila.apilar("Segundo");
        this.pila.apilar("Tercero");

        assertEquals(2, this.pila.size());
        assertEquals("Tercero", this.pila.obtenerCima());
        assertEquals("Tercero", this.pila.desapilar());
        assertEquals("Segundo", this.pila.desapilar());
        assertTrue(this.pila.estaVacia());
    }

    /**
     * Realiza una prueba de estrés masiva insertando y extrayendo 1000 elementos
     * para verificar la integridad de la cima y el orden LIFO.
     */
    @Test
    @DisplayName("Verificar integridad de la pila con múltiples elementos (prueba de volumen)")
    public void testPruebaDeVolumen() {
        Pila<Integer> pilaEnteros = new Pila<>();
        int total = 1000;

        for (int i = 0; i < total; i++) {
            pilaEnteros.apilar(i);
        }

        assertEquals(total, pilaEnteros.size());
        assertFalse(pilaEnteros.estaVacia());

        // Al desapilar deben salir en orden inverso: 999, 998, ..., 0
        for (int i = total - 1; i >= 0; i--) {
            assertEquals(i, pilaEnteros.desapilar());
        }

        assertTrue(pilaEnteros.estaVacia());
        assertEquals(0, pilaEnteros.size());
    }
}
