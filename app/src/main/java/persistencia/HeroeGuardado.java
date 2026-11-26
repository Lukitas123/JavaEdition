package persistencia;

import modelo.EfectoHeroe;
import modelo.Ubicacion;
import java.util.Map;

/**Datos del heroe (simplificado)*/
public record HeroeGuardado(
        int energia,
        Ubicacion ubicacion,
        int cofresAbiertos,
        Map<String, Integer> inventario,
        Map<EfectoHeroe, Integer> efectos){
    
}