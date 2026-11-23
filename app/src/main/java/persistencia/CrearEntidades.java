package persistencia;

import java.util.Random;

import entidades.cofres.*;
import entidades.monstruos.*;
import modelo.Costo;
import modelo.Posicion;

/** Crea monstruos y cofres a partir del nombre de su tipo */
public class CrearEntidades {

    /** Crea un monstruo del tipo indicado */
    public static Monstruo crearMonstruo(String tipo, Posicion posicion, Costo costos){
        if (tipo.equals("Errante")){
            return new Errante(posicion, new Random());
        }
        if (tipo.equals("Fantasma")){
            return new Fantasma(posicion, new Random());
        }
        if ("Cazador".equals(tipo)) {
            return new Cazador(posicion, costos);
        }
        if ("Dormilon".equals(tipo)) {
            return new Dormilon(posicion, costos);
        }

        throw new IllegalArgumentException("Tipo de monstruo invalido: " + tipo);
    }

    /** Crea un cofre del tipo indicado */
    public static Cofre crearCofre(String tipo, Posicion posicion, int danio){
        if (tipo.equals("CofreDePuntos")){
            return new CofreDePuntos(posicion);
        }
        if (tipo.equals("CofreDePocion")){
            return new CofreDePocion(posicion);
        }
        if (tipo.equals("TrampaDeEnergia")){
            return new TrampaDeEnergia(posicion,danio);
        }
        if ("CofreDeDaga".equals(tipo)) {
            return new CofreDeDaga(posicion);
        }
        if ("CofreZapatosDeAgua".equals(tipo)) {
            return new CofreZapatosDeAgua(posicion);
        }
        if ("CofreDeEscudo".equals(tipo)) {
            return new CofreDeEscudo(posicion);
        }
        if ("CofreDeBrujula".equals(tipo)) {
            return new CofreDeBrujula(posicion);
        }
        if ("CofreDeSueno".equals(tipo)) {
            return new CofreDeSueno(posicion);
        }
        if ("CofreDePuente".equals(tipo)) {
            return new CofreDePuente(posicion);
        }
        if ("TrampaDeLava".equals(tipo)) {
            return new TrampaDeLava(posicion);
        }

        throw new IllegalArgumentException("Tipo de cofre invalido: " + tipo);
    }

}
