-- V009__Insert_supplements_data.sql
-- Comprehensive supplements database

-- VITAMINS
INSERT INTO supplements (name, description, category, form, serving_size, serving_unit, servings_per_container,
                         main_ingredient, ingredient_amount, ingredient_unit, recommended_dosage, usage_instructions, verified)
VALUES
    ('Vitamina D3', 'Vitamina D3 (colecalciferol)', 'VITAMINS', 'SOFTGEL', 1, 'CAPSULES', 60, 'Vitamina D3', 2000, 'IU', '1 cápsula ao dia', 'Tomar com uma refeição contendo gordura', true),
    ('Vitamina C', 'Ácido ascórbico', 'VITAMINS', 'TABLET', 1, 'TABLETS', 90, 'Vitamina C', 1000, 'mg', '1 comprimido ao dia', 'Tomar com água, preferencialmente com o estômago vazio', true),
    ('Vitamina B12', 'Cianocobalamina', 'VITAMINS', 'TABLET', 1, 'TABLETS', 60, 'Vitamina B12', 2500, 'mcg', '1 comprimido ao dia', 'Tomar sublingual ou com água', true),
    ('Complexo B', 'Complexo de vitaminas do complexo B', 'VITAMINS', 'CAPSULE', 1, 'CAPSULES', 60, 'Complexo B', 50, 'mg', '1 cápsula ao dia', 'Tomar com as refeições', true),
    ('Vitamina A', 'Retinol', 'VITAMINS', 'SOFTGEL', 1, 'CAPSULES', 100, 'Vitamina A', 10000, 'IU', '1 cápsula ao dia', 'Tomar com refeição contendo gordura', true),
    ('Vitamina E', 'Tocoferol', 'VITAMINS', 'SOFTGEL', 1, 'CAPSULES', 60, 'Vitamina E', 400, 'IU', '1 cápsula ao dia', 'Tomar com refeição contendo gordura', true),
    ('Vitamina K2', 'Menaquinona', 'VITAMINS', 'TABLET', 1, 'TABLETS', 60, 'Vitamina K2', 100, 'mcg', '1 comprimido ao dia', 'Tomar com refeição contendo gordura', true),
    ('Ácido Fólico', 'Folato', 'VITAMINS', 'TABLET', 1, 'TABLETS', 90, 'Ácido Fólico', 400, 'mcg', '1 comprimido ao dia', 'Tomar com água', true),
    ('Biotina', 'Vitamina B7', 'VITAMINS', 'CAPSULE', 1, 'CAPSULES', 60, 'Biotina', 5000, 'mcg', '1 cápsula ao dia', 'Tomar com água', true);

-- MINERALS
INSERT INTO supplements (name, description, category, form, serving_size, serving_unit, servings_per_container,
                         main_ingredient, ingredient_amount, ingredient_unit, recommended_dosage, usage_instructions, verified)
VALUES
    ('Magnésio Dimalato', 'Magnésio quelado', 'MINERALS', 'CAPSULE', 2, 'CAPSULES', 60, 'Magnésio', 400, 'mg', '2 cápsulas ao dia', 'Tomar 1 pela manhã e 1 à noite', true),
    ('Zinco Quelato', 'Zinco quelado', 'MINERALS', 'CAPSULE', 1, 'CAPSULES', 60, 'Zinco', 15, 'mg', '1 cápsula ao dia', 'Tomar com estômago vazio ou 2h após refeições', true),
    ('Ferro Quelato', 'Ferro bisglicianato', 'MINERALS', 'CAPSULE', 1, 'CAPSULES', 60, 'Ferro', 18, 'mg', '1 cápsula ao dia', 'Tomar com estômago vazio junto com vitamina C', true),
    ('Cálcio + D3', 'Carbonato de cálcio com vitamina D3', 'MINERALS', 'TABLET', 2, 'TABLETS', 60, 'Cálcio', 600, 'mg', '2 comprimidos ao dia', 'Tomar com as refeições, dividir as doses', true),
    ('Selênio', 'Selenometionina', 'MINERALS', 'CAPSULE', 1, 'CAPSULES', 60, 'Selênio', 200, 'mcg', '1 cápsula ao dia', 'Tomar com água', true),
    ('Cromo', 'Picolinato de cromo', 'MINERALS', 'CAPSULE', 1, 'CAPSULES', 60, 'Cromo', 200, 'mcg', '1 cápsula ao dia', 'Tomar antes das refeições', true),
    ('Iodo', 'Iodeto de potássio', 'MINERALS', 'TABLET', 1, 'TABLETS', 90, 'Iodo', 150, 'mcg', '1 comprimido ao dia', 'Tomar com água', true),
    ('Potássio', 'Citrato de potássio', 'MINERALS', 'CAPSULE', 2, 'CAPSULES', 45, 'Potássio', 500, 'mg', '2 cápsulas ao dia', 'Tomar com as refeições', true);

