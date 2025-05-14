package processo;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import contadores.ContadorParalelo;

public class ProcessoParalelo {
    public static Map<String, Integer> executar(String texto, String[] palavras, int numThreads) {
        Map<String, Integer> resultado = null;
        try {
            long inicio = System.nanoTime();
            resultado = ContadorParalelo.contarPalavras(texto, palavras, numThreads);
            long fim = System.nanoTime();

            double tempoEmMilissegundos = (fim - inicio) / 1_000_000.0;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return resultado;
    }
}