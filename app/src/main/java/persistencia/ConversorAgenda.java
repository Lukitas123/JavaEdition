package persistencia;

import modelo.Agenda;
import modelo.EventoProgramado;

/**Convierte una Agenda en AgendaGuardada y viceversa */
public class ConversorAgenda {

    /**Copia el paso actual y los eventos pendientes de una Agenda */
    public static AgendaGuardada aGuardada(Agenda agenda) {
        EventoProgramado[] eventos = new EventoProgramado[agenda.cantidadDeEventos()];
        for (int i = 0; i < eventos.length; i++) {
            eventos[i] = agenda.eventoEn(i);
        }
        return new AgendaGuardada(agenda.getPasoActual(), eventos);
    }

    /**Reconstruye una Agenda con el mismo paso y los mismos eventos */
    public static Agenda aAgenda(AgendaGuardada guardada) {
        Agenda agenda = new Agenda();
        for (int i = 0; i < guardada.pasoActual(); i++) {
            agenda.avanzarPaso();
        }
        for (int i = 0; i < guardada.eventos().length; i++) {
            EventoProgramado evento = guardada.eventos()[i];
            int pasosDesdeAhora = Math.max(1, evento.pasoDeVencimiento() - agenda.getPasoActual());
            agenda.programar(pasosDesdeAhora, evento.tipo(), evento.dato());
        }
        return agenda;
    }
}