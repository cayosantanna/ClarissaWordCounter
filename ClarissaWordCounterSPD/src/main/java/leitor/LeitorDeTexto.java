package leitor;

import java.nio.file.Files;
import java.nio.file.Paths;

public class LeitorDeTexto {
    // Apenas lê o arquivo e retorna o conteúdo como String (setup)
    public static String lerArquivo(String caminho) throws Exception {
        return new String(Files.readAllBytes(Paths.get(caminho)));
    }
}
