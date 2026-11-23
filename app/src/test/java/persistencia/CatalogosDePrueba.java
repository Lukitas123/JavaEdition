package persistencia;

import java.util.Random;

import entidades.Catalogo;
import entidades.cofres.Cofre;
import entidades.cofres.CofreDeBrujula;
import entidades.cofres.CofreDeDaga;
import entidades.cofres.CofreDeEscudo;
import entidades.cofres.CofreDePocion;
import entidades.cofres.CofreDePuntos;
import entidades.cofres.CofreDePuente;
import entidades.cofres.CofreDeSueno;
import entidades.cofres.CofreZapatosDeAgua;
import entidades.cofres.TrampaDeEnergia;
import entidades.cofres.TrampaDeLava;
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

    /** Catalogo con todos los tipos de cofre disponibles */
    public static Catalogo<Cofre> cofres() {
        Catalogo<Cofre> catalogo = new Catalogo<>();
        catalogo.registrar("CofreDeBrujula", CofreDeBrujula::new);
        catalogo.registrar("CofreDeDaga", CofreDeDaga::new);
        catalogo.registrar("CofreDeEscudo", CofreDeEscudo::new);
        catalogo.registrar("CofreDePocion", CofreDePocion::new);
        catalogo.registrar("CofreDePuntos", CofreDePuntos::new);
        catalogo.registrar("CofreDePuente", CofreDePuente::new);
        catalogo.registrar("CofreDeSueno", CofreDeSueno::new);
        catalogo.registrar("CofreZapatosDeAgua", CofreZapatosDeAgua::new);
        catalogo.registrar("TrampaDeEnergia", posicion -> new TrampaDeEnergia(posicion, 25));
        catalogo.registrar("TrampaDeLava", TrampaDeLava::new);
        return catalogo;
    }
}
