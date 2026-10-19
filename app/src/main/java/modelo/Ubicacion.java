package modelo;

/**
 * lugar exacto en el mundo: en qué tablero y piso está, y en qué celda
 */
public record Ubicacion(int tablero, int piso, Posicion posicion) {
}
