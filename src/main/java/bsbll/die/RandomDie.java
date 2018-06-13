package bsbll.die;

import static java.util.Objects.requireNonNull;
import static tzeth.preconds.MorePreconditions.checkPositive;

import java.util.Random;

import javax.annotation.concurrent.Immutable;

@Immutable
final class RandomDie implements Die {
    private final int sides;
    private final Random random;
    
    public RandomDie(int sides, Random random) {
        this.sides = checkPositive(sides);
        this.random = requireNonNull(random);
    }

    @Override
    public int roll() {
        return random.nextInt(sides) + 1;
    }
}