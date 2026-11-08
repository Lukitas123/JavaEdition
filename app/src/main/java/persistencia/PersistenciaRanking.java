package persistencia;

import modelo.EntradaDeRanking;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;

//Lee, carga y guarda las entradas del ranking
public class PersistenciaRanking {
    /**Lee el archivo json con las entradas en formato string*/
    public static String leerRanking(String ruta){
        Path rutaEntradas=Path.of(ruta);
        if (!Files.exists(rutaEntradas)){
            return "[]";
        }
        try{
            return Files.readString(rutaEntradas);
        }
        catch (IOException e){
            throw new IllegalArgumentException("No se pudo leer: " + ruta + 
            ".  Asegúrese de que el archivo .json se encuentre en la misma carpeta que el programa" , e);
        }
    }
    /**Carga el string con las entradas y genera un array*/
    public static EntradaDeRanking[] cargarRanking(String ruta){
        String json=leerRanking(ruta);
        Gson gson=new Gson();
        EntradaDeRanking[] entradas=gson.fromJson(json, EntradaDeRanking[].class);
        if (entradas==null){
            return new EntradaDeRanking[0];
        }
        return entradas;
    }
    /**Guarda una lista de entradas en un archivo (json)*/
    public static void guardarRanking(EntradaDeRanking[] entradas, String ruta){
        Gson gson = new Gson();
        String json=gson.toJson(entradas);

        try{
            Files.writeString(Path.of(ruta),json);
        } catch (IOException e) {
            throw new IllegalArgumentException("No se pudo guardar: " + ruta, e);
        }
    }
}
