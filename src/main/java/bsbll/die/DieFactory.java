package bsbll.die;

@FunctionalInterface
public interface DieFactory {

    /**
     * Gets a die with the given number of sides.
     * <p>
     * When calling this method several times with the same number of sides, the same Die instance
     * can be returned in each call.
     */
    Die getDie(int sides);
    
    public static DieFactory random() {
        return new RandomDieFactory();
    }
    
    public static DieFactory random(long seed) {
        return new RandomDieFactory(seed);
    }
}
