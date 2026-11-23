package modelo;
/**
 * La informacion general de cada partida individual para implementar al ranking
 */
public record EntradaDeRanking(String nombre,int puntaje,Dificultad dificultad,String fecha) implements Comparable<EntradaDeRanking>{
    /** Compara dos entradas para ordenarlas en el ranking. */
    @Override
    public int compareTo(EntradaDeRanking entrada2) {
        int resultado = Integer.compare(entrada2.puntaje(), this.puntaje());
        if (resultado == 0) {
            resultado = this.nombre().compareTo(entrada2.nombre());
        }
        if (resultado == 0) {
            resultado = this.fecha().compareTo(entrada2.fecha());
        }
        if (resultado == 0) {
            resultado = this.dificultad().compareTo(entrada2.dificultad());
        }
        return resultado;
    }
    
}
