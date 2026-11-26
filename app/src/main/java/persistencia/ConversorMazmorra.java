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

import modelo.Mazmorra;

/**
 *Convierte una Mazmorra a una MazmorraGuardada y viceversa
*/
public class ConversorMazmorra {
    
    /**Copia una mazmorra/partida */
    public static  MazmorraGuardada aGuardada(Mazmorra mazmorra){

            Dificultad dificultad = mazmorra.getDificultad();
            EstadoPartida estado = mazmorra.getEstado();
            Mundo mundo = mazmorra.getMundo();
            Heroe heroe = mazmorra.getHeroe();
            Agenda agenda = mazmorra.getAgenda();
            Costo costos = mazmorra.getCosto();

            MundoGuardado mundoGuardado = ConversorMundo.aGuardado(mundo);
            AgendaGuardada agendaGuardada = ConversorAgenda.aGuardada(agenda);
            HeroeGuardado heroeGuardado = ConversorHeroe.aGuardado(heroe);
            
            return new MazmorraGuardada(dificultad, estado, mundoGuardado, heroeGuardado, agendaGuardada, costos);
    }

    /** Crea una Mazmorra a partir de una MazmorraGuardada */
    public static Mazmorra aMazmorra(MazmorraGuardada guardado, Catalogo<Monstruo> monstruos, Catalogo<Cofre> cofres) {
        if (guardado == null || guardado.costos() == null) {
            throw new IllegalArgumentException("La partida guardada no tiene costos");
        }
        Mundo mundo = ConversorMundo.aMundo(guardado.mundo(), monstruos, cofres);
        Heroe heroe = ConversorHeroe.aHeroe(guardado.heroe());
        Agenda agenda = ConversorAgenda.aAgenda(guardado.agenda());

        return new Mazmorra(guardado.dificultad(), mundo, heroe, agenda, guardado.costos(), guardado.estado());
    }
}
