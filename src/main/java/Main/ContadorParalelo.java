package Main; 

import java.util.*;
import java.util.concurrent.*;


public class ContadorParalelo {
    
    /**
     * @param numThreads Quantidade de threads que serão criadas
     * @return O ExecutorService (interface) que gerenciará as threads
     */

    public static ExecutorService iniciarProcesso(int numThreads) { 
        return Executors.newFixedThreadPool(numThreads);
    }
    
    /**
     * @param executor ExecutorService que será encerrado
     */
    public static void encerrarProcesso(ExecutorService executor) throws InterruptedException { //
        //não serão aceitas novas tarefas
        executor.shutdown();
        
        if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
            executor.shutdownNow();
        }
    }
    
    /**
     * @param palavras Array com todas as palavras do texto
     * @param conjunto Conjunto das palavras que devem ser contadas
     * @param executor ExecutorService já inicializado
     * @param numThreads Número de threads para dividir o trabalho
     */
    public static Map<String, Integer> contarPalavrasPreProcessadas(String[] palavras, Set<String> conjunto, 
            ExecutorService executor, int numThreads) throws InterruptedException, ExecutionException {
        Map<String, Integer> contagemFinal = new HashMap<>();
        
        for (String palavra : conjunto) {
            contagemFinal.put(palavra, 0);
        }
        
        int tamanho = palavras.length;
    
        int bloco = tamanho / numThreads; /*num palavras / num threads */
        
        /* Lista para armazenar os resultados futuros de cada thread */
        List<Future<Map<String, Integer>>> resultados = new ArrayList<>();
        
        for (int i = 0; i < numThreads; i++) { 
            int inicio = i * bloco;

            int fim;
            if (i == numThreads - 1) {
                fim = tamanho; 
            } else {
                fim = (i + 1) * bloco; 
            }
            
            // Submete uma tarefa para o executor que define o trabalho que cada thread irá executar
            resultados.add(executor.submit(() -> { 
                Map<String, Integer> parcial = new HashMap<>();
                
                
                for (String palavra : conjunto) parcial.put(palavra, 0);
                
                for (int j = inicio; j < fim; j++) {
                    String p = palavras[j];  //passa pra p palavra atual do array palavras 
                    if (conjunto.contains(p)) {
                        parcial.put(p, parcial.get(p) + 1);
                    }
                }
                
                // Retorna o resultado parcial desta thread
                return parcial;
            }));
        }
        
        // Combina os resultados de todas as threads
        for (Future<Map<String, Integer>> futuro : resultados) {
            Map<String, Integer> parcial = futuro.get();
            
            
            for (String palavra : conjunto) { 
                contagemFinal.put(palavra, contagemFinal.get(palavra) + parcial.get(palavra)); 
                // Obtem a contagem atual da palavra no mapa final
            }
        }
        
        return contagemFinal;
    }
}
