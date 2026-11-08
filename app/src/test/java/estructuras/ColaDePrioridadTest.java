package estructuras;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la Cola de Prioridad basada en Min-Heap (Montículo Mínimo).
 * Valida la propiedad de orden por prioridad ascendente, flotado (sift-up), hundido (sift-down),
 * redimensión dinámica, soporte genérico, casos de borde y pruebas de volumen.
 */
public class ColaDePrioridadTest {

    private ColaDePrioridad<Integer> cola;

    @BeforeEach
    public void inicializar() {
        this.cola = new ColaDePrioridad<>();
    }


    // ESTADO INICIAL Y CONSTRUCTORES

    /**
     * Verifica que una cola de prioridad recién instanciada esté vacía y con cantidad cero.
     */
    @Test
    @DisplayName("Una cola recién creada debe estar vacía y tener cantidad cero")
    public void testColaNuevaEstaVacia() {
        assertTrue(this.cola.estaVacia());
        assertEquals(0, this.cola.getCantidad());
        assertEquals(0, this.cola.cantidad());
        assertTrue(this.cola.esMinHeapValido());
    }

    /**
     * Valida que el constructor con capacidad personalizada asigne el espacio inicial correctamente.
     */
    @Test
    @DisplayName("El constructor con capacidad válida inicializa una cola vacía correctamente")
    public void testConstructorConCapacidadValida() {
        ColaDePrioridad<Integer> colaPersonalizada = new ColaDePrioridad<>(50);
        assertTrue(colaPersonalizada.estaVacia());
        assertEquals(0, colaPersonalizada.cantidad());
    }

