package mv.sdd.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// Lecture du fichier d'actions
public class ActionFileReader {
    public static List<Action> readActions(String filePath) throws IOException {
        List<Action> actions = new ArrayList<>();

        // TODO : Ajouter le code qui permet de lire et parser un fichier d'actions
        Path path = Paths.get(filePath);
        List<String> lines = Files.readAllLines(path);
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                actions.add(ActionParser.parseLigne(line));
            }
        }

        return actions;
    }
}
