package persistencia;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.google.gson.Gson;

import modelo.Agenda;

public class ConversorAgendaTest {

    /**Guarda una Agenda pasada por JSON y esta conserva paso, eventos y orden */
    @Test
    void agendaSobreviveAConversion() {
        Agenda original = new Agenda();
        original.avanzarPaso();
        original.avanzarPaso();
        original.avanzarPaso();
        original.programar(5, "evento1", "x");
        original.programar(2, "evento2", null);

        Gson gson = new Gson();
        String json = gson.toJson(ConversorAgenda.aGuardada(original));
        Agenda restaurada = ConversorAgenda.aAgenda(gson.fromJson(json, AgendaGuardada.class));

        assertEquals(3, restaurada.getPasoActual());
        assertEquals(2, restaurada.cantidadDeEventos());
        assertEquals("evento1", restaurada.eventoEn(0).tipo());
        assertEquals(8, restaurada.eventoEn(0).pasoDeVencimiento());

        restaurada.avanzarPaso();
        restaurada.avanzarPaso();
        assertTrue(restaurada.hayEventoVencido());
        assertEquals("evento2", restaurada.sacarProximo().tipo());
    }
}
