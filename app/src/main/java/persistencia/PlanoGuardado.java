package persistencia;

import modelo.Posicion;
import modelo.Terreno;

/**Datos de un plano (simplificado) */
public record PlanoGuardado(Terreno[][] terreno, boolean[][] visitadas, Posicion entrada, Posicion salida, 
    MonstruoGuardado[] monstruos, CofreGuardado[] cofres, Integer finSuenoMonstruos){
        
}

