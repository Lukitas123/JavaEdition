package persistencia;

import modelo.Ubicacion;
import java.util.Map;

/**Datos del heroe (simplificado)*/
public record HeroeGuardado(int energia, Ubicacion ubicacion, int cofresAbiertos, Map<String, Integer> inventario, String[] efectos){
    
}