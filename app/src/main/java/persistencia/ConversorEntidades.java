package persistencia;

import entidades.Catalogo;
import entidades.cofres.Cofre;
import entidades.monstruos.Monstruo;

/** Convierte monstruos y cofres en MonstruoGuardado y CofreGuardado y viceversa */
public class ConversorEntidades {

    /** Copia el estado de un monstruo */
    public static MonstruoGuardado monstruoAGuardado(Monstruo monstruo){
        return new MonstruoGuardado(monstruo.getTipo(), monstruo.getPosicion(),
        monstruo.getPosicionInicial(), monstruo.estaActivo(), monstruo.exportarEstado());
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
        Cofre cofre = catalogo.crear(guardado.tipo(), guardado.posicion());

        if (cofre.getDanio() != guardado.danio()) {
            throw new IllegalArgumentException(
                    "El daño del catálogo no coincide con la partida guardada");
        }

        return cofre;
    }

}
