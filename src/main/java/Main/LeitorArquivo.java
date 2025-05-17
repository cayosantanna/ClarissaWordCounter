package Main; 
import java.io.*; 
public class LeitorArquivo { 
    
   
    public static String ler(String caminho) throws IOException { 
        System.out.println("Lendo arquivo: " + caminho);

        /*concatena strings sem criar novos objetos desnecessários na memoria*/
        StringBuilder conteudo = new StringBuilder(); 

        try (BufferedReader leitor = new BufferedReader(new FileReader(caminho))) { 

            String linha; 
            
            while ((linha = leitor.readLine()) != null) {
                /*Espaço para evitar que palavras de linhas adjacentes se juntem*/
                conteudo.append(linha).append(" ");
            }
        }
        
        System.out.println("Arquivo carregado com sucesso!");
        
        /*Converte o StringBuilder para String e retorna o conteúdo completo*/
        return conteudo.toString();
    }
}
