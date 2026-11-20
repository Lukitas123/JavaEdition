package persistencia;

import modelo.Agenda;
import modelo.Dificultad;
import modelo.EstadoPartida;
import modelo.Heroe;
import modelo.Mundo;

/** Estado de una mazmorra a partir de una MazmorraGuardada */
public record Mazmorra(Dificultad dificultad, EstadoPartida estado, Mundo mundo, Heroe heroe, Agenda agenda){

}