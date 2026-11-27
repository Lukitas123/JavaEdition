package entidades;

import entidades.cofres.*;
import entidades.monstruos.*;
import entidades.objetos.*;
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

    public static CatalogoObjetos objetos(
            ParametrosEntidades parametros,
            Random azar) {

        if (parametros == null || azar == null) {
            throw new IllegalArgumentException(
                    "Parametros del catalogo invalidos");
        }

        CatalogoObjetos catalogo = new CatalogoObjetos();

        catalogo.registrar(ZapatosDeAgua.ID_OBJETO,
                new ZapatosDeAgua(parametros.duracionZapatosAgua()));

        catalogo.registrar(Escudo.ID_OBJETO,
                new Escudo(parametros.duracionEscudo()));

        catalogo.registrar(Sueno.ID_OBJETO,
                new Sueno(parametros.duracionSueno()));

        catalogo.registrar(PocionDeEnergia.id,
                new PocionDeEnergia(parametros.energiaPocion()));

        catalogo.registrar(Brujula.ID_OBJETO, new Brujula());

        catalogo.registrar(Daga.ID_OBJETO,
                new Daga(new Random(azar.nextLong())));

        return catalogo;
    }
}

