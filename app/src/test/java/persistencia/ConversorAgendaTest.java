package persistencia;

import static org.junit.jupiter.api.Assertions.*;

import modelo.TipoEvento;
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
        original.programar(5, TipoEvento.FIN_INMOVILIZACION, "x");
        original.programar(2, TipoEvento.FIN_ZAPATOS_DE_AGUA, null);

        Gson gson = new Gson();
        String json = gson.toJson(ConversorAgenda.aGuardada(original));
        Agenda restaurada = ConversorAgenda.aAgenda(gson.fromJson(json, AgendaGuardada.class));

        assertEquals(3, restaurada.getPasoActual());
        assertEquals(2, restaurada.cantidadDeEventos());
        assertEquals(TipoEvento.FIN_INMOVILIZACION, restaurada.eventoEn(0).tipo());
        assertEquals(8, restaurada.eventoEn(0).pasoDeVencimiento());

        restaurada.avanzarPaso();
        restaurada.avanzarPaso();
        assertTrue(restaurada.hayEventoVencido());
        assertEquals(TipoEvento.FIN_ZAPATOS_DE_AGUA, restaurada.sacarProximo().tipo());
    }

    @Test
    void conservaUnEventoYaVencidoAlCargar() {
        Agenda original = new Agenda();
        original.programar(1, TipoEvento.FIN_INMOVILIZACION, null);
        original.avanzarPaso();
        original.avanzarPaso(); // paso 2; el evento vencía en el 1

        Gson gson = new Gson();
        String json = gson.toJson(ConversorAgenda.aGuardada(original));
        Agenda restaurada = ConversorAgenda.aAgenda(
                gson.fromJson(json, AgendaGuardada.class));

        assertEquals(2, restaurada.getPasoActual());
        assertTrue(restaurada.hayEventoVencido());
        assertEquals(1, restaurada.sacarProximo().pasoDeVencimiento());
    }
}
