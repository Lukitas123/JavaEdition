package entidades.cofres;

import entidades.objetos.PocionDeEnergia;
import entidades.objetos.ZapatosDeAgua;
import modelo.Posicion;

public class CofreZapatosDeAgua extends CofreDeObjeto{
    public CofreZapatosDeAgua(Posicion posicion){
        super(posicion, ZapatosDeAgua.ID_OBJETO);
    }
}
