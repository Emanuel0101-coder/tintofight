INSERT INTO fighters (id, name, max_hp, difficulty_level) VALUES
(1, 'Snormax', 300, 1),
(2, 'TintaCruel', 140, 2),
(3, 'Baldestoise', 240, 2),
(4, 'Weepincel', 125, 2),
(5, 'ColorFable', 130, 3),
(6, 'Borrachauro', 175, 2),
(7, 'CaterPrimer', 150, 3),
(8, 'HitmonLixa', 120, 3);

-- e os INSERTS dos golpes aqui...
INSERT INTO moves (fighter_id, name, damage, has_buff, has_debuff, has_poison) VALUES
(1, 'Abrir franquia',        0,   1, 0, 0),  -- cura + speed
(1, 'Demissão',             100,  0, 0, 0),
(1, 'Jogar gatos',           20,  0, 0, 0),
(1, 'Trabalhar no sábado',   0,   0, 1, 0);  -- só debuff de speed inimigo

INSERT INTO moves (fighter_id, name, damage, has_buff, has_debuff, has_poison) VALUES
(2, 'Jogar tinta',          40,  0, 0, 0),
(2, 'Tinta xadrez',          0,  1, 1, 0),  -- buff próprio + debuff inimigo
(2, 'Tinta biodegradável',   0,  1, 0, 0),  -- só cura
(2, 'Thinner',              30,  0, 0, 1);  -- dano + veneno

INSERT INTO moves (fighter_id, name, damage, has_buff, has_debuff, has_poison) VALUES
(3, 'Guardar tinta',        0,  1, 0, 0),   -- regen 50
(3, 'Misturar tinta',       0,  1, 0, 0),   -- +25 speed
(3, 'Banho de tinta',      60,  0, 0, 0),
(3, 'Baldada',             30,  0, 0, 0);

INSERT INTO moves (fighter_id, name, damage, has_buff, has_debuff, has_poison) VALUES
(4, 'Pintada',             35,  0, 0, 0),
(4, 'Pincel maior',         0,  1, 1, 0),   -- cura + debuff de speed em si
(4, 'Pincel pequeno',      30,  1, 1, 0),   -- buff de speed + dano em si (trata como debuff também)
(4, 'Rabiscar',            45,  0, 0, 0);

INSERT INTO moves (fighter_id, name, damage, has_buff, has_debuff, has_poison) VALUES
(5, 'Aquarela',            0,  1, 0, 0),    -- regen 30
(5, 'Cor ácida',           25, 0, 0, 1),    -- dano + veneno
(5, 'Paletada',            35, 0, 0, 0),
(5, 'Tinta comestível',    0,  0, 1, 0);    -- só debuff de speed inimigo

INSERT INTO moves (fighter_id, name, damage, has_buff, has_debuff, has_poison) VALUES
(6, 'Virar líquido',       0,  1, 0, 0),    -- +20 speed
(6, 'Endurecer',           0,  1, 0, 0),    -- regen 30
(6, 'Borrachar',           35, 0, 0, 0),
(6, 'Apagar',              65, 0, 0, 0);

INSERT INTO moves (fighter_id, name, damage, has_buff, has_debuff, has_poison) VALUES
(7, 'Adesão',              0,  1, 0, 0),    -- +25 speed
(7, 'Primer vencido',      0,  0, 1, 0),    -- debuff próprio + inimigo
(7, 'Rebocar',             30, 0, 0, 0),
(7, 'Colar inimigo',       25, 0, 1, 0);    -- dano + -5 speed inimigo

INSERT INTO moves (fighter_id, name, damage, has_buff, has_debuff, has_poison) VALUES
(8, 'Lixar',               35, 0, 0, 0),
(8, 'Reutilizável',         0, 1, 0, 0),    -- regen 20
(8, 'Corte profundo',      50, 0, 0, 1),    -- dano + veneno
(8, 'Lixas maiores',        0, 1, 0, 0);    -- regen 30

