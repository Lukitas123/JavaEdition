package entidades;

import entidades.cofres.*;
import entidades.monstruos.*;
import modelo.Costo;

import java.util.Random;

public class CatalogosEntidades {
    private CatalogosEntidades() {
    }

    public static Catalogo<Cofre> cofres(int danioTrampa) {
        if (danioTrampa <= 0) {
            throw new IllegalArgumentException("El daño debe ser positivo");
        }

        Catalogo<Cofre> catalogo = new Catalogo<>();
        catalogo.registrar("CofreDePuntos", CofreDePuntos::new);
        catalogo.registrar("CofreDePocion", CofreDePocion::new);
        catalogo.registrar("TrampaDeEnergia",
                posicion -> new TrampaDeEnergia(posicion, danioTrampa));
        catalogo.registrar("CofreZapatosDeAgua", CofreZapatosDeAgua::new);
        catalogo.registrar("CofreDeDaga", CofreDeDaga::new);
        catalogo.registrar("CofreDeEscudo", CofreDeEscudo::new);
        catalogo.registrar("CofreDeBrujula", CofreDeBrujula::new);
        catalogo.registrar("CofreDeSueno", CofreDeSueno::new);
        catalogo.registrar("CofreDePuente", CofreDePuente::new);
        catalogo.registrar("TrampaDeLava", TrampaDeLava::new);
        return catalogo;
    }

    public static Catalogo<Monstruo> monstruos(
            Costo costos, int duracionCongelador, Random azar) {
        if (costos == null || costos.piso() < 0
                || costos.agua() < 0 || costos.lava() < 0
                || duracionCongelador <= 0 || azar == null) {
            throw new IllegalArgumentException("Parametros del catalogo invalidos");
        }

        Catalogo<Monstruo> catalogo = new Catalogo<>();
        catalogo.registrar("Errante",
                posicion -> new Errante(posicion, new Random(azar.nextLong())));
        catalogo.registrar("Cazador",
                posicion -> new Cazador(posicion, costos));
        catalogo.registrar("Fantasma",
                posicion -> new Fantasma(posicion, new Random(azar.nextLong())));
        catalogo.registrar("Congelador",
                posicion -> new Congelador(
                        posicion, new Random(azar.nextLong()), duracionCongelador));
        catalogo.registrar("Dormilon",
                posicion -> new Dormilon(posicion, costos));
        return catalogo;
    }
}

