package persistencia;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;

import modelo.Dificultad;
import modelo.EntradaDeRanking;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;


public class PersistenciaRankingTest {
    /**Si el archivo no existe, el lector tira un ranking vacio*/
    @Test
    void sinArchivoDevuelveRankingVacio() {
        EntradaDeRanking[] entradas = PersistenciaRanking.cargarRanking("no_existe.json");

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
        EntradaDeRanking[] entradas = PersistenciaRanking.cargarRanking("rankingPrueba.json");
        assertEquals(2, entradas.length);
        assertEquals("Juan", entradas[0].nombre());
        assertEquals(300, entradas[0].puntaje());
    }
    /**Si el json existe pero esta vacio, devuelve un array vacio*/
    @Test 
    void cargaEntradasDeArchivoVacio(){
        EntradaDeRanking[] entradas = PersistenciaRanking.cargarRanking("rankingVacio.json");
        assertEquals(0, entradas.length);
    }
    /**Compara el array guardado con el original*/
    @Test
    void guardarYCargarDaLoMismo(@TempDir Path carpeta) {
        String ruta = carpeta.resolve("rankingJunit.json").toString();
        EntradaDeRanking[] original = {new EntradaDeRanking
            ("Pedro", 800, Dificultad.FACIL, "2026-11-07")
        };

        PersistenciaRanking.guardarRanking(original,ruta);
        EntradaDeRanking[] cargarOriginal = PersistenciaRanking.cargarRanking(ruta);

        assertArrayEquals(original, cargarOriginal);
    }
}

