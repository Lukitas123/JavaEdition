package persistencia;

import modelo.Ubicacion;

/**Datos del heroe (simplificado)*/
public record HeroeGuardado(int energia, Ubicacion ubicacion, int cofresAbiertos, String[] inventario, String[] efectos){
    
}