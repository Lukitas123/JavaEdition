package persistencia;

import com.google.gson.Gson;
import modelo.Agenda;
import modelo.EventoProgramado;
import modelo.eventos.*;

import java.util.Map;

/**Convierte una Agenda en AgendaGuardada y viceversa */
public class ConversorAgenda {
    private static final Gson GSON = new Gson();

    private static final Map<String, Class<? extends Evento>> TIPOS = Map.of(
            "fin_efecto_heroe", FinEfectoHeroe.class,
            "fin_sueno", FinSueno.class,
            "fin_puente", FinPuente.class,
            "fin_lava", FinLava.class,
            "despertar_dormilones", DespertarDormilones.class
    );

    /**Copia el paso actual y los eventos pendientes de una Agenda */
    public static AgendaGuardada aGuardada(Agenda agenda) {
        EventoProgramado[] programados = agenda.exportarEventos();
        EventoGuardado[] eventos = new EventoGuardado[programados.length];

        for (int i = 0; i < programados.length; i++) {
            EventoProgramado programado = programados[i];
            Evento evento = programado.evento();

            eventos[i] = new EventoGuardado(
                    programado.pasoDeVencimiento(),
                    tipoDe(evento),
                    GSON.toJsonTree(evento));
        }

        return new AgendaGuardada(agenda.getPasoActual(), eventos);
    }

    /**Reconstruye una Agenda con el mismo paso y los mismos eventos */
    public static Agenda aAgenda(AgendaGuardada guardada) {
        if (guardada == null ||guardada.pasoActual() == null||  guardada.pasoActual() < 0
                || guardada.eventos() == null) {
            throw new IllegalArgumentException("Agenda guardada invalida");
        }
        Agenda agenda = new Agenda();

        for (int i = 0; i < guardada.pasoActual(); i++) {
            agenda.avanzarPaso();
        }

        for (EventoGuardado guardado : guardada.eventos()) {
            if (guardado == null || guardado.pasoDeVencimiento() == null
                    || guardado.pasoDeVencimiento() < 0
                    || guardado.tipo() == null || guardado.datos() == null
                    || !guardado.datos().isJsonObject()) {
                throw new IllegalArgumentException("Evento guardado invalido");
            }
            Class<? extends Evento> clase = TIPOS.get(guardado.tipo());
            if (clase == null) {
                throw new IllegalArgumentException(
                        "Tipo de evento desconocido: " + guardado.tipo());
            }

            Evento evento = GSON.fromJson(guardado.datos(), clase);
            agenda.restaurarEvento(
                    new EventoProgramado(guardado.pasoDeVencimiento(), evento));
        }

        return agenda;
    }

    private static String tipoDe(Evento evento) {
        for (Map.Entry<String, Class<? extends Evento>> entrada : TIPOS.entrySet()) {
            if (entrada.getValue().equals(evento.getClass())) {
                return entrada.getKey();
            }
        }
        throw new IllegalArgumentException(
                "Evento no registrado: " + evento.getClass().getName());
    }
}
