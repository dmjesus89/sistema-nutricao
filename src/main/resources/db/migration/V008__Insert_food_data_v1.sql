-- V008__Insert_comprehensive_foods_data.sql
-- Comprehensive food database with nutritional information

-- FRUITS (Expanded)
-- V008__Insert_comprehensive_foods_data.sql
-- Comprehensive food database with nutritional information and quantity equivalences

-- FRUITS (Expanded with quantity equivalences)
INSERT INTO foods (name, description, category, calories_per_100g, carbs_per_100g, protein_per_100g, fat_per_100g,
                   fiber_per_100g, sugar_per_100g, serving_size, serving_description, source, verified, quantity_equivalence)
VALUES ('Limão', 'Limão fresco, rico em vitamina C', 'FRUITS', 29, 9.3, 1.1, 0.3, 2.8, 1.5, 60, '1 unidade média',
        'TACO', true, '100g = 1.7 limões médios'),
       ('Lima', 'Lima da pérsia, cítrica', 'FRUITS', 30, 10.5, 0.7, 0.2, 2.8, 1.7, 70, '1 unidade média', 'TACO', true, '100g = 1.4 limas médias'),
       ('Tangerina', 'Tangerina/mexerica doce', 'FRUITS', 53, 13.3, 0.8, 0.3, 1.8, 10.6, 100, '1 unidade média', 'TACO',
        true, '100g = 1 tangerina média'),
       ('Pomelo', 'Pomelo, fruta cítrica grande', 'FRUITS', 38, 9.6, 0.8, 0.0, 1.0, 8.5, 200, '1 fatia grande', 'USDA',
        true, '100g = 0.5 fatia grande'),
       ('Manga', 'Manga tommy, doce e suculenta', 'FRUITS', 60, 15.0, 0.8, 0.4, 1.6, 13.7, 150, '1 unidade pequena',
        'TACO', true, '100g = 0.7 manga pequena'),
       ('Abacaxi', 'Abacaxi pérola, rico em enzimas', 'FRUITS', 50, 13.1, 0.5, 0.1, 1.4, 9.9, 140, '1 fatia média',
        'TACO', true, '100g = 0.7 fatia média'),
       ('Papaya', 'Mamão papaya maduro', 'FRUITS', 43, 10.8, 0.5, 0.3, 1.7, 7.8, 150, '1 fatia média', 'TACO', true, '100g = 0.7 fatia média'),
       ('Coco', 'Polpa de coco fresco', 'FRUITS', 354, 15.2, 3.3, 33.5, 9.0, 6.2, 45, '2 colheres sopa', 'TACO', true, NULL),
       ('Maracujá', 'Polpa de maracujá azedo', 'FRUITS', 97, 23.4, 2.2, 0.7, 10.4, 11.2, 100, '1/2 xícara polpa',
        'TACO', true, '100g = 4-5 maracujás (polpa)'),
       ('Caju', 'Castanha de caju torrada', 'FRUITS', 570, 18.5, 15.3, 46.3, 3.7, 5.9, 30, '1 punhado', 'TACO', true, '100g = 50-60 castanhas'),
       ('Pera', 'Pera fresca, rica em fibras', 'FRUITS', 57, 15.2, 0.4, 0.1, 3.1, 12.0, 150, '1 unidade média', 'TACO',
        true, '100g = 0.7 pera média'),
       ('Açaí', 'Polpa de açaí pura', 'FRUITS', 58, 6.2, 0.8, 3.9, 2.6, 0.0, 100, '1/2 xícara', 'TACO', true, NULL),
       ('Uva', 'Uva itália roxa', 'FRUITS', 69, 18.1, 0.7, 0.2, 0.9, 16.0, 100, '1 cacho pequeno', 'TACO', true, '100g = 15-20 uvas médias'),
       ('Amora', 'Amora preta fresca', 'FRUITS', 43, 9.6, 1.4, 0.5, 5.3, 4.9, 100, '3/4 xícara', 'USDA', true, '100g = 70-80 amoras'),
       ('Mirtilo', 'Blueberry importado', 'FRUITS', 57, 14.5, 0.7, 0.3, 2.4, 10.0, 100, '3/4 xícara', 'USDA', true, '100g = 140-160 mirtilos'),
       ('Pêssego', 'Pêssego em calda natural', 'FRUITS', 39, 9.5, 0.9, 0.3, 1.5, 8.4, 150, '1 unidade média', 'TACO',
        true, '100g = 0.7 pêssego médio'),
       ('Ameixa', 'Ameixa fresca roxa', 'FRUITS', 46, 11.4, 0.7, 0.3, 1.4, 9.9, 60, '2 unidades médias', 'USDA', true, '100g = 3-4 ameixas médias'),
       ('Cereja', 'Cereja doce fresca', 'FRUITS', 63, 16.0, 1.1, 0.2, 2.1, 12.8, 100, '10-12 unidades', 'USDA', true, '100g = 12-15 cerejas'),
       ('Caqui', 'Caqui chocolate maduro', 'FRUITS', 70, 18.6, 0.6, 0.2, 3.6, 15.0, 120, '1 unidade média', 'TACO',
        true, '100g = 0.8 caqui médio'),
       ('Jabuticaba', 'Jabuticaba fresca', 'FRUITS', 58, 15.3, 0.6, 0.1, 2.3, 12.0, 100, '20 unidades', 'TACO', true, '100g = 35-40 jabuticabas'),
       ('Pitanga', 'Pitanga vermelha', 'FRUITS', 41, 10.2, 0.8, 0.4, 3.0, 8.0, 100, '15 unidades', 'TACO', true, '100g = 25-30 pitangas'),
       ('Goiaba', 'Goiaba vermelha', 'FRUITS', 68, 17.0, 1.1, 0.4, 6.2, 14.0, 120, '1 unidade pequena', 'TACO', true, '100g = 0.8 goiaba pequena'),
       ('Jaca', 'Jaca madura', 'FRUITS', 95, 23.2, 1.7, 0.6, 1.5, 19.0, 100, '3-4 bagos', 'TACO', true, '100g = 8-10 bagos médios'),
       ('Fruta do Conde', 'Fruta do conde/ata', 'FRUITS', 94, 23.6, 2.1, 0.3, 4.4, 18.0, 100, '1/2 fruta', 'TACO',
        true, '100g = 0.5 fruta média'),
       ('Cupuaçu', 'Polpa de cupuaçu', 'FRUITS', 49, 12.0, 1.5, 0.6, 1.7, 9.0, 100, '1/2 xícara', 'TACO', true, NULL),
       ('Guaraná', 'Polpa de guaraná', 'FRUITS', 26, 6.2, 0.1, 0.0, 0.4, 5.0, 100, '1/2 xícara', 'TACO', true, NULL),
       ('Buriti', 'Polpa de buriti', 'FRUITS', 308, 19.0, 3.3, 27.2, 25.1, 12.0, 100, '2 colheres sopa', 'TACO', true, NULL),
       ('Pequi', 'Polpa de pequi', 'FRUITS', 205, 4.2, 2.8, 20.0, 7.2, 2.0, 50, '2 caroços', 'TACO', true, '100g = 4 caroços'),
       ('Maçã', 'Maçã fresca com casca', 'FRUITS', 56, 15.2, 0.3, 0.2, 2.4, 10.4, 130, '1 unidade média', 'TACO', true, '100g = 0.8 maçã média'),
       ('Morango', 'Morango fresco', 'FRUITS', 32, 7.7, 0.7, 0.3, 2.0, 4.9, 100, '8-10 unidades médias', 'USDA', true, '100g = 8-10 morangos médios'),
       ('Banana', 'Banana nanica/prata madura', 'FRUITS', 98, 25.8, 1.3, 0.1, 2.6, 14.4, 100, '1 unidade média', 'TACO', true, '100g = 1 banana média'),
      ('Mamão', 'Mamão formosa maduro', 'FRUITS', 43, 10.8, 0.5, 0.3, 1.7, 7.8, 150, '1 fatia média', 'TACO', true, '100g = 0.7 fatia média');

