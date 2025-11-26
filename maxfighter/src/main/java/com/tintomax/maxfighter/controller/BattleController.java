package com.tintomax.maxfighter.controller;

import com.tintomax.maxfighter.model.Fighter;
import com.tintomax.maxfighter.service.BattleService;
import com.tintomax.maxfighter.service.GeminiAiService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/battle")
public class BattleController {

    private final BattleService battleService;
    private final GeminiAiService geminiAiService;

    public BattleController(BattleService battleService, GeminiAiService geminiAiService) {
        this.battleService = battleService;
        this.geminiAiService = geminiAiService;
    }

    // ==============
    // TELA DE SELEÇÃO  -> start.html
    // ==============

    @GetMapping("/select")
    public String selectFighter(Model model) {
        model.addAttribute("fighters", battleService.getAllBaseFighters());
        // agora carrega o template start.html
        return "start";
    }

    @PostMapping("/select")
    public String doSelectFighter(@RequestParam String playerName) {
        battleService.startBattleWithPlayer(playerName);
        // depois da seleção, vai para a tela de game
        return "redirect:/battle/game";
    }

    // ==============
    // TELA DE BATALHA -> game.html
    // ==============

    @GetMapping("/game")
    public String battleUI(Model model) {
        Fighter player = battleService.getPlayer();
        Fighter ai = battleService.getAi();

        model.addAttribute("player", player);
        model.addAttribute("ai", ai);
        model.addAttribute("log", battleService.getLog());

        // agora usa o template game.html
        return "game";
    }

    @PostMapping("/start")
    public String startBattle() {
        battleService.startBattle();
        return "redirect:/battle/game";
    }

    // ==============
    // TURNO (PLAYER + IA)
    // ==============

    @PostMapping("/turn/{moveIndex}")
    public String playerAndAiTurn(@PathVariable int moveIndex) {
        Fighter player = battleService.getPlayer();
        Fighter ai = battleService.getAi();

        if (player == null || ai == null) {
            return "redirect:/battle/select";
        }

        battleService.getLog().add("\n===== NOVO TURNO =====");

        // 1) Aplica veneno no começo do turno (player e IA)
        battleService.applyPoisonAtTurnStart(player);
        battleService.applyPoisonAtTurnStart(ai);

        // Alguém morreu só por veneno?
        if (player.getCurrentHp() <= 0 && ai.getCurrentHp() <= 0) {
            battleService.getLog().add("Ambos caíram pelo veneno! Empate trágico!");
            return "redirect:/battle/game";
        }
        if (player.getCurrentHp() <= 0) {
            battleService.getLog().add("Você foi derrotado pelo veneno!");
            return "redirect:/battle/game";
        }
        if (ai.getCurrentHp() <= 0) {
            battleService.getLog().add("O oponente foi derrotado pelo veneno!");
            return "redirect:/battle/game";
        }

        // 2) Define quem ataca primeiro pela SPEED
        // Regra: se speed igual, prioridade do player
        boolean playerFirst = player.getSpeed() >= ai.getSpeed();

        if (playerFirst) {
            // --------- PLAYER ATACA PRIMEIRO ----------
            battleService.getLog().add("\n=== TURNO DO PLAYER ===");
            battleService.executeMove(player, ai, moveIndex);

            if (ai.getCurrentHp() <= 0) {
                battleService.getLog().add("Oponente derrotado!");
                return "redirect:/battle/game";
            }

            // --------- IA ATACA DEPOIS ----------
            battleService.getLog().add("\n=== TURNO DA IA ===");
            String aiAnswer = geminiAiService.escolherGolpe(ai, player);
            int aiMoveIndex = findMoveIndexFromAiAnswer(ai, aiAnswer);
            battleService.executeMove(ai, player, aiMoveIndex);

            if (player.getCurrentHp() <= 0) {
                battleService.getLog().add("Você foi derrotado!");
            }

        } else {
            // --------- IA ATACA PRIMEIRO ----------
            battleService.getLog().add("\n=== TURNO DA IA ===");
            String aiAnswer = geminiAiService.escolherGolpe(ai, player);
            int aiMoveIndex = findMoveIndexFromAiAnswer(ai, aiAnswer);
            battleService.executeMove(ai, player, aiMoveIndex);

            if (player.getCurrentHp() <= 0) {
                battleService.getLog().add("Você foi derrotado!");
                return "redirect:/battle/game";
            }

            // --------- PLAYER ATACA DEPOIS ----------
            battleService.getLog().add("\n=== TURNO DO PLAYER ===");
            battleService.executeMove(player, ai, moveIndex);

            if (ai.getCurrentHp() <= 0) {
                battleService.getLog().add("Oponente derrotado!");
            }
        }

        return "redirect:/battle/game";
    }

    /**
     * Interpreta a resposta do Gemini e transforma em índice de golpe.
     */
    private int findMoveIndexFromAiAnswer(Fighter ai, String answer) {
        if (answer == null) {
            return 0;
        }

        String normalized = answer.toLowerCase().trim();
        System.out.println("Resposta da IA (normalizada): " + normalized);

        // 1. match exato com o nome do golpe
        for (int i = 0; i < ai.getMoves().size(); i++) {
            String moveName = ai.getMoves().get(i).getName();
            if (moveName.equalsIgnoreCase(answer.trim())) {
                System.out.println("Match exato com: " + moveName);
                return i;
            }
        }

        // 2. substring do nome do golpe dentro da resposta
        for (int i = 0; i < ai.getMoves().size(); i++) {
            String moveName = ai.getMoves().get(i).getName().toLowerCase();
            if (normalized.contains(moveName)) {
                System.out.println("Match parcial com: " + moveName);
                return i;
            }
        }

        // 3. números (1, 2, 3, 4)
        if (normalized.contains("1")) return 0;
        if (normalized.contains("2")) return 1;
        if (normalized.contains("3")) return 2;
        if (normalized.contains("4")) return 3;

        // 4. fallback
        System.out.println("Nenhum golpe reconhecido. Usando índice 0.");
        return 0;
    }
}