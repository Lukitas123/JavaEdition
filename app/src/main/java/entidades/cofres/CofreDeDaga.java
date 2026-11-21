package entidades.cofres;

import entidades.objetos.Daga;
import modelo.Posicion;

public class CofreDeDaga extends CofreDeObjeto{
    public CofreDeDaga(Posicion posicion){
        super(posicion, Daga.ID_OBJETO);
    }
}
