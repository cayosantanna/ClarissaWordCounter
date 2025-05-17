package Main; 

public class ProcessadorTexto {
    
    /* Utilizamos regex para fazer o pré-processamento do texto */
    public static String[] preprocessarTexto(String texto) {

        String textoProcessado = texto.toLowerCase();
        
        /*Remove possessivos do inglês */
        textoProcessado = textoProcessado.replaceAll("'s\\b", " ");
        
        /* Remove pontuação, números e caracteres especiais*/
        textoProcessado = textoProcessado.replaceAll("[^a-z ]", " ");
        
        /* Substitui sequências de espaços por um único espaço*/
        textoProcessado = textoProcessado.replaceAll("\\s+", " ").trim();
        
        /*Divide o texto em um array de palavras usando o espaço como separador */
        return textoProcessado.split("\\s+");
    }
}