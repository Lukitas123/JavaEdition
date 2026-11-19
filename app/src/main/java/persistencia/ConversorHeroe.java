package persistencia;

import modelo.Heroe;
import estructuras.Lista;

import java.util.HashMap;
import java.util.Map;

/**
 *Convierte un Heroe a un HeroeGuardado y viceversa
*/
public class ConversorHeroe {
    
    /**Copia el estado de un Heroe a un HeroeGuardado */
    public static HeroeGuardado aGuardado(Heroe heroe){
        Map<String,Integer> inventario = new HashMap<>(heroe.getInventario());

        Lista<String> listaEfectos = heroe.getListaEfectos();
        String[] efectos = new String[listaEfectos.cantidad()];
        
        for (int j = 0; j<efectos.length;j++){
            efectos[j] = listaEfectos.obtener(j);
        }

        return new HeroeGuardado(heroe.getEnergia(), heroe.getUbicacion(), heroe.getCofresAbiertos(), inventario, efectos);

    }
    /**Crea un heroe a partir de un HeroeGuardado*/
    public static Heroe aHeroe(HeroeGuardado guardado){
        Heroe heroe = new Heroe(guardado.energia(), guardado.ubicacion());

        for (Map.Entry<String, Integer> item : guardado.inventario().entrySet()) {
            for (int i = 0; i < item.getValue(); i++) {
                heroe.agregarObjeto(item.getKey());
            }
        }

        for (int i = 0; i < guardado.efectos().length; i++) {
            heroe.activarEfecto(guardado.efectos()[i]);
        }

        for (int i=0; i < guardado.cofresAbiertos(); i++) {
            heroe.registrarCofreAbierto();
        }

        return heroe;
    } 
}
