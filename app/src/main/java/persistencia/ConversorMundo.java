package persistencia;

import estructuras.Lista;
import modelo.Conexion;
import modelo.Mundo;
import entidades.Catalogo;
import entidades.monstruos.Monstruo;
import entidades.cofres.Cofre;

/** Convierte un Mundo en MundoGuardado y viceversa */
public class ConversorMundo {

    /** Copia todos los planos y las conexiones del mundo */
    public static MundoGuardado aGuardado(Mundo mundo){
        PlanoGuardado[][] planos = new PlanoGuardado[mundo.getTableros()][mundo.getPisos()];

        for (int i = 1; i <= mundo.getTableros(); i++){
            for (int j = 1; j <= mundo.getPisos(); j++){
                planos[i-1][j-1] = ConversorPlano.aGuardado(mundo.planoEn(i,j));
            }
        }

        Lista<Conexion> lista = mundo.getConexiones();
        Conexion[] conexiones = new Conexion[lista.cantidad()];
        for (int i = 0; i < conexiones.length; i++){
            conexiones[i] = lista.obtener(i);
        }
        return new MundoGuardado(planos, conexiones);
    }

    public static Mundo aMundo(MundoGuardado guardado, Catalogo<Monstruo> monstruos, Catalogo<Cofre> cofres) {
        int tableros = guardado.planos().length;
        int pisos = guardado.planos()[0].length;
        Mundo mundo = new Mundo(tableros, pisos);

        for (int i = 1; i <= tableros; i++) {
            for (int j = 1; j <= pisos; j++) {
                mundo.agregarPlano(i, j,
                        ConversorPlano.aPlano(guardado.planos()[i - 1][j - 1]));
            }
        }

        for (Conexion conexion : guardado.conexiones()) {
            mundo.agregarConexion(conexion);
        }

        for (int i = 1; i <= tableros; i++) {
            for (int j = 1; j <= pisos; j++) {
                ConversorPlano.agregarEntidades(
                        mundo.planoEn(i, j), guardado.planos()[i - 1][j - 1],
                        monstruos, cofres);
            }
        }

        return mundo;
    }
}