-- OMEGA 3 AND FATTY ACIDS
INSERT INTO supplements (name, description, category, form, serving_size, serving_unit, servings_per_container,
                         main_ingredient, ingredient_amount, ingredient_unit, recommended_dosage, usage_instructions, verified)
VALUES
    ('Ômega 3', 'Óleo de peixe concentrado EPA/DHA', 'OMEGA3', 'SOFTGEL', 2, 'CAPSULES', 60, 'EPA + DHA', 1000, 'mg', '2 cápsulas ao dia', 'Tomar com as refeições', true),
    ('Ômega 3 Concentrado', 'Óleo de peixe ultra concentrado', 'OMEGA3', 'SOFTGEL', 1, 'CAPSULES', 60, 'EPA + DHA', 1200, 'mg', '1 cápsula ao dia', 'Tomar com refeição', true),
    ('Óleo de Linhaça', 'Óleo de linhaça prensado a frio', 'OMEGA3', 'SOFTGEL', 2, 'CAPSULES', 90, 'ALA', 1000, 'mg', '2 cápsulas ao dia', 'Tomar com as refeições', true),
    ('Óleo de Prímula', 'Óleo de prímula rico em GLA', 'OMEGA3', 'SOFTGEL', 2, 'CAPSULES', 60, 'GLA', 500, 'mg', '2 cápsulas ao dia', 'Tomar com as refeições', true),
    ('Ômega 3 Vegano', 'Óleo de algas marinhas', 'OMEGA3', 'SOFTGEL', 1, 'CAPSULES', 60, 'EPA + DHA', 800, 'mg', '1 cápsula ao dia', 'Tomar com refeição', true);

-- CREATINE
INSERT INTO supplements (name, description, category, form, serving_size, serving_unit, servings_per_container,
                         main_ingredient, ingredient_amount, ingredient_unit, recommended_dosage, usage_instructions, verified)
VALUES
    ('Creatina Monohidrato', 'Creatina monohidrato pura', 'CREATINE', 'POWDER', 3, 'GRAMS', 100, 'Creatine Monohydrate', 3, 'g', '3g ao dia', 'Pode ser consumida a qualquer momento do dia', true),
    ('Creatina Creapure', 'Creatina monohidrato alemã premium', 'CREATINE', 'POWDER', 5, 'GRAMS', 60, 'Creatine Monohydrate', 5, 'g', '5g ao dia', 'Misturar com água ou suco', true),
    ('Creatina HCL', 'Cloridrato de creatina', 'CREATINE', 'CAPSULE', 4, 'CAPSULES', 90, 'Creatine HCL', 3, 'g', '4 cápsulas ao dia', 'Tomar com água abundante', true);

-- PROTEIN SUPPLEMENTS
INSERT INTO supplements (name, description, category, form, serving_size, serving_unit, servings_per_container,
                         calories_per_serving, carbs_per_serving, protein_per_serving, fat_per_serving,
                         main_ingredient, ingredient_amount, ingredient_unit, recommended_dosage, usage_instructions, verified)
