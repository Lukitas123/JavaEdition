package modelo;

import static modelo.MundosDePrueba.desdeCadenas;
import static modelo.MundosDePrueba.mazmorraDe;
import static org.junit.jupiter.api.Assertions.*;

import entidades.cofres.CofreDePuente;
import entidades.cofres.TrampaDeLava;
import entidades.monstruos.Cazador;
import entidades.monstruos.Congelador;
import entidades.monstruos.Dormilon;
import entidades.objetos.Sueno;
import modelo.eventos.FinEfectoHeroe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

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
        mazmorra.getHeroe().activarEfecto(EfectoHeroe.ZAPATOS_DE_AGUA, 100);

        mazmorra.moverHeroe(Direccion.DERECHA);
        mazmorra.moverHeroe(Direccion.DERECHA);

        assertEquals(98, mazmorra.getHeroe().getEnergia());
    }

    @Test
    void elEventoVencidoApagaElEfectoAlEmpezarElPaso() {
        Heroe heroe = mazmorra.getHeroe();
        heroe.activarEfecto(EfectoHeroe.ZAPATOS_DE_AGUA, 2);
        mazmorra.getAgenda().programar(
                2, new FinEfectoHeroe(EfectoHeroe.ZAPATOS_DE_AGUA));

        mazmorra.moverHeroe(Direccion.DERECHA);
        assertTrue(heroe.tieneEfecto(EfectoHeroe.ZAPATOS_DE_AGUA));

        // En el paso 2 el efecto vence antes de moverse: el agua vuelve a costar 3
        mazmorra.moverHeroe(Direccion.DERECHA);
        assertFalse(heroe.tieneEfecto(EfectoHeroe.ZAPATOS_DE_AGUA));
        assertEquals(96, heroe.getEnergia());
        assertEquals(0, mazmorra.getAgenda().cantidadDeEventos());
    }

    @Test
    void esperarTambienCorreLaAgenda() {
        Heroe heroe = mazmorra.getHeroe();
        heroe.activarEfecto(EfectoHeroe.ZAPATOS_DE_AGUA, 1);
        mazmorra.getAgenda().programar(
                1, new FinEfectoHeroe(EfectoHeroe.ZAPATOS_DE_AGUA));

        mazmorra.esperar();

        assertFalse(heroe.tieneEfecto(EfectoHeroe.ZAPATOS_DE_AGUA));
        assertEquals(100, heroe.getEnergia());
        assertEquals(1, mazmorra.getAgenda().getPasoActual());
    }

    @Test
    void unEventoSinEfectoActivoSeConsumeSinRomperNada() {
        mazmorra.getAgenda().programar(
                1, new FinEfectoHeroe(EfectoHeroe.ZAPATOS_DE_AGUA));

        mazmorra.moverHeroe(Direccion.DERECHA);

        assertEquals(0, mazmorra.getAgenda().cantidadDeEventos());
        assertEquals(99, mazmorra.getHeroe().getEnergia());
    }
    @Test
    void puenteVenceEnSuPlanoAunqueElHeroeCambieDePiso() {
        Mundo mundo = MundosDePrueba.mundoConEscalera();
        Plano planoOriginal = mundo.planoEn(1, 1);
        Posicion agua = new Posicion(2, 4);

        planoOriginal.cambiarTerreno(agua, Terreno.AGUA);
        planoOriginal.agregarCofre(new CofreDePuente(new Posicion(2, 3), 15));
        Mazmorra partida = mazmorraDe(mundo, 100);

        partida.moverHeroe(Direccion.DERECHA); // Abre el Puente en el paso 1.
        assertEquals(Terreno.PISO, planoOriginal.terrenoEn(agua));

        partida.moverHeroe(Direccion.ABAJO); // Usa la escalera.
        assertEquals(2, partida.getHeroe().getUbicacion().piso());

        while (partida.getAgenda().getPasoActual() < 16) {
            partida.esperar();
        }
        assertEquals(Terreno.PISO, planoOriginal.terrenoEn(agua));

        partida.esperar(); // Paso 17: vence el Puente.
        assertEquals(Terreno.AGUA, planoOriginal.terrenoEn(agua));
    }

    @Test
    void trampaDeLavaVenceAlAvanzarLaPartida() {
        Plano plano = mazmorra.planoActual();
        Posicion cofre = new Posicion(2, 3);
        Posicion agua = new Posicion(2, 4);
        plano.agregarCofre(new TrampaDeLava(cofre, 10));

        mazmorra.moverHeroe(Direccion.DERECHA); // Abre la trampa en el paso 1.
        assertEquals(Terreno.LAVA, plano.terrenoEn(agua));

        while (mazmorra.getAgenda().getPasoActual() < 11) {
            mazmorra.esperar();
        }
        assertEquals(Terreno.LAVA, plano.terrenoEn(agua));

        mazmorra.esperar(); // Paso 12: vence la lava.
        assertEquals(Terreno.AGUA, plano.terrenoEn(agua));
    }
    @Test
    void suenoImpideActuarHastaQueElMotorLoFinaliza() {
        Plano plano = mazmorra.planoActual();
        Cazador cazador = new Cazador(new Posicion(2, 5), new Costo(1, 3, 8),30);
        plano.agregarMonstruo(cazador);

        new Sueno().usar(mazmorra.getHeroe(), plano, mazmorra.getAgenda());

        for (int i = 0; i < 7; i++) {
            mazmorra.esperar();
        }
        assertTrue(plano.getEfectos().monstruosDormidos());
        assertEquals(new Posicion(2, 5), cazador.getPosicion());

        mazmorra.esperar(); // Paso 8: vence el sueño y actúa el cazador.
        assertFalse(plano.getEfectos().monstruosDormidos());
        assertEquals(new Posicion(2, 4), cazador.getPosicion());
    }

    @Test
    void dormilonDespiertaEnElPasoSiguiente() {
        Plano plano = mazmorra.planoActual();
        Dormilon dormilon = new Dormilon(
                new Posicion(2, 5), new Costo(1, 3, 8),30);
        plano.agregarMonstruo(dormilon);

        mazmorra.moverHeroe(Direccion.DERECHA);
        assertFalse(dormilon.estaDespierto());
        assertEquals(new Posicion(2, 5), dormilon.getPosicion());

        mazmorra.esperar();
        assertTrue(dormilon.estaDespierto());
        assertEquals(new Posicion(2, 4), dormilon.getPosicion());
    }

    @Test
    void congelacionImpideMoverseHastaSuVencimiento() {
        Heroe heroe = mazmorra.getHeroe();
        Posicion inicio = heroe.getUbicacion().posicion();
        Congelador congelador = new Congelador(inicio, new Random(1), 3);

        congelador.alcanzarAlHeroe(heroe, mazmorra.getAgenda());

        assertTrue(mazmorra.debeEsperarPorInmovilizacion());
        assertFalse(mazmorra.moverHeroe(Direccion.DERECHA));
        assertEquals(0, mazmorra.getAgenda().getPasoActual());
        assertEquals(inicio, heroe.getUbicacion().posicion());

        for (int i = 0; i < 3; i++) {
            mazmorra.esperar();
        }

        assertFalse(mazmorra.debeEsperarPorInmovilizacion());
        assertTrue(mazmorra.moverHeroe(Direccion.DERECHA));
        assertFalse(heroe.tieneEfecto(EfectoHeroe.INMOVILIZADO));
        assertEquals(new Posicion(2, 3), heroe.getUbicacion().posicion());
    }
}
