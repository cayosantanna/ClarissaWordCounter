/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package Main;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import leitor.LeitorDeTexto;
import processo.ProcessoParalelo;
import processo.ProcessoSequencial;

public class ClarissaWordCounterSPD {
    public static void main(String[] args) throws Exception {
        // Palavras a serem buscadas (dois conjuntos)
        String[] palavras1 = {"clarissa", "letter", "lovelace", "virtue", "dear", "miss"};
        String[] palavras2 = {"eita", "bacana", "vixe", "forbidden", "indignation", "oppression"};
        int[] threads = {2, 4, 8};
        int rodadas = 30;

        String caminhoArquivo = "C:\\Users\\famil\\OneDrive\\Documentos\\NetBeansProjects\\ClarissaWordCounter - Paralelo x Sequencial\\arquivo\\Clarissa_Harlowe.txt";
        String texto = LeitorDeTexto.lerArquivo(caminhoArquivo);

        List<String[]> conjuntos = Arrays.asList(palavras1, palavras2);

        // Contagem de palavras
        for (int idx = 0; idx < conjuntos.size(); idx++) {
            String[] palavras = conjuntos.get(idx);
            System.out.println();
            System.out.print("? CONJUNTO DE PALAVRAS " + (idx + 1) + ": ");
            System.out.println(String.join(", ", palavras));

            // Mostra contagem de cada palavra
            Map<String, Integer> contagem = ProcessoSequencial.executar(texto, palavras);
            for (String palavra : palavras) {
                System.out.printf("Palavras: %s %d\n", palavra, contagem.getOrDefault(palavra.toLowerCase(), 0));
            }

            // Performance SEQUENCIAL - sem imprimir tempo a cada rodada
            double[] temposSeq = new double[rodadas];
            for (int i = 0; i < rodadas; i++) {
                long ini = System.nanoTime();
                ProcessoSequencial.executar(texto, palavras);
                long fim = System.nanoTime();
                temposSeq[i] = (fim - ini) / 1000.0; // microssegundos
            }
            double mediaSeq = media(temposSeq);
            double stdSeq = std(temposSeq, mediaSeq);

            System.out.println("============================================== RESULTADOS DE PERFORMANCE ===============================================");
            System.out.printf("│ SEQUENCIAL    │ Tempo médio: %8.2f µs │ Desvio padrão: %8.2f µs │\n", mediaSeq, stdSeq);

            // Performance PARALELO
            System.out.println("├───────────────┼─────────────────────────┼──────────────────────────┼───────────────────┼──────────────────────────────┤");
            System.out.println("│   PARALELO    │       TEMPO MÉDIO       │      DESVIO PADRÃO       │      SPEEDUP      │          EFICIÊNCIA          │");
            System.out.println("├───────────────┼─────────────────────────┼──────────────────────────┼───────────────────┼──────────────────────────────┤");
            for (int nThreads : threads) {
                double[] temposPar = new double[rodadas];
                for (int i = 0; i < rodadas; i++) {
                    long ini = System.nanoTime();
                    ProcessoParalelo.executar(texto, palavras, nThreads);
                    long fim = System.nanoTime();
                    temposPar[i] = (fim - ini) / 1000.0; // microssegundos
                }
                double mediaPar = media(temposPar);
                double stdPar = std(temposPar, mediaPar);
                double speedup = mediaSeq / mediaPar;
                double eficiencia = (speedup / nThreads) * 100.0;
                System.out.printf("│ %2d Threads    │ %8.2f µs             │ %8.2f µs              │ %5.2fx           │ %6.2f%%                     │\n",
                        nThreads, mediaPar, stdPar, speedup, eficiencia);
            }
            System.out.println("└───────────────┴─────────────────────────┴──────────────────────────┴───────────────────┴──────────────────────────────┘");
        }
    }

    // Função para média
    private static double media(double[] v) {
        double s = 0;
        for (double d : v) s += d;
        return s / v.length;
    }

    // Função para desvio padrão
    private static double std(double[] v, double m) {
        double s = 0;
        for (double d : v) s += (d - m) * (d - m);
        return Math.sqrt(s / v.length);
    }
}


