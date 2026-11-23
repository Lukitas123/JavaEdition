package modelo;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import estructuras.Lista;

public class RankingTest {
    /**Devuelve todas las entradas disponibles si no hay mas de 10 en orden*/
    @Test 
    void rankingDe3Entradas() {
        Ranking ranking=new Ranking();
        ranking.agregar(new EntradaDeRanking("Ana", 300, Dificultad.MEDIO, "2026-11-08"));
        ranking.agregar(new EntradaDeRanking("Beto", 800, Dificultad.MEDIO, "2026-11-08"));
        ranking.agregar(new EntradaDeRanking("Caro", 500, Dificultad.MEDIO, "2026-11-08"));

        Lista<EntradaDeRanking> mejores = ranking.top10();

        assertEquals("Beto", mejores.obtener(0).nombre());
        assertEquals(3, mejores.cantidad());
    }
    /**Solo devuelve los 10 mejores*/
    @Test 
    void rankingDeMasDe10Entradas() {
        Ranking ranking=new Ranking();
        ranking.agregar(new EntradaDeRanking("Ana", 900, Dificultad.MEDIO, "2026-11-08"));
        ranking.agregar(new EntradaDeRanking("Beto", 800, Dificultad.MEDIO, "2026-11-08"));
        ranking.agregar(new EntradaDeRanking("Caro", 600, Dificultad.MEDIO, "2026-11-08"));
        ranking.agregar(new EntradaDeRanking("Tomas", 500, Dificultad.FACIL, "2026-11-08"));
        ranking.agregar(new EntradaDeRanking("Carla", 450, Dificultad.DIFICIL, "2026-11-08"));
        ranking.agregar(new EntradaDeRanking("Juan", 400, Dificultad.MEDIO, "2026-11-08"));
        ranking.agregar(new EntradaDeRanking("Felipe", 400, Dificultad.FACIL, "2026-11-08"));
        ranking.agregar(new EntradaDeRanking("Mati", 300, Dificultad.FACIL, "2026-11-08"));
        ranking.agregar(new EntradaDeRanking("Maria", 300, Dificultad.DIFICIL, "2026-11-08"));
        ranking.agregar(new EntradaDeRanking("Pedro", 200, Dificultad.MEDIO, "2026-11-08"));
        ranking.agregar(new EntradaDeRanking("Ignacio", 100, Dificultad.DIFICIL, "2026-11-08"));

        Lista<EntradaDeRanking> mejores = ranking.top10();

        assertEquals(10, mejores.cantidad());
        assertEquals("Ana", mejores.obtener(0).nombre());
        for (int i=0;i<10;i++){
            assertNotEquals("Ignacio",mejores.obtener(i).nombre());
        }
    }

    /**Metodo para devolver todas las entradas */
    @Test
    void todasLasEntradas() {
        Ranking ranking = new Ranking();
        for (int i = 1; i <= 12; i++) {
            ranking.agregar(new EntradaDeRanking("Jugador" + i, i * 100, Dificultad.MEDIO, "2026-11-08"));
        }

        Lista<EntradaDeRanking> todas = ranking.totalEntradas();

        assertEquals(12, todas.cantidad());
        assertEquals("Jugador12", todas.obtener(0).nombre());
}
    
}
