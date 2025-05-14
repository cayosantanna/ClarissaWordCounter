package processo;

import java.util.Map;
import contadores.ContadorSequencial;

public class ProcessoSequencial {
    public static Map<String, Integer> executar(String texto, String[] palavras) {
        // Início do processo (não conta no tempo)
        long inicio = System.nanoTime();
        Map<String, Integer> resultado = ContadorSequencial.contarPalavras(texto, palavras);
        long fim = System.nanoTime();
        // Fim do processo (não conta no tempo)

        double tempoEmMilissegundos = (fim - inicio) / 1_000_000.0;

        return resultado;
    }
}

