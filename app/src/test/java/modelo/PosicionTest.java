package modelo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

class PosicionTest {
    @Test
    void moverEnCadaDireccion() {
        Posicion p = new Posicion(3, 3);
        assertEquals(new Posicion(2, 3), p.mover(Direccion.ARRIBA));
        assertEquals(new Posicion(4, 3), p.mover(Direccion.ABAJO));
        assertEquals(new Posicion(3, 2), p.mover(Direccion.IZQUIERDA));
        assertEquals(new Posicion(3, 4), p.mover(Direccion.DERECHA));
    }

    @Test
    void distanciaManhattan() {
        assertEquals(7, new Posicion(8, 3).distanciaManhattan(new Posicion(2, 2)));
        assertEquals(0, new Posicion(2, 2).distanciaManhattan(new Posicion(2, 2)));
    }

    @Test
    void posicionesIgualesTienenElMismoHashCode() {
        Posicion a = new Posicion(2, 3);
        Posicion b = new Posicion(2, 3);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, new Posicion(3, 2));
    }

    @Test
    void funcionaComoClaveDeHash() {
        Set<Posicion> visitadas = new HashSet<>();
        visitadas.add(new Posicion(2, 3));
        visitadas.add(new Posicion(2, 3));

        assertEquals(1, visitadas.size());
        assertTrue(visitadas.contains(new Posicion(2, 3)));
        assertFalse(visitadas.contains(new Posicion(3, 2)));
    }
}