-- VEGETABLES (Expanded)
INSERT INTO foods (name, description, category, calories_per_100g, carbs_per_100g, protein_per_100g, fat_per_100g,
                   fiber_per_100g, sugar_per_100g, sodium_per_100g, serving_size, serving_description, source, verified, quantity_equivalence)
VALUES ('Espinafre', 'Espinafre cru fresco', 'VEGETABLES', 23, 3.6, 2.9, 0.4, 2.2, 1.4, 79, 100, '2 xícaras', 'TACO',
        true, NULL),
       ('Rúcula', 'Rúcula fresca', 'VEGETABLES', 25, 3.7, 2.6, 0.7, 1.6, 2.0, 27, 50, '1 xícara', 'USDA', true, NULL),
       ('Alface', 'Alface americana', 'VEGETABLES', 15, 2.9, 1.4, 0.1, 1.3, 1.2, 28, 100, '2 xícaras', 'TACO', true, NULL),
       ('Agrião', 'Agrião fresco', 'VEGETABLES', 11, 1.3, 2.3, 0.1, 0.5, 0.8, 41, 50, '1 xícara', 'TACO', true, NULL),
       ('Repolho', 'Repolho branco cru', 'VEGETABLES', 25, 5.8, 1.3, 0.1, 2.5, 3.2, 18, 100, '1 xícara picado', 'TACO',
        true, NULL),
       ('Couve-flor', 'Couve-flor cozida', 'VEGETABLES', 25, 5.3, 1.9, 0.3, 2.0, 2.4, 19, 100, '1 xícara', 'TACO',
        true, '100g = 8-10 florzinhas médias'),
       ('Couve de Bruxelas', 'Couve de bruxelas cozida', 'VEGETABLES', 43, 8.9, 3.4, 0.3, 3.8, 2.2, 25, 100,
        '8-10 unidades', 'USDA', true, '100g = 8-10 unidades'),
       ('Cenoura', 'Cenoura crua ralada', 'VEGETABLES', 41, 9.6, 0.9, 0.2, 2.8, 4.7, 69, 100, '1 unidade média', 'TACO',
        true, '100g = 1 cenoura média'),
       ('Beterraba', 'Beterraba cozida', 'VEGETABLES', 44, 10.0, 1.6, 0.2, 2.0, 6.8, 78, 100, '1 unidade pequena',
        'TACO', true, '100g = 1 beterraba pequena'),
       ('Rabanete', 'Rabanete cru', 'VEGETABLES', 16, 3.4, 0.7, 0.1, 1.6, 1.9, 39, 100, '10 unidades pequenas', 'TACO',
        true, '100g = 10-12 rabanetes pequenos'),
       ('Abóbora', 'Abóbora moranga cozida', 'VEGETABLES', 26, 6.5, 1.0, 0.1, 0.5, 2.8, 1, 100, '1/2 xícara', 'TACO',
        true, NULL),
       ('Chuchu', 'Chuchu cozido', 'VEGETABLES', 19, 4.5, 0.8, 0.1, 1.7, 1.7, 2, 100, '1 unidade pequena', 'TACO',
        true, '100g = 1 chuchu pequeno'),
       ('Pepino', 'Pepino cru com casca', 'VEGETABLES', 16, 3.6, 0.7, 0.1, 0.5, 1.7, 2, 100, '1 unidade pequena',
        'TACO', true, '100g = 0.5 pepino médio'),
       ('Pimentão Verde', 'Pimentão verde cru', 'VEGETABLES', 20, 4.6, 0.9, 0.2, 2.5, 2.5, 4, 100, '1 unidade média',
        'TACO', true, '100g = 0.7 pimentão médio'),
       ('Pimentão Vermelho', 'Pimentão vermelho cru', 'VEGETABLES', 31, 7.3, 1.0, 0.3, 2.5, 4.2, 4, 100,
        '1 unidade média', 'USDA', true, '100g = 0.7 pimentão médio'),
       ('Pimenta Dedo de Moça', 'Pimenta vermelha picante', 'VEGETABLES', 40, 8.8, 1.9, 0.4, 1.5, 5.3, 9, 10,
        '1 unidade', 'TACO', true, '100g = 10 pimentas'),
       ('Tomate', 'Tomate salada maduro', 'VEGETABLES', 18, 3.9, 0.9, 0.2, 1.2, 2.6, 5, 120, '1 unidade média', 'TACO',
        true, '100g = 0.8 tomate médio'),
       ('Cebola', 'Cebola branca crua', 'VEGETABLES', 40, 9.3, 1.1, 0.1, 1.7, 4.2, 4, 100, '1 unidade média', 'TACO',
        true, '100g = 0.8 cebola média'),
       ('Alho', 'Alho cru descascado', 'VEGETABLES', 149, 33.1, 6.4, 0.5, 2.1, 1.0, 17, 3, '1 dente', 'TACO', true, '100g = 30-35 dentes'),
       ('Berinjela', 'Berinjela refogada', 'VEGETABLES', 25, 5.9, 1.0, 0.2, 3.0, 3.5, 2, 100, '3-4 fatias', 'TACO',
        true, '100g = 0.4 berinjela média');


