package vista;

import modelo.Plano;
import modelo.Heroe;

public interface Vista{
    void mostrarPartida(Plano plano, Heroe heroe, int numeroPaso);
}