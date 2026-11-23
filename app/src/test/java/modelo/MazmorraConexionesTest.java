package modelo;

import static modelo.MundosDePrueba.mazmorraDe;
import static modelo.MundosDePrueba.mundoConEscalera;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Regla: pisar una escalera o un pasillo lleva al héroe al otro extremo de la conexión */
class MazmorraConexionesTest {
    private Mundo mundo;
    private Mazmorra mazmorra;

    @BeforeEach
    void armar() {
        mundo = mundoConEscalera();
        mazmorra = mazmorraDe(mundo, 100);
    }

    /** Del inicio (2,2) del piso 1 pisa la escalera en (3,3): abajo y derecha */
    private void pisarLaEscalera() {
        mazmorra.moverHeroe(Direccion.ABAJO);
        mazmorra.moverHeroe(Direccion.DERECHA);
    }

    @Test
    void pisarLaEscaleraLlevaAlOtroPiso() {
        pisarLaEscalera();

        assertEquals(new Ubicacion(1, 2, new Posicion(3, 3)), mazmorra.getHeroe().getUbicacion());
        assertSame(mundo.planoEn(1, 2), mazmorra.planoActual());
        assertEquals(98, mazmorra.getHeroe().getEnergia());
    }

    @Test
    void laConexionSeRecorreEnAmbosSentidos() {
        pisarLaEscalera();

        mazmorra.moverHeroe(Direccion.IZQUIERDA);
        mazmorra.moverHeroe(Direccion.DERECHA);

        assertEquals(new Ubicacion(1, 1, new Posicion(3, 3)), mazmorra.getHeroe().getUbicacion());
    }

    @Test
    void alLlegarNoRebotaPorLaMismaConexion() {
        pisarLaEscalera();

        assertEquals(2, mazmorra.getHeroe().getUbicacion().piso());
        assertEquals(2, mazmorra.getAgenda().getPasoActual());
    }

    @Test
    void sePuedeGanarEnElOtroPiso() {
        pisarLaEscalera();

        mazmorra.moverHeroe(Direccion.ABAJO);
        mazmorra.moverHeroe(Direccion.DERECHA);

        assertEquals(EstadoPartida.GANADA, mazmorra.getEstado());
        // 0 cofres por 100 + 96 de energia por 2 - 4 pasos
        assertEquals(188, mazmorra.calcularPuntaje().valor());
    }
}
