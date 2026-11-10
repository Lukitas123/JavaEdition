package estructuras;

import modelo.Posicion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la estructura GrafoConPesos.
 * Valida creación de vértices, aristas dirigidas y no dirigidas, consulta y actualización
 * de pesos, excepciones ante nulos y pesos negativos, iteración sobre vértices y pruebas
 * con tipos genéricos del modelo (Posicion).
 */
public class GrafoConPesosTest {

    private GrafoConPesos<String> grafo;

    @BeforeEach
    public void inicializar() {
        this.grafo = new GrafoConPesos<>();
    }

    // ==========================================
    // 1. ESTADO INICIAL
    // ==========================================

    /**
     * Verifica que un grafo recién instanciado se encuentre vacío y con 0 vértices.
     */
    @Test
    @DisplayName("Un grafo recién creado debe estar vacío y tener cantidad de vértices cero")
    public void testGrafoNuevoEstaVacio() {
        assertTrue(this.grafo.estaVacio());
        assertEquals(0, this.grafo.cantidadVertices());
        assertEquals(0, this.grafo.cantidad());
        assertNotNull(this.grafo.obtenerVertices());
        assertEquals(0, this.grafo.obtenerVertices().cantidad());
    }

    /**
     * Comprueba que consultar vértices en un grafo vacío retorne false o null de forma segura.
     */
    @Test
    @DisplayName("Consultar o buscar en un grafo vacío retorna false o null")
    public void testConsultasEnGrafoVacio() {
        assertFalse(this.grafo.contieneVertice("A"));
        assertFalse(this.grafo.contieneVertice(null));
        assertNull(this.grafo.obtenerVertice("A"));
        assertNull(this.grafo.obtenerAdyacentes("A"));
        assertFalse(this.grafo.contieneArista("A", "B"));
    }

    // ==========================================
    // 2. GESTIÓN DE VÉRTICES
    // ==========================================

    /**
     * Valida que agregar un vértice incremente la cantidad, deje de estar vacío y permita recuperarlo.
     */
    @Test
    @DisplayName("Agregar un vértice incrementa la cantidad y permite obtenerlo")
    public void testAgregarVerticeUnico() {
        this.grafo.agregarVertice("Inicio");

        assertFalse(this.grafo.estaVacio());
        assertEquals(1, this.grafo.cantidadVertices());
        assertTrue(this.grafo.contieneVertice("Inicio"));

        GrafoConPesos.Vertice<String> v = this.grafo.obtenerVertice("Inicio");
        assertNotNull(v);
        assertEquals("Inicio", v.getDato());
        assertEquals(0, v.cantidadAdyacentes());
    }

    /**
     * Verifica que agregar un vértice duplicado no incremente el conteo total de vértices.
     */
    @Test
    @DisplayName("Agregar vértices duplicados es idempotente y no altera el tamaño")
    public void testAgregarVerticeDuplicado() {
        this.grafo.agregarVertice("A");
        this.grafo.agregarVertice("A");
        this.grafo.agregarVertice("A");

        assertEquals(1, this.grafo.cantidadVertices());
        assertTrue(this.grafo.contieneVertice("A"));
    }