VALUES
    ('Caseína', 'Proteína de digestão lenta', 'PROTEIN', 'POWDER', 35, 'GRAMS', 28, 130, 4, 24, 1, 'Casein Protein', 24, 'g', '1 dose ao dia', 'Ideal antes de dormir', true),
    ('Albumina', 'Proteína da clara do ovo', 'PROTEIN', 'POWDER', 30, 'GRAMS', 33, 115, 2, 24, 0, 'Egg Albumin', 24, 'g', '1 dose ao dia', 'Misturar com água ou suco', true),
    ('Colágeno Hidrolisado', 'Peptídeos de colágeno', 'PROTEIN', 'POWDER', 10, 'GRAMS', 60, 40, 0, 10, 0, 'Collagen Peptides', 10, 'g', '1 dose ao dia', 'Misturar com qualquer bebida', true);

-- AMINO ACIDS
INSERT INTO supplements (name, description, category, form, serving_size, serving_unit, servings_per_container,
                         main_ingredient, ingredient_amount, ingredient_unit, recommended_dosage, usage_instructions, verified)
VALUES
    ('BCAA 2:1:1', 'Aminoácidos de cadeia ramificada', 'AMINO_ACIDS', 'POWDER', 10, 'GRAMS', 30, 'BCAA', 5000, 'mg', '1 dose durante treino', 'Misturar com água', true),
    ('L-Glutamina', 'Glutamina livre', 'AMINO_ACIDS', 'POWDER', 5, 'GRAMS', 60, 'L-Glutamine', 5, 'g', '5g ao dia', 'Tomar após treino ou antes de dormir', true),
    ('L-Arginina', 'Arginina livre', 'AMINO_ACIDS', 'CAPSULE', 3, 'CAPSULES', 60, 'L-Arginine', 1500, 'mg', '3 cápsulas ao dia', 'Tomar com estômago vazio', true),
    ('L-Carnitina', 'Carnitina tartarato', 'AMINO_ACIDS', 'CAPSULE', 2, 'CAPSULES', 60, 'L-Carnitine', 1000, 'mg', '2 cápsulas ao dia', 'Tomar antes do exercício', true),
    ('Taurina', 'Taurina livre', 'AMINO_ACIDS', 'CAPSULE', 2, 'CAPSULES', 90, 'Taurine', 1000, 'mg', '2 cápsulas ao dia', 'Tomar com água', true),
    ('L-Tirosina', 'Tirosina livre', 'AMINO_ACIDS', 'CAPSULE', 2, 'CAPSULES', 60, 'L-Tyrosine', 1000, 'mg', '2 cápsulas ao dia', 'Tomar com estômago vazio', true),
    ('EAA', 'Aminoácidos essenciais completos', 'AMINO_ACIDS', 'POWDER', 15, 'GRAMS', 20, 'Essential Amino Acids', 10, 'g', '1 dose ao dia', 'Tomar durante ou após treino', true);

-- PRE-WORKOUT
INSERT INTO supplements (name, description, category, form, serving_size, serving_unit, servings_per_container,
                         main_ingredient, ingredient_amount, ingredient_unit, recommended_dosage, usage_instructions, verified)
VALUES
    ('Pré-treino', 'Fórmula pré-treino com cafeína', 'PRE_WORKOUT', 'POWDER', 10, 'GRAMS', 30, 'Caffeine', 200, 'mg', '1 dose 30min antes', 'Misturar com água fria', true),
    ('Beta-Alanina', 'Beta-alanina pura', 'PRE_WORKOUT', 'POWDER', 3, 'GRAMS', 100, 'Beta-Alanine', 3, 'g', '3g ao dia', 'Dividir em 2-3 doses', true),
    ('Citrulina Malato', 'L-Citrulina com malato', 'PRE_WORKOUT', 'POWDER', 6, 'GRAMS', 50, 'L-Citrulline Malate', 6, 'g', '6g antes do treino', 'Misturar com água', true),
    ('Cafeína', 'Cafeína anidra', 'PRE_WORKOUT', 'CAPSULE', 1, 'CAPSULES', 100, 'Caffeine', 200, 'mg', '1 cápsula ao dia', 'Tomar 30-45min antes do exercício', true);

