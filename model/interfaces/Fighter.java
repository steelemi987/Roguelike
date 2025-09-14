package model.interfaces;

import static model.Support.*;

/**
 * Интерфейс для проведения боев между персонажем и врагом.
 */
public interface Fighter {
    /**
     * Расчет атаки.
     * @return атаку бойца
     */
    int makeAttackRoll();

    /**
     * Расчет защиты.
     * @return защиту бойца
     */
    int makeDefenseRoll();

    /**
     * Расчет урона, наносимого оппоненту.
     * @return урон
     */
    int getDamage();

    /**
     * Расчет получаемого урона.
     * @param damage изначальный урон
     */
    void takeDamage(int damage);

    /**
     * Получение эффекта при удачной атаки по противнику.
     * @return код эффекта
     */
    int getAttackEffect();

    /**
     * Получение эффекта при защите от противника.
     * @return код эффекта
     */
    int getDefEffect();

    /**
     * Применить негативный эффект.
     * @param effect код эффекта
     */
    void applyNegativeEffect(int effect);

    /**
     * Проверка на смерть бойца
     * @return true - если мертв, false - если жив.
     */
    boolean isDead();

    /**
     * Перечисление эффектов в бою.
     */
     enum Effect {NO_EFFECT, VAMPIRE, SLEEP, COUNTERATTACK}

    /**
     * Класс для проведения боев.
     */
    class Fight {
        private final Fighter attacker;

        public Fight(Fighter attacker) {
            this.attacker = attacker;
        }

        /**
         * Основная логика боя. Расчет защиты, атаки бойцов. Если удар попал, то расчет урона и применение эффектов.
         * @param defender боец которого атакуют.
         */
        public void getFight(Fighter defender) {
            int attack = attacker.makeAttackRoll();
            int defense = defender.makeDefenseRoll();
            if (hitCheck(attack, defense)) {
                int damage = attacker.getDamage();
                defender.takeDamage(damage);
                int effect = attacker.getAttackEffect();
                if (effect != Effect.NO_EFFECT.ordinal()) {
                    defender.applyNegativeEffect(effect);
                }
                if (defender.getDefEffect() == Effect.COUNTERATTACK.ordinal()) {
                    Fight f = new Fight(defender);
                    f.getFight(attacker);
                }
            }
        }

        /**
         * Проверка на попадание по противнику
         * @param attack ловкость атакующего
         * @param defense ловкость защитника
         * @return true - если попал, else - обратное
         */
        private boolean hitCheck(int attack, int defense){
            int chanceOfHitting = BASE_HIT_CHANCE + (attack - defense) * HIT_CHANCE_MULTIPLIER;
            int realChanceOfHitting = Math.min(MAX_HIT_CHANCE, Math.max(MIN_HIT_CHANCE, chanceOfHitting));
            return randInDiapason(MIN_PERCENTAGE, MAX_PERCENTAGE) <= realChanceOfHitting;
        }
    } // class Fight
}
