package entidades.objetos;

import java.util.HashMap;
import java.util.Map;

/**
 * Relaciona los id de los objetos con su comportamiento
 */
public class CatalogoObjetos {
    private final Map<String, Objeto> objetos = new HashMap<>();

    /**
     * Registra un objeto con un id unico
     * Pre: el id no es null ni vacio
     * Post: el objeto queda disponible para buscar por id
     */
    public void registrar(String id, Objeto objeto){
        if(id== null || id.isBlank() || objeto == null){
            throw new IllegalArgumentException("id y Objeto son obligatorios");
        }
        if(objetos.containsKey(id)){
            throw new IllegalArgumentException("El objeto ya esta registrado: " + id);
        }
        objetos.put(id,objeto);
    }

    /**
     * Busca un objeto resgistrado
     * @throws IllegalArgumentException si el id no esta registrado
     */
    public Objeto obtener(String id){
        Objeto objeto = objetos.get(id);
        if(objeto == null){
            throw new IllegalArgumentException("Objeto no registrado: " + id);
        }
        return objeto;
    }
}