-- WEIGHT MANAGEMENT (replacing ADAPTOGENS category)
INSERT INTO supplements (name, description, category, form, serving_size, serving_unit, servings_per_container,
                         main_ingredient, ingredient_amount, ingredient_unit, recommended_dosage, usage_instructions, verified)
VALUES
    ('Ashwagandha', 'Extrato de Withania somnifera', 'WEIGHT_LOSS', 'CAPSULE', 2, 'CAPSULES', 60, 'Ashwagandha Extract', 600, 'mg', '2 cápsulas ao dia', 'Tomar com as refeições', true),
    ('Rhodiola Rosea', 'Extrato de rhodiola', 'WEIGHT_LOSS', 'CAPSULE', 1, 'CAPSULES', 60, 'Rhodiola Extract', 400, 'mg', '1 cápsula ao dia', 'Tomar pela manhã com estômago vazio', true),
    ('Ginseng', 'Extrato de Panax ginseng', 'ENERGY', 'CAPSULE', 1, 'CAPSULES', 60, 'Ginseng Extract', 500, 'mg', '1 cápsula ao dia', 'Tomar pela manhã', true),
    ('Cúrcuma', 'Extrato de curcumina', 'OTHER', 'CAPSULE', 2, 'CAPSULES', 60, 'Curcumin', 500, 'mg', '2 cápsulas ao dia', 'Tomar com refeição contendo gordura', true),
    ('Spirulina', 'Spirulina em pó', 'OTHER', 'POWDER', 5, 'GRAMS', 60, 'Spirulina', 5, 'g', '5g ao dia', 'Misturar com suco ou água', true),
    ('Chlorella', 'Chlorella em comprimidos', 'OTHER', 'TABLET', 6, 'TABLETS', 100, 'Chlorella', 3, 'g', '6 comprimidos ao dia', 'Tomar com água', true);

-- DIGESTIVE HEALTH (using PROBIOTICS category)
INSERT INTO supplements (name, description, category, form, serving_size, serving_unit, servings_per_container,
                         main_ingredient, ingredient_amount, ingredient_unit, recommended_dosage, usage_instructions, verified)
VALUES
    ('Probióticos', 'Mix de lactobacilos e bifidobactérias', 'PROBIOTICS', 'CAPSULE', 1, 'CAPSULES', 60, 'Probiotic Blend', 10, 'billion CFU', '1 cápsula ao dia', 'Tomar com estômago vazio', true),
    ('Enzimas Digestivas', 'Complexo de enzimas digestivas', 'PROBIOTICS', 'CAPSULE', 2, 'CAPSULES', 90, 'Digestive Enzymes', 500, 'mg', '2 cápsulas antes das refeições', 'Tomar com abundante água', true),
    ('Fibras Solúveis', 'Psyllium husk', 'OTHER', 'POWDER', 5, 'GRAMS', 60, 'Psyllium Husk', 5, 'g', '5g ao dia', 'Misturar com bastante água', true),
    ('L-Glutamina Intestinal', 'Glutamina para saúde intestinal', 'AMINO_ACIDS', 'POWDER', 5, 'GRAMS', 60, 'L-Glutamine', 5, 'g', '5g ao dia', 'Tomar com estômago vazio', true);

-- SLEEP AND RELAXATION (using OTHER category)
INSERT INTO supplements (name, description, category, form, serving_size, serving_unit, servings_per_container,
                         main_ingredient, ingredient_amount, ingredient_unit, recommended_dosage, usage_instructions, verified)