-- LEGUMES (Beans and Legumes)
INSERT INTO foods (name, description, category, calories_per_100g, carbs_per_100g, protein_per_100g, fat_per_100g,
                   fiber_per_100g, sugar_per_100g, sodium_per_100g, serving_size, serving_description, source, verified, quantity_equivalence)
VALUES ('Feijão Preto', 'Feijão preto cozido', 'VEGETABLES', 132, 23.0, 8.9, 0.5, 8.4, 0.3, 2, 100, '1 concha', 'TACO',
        true, NULL),
       ('Feijão Fradinho', 'Feijão fradinho cozido', 'VEGETABLES', 108, 19.3, 7.5, 0.6, 9.6, 1.0, 3, 100, '1 concha',
        'TACO', true, NULL),
       ('Feijão Branco', 'Feijão branco cozido', 'VEGETABLES', 139, 25.1, 9.7, 0.6, 6.3, 0.6, 2, 100, '1 concha',
        'USDA', true, NULL),
       ('Feijão Roxo', 'Feijão roxo cozido', 'VEGETABLES', 127, 22.8, 8.7, 0.5, 8.7, 0.3, 1, 100, '1 concha', 'USDA',
        true, NULL),
       ('Lentilha', 'Lentilha cozida', 'VEGETABLES', 116, 20.1, 9.0, 0.4, 7.9, 1.8, 2, 100, '1/2 xícara', 'TACO', true, NULL),
       ('Grão de Bico', 'Grão de bico cozido', 'VEGETABLES', 164, 27.4, 8.9, 2.6, 7.6, 4.8, 7, 100, '1/2 xícara',
        'TACO', true, NULL),
       ('Ervilha', 'Ervilha fresca cozida', 'VEGETABLES', 81, 14.5, 5.4, 0.4, 5.7, 5.7, 5, 100, '1/2 xícara', 'TACO',
        true, NULL),
       ('Soja', 'Grão de soja cozido', 'VEGETABLES', 172, 9.9, 16.6, 9.0, 6.0, 3.0, 2, 100, '1/2 xícara', 'TACO', true, NULL),
       ('Salada Mista', 'Mix de folhas verdes', 'VEGETABLES', 15, 2.9, 1.4, 0.1, 1.3, 1.2, 28, 100, '2 xícaras', 'TACO', true, NULL),
       ('Legumes Cozidos', 'Mix de legumes cozidos no vapor', 'VEGETABLES', 35, 7.5, 1.8, 0.3, 2.5, 3.2, 15, 100, '1 xícara', 'TACO', true, NULL);

