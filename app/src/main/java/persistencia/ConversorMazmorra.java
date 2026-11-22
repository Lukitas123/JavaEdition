package persistencia;

import modelo.*;

/**
 *Convierte una Mazmorra a una MazmorraGuardada y viceversa
*/
public class ConversorMazmorra {
    
    //**Copia una mazmorra/partida */
    public static  MazmorraGuardada aGuardada(Dificultad dificultad, EstadoPartida estado, 
        Mundo mundo, Heroe heroe, Agenda agenda, Costo costos){

            MundoGuardado mundoGuardado = ConversorMundo.aGuardado(mundo);
            AgendaGuardada agendaGuardada = ConversorAgenda.aGuardada(agenda);
            HeroeGuardado heroeGuardado = ConversorHeroe.aGuardado(heroe);
            
            return new MazmorraGuardada(dificultad, estado, mundoGuardado, heroeGuardado, agendaGuardada, costos);
    }
    /**Construye una mazmorra a partir de una MazmorraGuarda*/
    public static Mazmorra aMazmorra(MazmorraGuardada guardado) {
        Costo costos = guardado.costos();
        if (costos == null) {
            throw new IllegalArgumentException("La partida guardada no tiene costos");
        }

        Mundo mundo = ConversorMundo.aMundo(guardado.mundo(), costos);
        Heroe heroe = ConversorHeroe.aHeroe(guardado.heroe());
        Agenda agenda = ConversorAgenda.aAgenda(guardado.agenda());
        Dificultad dificultad = guardado.dificultad();
        EstadoPartida estado = guardado.estado();

        return new Mazmorra(dificultad, estado, mundo, heroe, agenda, costos);
    }
}
