package modelo;

import static modelo.MundosDePrueba.desdeCadenas;
import static modelo.MundosDePrueba.mazmorraDe;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Regla: moverse una celda, el costo de cada terreno y que la pared no es una acción */
class MazmorraMovimientoTest {
    private Plano plano;
    private Mazmorra mazmorra;

    @BeforeEach
    void armar() {
        plano = desdeCadenas(
                "#######",
                "#E.~..#",
                "#.^...#",
                "#######");
        mazmorra = mazmorraDe(plano, 100);
    }

    @Test
    void moverAUnPisoCuestaUnoYCuentaComoPaso() {
        assertTrue(mazmorra.moverHeroe(Direccion.DERECHA));

        assertEquals(new Ubicacion(1, 1, new Posicion(2, 3)), mazmorra.getHeroe().getUbicacion());
        assertEquals(99, mazmorra.getHeroe().getEnergia());
        assertEquals(1, mazmorra.getAgenda().getPasoActual());
    }

    @Test
    void entrarAlAguaCuestaTres() {
        mazmorra.moverHeroe(Direccion.DERECHA);
        mazmorra.moverHeroe(Direccion.DERECHA);

        assertEquals(new Posicion(2, 4), mazmorra.getHeroe().getUbicacion().posicion());
        assertEquals(96, mazmorra.getHeroe().getEnergia());
    }

    @Test
    void entrarALaLavaCuestaOcho() {
        mazmorra.moverHeroe(Direccion.ABAJO);
        mazmorra.moverHeroe(Direccion.DERECHA);

        assertEquals(new Posicion(3, 3), mazmorra.getHeroe().getUbicacion().posicion());
        assertEquals(91, mazmorra.getHeroe().getEnergia());
    }

    @Test
    void laParedNoEsUnaAccionValidaYNoCuentaComoPaso() {
        assertFalse(mazmorra.moverHeroe(Direccion.ARRIBA));

        assertEquals(new Posicion(2, 2), mazmorra.getHeroe().getUbicacion().posicion());
        assertEquals(100, mazmorra.getHeroe().getEnergia());
        assertEquals(0, mazmorra.getAgenda().getPasoActual());
    }

    @Test
    void losCostosSonConfigurables() {
        Mazmorra otra = mazmorraDe(plano, 100, new Costo(2, 5, 10));

        otra.moverHeroe(Direccion.DERECHA);
        otra.moverHeroe(Direccion.DERECHA);

        assertEquals(93, otra.getHeroe().getEnergia());
    }

    @Test
    void lasCeldasPisadasQuedanVisitadas() {
        assertFalse(plano.estaVisitada(new Posicion(2, 3)));

        mazmorra.moverHeroe(Direccion.DERECHA);

        assertTrue(plano.estaVisitada(new Posicion(2, 3)));
    }

    @Test
    void sinDireccionLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> mazmorra.moverHeroe(null));
    }
}
