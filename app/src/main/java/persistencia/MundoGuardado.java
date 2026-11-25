package persistencia;

import modelo.Conexion;

/** Datos del mundo (simplificado) */
public record MundoGuardado(PlanoGuardado[][] planos, Conexion[] conexiones){
    
}