-- PROTEINS (Greatly Expanded)
INSERT INTO foods (name, description, category, calories_per_100g, carbs_per_100g, protein_per_100g, fat_per_100g,
                   fiber_per_100g, sugar_per_100g, sodium_per_100g, serving_size, serving_description, source, verified, quantity_equivalence)
VALUES ('Frango Peito', 'Peito de frango sem pele, grelhado', 'PROTEINS', 165, 0, 31.0, 3.6, 0, 0, 74, 100,
        '1 filé médio', 'TACO', true, NULL),
       ('Peixe Branco', 'Filé de peixe branco grelhado', 'PROTEINS', 105, 0, 22.0, 1.5, 0, 0, 70, 150, '1 posta média', 'TACO', true, NULL),
       ('Frango Coxa', 'Coxa de frango sem pele, assada', 'PROTEINS', 209, 0, 26.0, 10.9, 0, 0, 95, 100, '1 coxa média',
        'USDA', true, NULL),
       ('Frango Sobrecoxa', 'Sobrecoxa sem pele, grelhada', 'PROTEINS', 165, 0, 25.9, 6.2, 0, 0, 90, 120, '1 sobrecoxa',
        'USDA', true, NULL),
       ('Peru', 'Peito de peru assado', 'PROTEINS', 135, 0, 30.1, 1.2, 0, 0, 60, 100, '3 fatias médias', 'USDA', true, NULL),
       ('Tilápia', 'Tilápia grelhada', 'PROTEINS', 96, 0, 20.1, 1.7, 0, 0, 52, 150, '1 filé médio', 'USDA', true, NULL),
       ('Pintado', 'Pintado grelhado', 'PROTEINS', 105, 0, 21.8, 1.8, 0, 0, 55, 150, '1 posta', 'TACO', true, NULL),
       ('Salmão', 'Salmão atlântico grelhado', 'PROTEINS', 206, 0, 22.1, 12.4, 0, 0, 59, 150, '1 filé médio', 'USDA',
        true, NULL),
       ('Atum', 'Atum fresco grelhado', 'PROTEINS', 144, 0, 25.4, 4.9, 0, 0, 39, 150, '1 posta', 'USDA', true, NULL),
       ('Sardinha', 'Sardinha fresca grelhada', 'PROTEINS', 208, 0, 24.6, 11.5, 0, 0, 307, 100, '2 unidades médias',
        'USDA', true, '100g = 2 sardinhas médias'),
       ('Alcatra', 'Alcatra bovina grelhada', 'PROTEINS', 219, 0, 31.9, 9.3, 0, 0, 64, 100, '1 bife médio', 'TACO',
        true, NULL),
       ('Filé Mignon', 'Filé mignon grelhado', 'PROTEINS', 179, 0, 26.8, 7.0, 0, 0, 54, 100, '1 bife pequeno', 'TACO',
        true, NULL),
       ('Picanha', 'Picanha bovina grelhada', 'PROTEINS', 237, 0, 27.4, 13.3, 0, 0, 62, 100, '1 fatia média', 'TACO',
        true, NULL),
       ('Lombo Suíno', 'Lombo de porco assado', 'PROTEINS', 201, 0, 28.8, 8.2, 0, 0, 68, 100, '1 fatia média', 'TACO',
        true, NULL),
       ('Camarão', 'Camarão rosa cozido', 'PROTEINS', 99, 0.9, 20.3, 1.4, 0, 0.1, 566, 100, '8-10 unidades grandes',
        'TACO', true, '100g = 8-10 camarões grandes'),
       ('Ovo Galinha', 'Ovo de galinha cozido', 'PROTEINS', 155, 1.1, 13.0, 10.6, 0, 0.4, 124, 50, '1 unidade', 'TACO',
        true, '100g = 2 ovos médios'),
       ('Tofu', 'Tofu firme', 'PROTEINS', 76, 1.9, 8.1, 4.8, 0.4, 0.6, 7, 100, '1 fatia média', 'USDA', true, NULL),
       ('Clara de Ovo', 'Clara de ovo cozida', 'PROTEINS', 52, 0.7, 10.9, 0.2, 0, 0.7, 166, 33, '1 clara grande', 'TACO', true, '100g = 3 claras grandes'),
       ('Ovo Mexido', 'Ovo inteiro mexido sem óleo', 'PROTEINS', 155, 1.1, 13.0, 10.6, 0, 0.4, 124, 50, '1 ovo médio', 'TACO', true, '100g = 2 ovos médios');

