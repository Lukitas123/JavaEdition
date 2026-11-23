package persistencia;

import modelo.*;

/** Estado de una mazmorra a partir de una MazmorraGuardada */
public record Mazmorra(Dificultad dificultad, EstadoPartida estado, Mundo mundo, Heroe heroe, Agenda agenda, Costo costos){

}