    /**
     * Comprueba que instanciar una cola con capacidad no positiva lance IllegalArgumentException.
     */
    @Test
    @DisplayName("Constructor con capacidad menor o igual a cero debe lanzar IllegalArgumentException")
    public void testConstructorConCapacidadInvalidaLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> new ColaDePrioridad<>(0));
        assertThrows(IllegalArgumentException.class, () -> new ColaDePrioridad<>(-10));
    }


    // 2. EXCEPCIONES EN COLA VACÍA Y NULOS

    /**
     * Comprueba que intentar ver el próximo elemento en una cola vacía lance NoSuchElementException.
     */
    @Test
    @DisplayName("obtenerProximo en una cola vacía debe lanzar NoSuchElementException")
    public void testObtenerProximoEnVaciaLanzaExcepcion() {
        assertThrows(NoSuchElementException.class, () -> {
            this.cola.obtenerProximo();
        });
    }

    /**
     * Comprueba que intentar extraer el próximo elemento en una cola vacía lance NoSuchElementException.
     */
    @Test
    @DisplayName("extraerProximo en una cola vacía debe lanzar NoSuchElementException")
    public void testExtraerProximoEnVaciaLanzaExcepcion() {
        assertThrows(NoSuchElementException.class, () -> {
            this.cola.extraerProximo();
        });
    }

    /**
     * Verifica que intentar insertar un elemento nulo lance IllegalArgumentException sin alterar la estructura.
     */
    @Test
    @DisplayName("Insertar un valor nulo debe lanzar IllegalArgumentException y preservar el estado")
    public void testInsertarNuloLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> {
            this.cola.insertar(null);
        });

        assertTrue(this.cola.estaVacia());
        assertEquals(0, this.cola.cantidad());
    }


    // 3. INSERCIÓN BÁSICA Y CONSULTA

    /**
     * Valida la inserción de un elemento único: deja de estar vacía, la cantidad pasa a uno
     * y dicho elemento se posiciona al frente.
     */
    @Test
    @DisplayName("Insertar un único elemento incrementa cantidad y lo ubica al frente")
    public void testInsertarUnicoElemento() {
        this.cola.insertar(42);

        assertFalse(this.cola.estaVacia());
        assertEquals(1, this.cola.cantidad());
        assertEquals(1, this.cola.getCantidad());
        assertEquals(42, this.cola.obtenerProximo());
        assertTrue(this.cola.esMinHeapValido());
    }

    /**
     * Verifica que obtenerProximo sea idempotente (retorna el menor elemento sin removerlo de la cola).
     */
    @Test
    @DisplayName("obtenerProximo retorna el elemento de mayor prioridad sin consumirlo")
    public void testObtenerProximoEsIdempotente() {
        this.cola.insertar(25);
        this.cola.insertar(10);
        this.cola.insertar(30);

        assertEquals(10, this.cola.obtenerProximo());
        assertEquals(10, this.cola.obtenerProximo());
        assertEquals(3, this.cola.cantidad());
    }


    // 4. COMPORTAMIENTO MIN-HEAP (ORDEN DE PRIORIDAD)

    /**
     * Verifica que elementos insertados en orden desordenado se extraigan estrictamente
     * en orden ascendente (propiedad de Min-Heap).
     */
    @Test
    @DisplayName("Elementos desordenados se extraen estrictamente en orden ascendente")
    public void testMinHeapExtraeEnOrdenAscendente() {
        int[] valores = {50, 10, 40, 20, 30};
        for (int v : valores) {
            this.cola.insertar(v);
        }

        assertEquals(5, this.cola.cantidad());
        assertTrue(this.cola.esMinHeapValido());

        assertEquals(10, this.cola.extraerProximo());
        assertEquals(20, this.cola.extraerProximo());
        assertEquals(30, this.cola.extraerProximo());
        assertEquals(40, this.cola.extraerProximo());
        assertEquals(50, this.cola.extraerProximo());

        assertTrue(this.cola.estaVacia());
        assertEquals(0, this.cola.cantidad());
    }

    /**
     * Valida el comportamiento de flotado continuo cuando se insertan elementos en orden decreciente.
     */
    @Test
    @DisplayName("Inserciones en orden decreciente disparan flotado y preservan el mínimo al frente")
    public void testInsertarOrdenDecrecienteFlotadoContinuo() {
        this.cola.insertar(100);
        assertEquals(100, this.cola.obtenerProximo());

        this.cola.insertar(80);
        assertEquals(80, this.cola.obtenerProximo());

        this.cola.insertar(60);
        assertEquals(60, this.cola.obtenerProximo());

        this.cola.insertar(40);
        assertEquals(40, this.cola.obtenerProximo());

        this.cola.insertar(20);
        assertEquals(20, this.cola.obtenerProximo());

        assertTrue(this.cola.esMinHeapValido());
        assertEquals(5, this.cola.cantidad());

        assertEquals(20, this.cola.extraerProximo());
        assertEquals(40, this.cola.extraerProximo());
        assertEquals(60, this.cola.extraerProximo());
        assertEquals(80, this.cola.extraerProximo());
        assertEquals(100, this.cola.extraerProximo());
    }

    /**
     * Valida que la estructura soporte elementos duplicados con la misma prioridad sin corromperse.
     */
    @Test
    @DisplayName("Soporta inserción de elementos duplicados con idéntica prioridad")
    public void testInsertarElementosDuplicados() {
        this.cola.insertar(15);
        this.cola.insertar(5);
        this.cola.insertar(15);
        this.cola.insertar(5);
        this.cola.insertar(10);

        assertEquals(5, this.cola.cantidad());
        assertTrue(this.cola.esMinHeapValido());

        assertEquals(5, this.cola.extraerProximo());
        assertEquals(5, this.cola.extraerProximo());
        assertEquals(10, this.cola.extraerProximo());
        assertEquals(15, this.cola.extraerProximo());
        assertEquals(15, this.cola.extraerProximo());

        assertTrue(this.cola.estaVacia());
    }

    // 5. INTERCALADO Y REDIMENSIÓN DINÁMICA

    /**
     * Valida una secuencia de operaciones intercaladas (insertar, extraer, reinsertar)
     * simulando el despacho continuo de eventos.
     */
    @Test
    @DisplayName("Secuencia intercalada de inserciones y extracciones mantiene siempre el mínimo al frente")
    public void testIntercaladoInsercionesYExtracciones() {
        this.cola.insertar(30);
        this.cola.insertar(20);
        assertEquals(20, this.cola.extraerProximo()); // Queda 30

        this.cola.insertar(10);
        this.cola.insertar(50);
        assertEquals(10, this.cola.extraerProximo()); // Quedan 30, 50

        this.cola.insertar(15);
        assertEquals(15, this.cola.extraerProximo()); // Quedan 30, 50
        assertEquals(30, this.cola.extraerProximo());
        assertEquals(50, this.cola.extraerProximo());

        assertTrue(this.cola.estaVacia());
    }

    /**
     * Verifica que la cola crezca dinámicamente superando la capacidad inicial por defecto (10 elementos)
     * sin perder elementos ni violar la propiedad de Min-Heap.
     */
    @Test
    @DisplayName("Redimensionamiento dinámico: supera la capacidad inicial sin alterar el orden de prioridad")
    public void testRedimensionDinamicaSuperaCapacidadInicial() {
        int total = 35; // Supera ampliamente la capacidad base de 10
        for (int i = total; i >= 1; i--) {
            this.cola.insertar(i);
        }

        assertEquals(total, this.cola.cantidad());
        assertTrue(this.cola.esMinHeapValido());

        // Debe extraerse en orden estrictamente ascendente del 1 al 35
        for (int i = 1; i <= total; i++) {
            assertEquals(i, this.cola.extraerProximo());
        }

        assertTrue(this.cola.estaVacia());
        assertEquals(0, this.cola.cantidad());
    }

    // 6. GENÉRICOS (STRINGS Y NÚMEROS NEGATIVOS)

    /**
     * Comprueba que la cola funcione con Strings respetando el orden lexicográfico.
     */
    @Test
    @DisplayName("Soporte genérico para String: extrae en orden alfabético")
    public void testGenericoConStrings() {
        ColaDePrioridad<String> colaStrings = new ColaDePrioridad<>();

        colaStrings.insertar("pera");
        colaStrings.insertar("manzana");
        colaStrings.insertar("banana");
        colaStrings.insertar("anana");
        colaStrings.insertar("durazno");

        assertEquals(5, colaStrings.cantidad());
        assertEquals("anana", colaStrings.extraerProximo());
        assertEquals("banana", colaStrings.extraerProximo());
        assertEquals("durazno", colaStrings.extraerProximo());
        assertEquals("manzana", colaStrings.extraerProximo());
        assertEquals("pera", colaStrings.extraerProximo());

        assertTrue(colaStrings.estaVacia());
    }

    /**
     * Verifica que la cola maneje adecuadamente números enteros negativos y cero.
     */
    @Test
    @DisplayName("Manejo adecuado de números negativos y cero")
    public void testValoresNegativosYCero() {
        this.cola.insertar(0);
        this.cola.insertar(-15);
        this.cola.insertar(25);
        this.cola.insertar(-50);
        this.cola.insertar(-5);

        assertEquals(-50, this.cola.extraerProximo());
        assertEquals(-15, this.cola.extraerProximo());
        assertEquals(-5, this.cola.extraerProximo());
        assertEquals(0, this.cola.extraerProximo());
        assertEquals(25, this.cola.extraerProximo());

        assertTrue(this.cola.estaVacia());
    }

    // 7. PRUEBA DE VOLUMEN (1000 ELEMENTOS ALEATORIOS)

    /**
     * Prueba de volumen y estrés con 1000 elementos pseudoaleatorios:
     * Verifica que tras cada inserción se conserve la invariante del Min-Heap,
     * y que al vaciar la cola mediante extracciones continuas los elementos salgan
     * en orden no decreciente (emulando HeapSort).
     */
    @Test
    @DisplayName("Prueba de estrés con 1000 elementos aleatorios: garantiza invariante y orden HeapSort")
    public void testVolumen1000ElementosAleatorios() {
        Random rnd = new Random(12345); // Semilla fija para reproducibilidad
        int total = 1000;

        for (int i = 0; i < total; i++) {
            this.cola.insertar(rnd.nextInt(50000));
        }

        assertEquals(total, this.cola.cantidad());
        assertTrue(this.cola.esMinHeapValido(), "La propiedad de Min-Heap debe cumplirse tras 1000 inserciones");

        int anterior = this.cola.extraerProximo();
        for (int i = 1; i < total; i++) {
            int actual = this.cola.extraerProximo();
            assertTrue(actual >= anterior,
                    "Violación de orden de prioridad: " + actual + " debe ser >= " + anterior);
            anterior = actual;
        }

        assertTrue(this.cola.estaVacia());
        assertEquals(0, this.cola.cantidad());
    }
}