-- DAIRY PRODUCTS (Expanded)
INSERT INTO foods (name, description, category, calories_per_100g, carbs_per_100g, protein_per_100g, fat_per_100g,
                   fiber_per_100g, sugar_per_100g, sodium_per_100g, serving_size, serving_description, source, verified, quantity_equivalence)
VALUES ('Leite Integral', 'Leite de vaca integral', 'DAIRY', 61, 4.6, 3.1, 3.2, 0, 4.6, 43, 200, '1 copo', 'TACO',
        true, NULL),
       ('Leite Desnatado', 'Leite de vaca desnatado', 'DAIRY', 35, 4.9, 3.4, 0.2, 0, 4.9, 44, 200, '1 copo', 'TACO',
        true, NULL),
       ('Iogurte Natural', 'Iogurte natural integral', 'DAIRY', 61, 4.7, 3.5, 3.3, 0, 4.7, 46, 170, '1 pote', 'TACO',
        true, NULL),
       ('Iogurte Grego', 'Iogurte grego natural', 'DAIRY', 97, 3.6, 9.0, 5.0, 0, 3.6, 36, 170, '1 pote pequeno', 'USDA',
        true, NULL),
       ('Queijo Mussarela', 'Queijo mussarela fatiado', 'DAIRY', 280, 3.1, 19.9, 19.5, 0, 1.0, 373, 30, '1 fatia',
        'TACO', true, '100g = 3-4 fatias médias'),
       ('Queijo Prato', 'Queijo prato fatiado', 'DAIRY', 360, 2.5, 25.8, 26.6, 0, 0.5, 560, 30, '1 fatia', 'TACO',
        true, '100g = 3-4 fatias médias'),
       ('Requeijão', 'Requeijão cremoso', 'DAIRY', 264, 3.0, 11.6, 23.0, 0, 3.0, 560, 30, '1 colher sopa', 'TACO',
        true, NULL),
       ('Leite de Coco', 'Leite de coco industrializado', 'DAIRY', 230, 5.5, 2.3, 23.8, 0, 5.5, 15, 200, '1 copo',
        'USDA', true, NULL),
       ('Iogurte Grego Light', 'Iogurte grego natural light', 'DAIRY', 59, 3.5, 10.0, 0.5, 0, 3.5, 36, 170, '1 pote pequeno', 'USDA', true, NULL),
       ('Iogurte Proteico', 'Iogurte com alto teor proteico', 'DAIRY', 75, 4.0, 12.0, 1.0, 0, 3.0, 40, 150, '1 pote', 'USDA', true, NULL);

