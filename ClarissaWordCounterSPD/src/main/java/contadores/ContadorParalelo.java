package contadores;

import java.util.*;
import java.util.concurrent.*;

public class ContadorParalelo {

    public static Map<String, Integer> contarPalavras(String texto, String[] palavras, int numThreads) throws InterruptedException, ExecutionException {
        Map<String, Integer> contagemFinal = new ConcurrentHashMap<>();
        for (String palavra : palavras) {
            contagemFinal.put(palavra.toLowerCase(), 0);
        }

        String[] palavrasNoTexto = texto.toLowerCase(Locale.ROOT).split("\\W+");
        int tamanho = palavrasNoTexto.length;
        int bloco = tamanho / numThreads;

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<Map<String, Integer>>> resultados = new ArrayList<>();

        for (int i = 0; i < numThreads; i++) {
            int inicio = i * bloco;
            int fim = (i == numThreads - 1) ? tamanho : (i + 1) * bloco; // último bloco vai até o fim
            String[] sublista = Arrays.copyOfRange(palavrasNoTexto, inicio, fim);

            Callable<Map<String, Integer>> tarefa = () -> {
                Map<String, Integer> parcial = new HashMap<>();
                for (String palavra : palavras) {
                    parcial.put(palavra.toLowerCase(), 0);
                }
                for (String p : sublista) {
                    for (String palavra : palavras) {
                        if (p.equals(palavra.toLowerCase())) {
                            parcial.put(palavra.toLowerCase(), parcial.get(palavra.toLowerCase()) + 1);
                        }
                    }
                }
                return parcial;
            };

            resultados.add(executor.submit(tarefa));
        }

        for (Future<Map<String, Integer>> futuro : resultados) {
            Map<String, Integer> parcial = futuro.get();
            for (String palavra : parcial.keySet()) {
                contagemFinal.put(palavra, contagemFinal.get(palavra) + parcial.get(palavra));
            }
        }

        executor.shutdown();
        return contagemFinal;
    }
}

