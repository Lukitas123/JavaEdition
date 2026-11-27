package entidades.monstruos;

import com.google.gson.Gson;
import entidades.Catalogo;
import entidades.CatalogosEntidades;
import modelo.*;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class ParametrosMonstruosTest {

    private Catalogo<Monstruo> catalogo() {
        String json = """
                {
                  "danioBasico": 11,
                  "danioTrampaEnergia": 13,
                  "duracionCongelador": 2,
                  "duracionPuente": 3,
                  "duracionLava": 4,
                  "duracionZapatosAgua": 5,
                  "duracionEscudo": 6,
                  "duracionSueno": 8,
                  "energiaPocion": 9
                }
                """;

        ParametrosEntidades parametros =
                new Gson().fromJson(json, ParametrosEntidades.class);

        return CatalogosEntidades.monstruos(
                new Costo(1, 3, 8), parametros, new Random(1));
    }

    private Heroe heroe() {
        return new Heroe(
                100,
                new Ubicacion(1, 1, new Posicion(1, 1)));
    }

    @Test
    void usaDanioDelJsonParaLosCuatroMonstruos() {
        Catalogo<Monstruo> catalogo = catalogo();

        for (String tipo : new String[] {
                "Errante", "Cazador", "Fantasma", "Dormilon"}) {

            Heroe heroe = heroe();

            catalogo.crear(tipo, new Posicion(1, 1))
                    .alcanzarAlHeroe(heroe, new Agenda());

            assertEquals(89, heroe.getEnergia(), tipo);
        }
    }

    @Test
    void congeladorUsaDuracionDelJsonSinDaniar() {
        Heroe heroe = heroe();

        catalogo().crear("Congelador", new Posicion(1, 1))
                .alcanzarAlHeroe(heroe, new Agenda());

        assertEquals(100, heroe.getEnergia());
        assertEquals(
                3,
                heroe.vencimientoDe(EfectoHeroe.INMOVILIZADO));
    }


    @Test
    void constructoresRechazanParametrosInvalidos() {
        Posicion inicio = new Posicion(1, 1);
        Costo costos = new Costo(1, 3, 8);

        assertThrows(
                IllegalArgumentException.class,
                () -> new Errante(inicio, new Random(1), -1));

        assertThrows(
                IllegalArgumentException.class,
                () -> new Cazador(inicio, costos, 0));

        assertThrows(
                IllegalArgumentException.class,
                () -> new Fantasma(inicio, new Random(1), 0));

        assertThrows(
                IllegalArgumentException.class,
                () -> new Dormilon(inicio, costos, 0));
    }
}
