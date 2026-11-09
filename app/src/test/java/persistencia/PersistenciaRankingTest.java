package persistencia;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;

import modelo.Dificultad;
import modelo.EntradaDeRanking;
import modelo.Ranking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import estructuras.Lista;


public class PersistenciaRankingTest {
    /**Si el archivo no existe, el lector tira un ranking vacio*/
    @Test
    void sinArchivoDevuelveRankingVacio() {
        EntradaDeRanking[] entradas=PersistenciaRanking.cargarRanking("no_existe.json");

        assertEquals(0, entradas.length);
    }
    /**Chequea si realmente lee bien el archivo (se fija si al menos dice nombre)*/
    @Test 
    void leeArchivo(){
        String entrada=PersistenciaRanking.leerRanking("rankingPrueba.json");
        assertTrue(entrada.contains("nombre"));
    }
    /**Chequeo que la informacion del json este en la entrada creada*/
    @Test
    void cargaLasEntradasDelArchivo() {
        EntradaDeRanking[] entradas=PersistenciaRanking.cargarRanking("rankingPrueba.json");
        assertEquals(2, entradas.length);
        assertEquals("Juan", entradas[0].nombre());
        assertEquals(300, entradas[0].puntaje());
    }
    /**Si el json existe pero esta vacio, devuelve un array vacio*/
    @Test 
    void cargaEntradasDeArchivoVacio(){
        EntradaDeRanking[] entradas=PersistenciaRanking.cargarRanking("rankingVacio.json");
        assertEquals(0, entradas.length);
    }
    /**Compara el array guardado con el original*/
    @Test
    void guardarYCargarDaLoMismo(@TempDir Path carpeta) {
        String ruta=carpeta.resolve("rankingJunit.json").toString();

        Ranking ranking=new Ranking();

        ranking.agregar(new EntradaDeRanking("Pedro", 800, Dificultad.FACIL, "2026-11-07"));
        ranking.agregar(new EntradaDeRanking("Ana", 300, Dificultad.MEDIO, "2026-11-07"));
        ranking.agregar(new EntradaDeRanking("Caro", 500, Dificultad.MEDIO, "2026-11-07"));

        PersistenciaRanking.guardarRanking(ranking.totalEntradas(), ruta);
        EntradaDeRanking[] cargadas=PersistenciaRanking.cargarRanking(ruta);

        assertEquals(3, cargadas.length);
        assertEquals("Pedro", cargadas[0].nombre());
        assertEquals("Ana", cargadas[2].nombre());
        assertEquals("Caro", cargadas[1].nombre());
    }
    /**Un ranking guardado y la lista de entradas armada a partir de ese archivo generado conserva la estructura original*/
    @Test
    void armarRankingDevuelveLasEntradasGuardadas(@TempDir Path carpeta) {
        String ruta=carpeta.resolve("rankingFinal.json").toString();

        Ranking original=new Ranking();

        original.agregar(new EntradaDeRanking("Pedro", 800, Dificultad.FACIL, "2026-11-08"));
        original.agregar(new EntradaDeRanking("Ana", 300, Dificultad.MEDIO, "2026-11-08"));
        original.agregar(new EntradaDeRanking("Caro", 500, Dificultad.DIFICIL, "2026-11-08"));

        PersistenciaRanking.guardarRanking(original.totalEntradas(), ruta);

        Ranking armado=PersistenciaRanking.armarRanking(ruta);
        Lista<EntradaDeRanking> entradas=armado.totalEntradas();

        assertEquals(3, entradas.cantidad());
        assertEquals("Pedro", entradas.obtener(0).nombre());
        assertEquals("Caro", entradas.obtener(1).nombre());
        assertEquals("Ana", entradas.obtener(2).nombre());
    }
}

