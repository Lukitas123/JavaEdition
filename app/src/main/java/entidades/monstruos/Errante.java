package entidades.monstruos;

import modelo.Heroe;
import modelo.Posicion;

import java.util.Random;

/**
 * Monstruo que elige al azar una celda vecina transitable.
 *
 * Cuando alcanza al héroe, le quita 30 puntos de energía y vuelve a su
 * posición inicial.
 */
public class Errante extends Monstruo{
    private final Random random;

    public Errante(Posicion posicionInicial, Random random){
        super(posicionInicial);
        if (random == null){
            throw new IllegalArgumentException("El generador aleatorio no puede ser null");
        }
        this.random = random;
    }


    /**
     * Le quita 30 puntos de energía al héroe y devuelve al errante a su posición
     * inicial.
     *
     * Pre: héroe y agenda no son {@code null}; el errante alcanzó al héroe.
     * Post: la energía del héroe disminuye en 30, sin bajar de cero, y la posición del errante
     * pasa a ser su posición inicial.
     * No programa eventos en la agenda.
     *
     * @param heroe héroe alcanzado
     * @param agenda agenda de la partida; el errante no la modifica
     */
    @Override
    public void alcanzarAlHeroe(Heroe heroe, Agenda agenda) {
        heroe.perderEnergia(30);
        volverAlInicio();
    }

    /**
     * Elige al azar una de las celdas vecinas transitables.
     *
     * Pre: plano y posición del héroe no son {@code null}.
     * Post:si existe al menos una celda vecina transitable, devuelve una elegida al azar;
     * si no existe ninguna, devuelve la posición actual. No modifica directamente la posición del
     * monstruo.
     *
     * @param plano plano donde se mueve el errante
     * @param posicionHeroe posición del héroe
     * @return destino elegido, o la posición actual si no puede moverse
     */
    @Override
    public Posicion decidirMovimiento(Plano plano, Posicion posicionHeroe) {
        return null;
    }
}
