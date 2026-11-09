package modelo;

import static org.junit.jupiter.api.Assertions.*;

import estructuras.ArbolAVL;
import estructuras.Lista;
import org.junit.jupiter.api.Test;

public class EntradaDeRankingTest {
    /**AVL devuelve las entradas de mayor a menor puntaje*/
    @Test
    void elArbolOrdenaDeMayorAMenorPuntaje() {
        ArbolAVL<EntradaDeRanking> arbol = new ArbolAVL<>();
        arbol.insertar(new EntradaDeRanking("Ana", 300, Dificultad.MEDIO, "2026-11-08"));
        arbol.insertar(new EntradaDeRanking("Beto", 800, Dificultad.MEDIO, "2026-11-08"));
        arbol.insertar(new EntradaDeRanking("Caro", 500, Dificultad.MEDIO, "2026-11-08"));

        Lista<EntradaDeRanking> ordenadas = arbol.inorden();

        assertEquals("Beto", ordenadas.obtener(0).nombre());
        assertEquals("Caro", ordenadas.obtener(1).nombre());
        assertEquals("Ana", ordenadas.obtener(2).nombre());
    }
    /**AVL devuelve entradas ordenadas alfabeticamente si comparten mismo puntaje (sin saltear una o la otra)*/
    @Test
    void dosEntradasConElMismoPuntajeNoSePisan() {
        ArbolAVL<EntradaDeRanking> arbol = new ArbolAVL<>();
        arbol.insertar(new EntradaDeRanking("Ana", 300, Dificultad.FACIL, "2026-11-08"));
        arbol.insertar(new EntradaDeRanking("Beto", 300, Dificultad.FACIL, "2026-11-08"));

        assertEquals(2, arbol.cantidad());
}
}