-- CEREALS AND GRAINS (Expanded)
INSERT INTO foods (name, description, category, calories_per_100g, carbs_per_100g, protein_per_100g, fat_per_100g,
                   fiber_per_100g, sugar_per_100g, sodium_per_100g, serving_size, serving_description, source, verified, quantity_equivalence)
VALUES ('Arroz Branco', 'Arroz branco polido cozido', 'CEREALS_GRAINS', 128, 28.1, 2.5, 0.1, 0.2, 0.1, 1, 150,
        '1 xícara', 'TACO', true, NULL),
       ('Arroz Integral', 'Arroz integral cozido', 'CEREALS_GRAINS', 124, 23.0, 2.6, 1.0, 1.8, 0.4, 5, 150, '1 xícara',
        'TACO', true, NULL),
       ('Quinoa', 'Quinoa cozida', 'CEREALS_GRAINS', 120, 21.3, 4.4, 1.9, 2.8, 0.9, 7, 100, '1/2 xícara', 'USDA', true, NULL),
       ('Macarrão Integral', 'Macarrão de trigo integral', 'CEREALS_GRAINS', 124, 23.2, 5.3, 1.1, 3.9, 0.8, 3, 100,
        '1 xícara cozida', 'TACO', true, NULL),
       ('Macarrão Comum', 'Macarrão de trigo comum', 'CEREALS_GRAINS', 131, 25.4, 4.7, 0.9, 1.4, 0.6, 1, 100,
        '1 xícara cozida', 'TACO', true, NULL),
       ('Pão Francês', 'Pão francês tradicional', 'CEREALS_GRAINS', 300, 58.6, 9.4, 3.1, 2.3, 1.0, 642, 50, '1 unidade',
        'TACO', true, '100g = 2 pães franceses'),
       ('Pão Integral', 'Pão de forma integral', 'CEREALS_GRAINS', 253, 43.0, 11.0, 4.0, 6.0, 3.0, 400, 25, '1 fatia',
        'TACO', true, '100g = 4 fatias'),
       ('Batata Inglesa', 'Batata inglesa cozida', 'CEREALS_GRAINS', 87, 20.1, 1.9, 0.1, 1.3, 0.8, 6, 150,
        '1 unidade média', 'TACO', true, '100g = 0.7 batata média'),
       ('Batata Doce', 'Batata doce assada', 'CEREALS_GRAINS', 118, 28.2, 2.0, 0.1, 3.0, 5.4, 7, 150, '1 unidade média',
        'TACO', true, '100g = 0.7 batata doce média'),
       ('Mandioca', 'Mandioca cozida', 'CEREALS_GRAINS', 125, 30.1, 1.2, 0.3, 1.6, 1.7, 14, 100, '1 pedaço médio',
        'TACO', true, NULL),
       ('Aveia', 'Aveia em flocos', 'CEREALS_GRAINS', 394, 66.3, 13.9, 8.5, 9.1, 0.7, 2, 30, '3 colheres sopa', 'TACO',
        true, NULL),
       ('Tapioca', 'Tapioca granulada', 'CEREALS_GRAINS', 358, 88.7, 0.6, 0.3, 0.2, 0.1, 1, 50, '1 crepe pequeno',
        'TACO', true, NULL);

