package com.tintomax.maxfighter.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tintomax.maxfighter.model.Fighter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;

@Service
public class GeminiAiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    /**
     * Exemplo recomendado no application.properties:
     *
     * gemini.api.url=https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent
     *
     * (sem o "-latest")
     */
    @Value("${gemini.api.url}")
    private String apiUrl;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private final Random random = new Random();

    public String escolherGolpe(Fighter aiFighter, Fighter playerFighter) {
        try {
            String prompt = montarPrompt(aiFighter, playerFighter);

            // Corpo no formato esperado pela API Gemini (v1beta generateContent)
            String requestBody = """
            {
              "contents": [
                {
                  "parts": [
                    { "text": %s }
                  ]
                }
              ]
            }
            """.formatted(mapper.writeValueAsString(prompt));

            // Monta request para a URL configurada + key via query param
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl + "?key=" + apiKey))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("=== RESPOSTA GEMINI ===");
            System.out.println("URL chamada: " + apiUrl);
            System.out.println("Status: " + response.statusCode());
            System.out.println("Body: " + response.body());
            System.out.println("=======================");

            // Se não vier 200, já cai no fallback
            if (response.statusCode() != 200) {
                System.out.println("Gemini retornou status != 200. Usando golpe aleatório.");
                return pickRandomMove(aiFighter);
            }

            JsonNode root = mapper.readTree(response.body());
            JsonNode candidates = root.path("candidates");

            if (!candidates.isArray() || candidates.isEmpty()) {
                System.out.println("Resposta do Gemini não possui 'candidates'. Usando golpe aleatório.");
                return pickRandomMove(aiFighter);
            }

            JsonNode firstCandidate = candidates.get(0);
            if (firstCandidate == null || firstCandidate.isNull()) {
                System.out.println("candidates[0] é nulo. Usando golpe aleatório.");
                return pickRandomMove(aiFighter);
            }

            JsonNode parts = firstCandidate.path("content").path("parts");
            if (!parts.isArray() || parts.isEmpty()) {
                System.out.println("content.parts está vazio. Usando golpe aleatório.");
                return pickRandomMove(aiFighter);
            }

            JsonNode firstPart = parts.get(0);
            if (firstPart == null || firstPart.isNull()) {
                System.out.println("parts[0] é nulo. Usando golpe aleatório.");
                return pickRandomMove(aiFighter);
            }

            String result = firstPart.path("text").asText().trim();
            if (result.isEmpty()) {
                System.out.println("Texto da resposta vazio. Usando golpe aleatório.");
                return pickRandomMove(aiFighter);
            }

            System.out.println("Gemini escolheu golpe (texto bruto): " + result);
            return result;

        } catch (Exception e) {
            System.out.println("Erro ao chamar/parsing Gemini. Usando golpe aleatório.");
            e.printStackTrace();
            return pickRandomMove(aiFighter);
        }
    }

    private String pickRandomMove(Fighter ai) {
        int idx = random.nextInt(ai.getMoves().size());
        String moveName = ai.getMoves().get(idx).getName();
        System.out.println("Golpe aleatório escolhido: " + moveName);
        return moveName;
    }

    private String montarPrompt(Fighter ai, Fighter player) {
        return """
                Você é a IA do jogo MAXFIGHTER (RPG de turnos).
                Escolha APENAS UM golpe.

                Regras:
                - Retorne SOMENTE o nome exato de um dos golpes.
                - Sem frases, sem explicações, sem nada além do nome.

                Seu personagem: %s (HP %d)
                Golpes disponíveis:
                1) %s
                2) %s
                3) %s
                4) %s

                Oponente: %s (HP %d)
                """
                .formatted(
                        ai.getName(), ai.getCurrentHp(),
                        ai.getMoves().get(0).getName(),
                        ai.getMoves().get(1).getName(),
                        ai.getMoves().get(2).getName(),
                        ai.getMoves().get(3).getName(),
                        player.getName(), player.getCurrentHp()
                );
    }
}
    