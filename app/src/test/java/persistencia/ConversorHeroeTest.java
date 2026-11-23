package persistencia;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

import modelo.Posicion;
import modelo.Ubicacion;

public class ConversorHeroeTest {

    /** Convierte un heroe a un HeroeGuardado y este conserva todo su estado */
    @Test
    void heroeSobreviveAConversion() {
        HeroeGuardado original = new HeroeGuardado(80, new Ubicacion(1, 2, new Posicion(3, 4)),
        2, Map.of("pocion", 1, "daga", 2), Map.of("zapatos", 20));

        Gson gson = new Gson();
        String json = gson.toJson(original);
        HeroeGuardado leido = gson.fromJson(json, HeroeGuardado.class);

        assertEquals(80, leido.energia());
        assertEquals(original.ubicacion(), leido.ubicacion());
        assertEquals(original.inventario(), leido.inventario());
        assertEquals(original.efectos(), leido.efectos());    }
}
