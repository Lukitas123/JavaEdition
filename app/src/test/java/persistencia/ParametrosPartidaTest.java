package persistencia;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entidades.CatalogosEntidades;
import entidades.monstruos.Monstruo;
import entidades.objetos.Escudo;
import modelo.*;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class ParametrosPartidaTest {
    private final ParametrosEntidades parametros =
            new ParametrosEntidades(
                    11, 13, 2, 3, 4, 5, 6, 7, 9);

    private Mazmorra original() {
        Plano plano = new Plano(
                new Terreno[][] {
                        {Terreno.PISO, Terreno.PISO, Terreno.AGUA}
                },
                new Posicion(1, 1),
                null);

        Costo costos = new Costo(1, 3, 8);

        plano.agregarMonstruo(
                CatalogosEntidades.monstruos(
                                costos, parametros, new Random(1))
                        .crear("Cazador", new Posicion(1, 2)));

        plano.agregarCofre(
                CatalogosEntidades.cofres(parametros)
                        .crear("CofreDePuente", new Posicion(1, 2)));

        Mundo mundo = new Mundo(1, 1);
        mundo.agregarPlano(1, 1, plano);

        Heroe heroe = new Heroe(100, mundo.getInicio());
        heroe.agregarObjeto(Escudo.ID_OBJETO);

        Mazmorra partida = new Mazmorra(
                Dificultad.FACIL,
                mundo,
                heroe,
                new Agenda(),
                costos,
                parametros,
                CatalogosEntidades.objetos(
                        parametros, new Random(1)));

        plano.getCofres().get(0)
                .abrir(heroe, plano, partida.getAgenda());

        return partida;
    }

    private MazmorraGuardada roundtrip(Mazmorra partida) {
        Gson gson = new Gson();

        return gson.fromJson(
                gson.toJson(ConversorMazmorra.aGuardada(partida)),
                MazmorraGuardada.class);
    }

    @Test
    void conservaParametrosYComportamientosTrasRoundtrip() {
        Mazmorra restaurada = ConversorMazmorra.aMazmorra(
                roundtrip(original()), new Random(2));

        assertEquals(
                parametros,
                restaurada.getParametrosEntidades());

        assertEquals(
                4,
                restaurada.planoActual()
                        .getEfectos().getFinPuente());

        assertEquals(
                4,
                restaurada.getAgenda().exportarEventos()[0]
                        .pasoDeVencimiento());

        Monstruo monstruo =
                restaurada.planoActual().getMonstruos().get(0);

        monstruo.alcanzarAlHeroe(
                restaurada.getHeroe(), restaurada.getAgenda());

        assertEquals(89, restaurada.getHeroe().getEnergia());

        assertTrue(restaurada.usarObjeto(Escudo.ID_OBJETO));

        assertEquals(
                7,
                restaurada.getHeroe()
                        .vencimientoDe(EfectoHeroe.ESCUDO));

        assertEquals(89, restaurada.getHeroe().getEnergia());
    }

    @Test
    void cofreCerradoConservaDuracionConfigurada() {
        Mazmorra partida = original();

        partida.planoActual().agregarCofre(
                CatalogosEntidades.cofres(parametros)
                        .crear("TrampaDeLava", new Posicion(1, 2)));

        Mazmorra restaurada = ConversorMazmorra.aMazmorra(
                roundtrip(partida), new Random(2));

        restaurada.planoActual().getCofres().get(0).abrir(
                restaurada.getHeroe(),
                restaurada.planoActual(),
                restaurada.getAgenda());

        assertEquals(
                5,
                restaurada.planoActual().getEfectos()
                        .getVencimientosLava()
                        .get(new Posicion(1, 3)));
    }

    @Test
    void rechazaGuardadoAnteriorConMensajeClaro() {
        Gson gson = new Gson();

        JsonObject json = gson.toJsonTree(
                        ConversorMazmorra.aGuardada(original()))
                .getAsJsonObject();

        json.remove("parametrosEntidades");

        MazmorraGuardada anterior =
                gson.fromJson(json, MazmorraGuardada.class);

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> ConversorMazmorra.aMazmorra(
                        anterior, new Random(1)));

        assertTrue(
                error.getMessage().contains("Guardado incompatible"));
    }
}