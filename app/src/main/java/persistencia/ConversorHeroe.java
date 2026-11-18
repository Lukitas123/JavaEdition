package persistencia;

import modelo.Heroe;
import estructuras.Lista;

/**
 *Convierte un Heroe a un HeroeGuardado y viceversa
*/
public class ConversorHeroe {
    
    /**Copia el estado de un Heroe a un HeroeGuardado */
    public static HeroeGuardado aGuardado(Heroe heroe){
        String[] inventario = new String[heroe.cantidadDeObjetos()];

        Lista<String> listaEfectos = heroe.getListaEfectos();
        String[] efectos = new String[listaEfectos.cantidad()];
        
        for (int i = 0; i<inventario.length;i++){
            inventario[i] = heroe.objetoEn(i);
        }

        for (int j = 0; j<efectos.length;j++){
            efectos[j] = listaEfectos.obtener(j);
        }

        return new HeroeGuardado(heroe.getEnergia(), heroe.getUbicacion(), heroe.getCofresAbiertos(), inventario, efectos);

    }
    /**Crea un heroe a partir de un HeroeGuardado*/
    public static Heroe aHeroe(HeroeGuardado guardado){
        Heroe heroe = new Heroe(guardado.energia(), guardado.ubicacion());

        for (int i = 0; i<guardado.inventario().length; i++){
            heroe.agregarObjeto(guardado.inventario()[i]);
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
