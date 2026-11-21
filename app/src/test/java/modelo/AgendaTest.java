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
        agenda.programar(3, TipoEvento.FIN_ZAPATOS_DE_AGUA, "zapatos");
        agenda.avanzarPaso();
        agenda.avanzarPaso();
        assertFalse(agenda.hayEventoVencido());
        agenda.avanzarPaso();
        assertTrue(agenda.hayEventoVencido());

        EventoProgramado evento = agenda.sacarProximo();
        assertEquals(TipoEvento.FIN_ZAPATOS_DE_AGUA, evento.tipo());
        assertEquals("zapatos", evento.dato());
        assertEquals(3, evento.pasoDeVencimiento());
        assertFalse(agenda.hayEventoVencido());
    }

    @Test
    void losEventosSalenEnOrdenDeVencimiento() {
        agenda.programar(5, TipoEvento.FIN_ZAPATOS_DE_AGUA, null);
        agenda.programar(2, TipoEvento.FIN_INMOVILIZACION, null);
        for (int i = 0; i < 5; i++) {
            agenda.avanzarPaso();
        }
        assertEquals(TipoEvento.FIN_INMOVILIZACION, agenda.sacarProximo().tipo());
        assertEquals(TipoEvento.FIN_ZAPATOS_DE_AGUA, agenda.sacarProximo().tipo());
    }

    @Test
    void conIgualVencimientoSaleElProgramadoPrimero() {
        agenda.programar(2, TipoEvento.FIN_INMOVILIZACION, null);
        agenda.programar(2, TipoEvento.FIN_ZAPATOS_DE_AGUA, null);
        agenda.avanzarPaso();
        agenda.avanzarPaso();
        assertEquals(TipoEvento.FIN_INMOVILIZACION, agenda.sacarProximo().tipo());
        assertEquals(TipoEvento.FIN_ZAPATOS_DE_AGUA, agenda.sacarProximo().tipo());
    }

    @Test
    void laProgramacionEsRelativaAlPasoActual() {
        agenda.avanzarPaso();
        agenda.avanzarPaso();
        agenda.programar(2, TipoEvento.FIN_INMOVILIZACION, null); // vence en el paso 4
        agenda.avanzarPaso();
        assertFalse(agenda.hayEventoVencido());
        agenda.avanzarPaso();
        assertTrue(agenda.hayEventoVencido());
    }

    @Test
    void sacarSinEventosVencidosLanzaExcepcion() {
        agenda.programar(5, TipoEvento.FIN_ZAPATOS_DE_AGUA, null);
        assertThrows(IllegalStateException.class, () -> agenda.sacarProximo());
    }

    @Test
    void parametrosInvalidosLanzanExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> agenda.programar(3, null, null));
    }
}