package persistencia;

import modelo.EfectoHeroe;
import modelo.Heroe;
import java.util.List;

import java.util.HashMap;
import java.util.Map;

/**
 *Convierte un Heroe a un HeroeGuardado y viceversa
*/
public class ConversorHeroe {
    
    /**Copia el estado de un Heroe a un HeroeGuardado */
    public static HeroeGuardado aGuardado(Heroe heroe){
        Map<String,Integer> inventario = new HashMap<>(heroe.getInventario());

        List<EfectoHeroe> listaEfectos = heroe.getListaEfectos();
        Map<EfectoHeroe, Integer> efectos = new HashMap<>();

        for (int i = 0; i < listaEfectos.size(); i++) {
            EfectoHeroe id = listaEfectos.get(i);
            efectos.put(id, heroe.vencimientoDe(id));
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

        for (Map.Entry<EfectoHeroe, Integer> efecto : guardado.efectos().entrySet()) {
            heroe.activarEfecto(efecto.getKey(), efecto.getValue());
        }

        for (int i=0; i < guardado.cofresAbiertos(); i++) {
            heroe.registrarCofreAbierto();
        }

        return heroe;
    } 
}
