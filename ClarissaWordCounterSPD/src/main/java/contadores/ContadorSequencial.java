package contadores;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ContadorSequencial {
    // Conta as palavras de forma sequencial
    public static Map<String, Integer> contarPalavras(String texto, String[] palavras) {
        Map<String, Integer> contagem = new HashMap<>();
        for (String palavra : palavras) {
            contagem.put(palavra.toLowerCase(), 0);
        }

        String[] palavrasNoTexto = texto.toLowerCase(Locale.ROOT).split("\\W+");

        for (String p : palavrasNoTexto) {
            for (String palavra : palavras) {
                if (p.equals(palavra.toLowerCase())) {
                    contagem.put(palavra.toLowerCase(), contagem.get(palavra.toLowerCase()) + 1);
                }
            }
        }

        return contagem;
    }
}
