package modelo;

/**
 * Puntaje de una partida (regla 9 del enunciado):
 * cofres abiertos × 100 + energía restante × 2 - pasos
 * Es un valor puro: no guarda nada de la partida, solo los tres números
 */
public record Puntaje(int cofresAbiertos, int energiaRestante, int pasos) {
    public static final int PUNTOS_POR_COFRE = 100;
    public static final int PUNTOS_POR_ENERGIA = 2;

    /** @return el puntaje total según la fórmula de la cátedra (puede ser negativo). */
    public int valor() {
        return cofresAbiertos * PUNTOS_POR_COFRE
                + energiaRestante * PUNTOS_POR_ENERGIA
                - pasos;
    }
}
