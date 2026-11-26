package entidades;

import entidades.cofres.Cofre;
import entidades.cofres.CofreDePocion;
import entidades.cofres.CofreDePuntos;
import java.util.Random;

import entidades.cofres.TrampaDeEnergia;
import entidades.monstruos.Congelador;
import entidades.monstruos.Monstruo;
import modelo.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CatalogoTest {

    @Test
    void creaInstanciasIndependientesEnSusPosiciones() {
        Catalogo<Cofre> catalogo = new Catalogo<>();
        catalogo.registrar("puntos", CofreDePuntos::new);

        Cofre primero = catalogo.crear("puntos", new Posicion(1, 1));
        Cofre segundo = catalogo.crear("puntos", new Posicion(2, 3));

        assertInstanceOf(CofreDePuntos.class, primero);
        assertNotSame(primero, segundo);
        assertEquals(new Posicion(1, 1), primero.getPosicion());
        assertEquals(new Posicion(2, 3), segundo.getPosicion());
    }

    @Test
    void rechazaTiposDuplicadosODesconocidos() {
        Catalogo<Cofre> catalogo = new Catalogo<>();
        catalogo.registrar("puntos", CofreDePuntos::new);

        assertThrows(IllegalArgumentException.class,
                () -> catalogo.registrar("puntos", CofreDePocion::new));
        assertThrows(IllegalArgumentException.class,
                () -> catalogo.crear("desconocido", new Posicion(1, 1)));
    }

    @Test
    void rechazaSorteoConCatalogoVacio() {
        Catalogo<Cofre> catalogo = new Catalogo<>();

        assertThrows(IllegalStateException.class,
                () -> catalogo.sortear(new Posicion(1, 1), new Random(1)));
    }

    @Test
    void mismaSemillaProduceLaMismaSecuenciaDeTipos() {
        Catalogo<Cofre> catalogo = new Catalogo<>();
        catalogo.registrar("puntos", CofreDePuntos::new);
        catalogo.registrar("pocion", CofreDePocion::new);

        Random primero = new Random(42);
        Random segundo = new Random(42);
        Posicion posicion = new Posicion(1, 1);

        for (int i = 0; i < 20; i++) {
            Cofre cofreA = catalogo.sortear(posicion, primero);
            Cofre cofreB = catalogo.sortear(posicion, segundo);

            assertEquals(cofreA.getClass(), cofreB.getClass());
        }
    }
    @Test
    void catalogoDeProduccionCreaLosCofres() {
        Catalogo<Cofre> catalogo = CatalogosEntidades.cofres(40);
        Posicion posicion = new Posicion(1, 1);

        String[] tipos = {
                "CofreDePuntos", "CofreDePocion", "TrampaDeEnergia",
                "CofreZapatosDeAgua", "CofreDeDaga", "CofreDeEscudo",
                "CofreDeBrujula", "CofreDeSueno", "CofreDePuente", "TrampaDeLava"
        };

        for (String tipo : tipos) {
            assertEquals(tipo, catalogo.crear(tipo, posicion).getTipo());
        }
        assertEquals(40, catalogo.crear("TrampaDeEnergia", posicion).getDanio());
    }

    @Test
    void catalogoDeProduccionCreaMonstruosConSusParametros() {
        Costo costos = new Costo(1, 3, 2);
        Catalogo<Monstruo> catalogo =
                CatalogosEntidades.monstruos(costos, 7, new Random(1));
        Posicion inicio = new Posicion(1, 1);

        String[] tipos = {"Errante", "Cazador", "Fantasma", "Congelador", "Dormilon"};
        for (String tipo : tipos) {
            assertEquals(tipo, catalogo.crear(tipo, inicio).getTipo());
        }

        Plano plano = new Plano(new Terreno[][] {
                {Terreno.PISO, Terreno.LAVA, Terreno.PISO},
                {Terreno.PISO, Terreno.PISO, Terreno.PISO}
        }, inicio, null);
        Monstruo cazador = catalogo.crear("Cazador", inicio);
        assertEquals(new Posicion(1, 2),
                cazador.decidirMovimiento(plano, new Posicion(1, 3)));

        Heroe heroe = new Heroe(100, new Ubicacion(1, 1, inicio));
        Monstruo congelador = catalogo.crear("Congelador", inicio);
        congelador.alcanzarAlHeroe(heroe, new Agenda());
        assertEquals(Integer.valueOf(8), heroe.vencimientoDe(EfectoHeroe.INMOVILIZADO));
    }

    private record DatosTrampa(Posicion posicion, int danio) {}

}