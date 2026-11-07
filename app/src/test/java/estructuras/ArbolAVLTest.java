package estructuras;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para el Árbol AVL genérico auto-balanceado (ArbolAVL).
 *
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



}
