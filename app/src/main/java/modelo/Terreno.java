package modelo;

/**
 * tipos de terreno de una celda, cofres, monstruos, entrada y salida NO son
 * terreno: son entidades o posiciones que viven sobre un piso
 */
public enum Terreno {
    PARED('#'),
    PISO('.'),
    AGUA('~'),
    LAVA('^');

    private final char simbolo;

    Terreno(char simbolo) {
        this.simbolo = simbolo;
    }

    public char getSimbolo() {
        return simbolo;
    }

    /**
     * convierte un símbolo del JSON del generador en terreno
     * C, M, E y S se leen como PISO
     *
     * @throws IllegalArgumentException si el símbolo no es uno de los ocho válidos
     */
    public static Terreno desdeSimbolo(char simbolo) {
        return switch (simbolo) {
            case '#' -> PARED;
            case '.', 'C', 'M', 'E', 'S' -> PISO;
            case '~' -> AGUA;
            case '^' -> LAVA;
            default -> throw new IllegalArgumentException("Simbolo invalido: " + simbolo);
        };
    }
}
