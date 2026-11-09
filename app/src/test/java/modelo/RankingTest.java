package modelo;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import estructuras.ArbolAVL;
import estructuras.Lista;
import modelo.Ranking;

public class RankingTest {
    /**Devuelve todas las entradas disponibles si no hay mas de 10 */
    @Test 
    void rankingDe3Entradas() {
        Ranking ranking=new Ranking();
        ranking.agregar(new EntradaDeRanking("Ana", 300, Dificultad.MEDIO, "2026-11-08"));
        ranking.agregar(new EntradaDeRanking("Beto", 800, Dificultad.MEDIO, "2026-11-08"));
        ranking.agregar(new EntradaDeRanking("Caro", 500, Dificultad.MEDIO, "2026-11-08"));

        Lista<EntradaDeRanking> mejores = ranking.top10();

        assertEquals(3, mejores.cantidad());
    }
    /**Solo devuelve los 10 mejores. Ignora el extra */
    @Test 
    void rankingDeMasDe10Entradas() {
        Ranking ranking=new Ranking();
        ranking.agregar(new EntradaDeRanking("Ana", 300, Dificultad.MEDIO, "2026-11-08"));
        ranking.agregar(new EntradaDeRanking("Beto", 800, Dificultad.MEDIO, "2026-11-08"));
        ranking.agregar(new EntradaDeRanking("Caro", 500, Dificultad.MEDIO, "2026-11-08"));
        ranking.agregar(new EntradaDeRanking("Tomas", 600, Dificultad.FACIL, "2026-11-08"));
        ranking.agregar(new EntradaDeRanking("Carla", 400, Dificultad.DIFICIL, "2026-11-08"));
        ranking.agregar(new EntradaDeRanking("Juan", 500, Dificultad.MEDIO, "2026-11-08"));
        ranking.agregar(new EntradaDeRanking("Felipe", 850, Dificultad.FACIL, "2026-11-08"));
        ranking.agregar(new EntradaDeRanking("Mati", 450, Dificultad.FACIL, "2026-11-08"));
        ranking.agregar(new EntradaDeRanking("Maria", 200, Dificultad.DIFICIL, "2026-11-08"));
        ranking.agregar(new EntradaDeRanking("Pedro", 500, Dificultad.MEDIO, "2026-11-08"));
        ranking.agregar(new EntradaDeRanking("Extra", 200, Dificultad.MEDIO, "2026-11-08"));

        Lista<EntradaDeRanking> mejores = ranking.top10();

        assertEquals(10, mejores.cantidad());
    }
    
}
