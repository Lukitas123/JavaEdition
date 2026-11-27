package entidades.monstruos;

import static org.junit.jupiter.api.Assertions.*;

import modelo.eventos.DespertarDormilones;
import org.junit.jupiter.api.Test;

import modelo.Agenda;
import modelo.Costo;
import modelo.EventoProgramado;
import modelo.Heroe;
import modelo.Plano;
import modelo.Posicion;
import modelo.Terreno;
import modelo.Ubicacion;

class DormilonTest {
    @Test
    void detectaAlHeroeProgramaUnSoloDespertarYLuegoPersigue() {
        Posicion inicio = new Posicion(1, 1);
        Plano plano = new Plano(new Terreno[][] {
                {Terreno.PISO, Terreno.PISO, Terreno.PISO,
                        Terreno.PISO, Terreno.PISO}
        }, inicio, null);
        Dormilon dormilon = new Dormilon(inicio, new Costo(1, 3, 8),30);
        Heroe heroe = new Heroe(100, new Ubicacion(1, 1, new Posicion(1, 5)));
        Agenda agenda = new Agenda();

        dormilon.actuar(plano, heroe, agenda);
        assertEquals(0, agenda.cantidadDeEventos());
        assertEquals(inicio, dormilon.getPosicion());

        heroe.irA(new Ubicacion(1, 1, new Posicion(1, 4)));
        dormilon.actuar(plano, heroe, agenda);
        dormilon.actuar(plano, heroe, agenda);

        assertFalse(dormilon.estaDespierto());
        assertEquals(inicio, dormilon.getPosicion());
        assertEquals(1, agenda.cantidadDeEventos());

        agenda.avanzarPaso();
        EventoProgramado evento = agenda.sacarProximo();
        assertEquals(new DespertarDormilones(1, 1), evento.evento());

        dormilon.despertarSiCorresponde(agenda.getPasoActual());
        assertTrue(dormilon.estaDespierto());

        dormilon.actuar(plano, heroe, agenda);
        assertEquals(new Posicion(1, 2), dormilon.getPosicion());
    }

    @Test
    void elSuenoDelPlanoBloqueaAunqueElDormilonEsteDespierto() {
        Posicion inicio = new Posicion(1, 1);
        Plano plano = new Plano(
                new Terreno[][] {{Terreno.PISO, Terreno.PISO}},
                inicio, null);
        Dormilon dormilon = new Dormilon(inicio, new Costo(1, 3, 8),30);
        Heroe heroe = new Heroe(100, new Ubicacion(1, 1, new Posicion(1, 2)));
        Agenda agenda = new Agenda();

        dormilon.actuar(plano, heroe, agenda);
        agenda.avanzarPaso();
        agenda.sacarProximo();
        dormilon.despertarSiCorresponde(agenda.getPasoActual());

        plano.getEfectos().dormirMonstruos(agenda.getPasoActual(), 7);
        dormilon.actuar(plano, heroe, agenda);

        assertTrue(dormilon.estaDespierto());
        assertEquals(inicio, dormilon.getPosicion());
        assertEquals(100, heroe.getEnergia());
    }
}
