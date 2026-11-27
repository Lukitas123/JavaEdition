package modelo;

import modelo.eventos.Evento;

import java.util.Arrays;
import java.util.PriorityQueue;

/**
 * Cola de prioridad de eventos que ocurren en pasos futuros (vencimiento de zapatos
 * de agua, despertar de un dormilón, fin de un congelamiento, etc.).
 *
 * Uso típico desde Mazmorra, una vez por paso del héroe:
 *   agenda.avanzarPaso();
 *   while (agenda.hayEventoVencido()) {
 *       ejecutar(agenda.sacarProximo());
 *   }
 *
 * Implementada con {@link PriorityQueue} ordenando por paso de vencimiento en O(log n)
 * y desempatando por orden de inserción (FIFO).
 */
public class Agenda {

    private record EntradaAgenda(EventoProgramado evento, long secuencia)
            implements Comparable<EntradaAgenda> {
        @Override
        public int compareTo(EntradaAgenda otra) {
            int porPaso = Integer.compare(
                    this.evento.pasoDeVencimiento(),
                    otra.evento.pasoDeVencimiento());
            if (porPaso != 0) {
                return porPaso;
            }
            return Long.compare(this.secuencia, otra.secuencia);
        }
    }

    private final PriorityQueue<EntradaAgenda> eventos = new PriorityQueue<>();
    private long siguienteSecuencia = 0;
    private int pasoActual = 0;

    public int getPasoActual() {
        return pasoActual;
    }

    public int cantidadDeEventos() {
        return eventos.size();
    }

    /**
     * Programa un evento para dentro de N pasos.
     *
     * @param pasosDesdeAhora debe ser al menos 1.
     * @param evento          evento a ejecutar al vencer.
     */
    public void programar(int pasosDesdeAhora, Evento evento) {
        if (pasosDesdeAhora < 1) {
            throw new IllegalArgumentException("Los pasos desde ahora deben ser al menos 1.");
        }
        if (evento == null) {
            throw new IllegalArgumentException("El evento no puede ser nulo.");
        }

        eventos.add(new EntradaAgenda(
                new EventoProgramado(pasoActual + pasosDesdeAhora, evento),
                siguienteSecuencia++));
    }

    /** Pasa al paso siguiente. No ejecuta nada: eso lo hace quien saca los eventos. */
    public void avanzarPaso() {
        pasoActual++;
    }

    /** @return true si hay algún evento cuyo paso de vencimiento ya llegó. */
    public boolean hayEventoVencido() {
        return !eventos.isEmpty()
                && eventos.peek().evento().pasoDeVencimiento() <= pasoActual;
    }

    /**
     * Saca y devuelve el evento vencido más antiguo. Si dos vencen en el mismo paso,
     * sale primero el que se programó antes.
     *
     * @throws IllegalStateException si no hay ningún evento vencido.
     */
    public EventoProgramado sacarProximo() {
        if (!hayEventoVencido()) {
            throw new IllegalStateException("No hay eventos vencidos.");
        }
        return eventos.poll().evento();
    }

    /**
     * Restaura un evento conservando su paso de vencimiento original.
     * Puede estar vencido al momento de cargar la partida.
     */
    public void restaurarEvento(EventoProgramado evento) {
        if (evento == null || evento.evento() == null
                || evento.pasoDeVencimiento() < 0) {
            throw new IllegalArgumentException("Evento guardado invalido");
        }

        eventos.add(new EntradaAgenda(evento, siguienteSecuencia++));
    }

    /**
     * Devuelve los eventos pendientes ordenados por paso de vencimiento
     * (y por orden de programación en caso de empate). Utilizado en ConversorAgenda.
     */
    public EventoProgramado[] exportarEventos() {
        EntradaAgenda[] copia = eventos.toArray(new EntradaAgenda[0]);
        Arrays.sort(copia);
        EventoProgramado[] resultado = new EventoProgramado[copia.length];
        for (int i = 0; i < copia.length; i++) {
            resultado[i] = copia[i].evento();
        }
        return resultado;
    }
}
