package persistencia;

import static org.junit.jupiter.api.Assertions.assertEquals;

import modelo.*;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

import entidades.Catalogo;
import entidades.cofres.Cofre;
import entidades.monstruos.Errante;
import entidades.monstruos.Monstruo;

import java.util.Random;

public class ConversorMundoTest {

    private static Catalogo<Monstruo> pruebaMonstruos(){
        return CatalogosDePrueba.monstruos();
    }

    private static Catalogo<Cofre> pruebaCofre(){
        return CatalogosDePrueba.cofres();
    }

    private static Terreno[][] terrenoDePrueba() {
        Terreno p = Terreno.PARED;
        Terreno o = Terreno.PISO;
        return new Terreno[][] {
            {p, p, p, p},
            {p, o, o, p},
            {p, o, o, p},
            {p, p, p, p}
        };
    }

    /** Un monstruo parado sobre una conexion se puede guardar y cargar */
    @Test
    void monstruoSobreUnaConexionSobrevive() {
        Mundo original = new Mundo(1, 2);
        original.agregarPlano(1, 1, new Plano(terrenoDePrueba(), new Posicion(2, 2), null));
        original.agregarPlano(1, 2, new Plano(terrenoDePrueba(), new Posicion(2, 2),
                                                            new Posicion(3, 3)));
        original.agregarConexion(new Conexion(
                new Ubicacion(1, 1, new Posicion(3, 2)), new Ubicacion(1, 2, new Posicion(3, 2))));
        original.planoEn(1, 1).agregarMonstruo(new Errante(new Posicion(3, 2), new Random(1),30));

        Gson gson = new Gson();
        String json = gson.toJson(ConversorMundo.aGuardado(original));
        Mundo restaurado = ConversorMundo.aMundo(gson.fromJson(json, MundoGuardado.class), pruebaMonstruos(), pruebaCofre());

        assertEquals(1, restaurado.getConexiones().cantidad());
        assertEquals(1, restaurado.planoEn(1, 1).getMonstruos().cantidad());
    }
}
