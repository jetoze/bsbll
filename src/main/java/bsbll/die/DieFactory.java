package bsbll.die;

import java.util.Random;

@FunctionalInterface
public interface DieFactory {

    Die newDie(int sides);
 
    // TODO: Use caching, so that the same die is returned in each request
    //       with the same number of sides. 
    //       --> Consider using ThreadLocalRandom.
    
    public static DieFactory random() {
        return sides -> new RandomDie(sides, new Random());
    }
    
    public static DieFactory random(long seed) {
        return sides -> new RandomDie(sides, new Random(seed));
    }
    
}
