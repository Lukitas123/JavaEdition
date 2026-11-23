package modelo;

import static modelo.MundosDePrueba.desdeCadenas;
import static modelo.MundosDePrueba.mazmorraDe;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Regla: al empezar cada paso se ejecutan los eventos de la agenda cuyo momento llegó */
class MazmorraAgendaTest {
    private Mazmorra mazmorra;

    @BeforeEach
    void armar() {
        mazmorra = mazmorraDe(desdeCadenas(
                "#######",
                "#E.~~.#",
                "#######"), 100);
    }

    @Test
    void sinZapatosElAguaCuestaTres() {
        mazmorra.moverHeroe(Direccion.DERECHA);
        mazmorra.moverHeroe(Direccion.DERECHA);

        assertEquals(96, mazmorra.getHeroe().getEnergia());
    }

    @Test
    void conZapatosElAguaCuestaComoPiso() {
        mazmorra.getHeroe().activarEfecto(Mazmorra.EFECTO_ZAPATOS_AGUA, 100);

        mazmorra.moverHeroe(Direccion.DERECHA);
        mazmorra.moverHeroe(Direccion.DERECHA);

        assertEquals(98, mazmorra.getHeroe().getEnergia());
    }

    @Test
    void elEventoVencidoApagaElEfectoAlEmpezarElPaso() {
        Heroe heroe = mazmorra.getHeroe();
        heroe.activarEfecto(Mazmorra.EFECTO_ZAPATOS_AGUA, 2);
        mazmorra.getAgenda().programar(
                2, TipoEvento.FIN_ZAPATOS_DE_AGUA, null);

        mazmorra.moverHeroe(Direccion.DERECHA);
        assertTrue(heroe.tieneEfecto(Mazmorra.EFECTO_ZAPATOS_AGUA));

        // En el paso 2 el efecto vence antes de moverse: el agua vuelve a costar 3
        mazmorra.moverHeroe(Direccion.DERECHA);
        assertFalse(heroe.tieneEfecto(Mazmorra.EFECTO_ZAPATOS_AGUA));
        assertEquals(96, heroe.getEnergia());
        assertEquals(0, mazmorra.getAgenda().cantidadDeEventos());
    }

    @Test
    void esperarTambienCorreLaAgenda() {
        Heroe heroe = mazmorra.getHeroe();
        heroe.activarEfecto(Mazmorra.EFECTO_ZAPATOS_AGUA, 1);
        mazmorra.getAgenda().programar(
                1, TipoEvento.FIN_ZAPATOS_DE_AGUA, null);

        mazmorra.esperar();

        assertFalse(heroe.tieneEfecto(Mazmorra.EFECTO_ZAPATOS_AGUA));
        assertEquals(100, heroe.getEnergia());
        assertEquals(1, mazmorra.getAgenda().getPasoActual());
    }

    @Test
    void unEventoSinEfectoActivoSeConsumeSinRomperNada() {
        mazmorra.getAgenda().programar(
                1, TipoEvento.FIN_INMOVILIZACION, null);

        mazmorra.moverHeroe(Direccion.DERECHA);

        assertEquals(0, mazmorra.getAgenda().cantidadDeEventos());
        assertEquals(99, mazmorra.getHeroe().getEnergia());
    }
}
