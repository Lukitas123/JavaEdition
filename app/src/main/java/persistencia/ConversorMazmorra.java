package persistencia;

import entidades.Catalogo;
import entidades.cofres.Cofre;
import entidades.monstruos.Monstruo;
import modelo.Agenda;
import modelo.Costo;
import modelo.Dificultad;
import modelo.EstadoPartida;
import modelo.Heroe;
import modelo.Mundo;

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


    public static Mazmorra aMazmorra(MazmorraGuardada guardado, Catalogo<Monstruo> monstruos, Catalogo<Cofre> cofres){
        if (guardado == null || guardado.costos() == null) {
            throw new IllegalArgumentException("La partida guardada no tiene costos");
        }
        return reconstruir(
                guardado, ConversorMundo.aMundo(guardado.mundo(), monstruos, cofres));
    }

    private static Mazmorra reconstruir(MazmorraGuardada guardado, Mundo mundo) {
        Heroe heroe = ConversorHeroe.aHeroe(guardado.heroe());
        Agenda agenda = ConversorAgenda.aAgenda(guardado.agenda());
        Dificultad dificultad = guardado.dificultad();
        EstadoPartida estado = guardado.estado();

        return new Mazmorra(
                dificultad, estado, mundo, heroe, agenda, guardado.costos());
    }
}
