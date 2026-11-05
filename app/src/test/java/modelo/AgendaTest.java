package modelo;

import static org.junit.jupiter.api.Assertions.*;

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
        agenda.programar(3, "VENCE_ZAPATOS", "zapatos");
        agenda.avanzarPaso();
        agenda.avanzarPaso();
        assertFalse(agenda.hayEventoVencido());
        agenda.avanzarPaso();
        assertTrue(agenda.hayEventoVencido());

        EventoProgramado evento = agenda.sacarProximo();
        assertEquals("VENCE_ZAPATOS", evento.tipo());
        assertEquals("zapatos", evento.dato());
        assertEquals(3, evento.pasoDeVencimiento());
        assertFalse(agenda.hayEventoVencido());
    }

    @Test
    void losEventosSalenEnOrdenDeVencimiento() {
        agenda.programar(5, "TARDE", null);
        agenda.programar(2, "TEMPRANO", null);
        for (int i = 0; i < 5; i++) {
            agenda.avanzarPaso();
        }
        assertEquals("TEMPRANO", agenda.sacarProximo().tipo());
        assertEquals("TARDE", agenda.sacarProximo().tipo());
    }

    @Test
    void conIgualVencimientoSaleElProgramadoPrimero() {
        agenda.programar(2, "A", null);
        agenda.programar(2, "B", null);
        agenda.avanzarPaso();
        agenda.avanzarPaso();
        assertEquals("A", agenda.sacarProximo().tipo());
        assertEquals("B", agenda.sacarProximo().tipo());
    }

    @Test
    void laProgramacionEsRelativaAlPasoActual() {
        agenda.avanzarPaso();
        agenda.avanzarPaso();
        agenda.programar(2, "DESPUES", null); // vence en el paso 4
        agenda.avanzarPaso();
        assertFalse(agenda.hayEventoVencido());
        agenda.avanzarPaso();
        assertTrue(agenda.hayEventoVencido());
    }

    @Test
    void sacarSinEventosVencidosLanzaExcepcion() {
        agenda.programar(5, "LEJANO", null);
        assertThrows(IllegalStateException.class, () -> agenda.sacarProximo());
    }

    @Test
    void parametrosInvalidosLanzanExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> agenda.programar(0, "X", null));
        assertThrows(IllegalArgumentException.class, () -> agenda.programar(3, " ", null));
        assertThrows(IllegalArgumentException.class, () -> agenda.programar(3, null, null));
    }
}