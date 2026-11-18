package persistencia;

import modelo.Dificultad;
import modelo.EstadoPartida;

/**Todo el estado de una partida*/
public record PartidaGuardada(Dificultad dificultad, EstadoPartida estado, MundoGuardado mundo, 
    HeroeGuardado heroe, AgendaGuardada agenda){
        
}
