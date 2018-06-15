package bsbll.die;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

final class RandomDieFactory implements DieFactory {
    @Nullable
    private final Long seed;
    private final ConcurrentHashMap<Integer, Die> dice = new ConcurrentHashMap<>();
    
    public RandomDieFactory() {
        this.seed = null;
    }
    
    public RandomDieFactory(long seed) {
        this.seed = Long.valueOf(seed);
    }

    @Override
    public Die getDie(int sides) {
        return this.dice.computeIfAbsent(Integer.valueOf(sides), this::newDie);
    }
    
    private Die newDie(int sides) {
        // TODO: Consider using ThreadLocalRandom.
        Random rnd = (seed != null)
                ? new Random(seed.longValue())
                : new Random();
        return new RandomDie(sides, rnd);
    }

}
