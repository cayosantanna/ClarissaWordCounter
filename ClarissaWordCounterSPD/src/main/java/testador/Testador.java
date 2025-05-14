package testador;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.*;
import leitor.LeitorDeTexto;
import contadores.ContadorSequencial;
import contadores.ContadorParalelo;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;

public class Testador {
    private static final int TOTAL_EXECUCOES = 30;
    private static final int WARMUP = 5;

    public static void testar(String texto, String[] palavras, boolean paralelo, int numThreads, String nomeArquivo) throws Exception {
        List<Double> tempos = new ArrayList<>();

        // Warm-up (descartado)
        for (int i = 0; i < WARMUP; i++) {
            if (paralelo) {
                ContadorParalelo.contarPalavras(texto, palavras, numThreads);
            } else {
                ContadorSequencial.contarPalavras(texto, palavras);
            }
        }

        // Execuções cronometradas
        for (int i = 0; i < TOTAL_EXECUCOES; i++) {
            long inicio = System.nanoTime();
            if (paralelo) {
                ContadorParalelo.contarPalavras(texto, palavras, numThreads);
            } else {
                ContadorSequencial.contarPalavras(texto, palavras);
            }
            long fim = System.nanoTime();
            tempos.add((fim - inicio) / 1_000_000.0);
        }

        double[] temposArray = tempos.stream().mapToDouble(Double::doubleValue).toArray();

        // Remover outliers (IQR)
        Percentile percentile = new Percentile();
        percentile.setData(temposArray);
        double q1 = percentile.evaluate(25);
        double q3 = percentile.evaluate(75);
        double iqr = q3 - q1;
        double limiteInferior = q1 - 1.5 * iqr;
        double limiteSuperior = q3 + 1.5 * iqr;

        List<Double> filtrados = new ArrayList<>();
        for (double t : temposArray) {
            if (t >= limiteInferior && t <= limiteSuperior) {
                filtrados.add(t);
            }
        }

        double[] filtradosArray = filtrados.stream().mapToDouble(Double::doubleValue).toArray();
        double media = Arrays.stream(filtradosArray).average().orElse(0);
        double desvioPadrao = new StandardDeviation().evaluate(filtradosArray);

        try (PrintWriter out = new PrintWriter(new FileWriter(nomeArquivo))) {
            out.println("Execução;Tempo (ms)");
            for (int i = 0; i < tempos.size(); i++) {
                out.printf("%d;%.3f\n", i + 1, tempos.get(i));
            }
            out.printf("\nTempo médio (sem warm-up e outliers);%.3f\n", media);
            out.printf("Desvio padrão;%.3f\n", desvioPadrao);
        }

        System.out.printf("Tempo médio (%s): %.3f ms | Desvio padrão: %.3f ms\n",
                paralelo ? "paralelo - " + numThreads + " threads" : "sequencial", media, desvioPadrao);
    }

    public static void main(String[] args) throws Exception {
        // Leitura do texto (setup)
        String texto = LeitorDeTexto.lerArquivo("arquivo/Clarissa_Harlowe.txt");

        // Conjuntos de palavras
        String[] conjunto1 = {"clarissa", "letter", "lovelace", "virtue", "dear", "miss"};
        String[] conjunto2 = {"eita", "bacana", "vixe", "forbidden", "indignation", "oppression"};

        // Testes para cada conjunto
        System.out.println("==== Testando conjunto 1 ====");
        testar(texto, conjunto1, false, 0, "dados_sequencial_conjunto1.csv");
        testar(texto, conjunto1, true, 2, "dados_paralelo2_conjunto1.csv");
        testar(texto, conjunto1, true, 4, "dados_paralelo4_conjunto1.csv");
        testar(texto, conjunto1, true, 8, "dados_paralelo8_conjunto1.csv");

        System.out.println("\n==== Testando conjunto 2 ====");
        testar(texto, conjunto2, false, 0, "dados_sequencial_conjunto2.csv");
        testar(texto, conjunto2, true, 2, "dados_paralelo2_conjunto2.csv");
        testar(texto, conjunto2, true, 4, "dados_paralelo4_conjunto2.csv");
        testar(texto, conjunto2, true, 8, "dados_paralelo8_conjunto2.csv");
    }
}