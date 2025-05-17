package Main; 

import java.util.*; 
import java.util.concurrent.*;

public class ContadorSequencial {
    
    public static ExecutorService iniciarProcesso() {
        return Executors.newSingleThreadExecutor();
    }
    

    public static void encerrarProcesso(ExecutorService executor) throws InterruptedException {
        executor.shutdown();
        
        if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
            executor.shutdownNow();
        }
    }
    
    /**
     * @param palavras Array contendo todas as palavras do texto, já pré-processadas
     * @param conjunto Conjunto das palavras específicas que devem ser contadas
     */
    public static Map<String, Integer> contarPalavrasPreProcessadas(String[] palavras, Set<String> conjunto) {
        Map<String, Integer> contagem = new HashMap<>();
        
        /* palavras que não aparecem no texto também estejam no resultado com resultado 0*/
        for (String palavra : conjunto) {
            contagem.put(palavra, 0);
        }
        
        for (String p : palavras) {
            if (conjunto.contains(p)) {
                contagem.put(p, contagem.get(p) + 1); 
            }
        }
        
        return contagem;
    }
}
