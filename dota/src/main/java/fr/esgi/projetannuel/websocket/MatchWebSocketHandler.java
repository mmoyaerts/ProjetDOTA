package fr.esgi.projetannuel.websocket;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MatchWebSocketHandler extends TextWebSocketHandler {
    private Process pythonProcess;
    private Thread readerThread;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String query = session.getUri().getQuery();
        String scriptName;
        String accountId = "";

        if (query != null) {
            Map<String, String> params = Arrays.stream(query.split("&"))
                    .map(s -> s.split("=", 2))
                    .filter(kv -> kv.length == 2)
                    .collect(Collectors.toMap(kv -> kv[0], kv -> kv[1]));

            scriptName = params.getOrDefault("script", "test.py");

            if (params.containsKey("accountId")) {
                accountId = params.get("accountId");
            }
        } else {
            scriptName = "test.py";
        }
        System.out.println("Démarrage du script : " + scriptName);

        Path scriptPath = Paths.get("python", scriptName).toAbsolutePath();

        // Construction de la commande
        List<String> command = new ArrayList<>();
        command.add("python");
        command.add(scriptPath.toString());
        if (accountId != null) {
            command.add(accountId);
        }

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        pythonProcess = pb.start();

        readerThread = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(pythonProcess.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null && session.isOpen()) {
                    System.out.println("📨 [" + scriptName + "] " + line);
                    session.sendMessage(new TextMessage(line));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        readerThread.start();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        if (pythonProcess != null && pythonProcess.isAlive()) {
            pythonProcess.destroy();
        }
        if (readerThread != null && readerThread.isAlive()) {
            readerThread.interrupt();
        }
    }
}

