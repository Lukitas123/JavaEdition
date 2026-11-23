package entidades.cofres;

import entidades.objetos.Escudo;
import modelo.Posicion;

public class CofreDeEscudo extends CofreDeObjeto{

    public CofreDeEscudo(Posicion posicion){
        super(posicion, Escudo.ID_OBJETO);
    }
}
