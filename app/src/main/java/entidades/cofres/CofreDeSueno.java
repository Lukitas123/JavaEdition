package entidades.cofres;

import entidades.objetos.Sueno;
import modelo.Posicion;

public class CofreDeSueno extends CofreDeObjeto{

    public CofreDeSueno(Posicion posicion){
        super(posicion, Sueno.ID_OBJETO);
    }
}
