package persistencia;

import modelo.EventoProgramado;

/**Datos de la agenda (simplificado)*/
public record AgendaGuardada(int pasoActual, EventoProgramado[] eventos){
    
}
