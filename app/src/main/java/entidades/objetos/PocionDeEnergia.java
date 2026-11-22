package entidades.objetos;

import modelo.Agenda;
import modelo.Heroe;
import modelo.Plano;

public class PocionDeEnergia implements Objeto{
    public static final String id="pocion_energia";

    private final int energiaQueRecupera;

    public PocionDeEnergia(int energiaQueRecupera){
        if(energiaQueRecupera<=0){
            throw new IllegalArgumentException("La energia debe ser positiva");
        }
        this.energiaQueRecupera = energiaQueRecupera;
    }


    /**
     * Recupera energia configurada en energiaQueRecupera
     * Pre: heroe, agenda y  plano no null
     * Post: la energia del heroe aumenta la cantidad configurada
     */
    @Override
    public ResultadoUso usar(Heroe heroe, Plano plano, Agenda agenda) {
        if(heroe== null || plano == null || agenda == null){
            throw new IllegalArgumentException("Heroe, plano y agenda son obligatorios");
        }
        heroe.ganarEnergia(energiaQueRecupera);
        return new ResultadoUso(true, null);
    }
}
