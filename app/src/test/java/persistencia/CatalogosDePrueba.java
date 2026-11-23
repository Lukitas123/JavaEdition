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
import entidades.monstruos.*;
import modelo.Costo;

/** Catalogos chicos para los tests de persistencia */
public class CatalogosDePrueba {

    /** Catálogo con Errante y Fantasma. */
    public static Catalogo<Monstruo> monstruos() {
        Catalogo<Monstruo> catalogo = new Catalogo<>();
        catalogo.registrar("Errante", posicion -> new Errante(posicion, new Random(1)));
        catalogo.registrar("Fantasma", posicion -> new Fantasma(posicion, new Random(1)));
        return catalogo;
    }
    public static Catalogo<Monstruo> monstruos(Costo costos) {
        Catalogo<Monstruo> catalogo = monstruos();
        catalogo.registrar("Cazador", posicion -> new Cazador(posicion, costos));
        catalogo.registrar("Dormilon", posicion -> new Dormilon(posicion, costos));
        return catalogo;
    }


    public static Catalogo<Cofre> cofres() {
        return cofres(25);
    }
        /** Catalogo con todos los tipos de cofre disponibles */
    public static Catalogo<Cofre> cofres(int danioTrampa) {
        Catalogo<Cofre> catalogo = new Catalogo<>();
        catalogo.registrar("CofreDeBrujula", CofreDeBrujula::new);
        catalogo.registrar("CofreDeDaga", CofreDeDaga::new);
        catalogo.registrar("CofreDeEscudo", CofreDeEscudo::new);
        catalogo.registrar("CofreDePocion", CofreDePocion::new);
        catalogo.registrar("CofreDePuntos", CofreDePuntos::new);
        catalogo.registrar("CofreDePuente", CofreDePuente::new);
        catalogo.registrar("CofreDeSueno", CofreDeSueno::new);
        catalogo.registrar("CofreZapatosDeAgua", CofreZapatosDeAgua::new);
        catalogo.registrar("TrampaDeEnergia",posicion -> new TrampaDeEnergia(posicion, danioTrampa));        catalogo.registrar("TrampaDeLava", TrampaDeLava::new);
        return catalogo;
    }
}
