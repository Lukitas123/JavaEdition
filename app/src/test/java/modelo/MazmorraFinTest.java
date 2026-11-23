package modelo;

import static modelo.MundosDePrueba.desdeCadenas;
import static modelo.MundosDePrueba.mazmorraDe;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/** Regla: se gana al pisar la salida y se pierde si la energía llega a 0 */
class MazmorraFinTest {
    private static Plano corredorConSalida() {
        return desdeCadenas(
                "#####",
                "#E.S#",
                "#####");
    }

    @Test
    void pisarLaSalidaGana() {
        Mazmorra mazmorra = mazmorraDe(corredorConSalida(), 100);

        mazmorra.moverHeroe(Direccion.DERECHA);
        assertEquals(EstadoPartida.EN_CURSO, mazmorra.getEstado());
        mazmorra.moverHeroe(Direccion.DERECHA);

        assertEquals(EstadoPartida.GANADA, mazmorra.getEstado());
        assertTrue(mazmorra.estaTerminada());
        // 0 cofres x 100 + 98 de energia x 2 - 2 pasos
        assertEquals(194, mazmorra.calcularPuntaje().valor());
    }

    @Test
    void quedarseSinEnergiaPierde() {
        Mazmorra mazmorra = mazmorraDe(desdeCadenas(
                "#####",
                "#E^.#",
                "#####"), 5);

        mazmorra.moverHeroe(Direccion.DERECHA); // la lava cuesta 8

        assertEquals(0, mazmorra.getHeroe().getEnergia());
        assertEquals(EstadoPartida.PERDIDA, mazmorra.getEstado());
    }

    @Test
    void siPisaLaSalidaConLaUltimaEnergiaGana() {
        Mazmorra mazmorra = mazmorraDe(desdeCadenas(
                "####",
                "#ES#",
                "####"), 1);

        mazmorra.moverHeroe(Direccion.DERECHA);

        assertEquals(0, mazmorra.getHeroe().getEnergia());
        assertEquals(EstadoPartida.GANADA, mazmorra.getEstado());
    }

    @Test
    void unaPartidaGanadaNoAdmiteMasAcciones() {
        Mazmorra mazmorra = mazmorraDe(corredorConSalida(), 100);
        mazmorra.moverHeroe(Direccion.DERECHA);
        mazmorra.moverHeroe(Direccion.DERECHA);

        assertThrows(IllegalStateException.class, () -> mazmorra.moverHeroe(Direccion.IZQUIERDA));
        assertThrows(IllegalStateException.class, () -> mazmorra.esperar());
    }

    @Test
    void unaPartidaPerdidaTampocoAdmiteMasAcciones() {
        Mazmorra mazmorra = mazmorraDe(desdeCadenas(
                "#####",
                "#E^.#",
                "#####"), 5);
        mazmorra.moverHeroe(Direccion.DERECHA);

        assertThrows(IllegalStateException.class, () -> mazmorra.moverHeroe(Direccion.DERECHA));
        assertThrows(IllegalStateException.class, () -> mazmorra.esperar());
    }
}
