package modelo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

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
}