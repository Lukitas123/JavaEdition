package entidades;

import entidades.cofres.*;
import entidades.monstruos.*;
import modelo.Costo;
import modelo.ParametrosEntidades;

import java.util.Random;

public class CatalogosEntidades {
    private CatalogosEntidades() {
    }

    public static Catalogo<Cofre> cofres(ParametrosEntidades parametros) {
        if (parametros == null) {
            throw new IllegalArgumentException(
                    "Faltan los parametros de entidades");
        }

        Catalogo<Cofre> catalogo = new Catalogo<>();
        catalogo.registrar("CofreDePuntos", CofreDePuntos::new);
        catalogo.registrar("CofreDePocion", CofreDePocion::new);
        catalogo.registrar("TrampaDeEnergia", posicion -> new TrampaDeEnergia(
                        posicion, parametros.danioTrampaEnergia()));
        catalogo.registrar("CofreZapatosDeAgua", CofreZapatosDeAgua::new);
        catalogo.registrar("CofreDeDaga", CofreDeDaga::new);
        catalogo.registrar("CofreDeEscudo", CofreDeEscudo::new);
        catalogo.registrar("CofreDeBrujula", CofreDeBrujula::new);
        catalogo.registrar("CofreDeSueno", CofreDeSueno::new);
        catalogo.registrar("CofreDePuente",
                posicion -> new CofreDePuente(
                        posicion, parametros.duracionPuente()));

        catalogo.registrar("TrampaDeLava",
                posicion -> new TrampaDeLava(
                        posicion, parametros.duracionLava()));
        return catalogo;
    }

    public static Catalogo<Monstruo> monstruos(Costo costos, ParametrosEntidades parametros, Random azar) {
        if (costos == null || parametros == null || azar == null
                || costos.piso() < 0
                || costos.agua() < 0
                || costos.lava() < 0) {
            throw new IllegalArgumentException(
                    "Parametros del catalogo invalidos");
        }

        Catalogo<Monstruo> catalogo = new Catalogo<>();

        catalogo.registrar("Errante",
                posicion -> new Errante(
                        posicion,
                        new Random(azar.nextLong()),
                        parametros.danioBasico()));

        catalogo.registrar("Cazador",
                posicion -> new Cazador(
                        posicion,
                        costos,
                        parametros.danioBasico()));

        catalogo.registrar("Fantasma",
                posicion -> new Fantasma(
                        posicion,
                        new Random(azar.nextLong()),
                        parametros.danioBasico()));

        catalogo.registrar("Congelador",
                posicion -> new Congelador(
                        posicion,
                        new Random(azar.nextLong()),
                        parametros.duracionCongelador()));

        catalogo.registrar("Dormilon",
                posicion -> new Dormilon(
                        posicion,
                        costos,
                        parametros.danioBasico()));

        return catalogo;
    }
}

