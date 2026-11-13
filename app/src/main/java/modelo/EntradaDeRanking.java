package modelo;
/**
 * La informacion general de cada partida individual para implementar al ranking
 */
public record EntradaDeRanking(String nombre,int puntaje,Dificultad dificultad,String fecha) implements Comparable<EntradaDeRanking>{
    /** Compara dos entradas para ordenarlas en el ranking. */
    @Override
    public int compareTo(EntradaDeRanking entrada2) {
        int res = Integer.compare( entrada2.puntaje(),this.puntaje());
        if (res!=0){
            return res;
        }
    return this.nombre().compareTo(entrada2.nombre());
    }
    
}
