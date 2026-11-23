package entidades.cofres;

import modelo.Agenda;
import modelo.Heroe;
import modelo.Plano;
import modelo.Posicion;

public class CofreDeObjeto extends Cofre{
    private final String idObjeto;
    protected CofreDeObjeto(Posicion posicion, String idObjeto){
        super(posicion);
        if (idObjeto == null || idObjeto.isBlank()) {
            throw new IllegalArgumentException("El id del objeto es obligatorio");
        }
        this.idObjeto = idObjeto;
    }

    @Override
    protected void aplicarEfecto(Heroe heroe, Plano plano, Agenda agenda) {
        heroe.agregarObjeto(idObjeto);
    }
}
