package vista;

import modelo.Plano;
import modelo.Heroe;

/*Declara la informacion necesaria para formar el bitmap: plano, heroe, y el numero de Paso */
public interface Vista{
    //metodo principal. Muestra plano, donde esta el heroe y el resto de las entidades
    void mostrarPartida(Plano plano, Heroe heroe, int numeroPaso);
    //Devuelve la ultima tecla del jugador
    char leerTecla();
}