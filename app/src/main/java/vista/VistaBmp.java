package vista;

import modelo.Plano;
import modelo.Posicion;
import modelo.Terreno;
import modelo.Heroe;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.imageio.ImageIO;

import entidades.cofres.Cofre;
import entidades.monstruos.Monstruo;
import java.util.List;

public class VistaBmp implements Vista {
    private static final int TAMANIO_CELDA = 16;
    private static final int ALTO_BARRA = 30;
    private static final int MARGEN = 10;

    private final int tamanioCelda;
    private final int energiaMaxima;

    private final Scanner teclado = new Scanner(System.in);

    /**Crea una vista con celdas del tamaño por defecto */
    public VistaBmp(int energiaMaxima){
        if (energiaMaxima <= 0) {
            throw new IllegalArgumentException("La energia maxima debe ser mayor que 0");
        }
        this.tamanioCelda=TAMANIO_CELDA;
        this.energiaMaxima=energiaMaxima;
    }

    /** Crea una vista con el tamaño de celda y la energia maxima especificados */
    public VistaBmp(int tamanioCelda, int energiaMaxima) {
        if (tamanioCelda <= 0 || energiaMaxima <= 0) {
            throw new IllegalArgumentException("El tamaño de celda y la energia maxima deben ser mayores a 0");
        }
        this.tamanioCelda = tamanioCelda;
        this.energiaMaxima = energiaMaxima;
    }
    /**
     * Crea un bitmap a partir de un plano junto con la cantidad de energia del heroe 
     * 
    */
    @Override
    public void mostrarPartida(Plano plano, Heroe heroe, int numeroPaso){
        int filas=plano.getFilas();
        int columnas=plano.getColumnas();

        int ancho=tamanioCelda*columnas;
        int alto=(tamanioCelda*filas) + ALTO_BARRA;
        
        BufferedImage imagen = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = imagen.createGraphics();

        //Dibuja el fondo de la partida
        for (int fila=1;fila<=filas;fila++){
            for (int columna=1;columna<=columnas;columna++){
                Terreno terrenoActual=plano.terrenoEn(new Posicion(fila,columna));

                int posX=(columna-1)*tamanioCelda;
                int posY=(fila-1)*tamanioCelda;
                g.setColor(obtenerColorTerreno(terrenoActual));
                g.fillRect(posX,posY, tamanioCelda, tamanioCelda);

            } 
        }
        //Dibujar Salida
        if (plano.tieneSalida()){
                    Posicion salida = plano.getSalida();
                    g.setColor(Color.YELLOW);
                    g.fillRect((salida.columna()-1)*tamanioCelda, (salida.fila()-1)*tamanioCelda, tamanioCelda, tamanioCelda);
                }

        //dibujar heroe
        Posicion posHeroe=heroe.getUbicacion().posicion();
        g.setColor(Color.ORANGE);
        int posXHeroe=(posHeroe.columna()-1)*tamanioCelda;
        int posYHeroe=(posHeroe.fila()-1)*tamanioCelda;
        g.fillOval(posXHeroe,posYHeroe,tamanioCelda,tamanioCelda);

        //dibujar cofres y monstruos
        List<Cofre> cofres = plano.getCofres();
        for (Cofre c : cofres){
            Posicion posCofre=c.getPosicion();

            g.setColor(new Color(139, 69, 19));
            int posX=(posCofre.columna()-1)*tamanioCelda;
            int posY=(posCofre.fila()-1)*tamanioCelda;
            g.fillOval(posX,posY,tamanioCelda,tamanioCelda);
        }
        List<Monstruo> monstruos=plano.getMonstruos();
        for (Monstruo m : monstruos){
            Posicion posMonstruo=m.getPosicion();

            g.setColor(Color.MAGENTA);
            int posX=(posMonstruo.columna()-1)*tamanioCelda;
            int posY=(posMonstruo.fila()-1)*tamanioCelda;
            g.fillOval(posX,posY,tamanioCelda,tamanioCelda);
        }

        //Indica la energia restante
        dibujarEnergia(g, heroe.getEnergia(), filas, ancho);

        g.dispose();

        guardarImagen(imagen,numeroPaso);
    }

    /** Marca la energia del heroe graficamente */
    private void dibujarEnergia(Graphics2D g, int energia, int filas, int ancho) {
        int posY = filas*tamanioCelda;
        g.setColor(Color.BLACK);
        g.fillRect(0, posY, ancho, ALTO_BARRA);

        int anchoMaximo = ancho - 2*MARGEN;
        int energiaVisible = Math.min(energia, energiaMaxima);
        int anchoRelleno = (anchoMaximo*energiaVisible)/energiaMaxima;

        g.setColor(Color.GREEN);
        g.fillRect(MARGEN, posY + 8, anchoRelleno, ALTO_BARRA-16);
        g.setColor(Color.WHITE);
        g.drawRect(MARGEN, posY + 8, anchoMaximo, ALTO_BARRA-16);
    }

    /**Elige el color segun el tipo de terreno*/
    private Color obtenerColorTerreno(Terreno terreno) {
        return switch (terreno) {
            case PARED -> Color.GRAY;
            case PISO -> Color.LIGHT_GRAY;
            case AGUA -> Color.BLUE;
            case LAVA -> Color.RED;
        };
    }
    /**Metodo aux para guardar el bitmap generado*/
    private void guardarImagen(BufferedImage imagen, int numeroPaso){
        File carpetaSalida=new File("salida");
        //creo una nueva carpeta "salida" dentro del main/java no?
        if (!carpetaSalida.exists()){
            carpetaSalida.mkdirs();
        }
        String nombreArchivo = String.format("salida/paso-%04d.bmp", numeroPaso);
        File archivo = new File(nombreArchivo);

        try{
            ImageIO.write(imagen,"bmp",archivo);
        }
        catch (IOException e) {
            System.err.println("Error al guardar la imagen BMP del paso " + numeroPaso + ": " + e.getMessage());
        }
    }

    @Override
    public char leerTecla(){
        if (!teclado.hasNextLine()){
            return 'q';
        }
        String linea = teclado.nextLine().trim().toLowerCase();
        if (linea.isEmpty()){
            return ' ';
        }
        return linea.charAt(0);
    }


}