    /**
     * Valida que intentar agregar un vértice nulo lance IllegalArgumentException sin alterar el grafo.
     */
    @Test
    @DisplayName("Agregar un vértice nulo debe lanzar IllegalArgumentException")
    public void testAgregarVerticeNuloLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> {
            this.grafo.agregarVertice(null);
        });

        assertTrue(this.grafo.estaVacio());
    }

    // ==========================================
    // 3. ARISTAS DIRIGIDAS
    // ==========================================

    /**
     * Valida la creación de una arista dirigida ponderada entre dos vértices.
     * Comprueba que la arista existe en el sentido indicado pero no en sentido contrario.
     */
    @Test
    @DisplayName("Agregar arista dirigida conecta en un solo sentido con el peso indicado")
    public void testAgregarAristaDirigida() {
        this.grafo.agregarArista("A", "B", 10);

        assertEquals(2, this.grafo.cantidadVertices());
        assertTrue(this.grafo.contieneVertice("A"));
        assertTrue(this.grafo.contieneVertice("B"));

        // Existe en el sentido A -> B
        assertTrue(this.grafo.contieneArista("A", "B"));
        assertEquals(10, this.grafo.obtenerPeso("A", "B"));

        // NO existe en el sentido B -> A
        assertFalse(this.grafo.contieneArista("B", "A"));

        // El vértice A tiene 1 adyacente y B tiene 0 adyacentes
        assertEquals(1, this.grafo.obtenerVertice("A").cantidadAdyacentes());
        assertEquals(0, this.grafo.obtenerVertice("B").cantidadAdyacentes());
    }

    /**
     * Verifica que agregar aristas con peso negativo lance IllegalArgumentException.
     */
    @Test
    @DisplayName("Agregar arista con peso negativo debe lanzar IllegalArgumentException")
    public void testAgregarAristaPesoNegativoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> {
            this.grafo.agregarArista("A", "B", -5);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            this.grafo.agregarAristaNoDirigida("A", "B", -1);
        });
    }

    /**
     * Comprueba que agregar una arista con vértices nulos lance IllegalArgumentException.
     */
    @Test
    @DisplayName("Agregar arista con vértices nulos debe lanzar IllegalArgumentException")
    public void testAgregarAristaConVerticesNulosLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> {
            this.grafo.agregarArista(null, "B", 5);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            this.grafo.agregarArista("A", null, 5);
        });
    }

    /**
     * Valida que agregar una arista entre vértices que ya tenían una arista previa
     * actualice el peso en lugar de crear aristas paralelas duplicadas.
     */
    @Test
    @DisplayName("Agregar arista existente actualiza su peso y no duplica adyacentes")
    public void testActualizarPesoAristaExistente() {
        this.grafo.agregarArista("A", "B", 5);
        assertEquals(5, this.grafo.obtenerPeso("A", "B"));
        assertEquals(1, this.grafo.obtenerVertice("A").cantidadAdyacentes());

        // Actualizamos el peso
        this.grafo.agregarArista("A", "B", 25);
        assertEquals(25, this.grafo.obtenerPeso("A", "B"));
        assertEquals(1, this.grafo.obtenerVertice("A").cantidadAdyacentes());
    }

    // ==========================================
    // 4. ARISTAS NO DIRIGIDAS
    // ==========================================

    /**
     * Comprueba que agregar una arista no dirigida cree aristas simétricas con igual peso
     * en ambos sentidos.
     */
    @Test
    @DisplayName("Agregar arista no dirigida genera conexión simétrica en ambos sentidos")
    public void testAgregarAristaNoDirigida() {
        this.grafo.agregarAristaNoDirigida("Habitacion1", "Habitacion2", 8);

        assertEquals(2, this.grafo.cantidadVertices());
        assertTrue(this.grafo.contieneArista("Habitacion1", "Habitacion2"));
        assertTrue(this.grafo.contieneArista("Habitacion2", "Habitacion1"));

        assertEquals(8, this.grafo.obtenerPeso("Habitacion1", "Habitacion2"));
        assertEquals(8, this.grafo.obtenerPeso("Habitacion2", "Habitacion1"));

        assertEquals(1, this.grafo.obtenerVertice("Habitacion1").cantidadAdyacentes());
        assertEquals(1, this.grafo.obtenerVertice("Habitacion2").cantidadAdyacentes());
    }

    // ==========================================
    // 5. CONSULTAS DE PESO Y EXCEPCIONES
    // ==========================================

    /**
     * Verifica que intentar obtener el peso de una arista inexistente lance IllegalArgumentException.
     */
    @Test
    @DisplayName("obtenerPeso de una arista inexistente debe lanzar IllegalArgumentException")
    public void testObtenerPesoAristaInexistenteLanzaExcepcion() {
        this.grafo.agregarVertice("A");
        this.grafo.agregarVertice("B");

        assertThrows(IllegalArgumentException.class, () -> {
            this.grafo.obtenerPeso("A", "B");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            this.grafo.obtenerPeso("A", "Inexistente");
        });
    }

    // ==========================================
    // 6. ADYACENTES Y AUTO-ARISTAS
    // ==========================================

    /**
     * Valida que un vértice pueda tener múltiples aristas salientes hacia distintos destinos.
     */
    @Test
    @DisplayName("Un vértice puede tener múltiples aristas salientes con distintos pesos")
    public void testMultiplesAdyacentes() {
        this.grafo.agregarArista("Centro", "Norte", 1);
        this.grafo.agregarArista("Centro", "Sur", 3);
        this.grafo.agregarArista("Centro", "Este", 8);

        Lista<GrafoConPesos.Arista<String>> ady = this.grafo.obtenerAdyacentes("Centro");
        assertNotNull(ady);
        assertEquals(3, ady.cantidad());

        assertEquals(1, this.grafo.obtenerPeso("Centro", "Norte"));
        assertEquals(3, this.grafo.obtenerPeso("Centro", "Sur"));
        assertEquals(8, this.grafo.obtenerPeso("Centro", "Este"));
    }

    /**
     * Comprueba el soporte de bucles o auto-aristas (vértice conectado a sí mismo).
     */
    @Test
    @DisplayName("Soporta auto-aristas (vértice conectado consigo mismo)")
    public void testAutoArista() {
        this.grafo.agregarArista("Portal", "Portal", 0);

        assertTrue(this.grafo.contieneArista("Portal", "Portal"));
        assertEquals(0, this.grafo.obtenerPeso("Portal", "Portal"));
        assertEquals(1, this.grafo.obtenerVertice("Portal").cantidadAdyacentes());
    }

    // ==========================================
    // 7. ITERACIÓN SOBRE VÉRTICES
    // ==========================================

    /**
     * Valida que obtenerVertices devuelva una lista con todos los vértices agregados.
     */
    @Test
    @DisplayName("obtenerVertices retorna la lista completa de vértices presentes")
    public void testObtenerVerticesListaCompleta() {
        this.grafo.agregarVertice("A");
        this.grafo.agregarVertice("B");
        this.grafo.agregarVertice("C");

        Lista<GrafoConPesos.Vertice<String>> vertices = this.grafo.obtenerVertices();
        assertEquals(3, vertices.cantidad());

        boolean tieneA = false, tieneB = false, tieneC = false;
        for (int i = 0; i < vertices.cantidad(); i++) {
            String dato = vertices.obtener(i).getDato();
            if ("A".equals(dato)) tieneA = true;
            if ("B".equals(dato)) tieneB = true;
            if ("C".equals(dato)) tieneC = true;
        }

        assertTrue(tieneA && tieneB && tieneC);
    }

    // ==========================================
    // 8. SOPORTE DE TIPOS GENÉRICOS DEL DOMINIO (Posicion)
    // ==========================================

    /**
     * Comprueba que GrafoConPesos funcione perfectamente con tipos de dominio del juego,
     * como el record Posicion(fila, columna).
     */
    @Test
    @DisplayName("GrafoConPesos funciona correctamente con Posicion del modelo del juego")
    public void testGenericoConPosicionDelJuego() {
        GrafoConPesos<Posicion> grafoLaberinto = new GrafoConPesos<>();

        Posicion p1 = new Posicion(1, 1);
        Posicion p2 = new Posicion(1, 2);
        Posicion p3 = new Posicion(2, 1);

        // Simulamos costo de energía: pasar de p1 a p2 cuesta 3 (agua), de p1 a p3 cuesta 1 (piso)
        grafoLaberinto.agregarAristaNoDirigida(p1, p2, 3);
        grafoLaberinto.agregarAristaNoDirigida(p1, p3, 1);

        assertEquals(3, grafoLaberinto.cantidadVertices());
        assertTrue(grafoLaberinto.contieneArista(p1, p2));
        assertEquals(3, grafoLaberinto.obtenerPeso(p1, p2));
        assertEquals(1, grafoLaberinto.obtenerPeso(p1, p3));
    }

    // ==========================================
    // 9. PRUEBA DE VOLUMEN (RED TIPO GRILLA)
    // ==========================================

    /**
     * Prueba de volumen construyendo un grafo tipo grilla 10x10 (100 vértices y aristas bidireccionales).
     * Simula la representación completa de un plano del laberinto para algoritmos de caminos mínimos.
     */
    @Test
    @DisplayName("Prueba de volumen con una grilla 10x10 de 100 vértices y aristas ponderadas")
    public void testVolumenGrilla10x10() {
        GrafoConPesos<Posicion> grillaGrafo = new GrafoConPesos<>();
        int filas = 10;
        int columnas = 10;

        // Conectar cada celda con sus vecinas derecha y abajo con costo variable
        for (int f = 1; f <= filas; f++) {
            for (int c = 1; c <= columnas; c++) {
                Posicion actual = new Posicion(f, c);

                if (c < columnas) {
                    Posicion derecha = new Posicion(f, c + 1);
                    int costo = (f + c) % 5 + 1; // Costo entre 1 y 5
                    grillaGrafo.agregarAristaNoDirigida(actual, derecha, costo);
                }

                if (f < filas) {
                    Posicion abajo = new Posicion(f + 1, c);
                    int costo = (f * c) % 8 + 1; // Costo entre 1 y 8
                    grillaGrafo.agregarAristaNoDirigida(actual, abajo, costo);
                }
            }
        }

        assertEquals(100, grillaGrafo.cantidadVertices());
        assertFalse(grillaGrafo.estaVacio());

        // Verificamos conectividad de esquinas
        assertTrue(grillaGrafo.contieneArista(new Posicion(1, 1), new Posicion(1, 2)));
        assertTrue(grillaGrafo.contieneArista(new Posicion(1, 1), new Posicion(2, 1)));
        assertTrue(grillaGrafo.contieneArista(new Posicion(10, 10), new Posicion(10, 9)));
        assertTrue(grillaGrafo.contieneArista(new Posicion(10, 10), new Posicion(9, 10)));
    }
}
