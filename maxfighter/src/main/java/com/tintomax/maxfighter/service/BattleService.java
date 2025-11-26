package com.tintomax.maxfighter.service;

import com.tintomax.maxfighter.model.Fighter;
import com.tintomax.maxfighter.model.Move;
import com.tintomax.maxfighter.model.Move.MoveType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class BattleService {

    private Fighter player;
    private Fighter ai;
    private final List<String> log = new ArrayList<>();
    private final Random random = new Random();

    // ==========================
    // GETTERS BÁSICOS
    // ==========================

    public Fighter getPlayer() {
        return player;
    }

    public Fighter getAi() {
        return ai;
    }

    public List<String> getLog() {
        return log;
    }

    // ==========================
    // INÍCIO DE BATALHA
    // ==========================

    public void startBattle() {
        startBattleWithPlayer("Snormax");
    }

    public void startBattleWithPlayer(String playerName) {

        this.player = createFighterByName(playerName);

        List<String> allNames = new ArrayList<>(List.of(
                "Snormax",
                "Testbot",
                "TintaCruel",
                "Baldestoise",
                "Weepincel",
                "ColorFable",
                "Borrachauro",
                "CaterPrimer",
                "HitmonLixa"
        ));

        allNames.removeIf(name -> name.equalsIgnoreCase(playerName));

        String aiName = allNames.get(random.nextInt(allNames.size()));
        this.ai = createFighterByName(aiName);

        log.clear();
        log.add("🎮 Batalha iniciada!");
        log.add("Player escolheu " + player.getName() + " (HP " + player.getCurrentHp() + ", Speed " + player.getSpeed() + ")");
        log.add("IA escolheu " + ai.getName() + " (HP " + ai.getCurrentHp() + ", Speed " + ai.getSpeed() + ")");
    }

    // ==========================
    // VENENO CONTÍNUO
    // ==========================

    public void applyPoisonAtTurnStart(Fighter fighter) {
        if (fighter == null) return;

        if (fighter.getPoisonRemainingTurns() > 0) {

            int dmg = fighter.getPoisonDamagePerTurn();
            fighter.setCurrentHp(fighter.getCurrentHp() - dmg);

            fighter.setPoisonRemainingTurns(fighter.getPoisonRemainingTurns() - 1);

            log.add("☠️ " + fighter.getName() + " sofre " + dmg +
                    " de veneno. Turnos restantes: " + fighter.getPoisonRemainingTurns());
        }
    }

    // ==========================
    // EXECUÇÃO DE MOVES
    // ==========================

    public void executeMove(Fighter attacker, Fighter defender, int moveIndex) {

        if (attacker == null || defender == null) return;
        if (moveIndex < 0 || moveIndex >= attacker.getMoves().size()) return;

        Move move = attacker.getMoves().get(moveIndex);

        // Verifica usos
        if (!move.hasUsesLeft()) {
            log.add(attacker.getName() + " tentou usar " + move.getName() + ", mas está sem usos!");
            return;
        }

        move.consumeUse();

        log.add("[" + attacker.getName() + "] usou " + move.getName());

        // --- EFEITO PRINCIPAL ---
        switch (move.getType()) {

            case DAMAGE -> {
                int dmg = move.getPower() + attacker.getBonusDamage();
                defender.setCurrentHp(defender.getCurrentHp() - dmg);
                log.add("Causou " + dmg + " de dano em " + defender.getName() +
                        " (HP restante: " + defender.getCurrentHp() + ")");
            }

            case HEAL -> {
                int before = attacker.getCurrentHp();
                attacker.setCurrentHp(attacker.getCurrentHp() + move.getPower());
                int healed = attacker.getCurrentHp() - before;
                log.add("Regenerou +" + healed + " HP (agora: " + attacker.getCurrentHp() + ")");
            }

            case BUFF_SPEED -> {
                int before = attacker.getSpeed();
                attacker.setSpeed(attacker.getSpeed() + move.getPower());
                int gained = attacker.getSpeed() - before;
                log.add("Speed aumentou em +" + gained + " (atual: " + attacker.getSpeed() + ")");
            }

            case DEBUFF_SPEED -> {
                int before = defender.getSpeed();
                defender.setSpeed(defender.getSpeed() - move.getPower());
                int lost = before - defender.getSpeed();
                log.add("Speed do inimigo diminuiu -" + lost +
                        " (atual: " + defender.getSpeed() + ")");
            }

            case BUFF_DAMAGE -> {
                attacker.addBonusDamage(move.getPower());
                log.add("Dano aumentado em +" + move.getPower() +
                        " (bônus total: " + attacker.getBonusDamage() + ")");
            }

            case POISON -> {
                int dmg = move.getPower() + attacker.getBonusDamage();
                defender.setCurrentHp(defender.getCurrentHp() - dmg);

                defender.setPoisonDamagePerTurn(move.getPoisonDamagePerTurn());
                defender.setPoisonRemainingTurns(move.getPoisonDuration());

                log.add("Aplicou veneno: " + move.getPoisonDamagePerTurn() +
                        " por turno durante " + move.getPoisonDuration() + " turnos.");
                log.add("Dano imediato: " + dmg + " (HP restante do inimigo: " + defender.getCurrentHp() + ")");
            }
        }

        // --- EFEITO SECUNDÁRIO (se existir) ---
        applySecondaryEffect(attacker, defender, move);
    }

    private void applySecondaryEffect(Fighter attacker, Fighter defender, Move move) {
        MoveType type = move.getSecondaryType();
        if (type == null) {
            return;
        }

        boolean onSelf = move.isSecondaryTargetsSelf();
        Fighter target;

        switch (type) {
            case DAMAGE -> {
                target = onSelf ? attacker : defender;
                int dmg2 = move.getSecondaryPower();
                // Só conta bonusDamage quando bate no inimigo
                if (!onSelf) {
                    dmg2 += attacker.getBonusDamage();
                }
                target.setCurrentHp(target.getCurrentHp() - dmg2);
                log.add("Efeito extra: " + dmg2 + " de dano em " + target.getName() +
                        " (HP restante: " + target.getCurrentHp() + ")");
            }
            case HEAL -> {
                target = attacker; // buff/heal sempre em si mesmo
                int before = target.getCurrentHp();
                target.setCurrentHp(target.getCurrentHp() + move.getSecondaryPower());
                int healed2 = target.getCurrentHp() - before;
                log.add("Efeito extra: regenerou +" + healed2 + " HP (agora: " + target.getCurrentHp() + ")");
            }
            case BUFF_SPEED -> {
                target = attacker;
                int before = target.getSpeed();
                target.setSpeed(target.getSpeed() + move.getSecondaryPower());
                int gained2 = target.getSpeed() - before;
                log.add("Efeito extra: Speed de " + target.getName() + " aumentou +" + gained2 +
                        " (atual: " + target.getSpeed() + ")");
            }
            case DEBUFF_SPEED -> {
                target = onSelf ? attacker : defender;
                int before = target.getSpeed();
                target.setSpeed(target.getSpeed() - move.getSecondaryPower());
                int lost2 = before - target.getSpeed();
                log.add("Efeito extra: Speed de " + target.getName() + " diminuiu -" + lost2 +
                        " (atual: " + target.getSpeed() + ")");
            }
            case BUFF_DAMAGE -> {
                target = attacker;
                target.addBonusDamage(move.getSecondaryPower());
                log.add("Efeito extra: dano de " + target.getName() + " aumentado em +" +
                        move.getSecondaryPower() + " (bônus total: " + target.getBonusDamage() + ")");
            }
            case POISON -> {
                target = onSelf ? attacker : defender;
                target.setPoisonDamagePerTurn(move.getPoisonDamagePerTurn());
                target.setPoisonRemainingTurns(move.getPoisonDuration());
                log.add("Efeito extra: veneno aplicado em " + target.getName() +
                        " (" + move.getPoisonDamagePerTurn() + " por turno, " +
                        move.getPoisonDuration() + " turnos).");
            }
        }
    }

    // ==========================
    // PERSONAGENS DISPONÍVEIS
    // ==========================

    public List<Fighter> getAllBaseFighters() {

        List<Fighter> list = new ArrayList<>();
        list.add(createSnormax());
        list.add(createTestbot());
        list.add(createTintaCruel());
        list.add(createBaldestoise());
        list.add(createWeepincel());
        list.add(createColorFable());
        list.add(createBorrachauro());
        list.add(createCaterPrimer());
        list.add(createHitmonLixa());
        return list;
    }

    // ==========================
    // FACTORY DE LUTADORES
    // ==========================

    private Fighter createFighterByName(String name) {
        return switch (name.toLowerCase()) {
            case "snormax" -> createSnormax();
            case "testbot" -> createTestbot();
            case "tintacruel" -> createTintaCruel();
            case "baldestoise" -> createBaldestoise();
            case "weepincel" -> createWeepincel();
            case "colorfable" -> createColorFable();
            case "borrachauro" -> createBorrachauro();
            case "caterprimer" -> createCaterPrimer();
            case "hitmonlixa" -> createHitmonLixa();
            default -> createSnormax();
        };
    }

    // ==========================
    // PERSONAGENS COMPLETOS
    // ==========================

    // SNORMAX
    // Hp: 300 | Speed: 10
    private Fighter createSnormax() {
        Fighter f = new Fighter("Snormax", 300, 10, List.of(
                // abrir franquia: +10 vida +10 speed usos:7
                new Move(
                        "Abrir franquia",
                        10,
                        MoveType.HEAL,
                        "+10 de vida e +10 de speed.",
                        7,
                        MoveType.BUFF_SPEED,
                        10,
                        true
                ),
                // demissão: 100 dano usos:1
                new Move("Demissão", 100, MoveType.DAMAGE, "Causa 100 de dano.", 1),
                // jogar gatos: 20 dano usos:30
                new Move("Jogar gatos", 20, MoveType.DAMAGE, "Causa 20 de dano.", 30),
                // trabalhar no sábado: -20 de speed inimigo usos:10
                new Move("Trabalhar no sábado", 20, MoveType.DEBUFF_SPEED, "-20 Speed do inimigo.", 10)
        ));
        f.setSpriteUrl("/img/snormax.png");
        f.setBackSpriteUrl("/img/snormaxcosta.png");
        return f;
    }

    // TESTBOT (só pra debug)
    private Fighter createTestbot() {
        Fighter f = new Fighter("Testbot", 100, 50, List.of(
                new Move("Golpe 1", 10, MoveType.DAMAGE, "", 99),
                new Move("Golpe 2", 20, MoveType.DAMAGE, "", 99),
                new Move("Golpe 3", 30, MoveType.DAMAGE, "", 99),
                new Move("Golpe 4", 40, MoveType.DAMAGE, "", 99)
        ));
        f.setSpriteUrl("/img/bot.png");
        f.setBackSpriteUrl("/img/bot.png");
        return f;
    }

    // TINTACRUEL
    // Hp: 140 | Speed: 60
    private Fighter createTintaCruel() {
        Fighter f = new Fighter("TintaCruel", 140, 60, List.of(
                // jogar tinta: 40 dano usos:10
                new Move("Jogar tinta", 40, MoveType.DAMAGE, "Causa 40 de dano.", 10),
                // tinta xadrez: +20 speed -20 speed inimigo usos:10
                new Move(
                        "Tinta xadrez",
                        20,
                        MoveType.BUFF_SPEED,
                        "+20 Speed próprio e -20 Speed inimigo.",
                        10,
                        MoveType.DEBUFF_SPEED,
                        20,
                        false
                ),
                // tinta biodegradável: regen 40 de vida usos:5
                new Move("Tinta biodegradável", 40, MoveType.HEAL, "Regenera 40 de vida.", 5),
                // thinner: 30 dano +5 de veneno 3 turnos usos:10
                new Move(
                        "Thinner",
                        30,
                        MoveType.POISON,
                        "30 de dano + 5 de veneno por 3 turnos.",
                        10,
                        5,
                        3
                )
        ));
        f.setSpriteUrl("/img/tintacruel.png");
        f.setBackSpriteUrl("/img/tintacosta.png");
        return f;
    }

    // BALDESTOISE
    // Hp: 240 | Speed: 40
    private Fighter createBaldestoise() {
        Fighter f = new Fighter("Baldestoise", 240, 40, List.of(
                // guardar tinta: regen 50 usos:10
                new Move("Guardar tinta", 50, MoveType.HEAL, "Regenera 50 de vida.", 10),
                // misturar tinta: +25 speed usos:10
                new Move("Misturar tinta", 25, MoveType.BUFF_SPEED, "+25 Speed.", 10),
                // banho de tinta: 60 dano usos:10
                new Move("Banho de tinta", 60, MoveType.DAMAGE, "Causa 60 de dano.", 10),
                // baldada: 30 dano usos:15
                new Move("Baldada", 30, MoveType.DAMAGE, "Causa 30 de dano.", 15)
        ));
        f.setSpriteUrl("/img/baldestoise.png");
        f.setBackSpriteUrl("/img/baldestoisecosta.png");
        return f;
    }

    // WEEPINCEL
    // Hp: 125 | Speed: 35
    private Fighter createWeepincel() {
        Fighter f = new Fighter("Weepincel", 125, 35, List.of(
                // pintada: 35 dano usos:12
                new Move("Pintada", 35, MoveType.DAMAGE, "Causa 35 de dano.", 12),
                // pincel maior: -5 speed em si mesmo + regen 55 usos:3
                new Move(
                        "Pincel maior",
                        55,
                        MoveType.HEAL,
                        "Regenera 55 e reduz 5 de Speed próprio.",
                        3,
                        MoveType.DEBUFF_SPEED,
                        5,
                        true
                ),
                // pincel pequeno: +30 speed -30 vida própria usos:3
                new Move(
                        "Pincel pequeno",
                        30,
                        MoveType.BUFF_SPEED,
                        "+30 Speed e perde 30 de HP.",
                        3,
                        MoveType.DAMAGE,
                        30,
                        true
                ),
                // rabiscar: 45 dano usos:7
                new Move("Rabiscar", 45, MoveType.DAMAGE, "Causa 45 de dano.", 7)
        ));
        f.setSpriteUrl("/img/weepincel.png");
        f.setBackSpriteUrl("/img/weepincelcosta.png");
        return f;
    }

    // COLORFABLE
    // Hp: 130 | Speed: 70
    private Fighter createColorFable() {
        Fighter f = new Fighter("ColorFable", 130, 70, List.of(
                // aquarela: regen 30 usos:7
                new Move("Aquarela", 30, MoveType.HEAL, "Regenera 30 de vida.", 7),
                // cor ácida: 25 dano + 10 veneno 3 turnos usos:3
                new Move(
                        "Cor ácida",
                        25,
                        MoveType.POISON,
                        "25 de dano + 10 de veneno por 3 turnos.",
                        3,
                        10,
                        3
                ),
                // paletada: 35 dano usos:10
                new Move("Paletada", 35, MoveType.DAMAGE, "Causa 35 de dano.", 10),
                // tinta comestível: -15 speed inimigo usos:10
                new Move("Tinta comestível", 15, MoveType.DEBUFF_SPEED, "-15 Speed do inimigo.", 10)
        ));
        f.setSpriteUrl("/img/colorfable.png");
        f.setBackSpriteUrl("/img/colorfablecosta.png");
        return f;
    }

    // BORRACHAURO
    // Hp: 175 | Speed: 30
    private Fighter createBorrachauro() {
        Fighter f = new Fighter("Borrachauro", 175, 30, List.of(
                // virar líquido: +20 speed usos:5
                new Move("Virar líquido", 20, MoveType.BUFF_SPEED, "+20 Speed.", 5),
                // endurecer: regen 30 usos:20
                new Move("Endurecer", 30, MoveType.HEAL, "Regenera 30 de vida.", 20),
                // borrachar: 35 dano usos:10
                new Move("Borrachar", 35, MoveType.DAMAGE, "Causa 35 de dano.", 10),
                // apagar: 65 dano usos:3
                new Move("Apagar", 65, MoveType.DAMAGE, "Causa 65 de dano.", 3)
        ));
        f.setSpriteUrl("/img/borrachauro.png");
        f.setBackSpriteUrl("/img/borrachaurocosta.png");
        return f;
    }

    // CATERPRIMER
    // Hp: 150 | Speed: 50
    private Fighter createCaterPrimer() {
        Fighter f = new Fighter("CaterPrimer", 150, 50, List.of(
                // adesão: +25 speed usos:5
                new Move("Adesão", 25, MoveType.BUFF_SPEED, "+25 Speed.", 5),
                // primer vencido: -10 speed próprio -20 inimigo usos:7
                new Move(
                        "Primer vencido",
                        20,
                        MoveType.DEBUFF_SPEED,
                        "-20 Speed do inimigo e -10 Speed próprio.",
                        7,
                        MoveType.DEBUFF_SPEED,
                        10,
                        true
                ),
                // rebocar: 30 dano usos:10
                new Move("Rebocar", 30, MoveType.DAMAGE, "Causa 30 de dano.", 10),
                // colar inimigo: 25 dano e -5 speed inimigo usos:7
                new Move(
                        "Colar inimigo",
                        25,
                        MoveType.DAMAGE,
                        "Causa 25 de dano e -5 Speed do inimigo.",
                        7,
                        MoveType.DEBUFF_SPEED,
                        5,
                        false
                )
        ));
        f.setSpriteUrl("/img/caterprimer.png");
        f.setBackSpriteUrl("/img/caterprimercosta.png");
        return f;
    }

    // HITMONLIXA
    // Hp: 120 | Speed: 85
    private Fighter createHitmonLixa() {
        Fighter f = new Fighter("HitmonLixa", 120, 85, List.of(
                // lixar: 35 dano usos:7
                new Move("Lixar", 35, MoveType.DAMAGE, "Causa 35 de dano.", 7),
                // reutilizável: regen 20 vida usos:5
                new Move("Reutilizável", 20, MoveType.HEAL, "Regenera 20 de vida.", 5),
                // corte profundo: 50 dano +10 veneno 2 turnos usos:1
                new Move(
                        "Corte profundo",
                        50,
                        MoveType.POISON,
                        "50 de dano + 10 de veneno por 2 turnos.",
                        1,
                        10,
                        2
                ),
                // lixas maiores: +30 vida usos:5
                new Move("Lixas maiores", 30, MoveType.HEAL, "Regenera 30 de vida.", 5)
        ));
        f.setSpriteUrl("/img/hitmonlixa.png");
        f.setBackSpriteUrl("/img/hitmoncosta.png");
        return f;
    }
}