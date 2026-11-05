package modelo;

import estructuras.Lista;

/**
 * Lista de cosas que pasan en pasos futuros (vencimiento de zapatos de agua,
 * despertar de un dormilón, fin de un congelamiento, etc.).
 *
 * Uso típico desde Mazmorra, una vez por paso del héroe:
 *   agenda.avanzarPaso();
 *   while (agenda.hayEventoVencido()) {
 *       ejecutar(agenda.sacarProximo());
 *   }
 *
 * Implementación actual: lista sin ordenar, con búsqueda del mínimo al sacar (O(n)).
 * Está pensada para reemplazarse por un heap sin cambiar la interfaz pública.
 */
public class Agenda {
    private final Lista<EventoProgramado> eventos = new Lista<>();
    private int pasoActual = 0;

    public int getPasoActual() {
        return pasoActual;
    }

    public int cantidadDeEventos() {
        return eventos.cantidad();
    }

    /**
     * Programa un evento para dentro de N pasos.
     *
     * @param pasosDesdeAhora debe ser al menos 1.
     * @param tipo            qué clase de evento es (no vacío).
     * @param dato            información extra del evento, puede ser null.
     */
    public void programar(int pasosDesdeAhora, String tipo, String dato) {
        if (pasosDesdeAhora < 1) {
            throw new IllegalArgumentException("Los pasos desde ahora deben ser al menos 1.");
        }
        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException("El tipo de evento no puede ser nulo ni vacio.");
        }
        eventos.agregar(new EventoProgramado(pasoActual + pasosDesdeAhora, tipo, dato));
    }

    /** Pasa al paso siguiente. No ejecuta nada: eso lo hace quien saca los eventos. */
    public void avanzarPaso() {
        pasoActual++;
    }

    /** @return true si hay algún evento cuyo paso de vencimiento ya llegó. */
    public boolean hayEventoVencido() {
        return indiceDelProximoVencido() != -1;
    }

    /**
     * Saca y devuelve el evento vencido más antiguo. Si dos vencen en el mismo paso,
     * sale primero el que se programó antes.
     *
     * @throws IllegalStateException si no hay ningún evento vencido.
     */
    public EventoProgramado sacarProximo() {
        int indice = indiceDelProximoVencido();
        if (indice == -1) {
            throw new IllegalStateException("No hay eventos vencidos.");
        }
        return eventos.eliminar(indice);
    }

    private int indiceDelProximoVencido() {
        int mejor = -1;
        for (int i = 0; i < eventos.cantidad(); i++) {
            EventoProgramado evento = eventos.obtener(i);
            if (evento.pasoDeVencimiento() <= pasoActual
                    && (mejor == -1
                    || evento.pasoDeVencimiento() < eventos.obtener(mejor).pasoDeVencimiento())) {
                mejor = i;
            }
        }
        return mejor;
    }
}