package modelo;

import java.util.LinkedList;
import java.util.List;

/**
 * Registro cronológico de eventos y acciones ocurridas durante una partida.
 * Implementado con {@link LinkedList} según el régimen de estructuras V2.
 */
public class LogPartida {
    private final LinkedList<String> entradas = new LinkedList<>();

    /**
     * Registra una entrada asociada a un paso de la partida.
     *
     * @param paso    número de paso actual (mayor o igual a 0).
     * @param mensaje descripción del suceso (no nula ni vacía).
     */
    public void registrar(int paso, String mensaje) {
        if (paso < 0) {
            throw new IllegalArgumentException("El paso no puede ser negativo.");
        }
        if (mensaje == null || mensaje.isBlank()) {
            throw new IllegalArgumentException("El mensaje del log no puede ser nulo ni vacio.");
        }
        entradas.addLast("[Paso " + paso + "] " + mensaje);
    }

    public int cantidad() {
        return entradas.size();
    }

    public boolean estaVacio() {
        return entradas.isEmpty();
    }

    /** @return copia inmutable de todas las entradas en orden cronológico. */
    public List<String> getEntradas() {
        return List.copyOf(entradas);
    }

    /**
     * Devuelve las últimas N entradas del log en orden cronológico.
     *
     * @param cantidad cantidad máxima de entradas a devolver (>= 0).
     */
    public List<String> ultimas(int cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa.");
        }
        if (cantidad == 0 || entradas.isEmpty()) {
            return List.of();
        }
        int desde = Math.max(0, entradas.size() - cantidad);
        return List.copyOf(entradas.subList(desde, entradas.size()));
    }
}
