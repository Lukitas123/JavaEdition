package entidades.objetos;

import modelo.Agenda;
import modelo.Heroe;
import modelo.Plano;


public interface Objeto {
    ResultadoUso usar(Heroe heroe, Plano plano, Agenda agenda);
}
