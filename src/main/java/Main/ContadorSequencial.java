package Main; 

import java.util.*; 
public class ContadorSequencial {
    
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
