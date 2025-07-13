package model.enemies;

import static model.Support.*;

/**
 * Интерфейс для проведения боев между персонажем и врагом.
 */
public interface Fighter {
    /**
     * Расчет атаки.
     * @return атаку бойца
     */
    public int makeAttackRoll();

    /**
     * Расчет защиты.
     * @return защиту бойца
     */
    public int makeDefenseRoll();

    /**
     * Расчет урона, наносимого оппоненту.
     * @return урон
     */
    public int getDamage();

    /**
     * Расчет получаемого урона.
     * @param damage изначальный урон
     */
    public void takeDamage(int damage);

    /**
     * Получение эффекта при удачной атаки по противнику.
     * @return код эффекта
     */
    public int getAttackEffect();

    /**
     * Получение эффекта при защите от противника.
     * @return код эффекта
     */
    public int getDefEffect();

    /**
     * Применить негативный эффект.
     * @param effect код эффекта
     */
    public void applyNegativeEffect(int effect);

    /**
     * Проверка на смерть бойца
     * @return true - если мертв, false - если жив.
     */
    public boolean isDead();

    /**
     * Перечисление эффектов в бою.
     */
    public static enum Effect {NO_EFFECT, VAMPIRE, SLEEP, COUNTERATTACK}

    /**
     * Класс для проведения боев.
     */
    public class Fight {
        private Fighter attacker;
        private Fighter defender;

        public Fight(Fighter attacker) {
            this.attacker = attacker;
        }

        /**
         * Основная логика боя. Расчет защиты, атаки бойцов. Если удар попал, то расчет урона и применение эффектов.
         * @param defender боец которого атакуют.
         */
        public void getFight(Fighter defender) {
//            findEnemy(position, defenders);
            this.defender = defender;
            int attack = attacker.makeAttackRoll();
            int defense = defender.makeDefenseRoll();

            if (hitCheck(attack, defense)) {
                int damage = attacker.getDamage();
                defender.takeDamage(damage);
//                if (attacker instanceof Enemy) {
//                Enemy e = (Enemy) attacker;
                int effect = attacker.getAttackEffect();
                if (effect != Effect.NO_EFFECT.ordinal()) {
                    defender.applyNegativeEffect(effect);
                }
                if (defender.getDefEffect() == Effect.COUNTERATTACK.ordinal()) {
                    Fight f = new Fight(defender);
                    f.getFight(attacker);
                }
//                }
            }
//            if (defender.isDead()) {
//                defenders.remove(defender);
//                defender = null;
//            }
        }

        /**
         * Проверка на попадание по противнику
         * @param attack ловкость атакующего
         * @param defense ловкость защитника
         * @return true - если попал, else - обратное
         */
        public boolean hitCheck(int attack, int defense){
            int chanceOfHitting = BASE_HIT_CHANCE + (attack - defense) * HIT_CHANCE_MULTIPLIER;
            int realChanceOfHitting = Math.min(MAX_HIT_CHANCE, Math.max(MIN_HIT_CHANCE, chanceOfHitting));
            return randInDiaposone(MIN_PERCENTAGE, MAX_PERCENTAGE) <= realChanceOfHitting;
        }

//        public void findEnemy(Coordinate position, List<Enemy> defenders) {
//            for (Enemy c : defenders) {
//                if (position.equals(c.getPosition())){
//                    this.defender = c;
//                }
//            }
//        }
    } // class Fight

}