-- NUTS AND SEEDS (Expanded)
INSERT INTO foods (name, description, category, calories_per_100g, carbs_per_100g, protein_per_100g, fat_per_100g,
                   fiber_per_100g, sugar_per_100g, sodium_per_100g, serving_size, serving_description, source, verified, quantity_equivalence)
VALUES ('Castanha do Pará', 'Castanha do Pará crua', 'NUTS_SEEDS', 656, 12.3, 14.3, 63.5, 7.5, 2.3, 3, 15, '3 unidades',
        'TACO', true, '100g = 20 castanhas'),
       ('Amendoim', 'Amendoim torrado sem sal', 'NUTS_SEEDS', 567, 16.1, 25.8, 47.1, 8.0, 4.7, 18, 30, '1 punhado',
        'TACO', true, '100g = 140-160 amendoins'),
       ('Nozes', 'Nozes cruas', 'NUTS_SEEDS', 654, 13.7, 15.2, 65.2, 6.7, 2.6, 2, 20, '4-5 unidades', 'USDA', true, '100g = 20-25 metades de noz'),
       ('Amêndoa', 'Amêndoa crua individual', 'NUTS_SEEDS', 579, 21.6, 21.2, 49.9, 12.5, 4.9, 1, 1.2, '1 unidade (1.2g)', 'USDA', true, '100g = 80-85 amêndoas'),
       ('Semente de Girassol', 'Semente de girassol torrada', 'NUTS_SEEDS', 584, 20.0, 20.8, 51.5, 8.6, 2.6, 9, 25,
        '2 colheres sopa', 'USDA', true, NULL),
       ('Linhaça', 'Semente de linhaça moída', 'NUTS_SEEDS', 534, 28.9, 18.3, 42.2, 27.3, 1.5, 30, 15, '1 colher sopa',
        'USDA', true, NULL),
       ('Chia', 'Semente de chia', 'NUTS_SEEDS', 486, 42.1, 16.5, 30.7, 34.4, 0, 16, 15, '1 colher sopa', 'USDA', true, NULL),
       ('Pasta de Amendoim', 'Pasta de amendoim natural', 'NUTS_SEEDS', 588, 20.0, 25.0, 50.0, 6.0, 6.0, 15, 15, '1 colher sopa', 'TACO', true, NULL);

-- FATS AND OILS
INSERT INTO foods (name, description, category, calories_per_100g, carbs_per_100g, protein_per_100g, fat_per_100g,
                   fiber_per_100g, sugar_per_100g, sodium_per_100g, serving_size, serving_description, source, verified, quantity_equivalence)
VALUES ('Óleo de Soja', 'Óleo de soja refinado', 'FATS_OILS', 884, 0, 0, 100, 0, 0, 0, 5, '1 colher chá', 'TACO', true, NULL),
       ('Azeite de Oliva', 'Azeite de oliva extra virgem', 'FATS_OILS', 884, 0, 0, 100, 0, 0, 2, 5, '1 colher chá',
        'USDA', true, NULL),
       ('Óleo de Coco', 'Óleo de coco extra virgem', 'FATS_OILS', 862, 0, 0, 100, 0, 0, 0, 5, '1 colher chá', 'USDA',
        true, NULL),
       ('Manteiga', 'Manteiga com sal', 'FATS_OILS', 717, 0.1, 0.9, 81.1, 0, 0.1, 643, 10, '1 colher chá', 'TACO',
        true, NULL),
       ('Margarina', 'Margarina vegetal', 'FATS_OILS', 720, 0.9, 0.6, 80.0, 0, 0.9, 943, 10, '1 colher chá', 'TACO',
        true, NULL);

-- BEVERAGES
INSERT INTO foods (name, description, category, calories_per_100g, carbs_per_100g, protein_per_100g, fat_per_100g,
                   fiber_per_100g, sugar_per_100g, sodium_per_100g, serving_size, serving_description, source, verified, quantity_equivalence)
