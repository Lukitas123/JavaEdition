package estructuras;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para el Árbol AVL genérico auto-balanceado (ArbolAVL).
 * Valida inserción, balanceo en los 4 casos de rotación (LL, RR, LR, RL),
 * cálculo de alturas, búsqueda (contiene), recorrido inorden, manejo de duplicados,
 * casos de borde y pruebas de volumen.
 */
public class ArbolAVLTest {

    private ArbolAVL<Integer> arbol;

    @BeforeEach
    public void inicializar() {
        this.arbol = new ArbolAVL<>();
    }

    /**
     * Comprueba que un árbol recién instanciado se encuentre vacío, con altura 0 y sin raíz.
     */
    @Test
    @DisplayName("Un árbol AVL recién creado debe estar vacío, con cantidad 0, altura 0 y raíz nula")
    public void testArbolNuevoEstaVacio() {
        assertTrue(this.arbol.estaVacio());
        assertEquals(0, this.arbol.cantidad());
        assertEquals(0, this.arbol.altura());
        assertNull(this.arbol.obtenerRaiz());
        assertTrue(this.arbol.esAVLValido());
    }

    /**
     * Verifica que intentar insertar un valor nulo lance IllegalArgumentException sin alterar el árbol.
     */
    @Test
    @DisplayName("Insertar un valor nulo debe lanzar IllegalArgumentException y no alterar el árbol")
    public void testInsertarNuloLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> {
            this.arbol.insertar(null);
        });

        assertTrue(this.arbol.estaVacio());
        assertEquals(0, this.arbol.cantidad());
    }

    /**
     * Valida la inserción de un único elemento: el árbol deja de estar vacío, su altura es 1
     * y dicho elemento se ubica como raíz.
     */
    @Test
    @DisplayName("Insertar un elemento único lo coloca como raíz con altura 1")
    public void testInsertarUnicoElemento() {
        this.arbol.insertar(50);

        assertFalse(this.arbol.estaVacio());
        assertEquals(1, this.arbol.cantidad());
        assertEquals(1, this.arbol.altura());
        assertEquals(50, this.arbol.obtenerRaiz());
        assertTrue(this.arbol.contiene(50));
        assertTrue(this.arbol.esAVLValido());
    }


    // ROTACIÓN FUNDAMENTALES

    /**
     * Caso Izquierda-Izquierda (LL):
     * Se insertan 30, 20, 10 en orden decreciente.
     * En un ABB no balanceado quedaría degenerado hacia la izquierda (altura 3, raíz 30).
     * En el AVL debe disparar una Rotación Simple a la Derecha en la raíz 30:
     * La nueva raíz debe ser 20, con 10 a su izquierda y 30 a su derecha, altura 2.
     */
    @Test
    @DisplayName("Rotación simple a la derecha (Caso LL): balancea inserciones consecutivas a la izquierda")
    public void testRotacionSimpleDerechaCasoLL() {
        this.arbol.insertar(30);
        this.arbol.insertar(20);
        this.arbol.insertar(10);

        assertEquals(3, this.arbol.cantidad());
        assertEquals(2, this.arbol.altura());
        assertEquals(20, this.arbol.obtenerRaiz());

        // Verificación de los punteros internos del nodo raíz
        assertNotNull(this.arbol.raiz);
        assertEquals(20, this.arbol.raiz.valor);
        assertEquals(10, this.arbol.raiz.izq.valor);
        assertEquals(30, this.arbol.raiz.der.valor);

        assertTrue(this.arbol.esAVLValido());
    }

    /**
     * Caso Derecha-Derecha (RR):
     * Se insertan 10, 20, 30 en orden creciente.
     * En un ABB no balanceado quedaría degenerado hacia la derecha (altura 3, raíz 10).
     * En el AVL debe disparar una Rotación Simple a la Izquierda en la raíz 10:
     * La nueva raíz debe ser 20, con 10 a su izquierda y 30 a su derecha, altura 2.
     */
    @Test
    @DisplayName("Rotación simple a la izquierda (Caso RR): balancea inserciones consecutivas a la derecha")
    public void testRotacionSimpleIzquierdaCasoRR() {
        this.arbol.insertar(10);
        this.arbol.insertar(20);
        this.arbol.insertar(30);

        assertEquals(3, this.arbol.cantidad());
        assertEquals(2, this.arbol.altura());
        assertEquals(20, this.arbol.obtenerRaiz());

        assertNotNull(this.arbol.raiz);
        assertEquals(20, this.arbol.raiz.valor);
        assertEquals(10, this.arbol.raiz.izq.valor);
        assertEquals(30, this.arbol.raiz.der.valor);

        assertTrue(this.arbol.esAVLValido());
    }

    /**
     * Caso Izquierda-Derecha (LR):
     * Se insertan 30, 10, 20 (el nuevo elemento queda en el hijo derecho del subárbol izquierdo).
     * El AVL debe realizar una Rotación Doble Izquierda-Derecha:
     * 1) Rotación a la izquierda sobre el hijo izquierdo (10).
     * 2) Rotación a la derecha sobre el nodo desbalanceado (30).
     * La nueva raíz debe ser 20, con 10 a la izquierda y 30 a la derecha, altura 2.
     */
    @Test
    @DisplayName("Rotación doble Izquierda-Derecha (Caso LR): balancea inserción en zig-zag izquierda-derecha")
    public void testRotacionDobleIzquierdaDerechaCasoLR() {
        this.arbol.insertar(30);
        this.arbol.insertar(10);
        this.arbol.insertar(20);

        assertEquals(3, this.arbol.cantidad());
        assertEquals(2, this.arbol.altura());
        assertEquals(20, this.arbol.obtenerRaiz());

        assertNotNull(this.arbol.raiz);
        assertEquals(20, this.arbol.raiz.valor);
        assertEquals(10, this.arbol.raiz.izq.valor);
        assertEquals(30, this.arbol.raiz.der.valor);

        assertTrue(this.arbol.esAVLValido());
    }

    /**
     * Caso Derecha-Izquierda (RL):
     * Se insertan 10, 30, 20 (el nuevo elemento queda en el hijo izquierdo del subárbol derecho).
     * El AVL debe realizar una Rotación Doble Derecha-Izquierda:
     * 1) Rotación a la derecha sobre el hijo derecho (30).
     * 2) Rotación a la izquierda sobre el nodo desbalanceado (10).
     * La nueva raíz debe ser 20, con 10 a la izquierda y 30 a la derecha, altura 2.
     */
    @Test
    @DisplayName("Rotación doble Derecha-Izquierda (Caso RL): balancea inserción en zig-zag derecha-izquierda")
    public void testRotacionDobleDerechaIzquierdaCasoRL() {
        this.arbol.insertar(10);
        this.arbol.insertar(30);
        this.arbol.insertar(20);

        assertEquals(3, this.arbol.cantidad());
        assertEquals(2, this.arbol.altura());
        assertEquals(20, this.arbol.obtenerRaiz());

        assertNotNull(this.arbol.raiz);
        assertEquals(20, this.arbol.raiz.valor);
        assertEquals(10, this.arbol.raiz.izq.valor);
        assertEquals(30, this.arbol.raiz.der.valor);

        assertTrue(this.arbol.esAVLValido());
    }


    // ROTACIONES EN SUBÁRBOLES PROFUNDOS

    /**
     * Verifica que una rotación simple a la derecha se propague y ejecute correctamente * cuando el desbalance ocurre en un subárbol interno no-raíz. */@Test
    @DisplayName("Rotación en subárbol izquierdo: mantiene balance sin alterar la raíz principal")
    public void testRotacionEnSubarbolIzquierdo() {
        // Insertamos un árbol con raíz 50 y rama derecha 70
        this.arbol.insertar(50);
        this.arbol.insertar(70);
        this.arbol.insertar(30);
        this.arbol.insertar(20);
        // Al insertar 10, el nodo 30 se desequilibra (LL en el subárbol izquierdo)
        this.arbol.insertar(10);

        assertEquals(5, this.arbol.cantidad());
        assertEquals(50, this.arbol.obtenerRaiz());
        // El subárbol izquierdo debe tener ahora como raíz a 20 con hijos 10 y 30
        assertEquals(20, this.arbol.raiz.izq.valor);
        assertEquals(10, this.arbol.raiz.izq.izq.valor);
        assertEquals(30, this.arbol.raiz.izq.der.valor);

        assertTrue(this.arbol.esAVLValido());
    }

    /**
     * Verifica una rotación doble LR en un subárbol interno. */@Test
    @DisplayName("Rotación doble LR en subárbol interno: reequilibra adecuadamente")
    public void testRotacionDobleLREnSubarbol() {
        this.arbol.insertar(50);
        this.arbol.insertar(70);
        this.arbol.insertar(20);
        this.arbol.insertar(10);
        // Insertar 15 genera desbalance LR en el nodo 20 sin desbalancear la raíz 50
        this.arbol.insertar(15);

        assertEquals(5, this.arbol.cantidad());
        assertEquals(50, this.arbol.obtenerRaiz());
        // El hijo izquierdo de 50 ahora debe ser 15
        assertEquals(15, this.arbol.raiz.izq.valor);
        assertEquals(10, this.arbol.raiz.izq.izq.valor);
        assertEquals(20, this.arbol.raiz.izq.der.valor);

        assertTrue(this.arbol.esAVLValido());
    }


    // 4. DUPLICADOS Y BÚSQUEDA (CONTIENE)

    /**
     * Valida que intentar insertar elementos repetidos sea idempotente: no incrementa la cantidad, * no crea nodos duplicados y preserva el balance. */@Test
    @DisplayName("Insertar elementos duplicados no incrementa la cantidad ni desbalancea el árbol")
    public void testInsertarElementosDuplicados() {
        this.arbol.insertar(15);
        this.arbol.insertar(10);
        this.arbol.insertar(20);

        assertEquals(3, this.arbol.cantidad());

        // Reinsertamos los mismos valores
        this.arbol.insertar(15);
        this.arbol.insertar(10);
        this.arbol.insertar(20);

        assertEquals(3, this.arbol.cantidad());
        assertEquals(2, this.arbol.altura());
        assertTrue(this.arbol.esAVLValido());
    }

    /**
     * Comprueba el funcionamiento de la búsqueda mediante `contiene`: * - En árbol vacío devuelve false. * - Con valores nulos devuelve false de forma segura. * - Elementos presentes devuelven true. * - Elementos ausentes (menores, mayores o intermedios) devuelven false. */@Test
    @DisplayName("El método contiene busca eficientemente en O(log N) y maneja nulos y ausentes")
    public void testMetodoContiene() {
        assertFalse(this.arbol.contiene(10));
        assertFalse(this.arbol.contiene(null));

        int[] valores = {40, 20, 60, 10, 30, 50, 70};
        for (int v : valores) {
            this.arbol.insertar(v);
        }

        // Elementos presentes
        for (int v : valores) {
            assertTrue(this.arbol.contiene(v), "Debe contener el valor: " + v);
        }

        // Elementos ausentes
        assertFalse(this.arbol.contiene(5));
        assertFalse(this.arbol.contiene(25));
        assertFalse(this.arbol.contiene(55));
        assertFalse(this.arbol.contiene(100));
        assertFalse(this.arbol.contiene(null));
    }

    // 5. RECORRIDO INORDEN

    /**
     * Verifica que el recorrido inorden devuelva los elementos en orden estrictamente ascendente, * utilizando la estructura propia Lista. */@Test
    @DisplayName("El recorrido inorden devuelve todos los elementos ordenados ascendentemente")
    public void testRecorridoInordenOrdenado() {
        int[] desordenados = {50, 20, 80, 10, 30, 70, 90, 5, 15, 25, 35};
        for (int v : desordenados) {
            this.arbol.insertar(v);
        }

        Lista<Integer> listaInorden = this.arbol.inorden();
        assertEquals(desordenados.length, listaInorden.cantidad());

        // Verificar que cada elemento sea menor que el siguiente
        for (int i = 0; i < listaInorden.cantidad() - 1; i++) {
            assertTrue(listaInorden.obtener(i) < listaInorden.obtener(i + 1),
                    "El elemento " + listaInorden.obtener(i) + " debe ser menor que " + listaInorden.obtener(i + 1));
        }

        // Primer y último elemento
        assertEquals(5, listaInorden.obtener(0));
        assertEquals(90, listaInorden.obtener(listaInorden.cantidad() - 1));
    }

    /**
     * Verifica que en un árbol vacío el recorrido inorden retorne una lista vacía. */@Test
    @DisplayName("El recorrido inorden en un árbol vacío retorna una lista vacía")
    public void testRecorridoInordenArbolVacio() {
        Lista<Integer> lista = this.arbol.inorden();
        assertTrue(lista.estaVacia());
        assertEquals(0, lista.cantidad());
    }

    // GENÉRICOS

    /**
     * Valida que el árbol AVL funcione correctamente con otros tipos genéricos que * implementan Comparable, como cadenas de texto (String). */@Test
    @DisplayName("El árbol AVL soporta tipos genéricos como String respetando el orden lexicográfico")
    public void testGenericoConStrings() {
        ArbolAVL<String> arbolStrings = new ArbolAVL<>();

        arbolStrings.insertar("delta");
        arbolStrings.insertar("alfa");
        arbolStrings.insertar("charlie");
        arbolStrings.insertar("bravo");
        arbolStrings.insertar("echo");

        assertEquals(5, arbolStrings.cantidad());
        assertTrue(arbolStrings.contiene("alfa"));
        assertTrue(arbolStrings.contiene("bravo"));
        assertFalse(arbolStrings.contiene("foxtrot"));

        assertTrue(arbolStrings.esAVLValido());

        Lista<String> ordenado = arbolStrings.inorden();
        assertEquals("alfa", ordenado.obtener(0));
        assertEquals("bravo", ordenado.obtener(1));
        assertEquals("charlie", ordenado.obtener(2));
        assertEquals("delta", ordenado.obtener(3));
        assertEquals("echo", ordenado.obtener(4));
    }

    // PRUEBAS DE VOLUMEN Y GARANTÍA LOGARÍTMICA

    /**
     * Prueba de degeneración prevenida (inserción secuencial creciente 1..100): * En un ABB común, la altura sería 100 (degenerado como lista enlazada). * En el AVL, la altura debe estar acotada logarítmicamente: h <= 1.44 * log2(N+2). * Para N=100, la altura máxima teórica de un AVL es 8. */@Test
    @DisplayName("Inserción secuencial 1..100: la altura permanece acotada logarítmicamente (máx 8)")
    public void testInsercionSecuencialCrecienteAlturaLogaritmica() {
        for (int i = 1; i <= 100; i++) {
            this.arbol.insertar(i);
            assertTrue(this.arbol.esAVLValido(), "Debe mantener balance tras insertar " + i);
        }

        assertEquals(100, this.arbol.cantidad());
        assertTrue(this.arbol.altura() <= 8, "La altura fue " + this.arbol.altura() + ", debía ser <= 8");

        // Todos deben encontrarse
        for (int i = 1; i <= 100; i++) {
            assertTrue(this.arbol.contiene(i));
        }
    }

    /**
     * Prueba de degeneración prevenida (inserción secuencial decreciente 100..1): * Verifica que inserciones continuas a la izquierda no degeneren el árbol. */@Test
    @DisplayName("Inserción secuencial decreciente 100..1: el árbol reequilibra continuamente")
    public void testInsercionSecuencialDecrecienteAlturaLogaritmica() {
        for (int i = 100; i >= 1; i--) {
            this.arbol.insertar(i);
        }

        assertEquals(100, this.arbol.cantidad());
        assertTrue(this.arbol.altura() <= 8, "La altura fue " + this.arbol.altura() + ", debía ser <= 8");
        assertTrue(this.arbol.esAVLValido());

        Lista<Integer> ordenado = this.arbol.inorden();
        assertEquals(1, ordenado.obtener(0));
        assertEquals(100, ordenado.obtener(99));
    }

    /**
     * Prueba de estrés aleatoria con 500 elementos: * Verifica la solidez del balanceo AVL ante secuencias complejas e impredecibles de rotaciones. */@Test
    @DisplayName("Prueba de volumen con 500 elementos aleatorios: preserva invariante AVL e inorden")
    public void testVolumenAleatorioPreservaInvariantes() {
        Random rnd = new Random(42); // Semilla fija para reproducibilidad
        int total = 500;

        for (int i = 0; i < total; i++) {
            int num = rnd.nextInt(10000);
            this.arbol.insertar(num);
        }

        assertTrue(this.arbol.esAVLValido(), "El árbol debe ser un AVL válido tras 500 inserciones");

        // Altura acotada logarítmicamente: para N<=500, h <= 12
        assertTrue(this.arbol.altura() <= 12, "La altura fue " + this.arbol.altura() + ", debía ser <= 12");

        // El inorden debe estar siempre ordenado
        Lista<Integer> inorden = this.arbol.inorden();
        for (int i = 0; i < inorden.cantidad() - 1; i++) {
            assertTrue(inorden.obtener(i) <= inorden.obtener(i + 1),
                    "Violación de orden en el recorrido inorden");
        }
    }
}
