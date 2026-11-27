package persistencia;

import modelo.Costo;
import modelo.Dificultad;
import modelo.EstadoPartida;
import modelo.ParametrosEntidades;

/**Todo el estado de una partida*/
public record MazmorraGuardada(Dificultad dificultad, EstadoPartida estado, MundoGuardado mundo,
                               HeroeGuardado heroe, AgendaGuardada agenda, Costo costos, ParametrosEntidades parametrosEntidades){
        
}
