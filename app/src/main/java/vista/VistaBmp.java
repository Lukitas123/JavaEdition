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
import java.util.List;

import javax.imageio.ImageIO;

import entidades.cofres.Cofre;
import entidades.monstruos.Monstruo;
import estructuras.Lista;

public class VistaBmp implements Vista{
    private final int tamañoCelda;

    public VistaBmp() {
        //tamaño de celda por defecto es 16x16
        this.tamañoCelda = 16; 
    }

    public VistaBmp(int tamañoCelda) {
        this.tamañoCelda = tamañoCelda;
    }

    @Override
    public void mostrarPartida(Plano plano, Heroe heroe, int numeroPaso){
        int filas=plano.getFilas();
        int columnas=plano.getColumnas();

        int ancho=tamañoCelda*columnas;
        int alto=(tamañoCelda*filas)+30;
        
        BufferedImage imagen = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = imagen.createGraphics();

        //Dibuja el fondo de la partida
        for (int fila=1;fila<=filas;fila++){
            for (int columna=1;columna<=columnas;columna++){
                Terreno terrenoActual=plano.terrenoEn(new Posicion(fila,columna));

                int posX=(columna-1)*tamañoCelda;
                int posY=(fila-1)*tamañoCelda;
                g.setColor(obtenerColorTerreno(terrenoActual));
                g.fillRect(posX,posY, tamañoCelda, tamañoCelda);

            }
        }
        //dibujar heroe
        Posicion posHeroe=heroe.getUbicacion().posicion();
        g.setColor(Color.ORANGE);
        int posXHeroe=(posHeroe.columna()-1)*tamañoCelda;
        int posYHeroe=(posHeroe.fila()-1)*tamañoCelda;
        g.fillOval(posXHeroe,posYHeroe,tamañoCelda,tamañoCelda);

        //dibujar cofres y monstruos
        Lista<Cofre> cofres = plano.getCofres();
        for (int i=0;i<cofres.cantidad();i++){
            Cofre c=cofres.obtener(i);
            Posicion posCofre=c.getPosicion();

            g.setColor(new Color(139, 69, 19));
            int posX=(posCofre.columna()-1)*tamañoCelda;
            int posY=(posCofre.fila()-1)*tamañoCelda;
            g.fillOval(posX,posY,tamañoCelda,tamañoCelda);
        }
        Lista<Monstruo> monstruos=plano.getMonstruos();
        for (int i=0;i<monstruos.cantidad();i++){
            Monstruo m=monstruos.obtener(i);
            Posicion posMonstruo=m.getPosicion();

            g.setColor(Color.MAGENTA);
            int posX=(posMonstruo.columna()-1)*tamañoCelda;
            int posY=(posMonstruo.fila()-1)*tamañoCelda;
            g.fillOval(posX,posY,tamañoCelda,tamañoCelda);
        }

        //Indica la energia restante
        g.setColor(Color.BLACK);
        g.fillRect(0,filas*tamañoCelda,ancho,30);
        g.setColor(Color.GREEN);
        g.drawString("Energía: "+ heroe.getEnergia(),10,filas*tamañoCelda+20);

        g.dispose();

        guardarImagen(imagen,numeroPaso);
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


}