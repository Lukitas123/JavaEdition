package persistencia;

import java.util.Random;

import entidades.Catalogo;
import entidades.cofres.Cofre;
import entidades.cofres.CofreDePuntos;
import entidades.cofres.TrampaDeEnergia;
import entidades.monstruos.Errante;
import entidades.monstruos.Fantasma;
import entidades.monstruos.Monstruo;

/** Catalogos chicos para los tests de persistencia */
public class CatalogosDePrueba {

    /** Catálogo con Errante y Fantasma. */
    public static Catalogo<Monstruo> monstruos() {
        Catalogo<Monstruo> catalogo = new Catalogo<>();
        catalogo.registrar("Errante", posicion -> new Errante(posicion, new Random(1)));
        catalogo.registrar("Fantasma", posicion -> new Fantasma(posicion, new Random(1)));
        return catalogo;
    }

    /** Catalogo con CofreDePuntos y TrampaDeEnergia */
    public static Catalogo<Cofre> cofres() {
        Catalogo<Cofre> catalogo = new Catalogo<>();
        catalogo.registrar("CofreDePuntos", CofreDePuntos::new);
        catalogo.registrar("TrampaDeEnergia", posicion -> new TrampaDeEnergia(posicion, 25));
        return catalogo;
    }
}
