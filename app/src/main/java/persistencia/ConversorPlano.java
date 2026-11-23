package persistencia;

import entidades.cofres.Cofre;
import entidades.monstruos.Monstruo;
import estructuras.Lista;
import modelo.Costo;
import modelo.Plano;
import modelo.Posicion;
import modelo.Terreno;

import java.util.HashMap;
import java.util.Map;

/**
 *Convierte un Plano a un PlanoGuardado y viceversa
*/
public class ConversorPlano {
    /** Copia terreno, celdas visitadas, posicion de entrada y salida, monstruos y cofres */
    public static PlanoGuardado aGuardado(Plano plano){
        Terreno[][] terreno = new Terreno[plano.getFilas()][plano.getColumnas()];
        boolean[][] visitadas = new boolean[plano.getFilas()][plano.getColumnas()];
        boolean[][] cubiertasPorPuente = new boolean[plano.getFilas()][plano.getColumnas()];

        for (int fila = 0; fila < plano.getFilas(); fila++){
            for (int colum = 0; colum < plano.getColumnas(); colum++){
                Posicion pos = new Posicion(fila + 1,colum + 1);
                terreno[fila][colum] = plano.terrenoBaseEn(pos);
                visitadas[fila][colum]=plano.estaVisitada(pos);
                cubiertasPorPuente[fila][colum] = plano.estaCubiertaPorPuente(pos);
            }
        }
          
        Lista<Monstruo> listaMonstruos = plano.getMonstruos();
        MonstruoGuardado[] monstruos = new MonstruoGuardado[listaMonstruos.cantidad()];

        Lista<Cofre> listaCofres = plano.getCofres();
        CofreGuardado[] cofres = new CofreGuardado[listaCofres.cantidad()];

        for (int i = 0; i < listaMonstruos.cantidad();i++){
            monstruos[i] = ConversorEntidades.monstruoAGuardado(listaMonstruos.obtener(i));
        }
        for (int i = 0; i < listaCofres.cantidad();i++){
            cofres[i] = ConversorEntidades.cofreAGuardado(listaCofres.obtener(i));
        }
        Map<Posicion, Integer> vencimientos = plano.getVencimientosLava();
        LavaGuardada[] lavaActiva = new LavaGuardada[vencimientos.size()];
        int indice = 0;

        for (Map.Entry<Posicion, Integer> entrada : vencimientos.entrySet()) {
            lavaActiva[indice++] = new LavaGuardada(
                    entrada.getKey(), entrada.getValue());
        }

        return new PlanoGuardado(terreno, visitadas, plano.getEntrada(), plano.getSalida(), monstruos, cofres, plano.getFinSuenoMonstruos(),  plano.getFinPuente(), cubiertasPorPuente, lavaActiva);
    }

    /** Crea un Plano a partir deun PlanoGuardado */
    public static Plano aPlano(PlanoGuardado guardado){
        Plano plano = new Plano(guardado.terreno(), guardado.entrada(), guardado.salida());

        Map<Posicion, Integer> lava = new HashMap<>();

        if (guardado.lavaActiva() != null) {
            for (LavaGuardada entrada : guardado.lavaActiva()) {
                if (entrada == null || entrada.posicion() == null
                        || entrada.vencimiento() == null) {
                    throw new IllegalArgumentException("Lava guardada invalida");
                }
                if (lava.putIfAbsent(entrada.posicion(), entrada.vencimiento()) != null) {
                    throw new IllegalArgumentException("Posicion de lava duplicada");
                }
            }
        }
        plano.restaurarPuente(guardado.finPuente(), guardado.cubiertasPorPuente());
        plano.restaurarLava(lava);
        plano.restaurarFinSuenoMonstruos(guardado.finSuenoMonstruos());

        for (int fila = 0; fila < guardado.visitadas().length; fila++){
            for (int colum = 0; colum < guardado.visitadas()[fila].length; colum++){
                if(guardado.visitadas()[fila][colum]){
                    plano.marcarVisitada(new Posicion(fila + 1 ,colum + 1));
                }
            }
        }
        return plano;
    }

    public static void agregarEntidades(Plano plano, PlanoGuardado guardado, Costo costos){
        if (guardado.monstruos()!=null){
            for (MonstruoGuardado m: guardado.monstruos()){
                Monstruo monstruo = ConversorEntidades.aMonstruo(m, costos);
                
                plano.agregarMonstruo(monstruo);
                monstruo.restaurarPosicion(m.posicion());
            }
        }
        if (guardado.cofres()!=null){
            for (CofreGuardado c: guardado.cofres()){
                plano.agregarCofre(ConversorEntidades.aCofre(c));
            }
        }
    }
}