VALUES
    ('Melatonina', 'Hormônio do sono', 'OTHER', 'TABLET', 1, 'TABLETS', 60, 'Melatonin', 3, 'mg', '1 comprimido à noite', 'Tomar 30-60min antes de dormir', true),
    ('Melatonina 5mg', 'Melatonina dose mais alta', 'OTHER', 'TABLET', 1, 'TABLETS', 60, 'Melatonin', 5, 'mg', '1 comprimido à noite', 'Tomar 30min antes de dormir', true),
    ('Valeriana', 'Extrato de Valeriana officinalis', 'OTHER', 'CAPSULE', 2, 'CAPSULES', 60, 'Valerian Extract', 500, 'mg', '2 cápsulas à noite', 'Tomar 1h antes de dormir', true),
    ('GABA', 'Ácido gama-aminobutírico', 'AMINO_ACIDS', 'CAPSULE', 2, 'CAPSULES', 60, 'GABA', 750, 'mg', '2 cápsulas à noite', 'Tomar com estômago vazio', true),
    ('L-Teanina', 'L-teanina extraída do chá verde', 'AMINO_ACIDS', 'CAPSULE', 1, 'CAPSULES', 60, 'L-Theanine', 200, 'mg', '1 cápsula ao dia', 'Pode ser tomada a qualquer momento', true);

-- WEIGHT MANAGEMENT
INSERT INTO supplements (name, description, category, form, serving_size, serving_unit, servings_per_container,
                         main_ingredient, ingredient_amount, ingredient_unit, recommended_dosage, usage_instructions, verified)
VALUES
    ('CLA', 'Ácido linoleico conjugado', 'WEIGHT_LOSS', 'SOFTGEL', 3, 'CAPSULES', 60, 'CLA', 1000, 'mg', '3 cápsulas ao dia', 'Tomar com as refeições', true),
    ('Termogênico', 'Fórmula termogênica natural', 'WEIGHT_LOSS', 'CAPSULE', 2, 'CAPSULES', 60, 'Green Tea Extract', 400, 'mg', '2 cápsulas ao dia', 'Tomar antes das refeições principais', true),
    ('Quitosana', 'Fibra de quitosana', 'WEIGHT_LOSS', 'CAPSULE', 3, 'CAPSULES', 90, 'Chitosan', 500, 'mg', '3 cápsulas antes das refeições', 'Tomar com abundante água', true),
    ('Garcinia Cambogia', 'Extrato de Garcinia cambogia', 'WEIGHT_LOSS', 'CAPSULE', 2, 'CAPSULES', 60, 'HCA', 500, 'mg', '2 cápsulas antes das refeições', 'Tomar 30-60min antes de comer', true);

-- WEIGHT GAIN (using existing category)
INSERT INTO supplements (name, description, category, form, serving_size, serving_unit, servings_per_container,
                         calories_per_serving, carbs_per_serving, protein_per_serving, fat_per_serving,
                         main_ingredient, ingredient_amount, ingredient_unit, recommended_dosage, usage_instructions, verified)
VALUES
    ('Hipercalórico', 'Massa hipercalórica com proteínas', 'WEIGHT_GAIN', 'POWDER', 100, 'GRAMS', 15, 380, 65, 20, 3, 'Carbohydrate Blend', 65, 'g', '1-2 doses ao dia', 'Misturar com leite ou água', true),
    ('Mass Gainer', 'Ganho de massa muscular', 'WEIGHT_GAIN', 'POWDER', 150, 'GRAMS', 10, 600, 110, 25, 5, 'Protein + Carbs', 25, 'g', '1 dose pós-treino', 'Misturar bem com líquido', true),
    ('Maltodextrina', 'Carboidrato de rápida absorção', 'WEIGHT_GAIN', 'POWDER', 30, 'GRAMS', 50, 120, 30, 0, 0, 'Maltodextrin', 30, 'g', '30g durante treino', 'Misturar com água', true);