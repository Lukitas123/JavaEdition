package entidades.cofres;

import entidades.objetos.Brujula;
import modelo.Posicion;

public class CofreDeBrujula extends CofreDeObjeto{
    public CofreDeBrujula(Posicion posicion){
        super(posicion, Brujula.ID_OBJETO);
    }
}
