package modelo;

import estructuras.ArbolAVL;

import java.util.List;

/**Guarda las entradas del ranking ordenadas de mayor a menor puntaje */
public class Ranking {
    private static final int CANTIDAD_MEJORES = 10;
    private final ArbolAVL<EntradaDeRanking> entradas;

    /**Crea un ranking vacio*/
    public Ranking() {
        this.entradas = new ArbolAVL<>();
    }

    /**Ingresa una entrada al ranking*/
    public void agregar(EntradaDeRanking entrada) {
        entradas.insertar(entrada);
    }

    /**Mejores 10 entradas*/
    public List<EntradaDeRanking> top10() {
        List<EntradaDeRanking> entradasTotales = entradas.inorden();

        if (entradasTotales.size()<CANTIDAD_MEJORES){
            return entradasTotales;
        }
        return entradasTotales.subList(0, CANTIDAD_MEJORES);
    }
    //**Devuelve TODAS las entradas en orden */
    public List<EntradaDeRanking> totalEntradas(){
        return entradas.inorden();
    }
}