package vista;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;

import modelo.Heroe;
import modelo.Plano;
import modelo.Posicion;
import modelo.Terreno;
import modelo.Ubicacion;

public class VistaBmpTest {
    private static final int FILAS = 5;
    private static final int COLUMNAS = 10;
    private static final int CELDA = 16;
    private static final int ENERGIA_MAXIMA = 100;

    //valores de vistaBmp de referencia
    private static final int ALTO_BARRA = 30;
    private static final int MARGEN = 10;
    private static final int ANCHO = CELDA*COLUMNAS;
    private static final int ANCHO_MAXIMO = ANCHO - 2*MARGEN;
    private static final int Y_MEDIO_DE_LA_BARRA = FILAS*CELDA + ALTO_BARRA/2;

    private static final int VERDE = Color.GREEN.getRGB();
    private static final int NEGRO = Color.BLACK.getRGB();

    /** Plano de 5 x 10 con borde de pared, piso adentro, entrada en (2,2) y salida en (4,9) */
    private static Plano planoDePrueba() {
        Terreno[][] terreno = new Terreno[FILAS][COLUMNAS];
        for (int f = 0; f < FILAS; f++) {
            for (int c = 0; c < COLUMNAS; c++) {
                boolean borde = f == 0 || c == 0 || f == FILAS - 1 || c == COLUMNAS - 1;
                terreno[f][c] = borde ? Terreno.PARED : Terreno.PISO;
            }
        }
        return new Plano(terreno, new Posicion(2, 2), new Posicion(4, 9));
    }

    /** Dibuja un paso con la energía indicada y lee de vuelta el archivo generado */
    private static BufferedImage dibujarYLeer(int energia, int paso) throws IOException {
        Heroe heroe = new Heroe(energia, new Ubicacion(1, 1, new Posicion(2, 2)));
        new VistaBmp(CELDA, ENERGIA_MAXIMA).mostrarPartida(planoDePrueba(), heroe, paso);
        return ImageIO.read(new File(String.format("salida/paso-%04d.bmp", paso)));
    }

    /** Color en (x, y) */
    private static int color(BufferedImage imagen, int x, int y) {
        return imagen.getRGB(x, y);
    }

    /** La imagen mide lo que corresponde al plano más la franja de energia */
    @Test
    void laImagenTieneElTamanioDelPlano() throws IOException {
        BufferedImage imagen = dibujarYLeer(100, 1);

        assertEquals(CELDA*COLUMNAS, imagen.getWidth());
        assertEquals(CELDA*FILAS + ALTO_BARRA, imagen.getHeight());
    }

    /** Con la mitad de la energia, la mitad izquierda de la barra es verde y la derecha no */
    @Test
    void laBarraSeLlenaSegunLaEnergia() throws IOException {
        BufferedImage imagen = dibujarYLeer(50, 2);

        assertEquals(VERDE, color(imagen, MARGEN + 5, Y_MEDIO_DE_LA_BARRA));
        assertEquals(NEGRO, color(imagen, MARGEN + ANCHO_MAXIMO - 5, Y_MEDIO_DE_LA_BARRA));
    }

    /** Energia es mayor a la maxima. No se sale del marco */
    @Test
    void energiaMayorAlMaximoNoDesbordaLaBarra() throws IOException {
        BufferedImage imagen = dibujarYLeer(150, 3);

        assertEquals(VERDE, color(imagen, MARGEN + ANCHO_MAXIMO - 5, Y_MEDIO_DE_LA_BARRA));
        assertEquals(NEGRO, color(imagen, ANCHO - 5, Y_MEDIO_DE_LA_BARRA));
    }
}
