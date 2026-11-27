package entidades.cofres;

import com.google.gson.Gson;
import entidades.Catalogo;
import entidades.CatalogosEntidades;
import modelo.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParametrosCofresTest {

    private Catalogo<Cofre> catalogo() {
        String json = """
                {
                  "danioBasico": 11,
                  "danioTrampaEnergia": 17,
                  "duracionCongelador": 2,
                  "duracionPuente": 2,
                  "duracionLava": 3,
                  "duracionZapatosAgua": 4,
                  "duracionEscudo": 5,
                  "duracionSueno": 6,
                  "energiaPocion": 7
                }
                """;

        ParametrosEntidades parametros =
                new Gson().fromJson(json, ParametrosEntidades.class);

        return CatalogosEntidades.cofres(parametros);
    }

    private Plano plano() {
        return new Plano(
                new Terreno[][] {{Terreno.PISO, Terreno.AGUA}},
                new Posicion(1, 1),
                null);
    }

    private Heroe abrir(String tipo, Plano plano, Agenda agenda) {
        Heroe heroe = new Heroe(
                100,
                new Ubicacion(1, 1, plano.getEntrada()));

        Cofre cofre = catalogo().crear(tipo, plano.getEntrada());
        plano.agregarCofre(cofre);
        cofre.abrir(heroe, plano, agenda);

        return heroe;
    }

    @Test
    void puenteUsaDuracionDelJson() {
        Plano plano = plano();
        Agenda agenda = new Agenda();
        Posicion agua = new Posicion(1, 2);

        abrir("CofreDePuente", plano, agenda);

        assertEquals(Terreno.PISO, plano.terrenoEn(agua));
        assertEquals(3, agenda.eventoEn(0).pasoDeVencimiento());

        plano.getEfectos().finalizarPuenteSiVencio(2);
        assertEquals(Terreno.PISO, plano.terrenoEn(agua));

        plano.getEfectos().finalizarPuenteSiVencio(3);
        assertEquals(Terreno.AGUA, plano.terrenoEn(agua));
    }

    @Test
    void lavaUsaDuracionDelJson() {
        Plano plano = plano();
        Agenda agenda = new Agenda();
        Posicion agua = new Posicion(1, 2);

        abrir("TrampaDeLava", plano, agenda);

        assertEquals(Terreno.LAVA, plano.terrenoEn(agua));
        assertEquals(4, agenda.eventoEn(0).pasoDeVencimiento());

        plano.getEfectos().finalizarLavaSiVencio(3);
        assertEquals(Terreno.LAVA, plano.terrenoEn(agua));

        plano.getEfectos().finalizarLavaSiVencio(4);
        assertEquals(Terreno.AGUA, plano.terrenoEn(agua));
    }

    @Test
    void trampaUsaDanioDelJson() {
        Heroe heroe = abrir(
                "TrampaDeEnergia", plano(), new Agenda());

        assertEquals(83, heroe.getEnergia());
    }

    @Test
    void rechazaDuracionesInvalidas() {
        Posicion posicion = new Posicion(1, 1);

        assertThrows(
                IllegalArgumentException.class,
                () -> new CofreDePuente(posicion, 0));

        assertThrows(
                IllegalArgumentException.class,
                () -> new TrampaDeLava(posicion, -1));

        assertThrows(
                IllegalArgumentException.class,
                () -> CatalogosEntidades.cofres(null));
    }
}