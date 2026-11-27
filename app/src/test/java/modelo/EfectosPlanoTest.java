package modelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EfectosPlanoTest {
    private Plano crearPlano() {
        return new Plano(
                new Terreno[][] {{Terreno.PISO, Terreno.AGUA}},
                new Posicion(1, 1), null);
    }

    @Test
    void losEfectosSoloAfectanAlPlanoAsociado() {
        Plano primero = crearPlano();
        Plano segundo = crearPlano();
        Posicion agua = new Posicion(1, 2);

        primero.getEfectos().activarPuente(0, 15);
        primero.getEfectos().dormirMonstruos(0, 7);

        assertEquals(Terreno.PISO, primero.terrenoEn(agua));
        assertEquals(Terreno.AGUA, segundo.terrenoEn(agua));
        assertTrue(primero.getEfectos().monstruosDormidos());
        assertFalse(segundo.getEfectos().monstruosDormidos());
    }

    @Test
    void accesoDirectoValidaPosicionesSinCambiarLosEfectos() {
        Plano plano = crearPlano();
        Posicion agua = new Posicion(1, 2);
        Posicion fuera = new Posicion(1, 3);
        EfectosPlano efectos = plano.getEfectos();
        efectos.activarPuente(0, 15);

        assertThrows(IllegalArgumentException.class,
                () -> efectos.estaCubiertaPorPuente(fuera));
        assertThrows(IllegalArgumentException.class,
                () -> efectos.estaCubiertaPorPuente(null));
        assertThrows(IllegalArgumentException.class,
                () -> efectos.cancelarEn(fuera));

        assertTrue(efectos.estaCubiertaPorPuente(agua));
        assertEquals(Terreno.PISO, plano.terrenoEn(agua));
    }
}
