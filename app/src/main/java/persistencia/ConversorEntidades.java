package persistencia;

import entidades.Catalogo;
import entidades.cofres.Cofre;
import entidades.monstruos.Monstruo;
import modelo.Costo;

/** Convierte monstruos y cofres en MonstruoGuardado y CofreGuardado y viceversa */
public class ConversorEntidades {

    /** Copia el estado de un monstruo */
    public static MonstruoGuardado monstruoAGuardado(Monstruo monstruo){
        return new MonstruoGuardado(monstruo.getTipo(), monstruo.getPosicion(),
        monstruo.getPosicionInicial(), monstruo.estaActivo(), monstruo.exportarEstado());
    }

    /** Recrea el monstruo en su posicion inicial, con su estado activo y su estado propio */
    public static Monstruo aMonstruo(MonstruoGuardado guardado, Costo costos){
        Monstruo monstruo = CrearEntidades.crearMonstruo(guardado.tipo(), guardado.posicionInicial(), costos);
        restaurarEstado(guardado, monstruo);
        return monstruo;
    }

    public static Monstruo aMonstruo(MonstruoGuardado guardado, Catalogo<Monstruo> catalogo) {
        Monstruo monstruo = catalogo.crear(guardado.tipo(), guardado.posicionInicial());
        restaurarEstado(guardado, monstruo);
        return monstruo;
    }

    private static void restaurarEstado(MonstruoGuardado guardado, Monstruo monstruo) {
        if (guardado.activo()){
            monstruo.activar();
        } 
        else{
            monstruo.desactivar();
        }
        monstruo.importarEstado(guardado.estado());
    }

    /** Copia el tipo y la posición de un cofre. */
    public static CofreGuardado cofreAGuardado(Cofre cofre) {
        return new CofreGuardado(cofre.getTipo(), cofre.getPosicion(), cofre.getDanio());
    }

    /** Recrea un cofre cerrado. */
    public static Cofre aCofre(CofreGuardado guardado, Catalogo<Cofre> catalogo){
        return catalogo.crear(guardado.tipo(), guardado.posicion());
    }

    public static Cofre aCofre(CofreGuardado guardado) {
        return CrearEntidades.crearCofre(
                guardado.tipo(), guardado.posicion(), guardado.danio());
    }
}