VALUES ('Água', 'Água potável', 'BEVERAGES', 0, 0, 0, 0, 0, 0, 0, 250, '1 copo', 'USDA', true, NULL),
       ('Água de Coco', 'Água de coco natural', 'BEVERAGES', 19, 3.7, 1.7, 0.2, 1.1, 2.6, 105, 200, '1 copo', 'TACO',
        true, NULL),
       ('Suco de Laranja', 'Suco de laranja natural', 'BEVERAGES', 45, 11.5, 0.7, 0.1, 0.2, 8.1, 1, 200, '1 copo',
        'TACO', true, NULL),
       ('Café', 'Café coado sem açúcar', 'BEVERAGES', 2, 0.3, 0.1, 0, 0, 0, 2, 150, '1 xícara', 'TACO', true, NULL),
       ('Chá Verde', 'Chá verde sem açúcar', 'BEVERAGES', 1, 0.3, 0, 0, 0, 0, 1, 200, '1 xícara', 'USDA', true, NULL);

-- CONDIMENTS AND SPICES
INSERT INTO foods (name, description, category, calories_per_100g, carbs_per_100g, protein_per_100g, fat_per_100g,
                   fiber_per_100g, sugar_per_100g, sodium_per_100g, serving_size, serving_description, source, verified, quantity_equivalence)
VALUES ('Sal', 'Sal de cozinha refinado', 'CONDIMENTS_SPICES', 0, 0, 0, 0, 0, 0, 38758, 1, '1 pitada', 'USDA', true, NULL),
       ('Açúcar Cristal', 'Açúcar cristal branco', 'CONDIMENTS_SPICES', 387, 99.8, 0, 0, 0, 99.8, 2, 5, '1 colher chá', 'TACO',
        true, NULL),
       ('Mel', 'Mel de abelha puro', 'CONDIMENTS_SPICES', 304, 82.4, 0.3, 0, 0.2, 82.1, 4, 10, '1 colher chá', 'TACO', true, NULL),
       ('Shoyu', 'Molho shoyu tradicional', 'CONDIMENTS_SPICES', 61, 5.6, 10.5, 0.1, 0.8, 0.8, 5637, 5, '1 colher chá', 'USDA',
        true, NULL),
       ('Azeite Virgem', 'Azeite de oliva virgem', 'CONDIMENTS_SPICES', 884, 0, 0, 100, 0, 0, 2, 5, '1 colher chá', 'USDA',
        true, NULL);

-- SWEETS AND DESSERTS
INSERT INTO foods (name, description, category, calories_per_100g, carbs_per_100g, protein_per_100g, fat_per_100g,
                   fiber_per_100g, sugar_per_100g, sodium_per_100g, serving_size, serving_description, source, verified, quantity_equivalence)
VALUES ('Chocolate Meio Amargo', 'Chocolate 70% cacau', 'SWEETS_DESSERTS', 598, 45.9, 7.8, 42.6, 11.0, 24.0, 20, 20,
        '2 quadradinhos', 'USDA', true, '100g = 10 quadradinhos'),
       ('Chocolate ao Leite', 'Chocolate ao leite', 'SWEETS_DESSERTS', 534, 59.4, 7.6, 29.7, 3.4, 51.5, 79, 25, '5 quadradinhos',
        'USDA', true, '100g = 20 quadradinhos'),
       ('Sorvete de Baunilha', 'Sorvete cremoso baunilha', 'SWEETS_DESSERTS', 207, 23.6, 3.5, 11.0, 0.7, 21.2, 80, 60, '1 bola',
        'USDA', true, NULL),
       ('Brigadeiro', 'Brigadeiro tradicional', 'SWEETS_DESSERTS', 424, 67.2, 4.8, 16.7, 2.1, 60.0, 52, 20, '1 unidade', 'TACO',
        true, '100g = 5 brigadeiros'),
       ('Açaí na Tigela', 'Açaí com granola', 'SWEETS_DESSERTS', 158, 20.8, 2.1, 7.8, 3.0, 15.0, 8, 300, '1 tigela média',
        'TACO', true, NULL);

-- WHEY PROTEIN SUPPLEMENT
INSERT INTO foods (name, description, category, calories_per_100g, carbs_per_100g, protein_per_100g, fat_per_100g,
                   fiber_per_100g, sugar_per_100g, sodium_per_100g, serving_size, serving_description, source, verified, quantity_equivalence)
VALUES ('Whey Isolate', 'Proteína isolada de soro de leite em pó', 'SUPPLEMENTS', 363, 3.3, 82.3, 2.0, 2.0, 1.7, 1000,
        30, '1 colher (30g)', 'ZUMUB', true, NULL)