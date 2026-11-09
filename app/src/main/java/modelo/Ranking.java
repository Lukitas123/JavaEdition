package modelo;

import estructuras.ArbolAVL;
import estructuras.Lista;

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
    public Lista<EntradaDeRanking> top10() {
        Lista<EntradaDeRanking> entradasTotales = entradas.inorden();
        Lista<EntradaDeRanking> mejores = new Lista<>();

        if (entradasTotales.cantidad()<CANTIDAD_MEJORES){
            return entradasTotales;
        }
        for (int i=0;i<CANTIDAD_MEJORES;i++){
            mejores.agregar(entradasTotales.obtener(i));
        }
        return mejores;
    }
}