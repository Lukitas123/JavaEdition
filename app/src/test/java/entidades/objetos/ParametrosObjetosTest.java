package entidades.objetos;

import entidades.CatalogosEntidades;
import modelo.*;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class ParametrosObjetosTest {
    private final ParametrosEntidades parametros =
            new ParametrosEntidades(
                    11, 13, 2, 3, 4, 2, 3, 4, 9);

    private Mazmorra partida() {
        Plano plano = new Plano(
                new Terreno[][] {{Terreno.PISO, Terreno.AGUA}},
                new Posicion(1, 1),
                null);

        Mundo mundo = new Mundo(1, 1);
        mundo.agregarPlano(1, 1, plano);

        return new Mazmorra(
                Dificultad.FACIL,
                mundo,
                new Heroe(100, mundo.getInicio()),
                new Agenda(),
                new Costo(1, 3, 8),
                parametros,
                CatalogosEntidades.objetos(parametros, new Random(1)));
    }

    @Test
    void objetosUsanDuracionesConfiguradasEnLaPartida() {
        String[] ids = {
                ZapatosDeAgua.ID_OBJETO,
                Escudo.ID_OBJETO,
                Sueno.ID_OBJETO
        };

        EfectoHeroe[] efectos = {
                EfectoHeroe.ZAPATOS_DE_AGUA,
                EfectoHeroe.ESCUDO
        };

        for (int i = 0; i < ids.length; i++) {
            Mazmorra partida = partida();
            partida.getHeroe().agregarObjeto(ids[i]);

            assertTrue(partida.usarObjeto(ids[i]));
            assertEquals(1, partida.getAgenda().getPasoActual());

            assertEquals(
                    i + 3,
                    partida.getAgenda().exportarEventos()[0]
                            .pasoDeVencimiento());

            for (int paso = 1; paso < i + 2; paso++) {
                partida.esperar();
            }

            if (i < 2) {
                assertTrue(
                        partida.getHeroe().tieneEfecto(efectos[i]));
            } else {
                assertTrue(
                        partida.planoActual().getEfectos()
                                .monstruosDormidos());
            }

            partida.esperar();

            if (i < 2) {
                assertFalse(
                        partida.getHeroe().tieneEfecto(efectos[i]));
            } else {
                assertFalse(
                        partida.planoActual().getEfectos()
                                .monstruosDormidos());
            }
        }
    }

    @Test
    void pocionUsaEnergiaConfigurada() {
        Mazmorra partida = partida();

        partida.getHeroe().perderEnergia(20);
        partida.getHeroe().agregarObjeto(PocionDeEnergia.id);

        assertTrue(partida.usarObjeto(PocionDeEnergia.id));
        assertEquals(89, partida.getHeroe().getEnergia());
    }

    @Test
    void rechazaDuracionesYCatalogosInvalidos() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ZapatosDeAgua(0));

        assertThrows(
                IllegalArgumentException.class,
                () -> new Escudo(-1));

        assertThrows(
                IllegalArgumentException.class,
                () -> new Sueno(0));

        assertThrows(
                IllegalArgumentException.class,
                () -> CatalogosEntidades.objetos(
                        null, new Random(1)));

        assertThrows(
                IllegalArgumentException.class,
                () -> CatalogosEntidades.objetos(
                        parametros, null));
    }
}
