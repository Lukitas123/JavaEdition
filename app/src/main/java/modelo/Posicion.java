package modelo;

/**
 * una celda de un plano
 * fila y columna van de 1 a n (NO de 0 a n-1).
 */
public record Posicion(int fila, int columna) {

    /** posición vecina en la dirección indicada */
    public Posicion mover(Direccion direccion) {
        return new Posicion(fila + direccion.getDeltaFila(),
                columna + direccion.getDeltaColumna());
    }

    public int distanciaManhattan(Posicion otra) {
        return Math.abs(fila - otra.fila) + Math.abs(columna - otra.columna);
    }
}
