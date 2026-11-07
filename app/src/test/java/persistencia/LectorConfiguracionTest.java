package persistencia;

import static org.junit.jupiter.api.Assertions.*;
import modelo.Configuracion;
import org.junit.jupiter.api.Test;


/**Pruebas de la lectura del archivo de configuracion*/
public class LectorConfiguracionTest {
    /**Si el archivo no existe, el lector tira IllegalArgumentException*/
    @Test 
    void leeSinEncontrarArchivo(){
        String ruta= "no_existe.json";
        assertThrows(IllegalArgumentException.class, () -> {
            LectorConfiguracion.leerConfiguracion(ruta);
        });
    }
    /**Si el archivo existe, el lector tira el contenido del archivo que contiene a energiaInicial*/
    @Test 
    void leeArchivo(){
        String config=LectorConfiguracion.leerConfiguracion("configuracion.json");
        assertTrue(config.contains("energiaInicial"));
    }
    /**Chequeo que la informacion del json este en el objeto creado*/
    @Test
    void puedePasarAObjeto(){
        Configuracion c = LectorConfiguracion.cargarConfiguracion("configuracion.json");
        assertEquals(100,c.energiaInicial());
        assertNull(c.semilla());
        assertEquals(6, c.dificultades().MEDIO().cofres());
    }
}
