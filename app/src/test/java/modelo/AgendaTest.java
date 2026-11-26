package modelo;

import static org.junit.jupiter.api.Assertions.*;

import modelo.eventos.FinEfectoHeroe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AgendaTest {
    private Agenda agenda;

    @BeforeEach
    void armarAgenda() {
        agenda = new Agenda();
    }

    @Test
    void empiezaVaciaEnPasoCero() {
        assertEquals(0, agenda.getPasoActual());
        assertEquals(0, agenda.cantidadDeEventos());
        assertFalse(agenda.hayEventoVencido());
    }

    @Test
    void elEventoVenceCuandoPasaLaCantidadDePasos() {
        FinEfectoHeroe finZapatos =
                new FinEfectoHeroe(EfectoHeroe.ZAPATOS_DE_AGUA);
        agenda.programar(3, finZapatos);

        agenda.avanzarPaso();
        agenda.avanzarPaso();
        assertFalse(agenda.hayEventoVencido());

        agenda.avanzarPaso();
        assertTrue(agenda.hayEventoVencido());

        EventoProgramado evento = agenda.sacarProximo();
        assertEquals(finZapatos, evento.evento());
        assertEquals(3, evento.pasoDeVencimiento());
        assertFalse(agenda.hayEventoVencido());
    }

    @Test
    void losEventosSalenEnOrdenDeVencimiento() {
        FinEfectoHeroe finZapatos =
                new FinEfectoHeroe(EfectoHeroe.ZAPATOS_DE_AGUA);
        FinEfectoHeroe finInmovilizacion =
                new FinEfectoHeroe(EfectoHeroe.INMOVILIZADO);

        agenda.programar(5, finZapatos);
        agenda.programar(2, finInmovilizacion);

        for (int i = 0; i < 5; i++) {
            agenda.avanzarPaso();
        }

        assertEquals(finInmovilizacion, agenda.sacarProximo().evento());
        assertEquals(finZapatos, agenda.sacarProximo().evento());
    }

    @Test
    void conIgualVencimientoSaleElProgramadoPrimero() {
        FinEfectoHeroe primero =
                new FinEfectoHeroe(EfectoHeroe.INMOVILIZADO);
        FinEfectoHeroe segundo =
                new FinEfectoHeroe(EfectoHeroe.ZAPATOS_DE_AGUA);

        agenda.programar(2, primero);
        agenda.programar(2, segundo);
        agenda.avanzarPaso();
        agenda.avanzarPaso();

        assertEquals(primero, agenda.sacarProximo().evento());
        assertEquals(segundo, agenda.sacarProximo().evento());
    }

    @Test
    void laProgramacionEsRelativaAlPasoActual() {
        agenda.avanzarPaso();
        agenda.avanzarPaso();
        agenda.programar(2, new FinEfectoHeroe(EfectoHeroe.INMOVILIZADO));

        agenda.avanzarPaso();
        assertFalse(agenda.hayEventoVencido());
        agenda.avanzarPaso();
        assertTrue(agenda.hayEventoVencido());
    }

    @Test
    void sacarSinEventosVencidosLanzaExcepcion() {
        agenda.programar(5, new FinEfectoHeroe(EfectoHeroe.ZAPATOS_DE_AGUA));
        assertThrows(IllegalStateException.class, () -> agenda.sacarProximo());
    }

    @Test
    void parametrosInvalidosLanzanExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> agenda.programar(3, null));
        assertThrows(IllegalArgumentException.class,
                () -> agenda.programar(0, new FinEfectoHeroe(EfectoHeroe.ESCUDO)));
    }
}