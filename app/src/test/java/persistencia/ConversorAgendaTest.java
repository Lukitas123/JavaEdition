package persistencia;

import static org.junit.jupiter.api.Assertions.*;

import modelo.EfectoHeroe;
import modelo.eventos.*;
import org.junit.jupiter.api.Test;
import com.google.gson.Gson;

import modelo.Agenda;

public class ConversorAgendaTest {

    private Agenda convertirPorJson(Agenda original) {
        Gson gson = new Gson();
        String json = gson.toJson(ConversorAgenda.aGuardada(original));
        AgendaGuardada leida = gson.fromJson(json, AgendaGuardada.class);
        return ConversorAgenda.aAgenda(leida);
    }

    /**Guarda una Agenda pasada por JSON y esta conserva paso, eventos y orden */
    @Test
    void agendaSobreviveAConversion() {
        Agenda original = new Agenda();
        original.avanzarPaso();
        original.avanzarPaso();
        original.avanzarPaso();
        Evento finInmovilizacion = new FinEfectoHeroe(EfectoHeroe.INMOVILIZADO);
        Evento finZapatos = new FinEfectoHeroe(EfectoHeroe.ZAPATOS_DE_AGUA);
        original.programar(5, finInmovilizacion);
        original.programar(2, finZapatos);

        Agenda restaurada = convertirPorJson(original);

        assertEquals(3, restaurada.getPasoActual());
        assertEquals(2, restaurada.cantidadDeEventos());
        assertEquals(finInmovilizacion, restaurada.eventoEn(0).evento());
        assertEquals(8, restaurada.eventoEn(0).pasoDeVencimiento());

        restaurada.avanzarPaso();
        restaurada.avanzarPaso();
        assertTrue(restaurada.hayEventoVencido());
        assertEquals(finZapatos, restaurada.sacarProximo().evento());
    }

    @Test
    void conservaUnEventoYaVencidoAlCargar() {
        Agenda original = new Agenda();
        Evento evento = new FinEfectoHeroe(EfectoHeroe.INMOVILIZADO);
        original.programar(1, evento);
        original.avanzarPaso();
        original.avanzarPaso();

        Agenda restaurada = convertirPorJson(original);

        assertEquals(2, restaurada.getPasoActual());
        assertTrue(restaurada.hayEventoVencido());
        assertEquals(evento, restaurada.sacarProximo().evento());
    }
    @Test
    void conservaTodosLosTiposYElOrdenConIgualVencimiento() {
        Agenda original = new Agenda();
        Evento[] eventos = {
                new FinEfectoHeroe(EfectoHeroe.ESCUDO),
                new FinSueno(1, 2),
                new FinPuente(2, 1),
                new FinLava(2, 3),
                new DespertarDormilones(3, 1)
        };

        for (Evento evento : eventos) {
            original.programar(1, evento);
        }

        Agenda restaurada = convertirPorJson(original);
        restaurada.avanzarPaso();

        for (Evento esperado : eventos) {
            assertEquals(esperado, restaurada.sacarProximo().evento());
        }
        assertFalse(restaurada.hayEventoVencido());
    }

    @Test
    void rechazaAgendaSinPasoActual() {
        AgendaGuardada guardada = new Gson().fromJson(
                "{\"eventos\":[]}", AgendaGuardada.class);

        assertThrows(IllegalArgumentException.class,
                () -> ConversorAgenda.aAgenda(guardada));
    }

    @Test
    void rechazaEventoSinVencimiento() {
        String json = """
            {"pasoActual": 0, "eventos": [
                {"tipo": "fin_puente", "datos": {"tablero": 1, "piso": 1}}
            ]}
            """;
        AgendaGuardada guardada = new Gson().fromJson(json, AgendaGuardada.class);

        assertThrows(IllegalArgumentException.class,
                () -> ConversorAgenda.aAgenda(guardada));
    }

    @Test
    void rechazaTipoDeEventoDesconocido() {
        String json = """
            {"pasoActual": 0, "eventos": [
                {"pasoDeVencimiento": 1, "tipo": "desconocido", "datos": {}}
            ]}
            """;
        AgendaGuardada guardada = new Gson().fromJson(json, AgendaGuardada.class);

        assertThrows(IllegalArgumentException.class,
                () -> ConversorAgenda.aAgenda(guardada));
    }

}
