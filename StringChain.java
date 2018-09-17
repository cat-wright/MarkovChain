import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * @author Catherine Wright Lab 6 - Markov Chain for CS251 101617696
 */
public class StringChain {

    private final int order;
    private final HashMap<Prefixes, Suffixes> MarkovMap = new HashMap<>();

    /**
     * Creates a new StringChain object to 
     * declare the order of the Markov chain
     * 
     * @param order
     */
    public StringChain(int order) {
        this.order = order;
    }

    /**
     * addItems method will add a new sequence of strings to
     * our empty stringchain, by using an iterator 
     * 
     * @param iterator
     */
    public void addItems(Iterator<String> iterator) {
        newPrefix add = new newPrefix();
        new FinalMapping<>(iterator, Optional::of).forEachRemaining(add);
        Collections.nCopies(order, Optional.<String> empty()).iterator()
                .forEachRemaining(add);
    }

    /** 
     *Takes our previous iterator from addItems and creates
     *a new iterator based on some applied function
     *
     * @param <String>
     * @param <R>
     */
    public class FinalMapping<String, R> implements Iterator<R> {
        private final Iterator<String> wrapString;
        private final Function<String, R> mapFunction;

        public FinalMapping(Iterator<String> wrapString,
                Function<String, R> mapFunction) {
            this.wrapString = wrapString;
            this.mapFunction = mapFunction;
        }

        @Override
        public boolean hasNext() {
            return wrapString.hasNext();
        }

        @Override
        public R next() {
            return mapFunction.apply(wrapString.next());
        }

        @Override
        public void remove() {
            wrapString.remove();
        }
    }

    /**
     * Uses a stream to create random objects in sequential order
     * 
     * @return
     */
    public Stream<String> randStream() {
        return randStream(new Random());
    }

   /**
    * Another stream of randomly generated objects using the nested 
    * randSup class 
    * @param rand
    */
    public Stream<String> randStream(Random rand) {
        return Stream.generate(new RandSup(rand));
    }
    
    /**
     * Creates a random list of length 'number'
     * 
     * @param number
     * @param rand
     */
    public List<String> generate(int number, Random rand) {
        return randStream(rand).limit(number).collect(Collectors.toList());
    }

    /**
     * newPrefix nested class will check if the new prefix is already on our
     * MarkovMap. If it is not, it adds the string to the map keys as a prefix.
     * If so, it adds the string to the map values as a suffix.
     */
    private class newPrefix implements Consumer<Optional<String>> {
        private Prefixes prefKey = new Prefixes();

        public void accept(Optional<String> t) {
            if (!MarkovMap.containsKey(prefKey)) {
                MarkovMap.put(prefKey, new Suffixes());
            }
            MarkovMap.get(prefKey).add(t);
            prefKey = prefKey.getNext(t);
        }
    }

    /**
     * Uses supplier interface methods to supply 
     * new random streams
     *
     */
    private class RandSup implements Supplier<String> {
        private final Random rand;
        private Prefixes prefKey = new Prefixes();

        public RandSup(Random rand) {
            this.rand = rand;
        }

        public String get() {
            Optional<String> t;
            do {
                t = MarkovMap.get(prefKey).nextRand(rand);
                prefKey = prefKey.getNext(t);
            } while (!t.isPresent());
            return t.get();
        }
    }

    /**
     *Prefixes class defines the prefixes added to the MarkovMap,
     *
     */
    private class Prefixes {
        private final LinkedList<Optional<String>> prefix;

        public Prefixes() {
            this(Collections.nCopies(order, Optional.empty()));
        }

        public Prefixes(Collection<Optional<String>> same) {
            prefix = new LinkedList<>(same);
        }

        public Prefixes(Collection<Optional<String>> previous,
                Optional<String> next) {
            this(previous);
            prefix.addLast(next);
            prefix.removeFirst();
        }

        public Prefixes getNext(Optional<String> next) {
            return new Prefixes(prefix, next);
        }

        @Override
        public boolean equals(Object obj) {
            return (obj != null)
                    && this.getClass().isAssignableFrom(obj.getClass())
                    && ((Prefixes) obj).prefix.equals(prefix);
        }

        @Override
        public int hashCode() {
            return prefix.hashCode();
        }
        
        @Override
        public String toString() {
            return prefix.toString();
        }
    }

    /**
     * Defines the suffixes in the MarkovMap, and creates a probability
     * check to account for each suffix
     *
     */
    private class Suffixes {
        private final LinkedHashMap<Optional<String>, Integer> suffix = new LinkedHashMap<>();
        private int totalValues;

        public void add(Optional<String> t) {
            if (suffix.containsKey(t)) {
                suffix.put(t, suffix.get(t) + 1);
            } else {
                suffix.put(t, 1);
            }
            totalValues++;
        }

        public int numSamples() {
            return totalValues;
        }

        public Optional<String> getNextTByIndex(int index) {
            if (index >= numSamples()) {
                throw new IllegalArgumentException();
            }
            for (Optional<String> key : suffix.keySet()) {
                index -= suffix.get(key);
                if (index < 0) {
                    return key;
                }
            }
            throw new RuntimeException();
        }

        public Optional<String> nextRand() {
            return nextRand(new Random());
        }

        public Optional<String> nextRand(Random rand) {
            return getNextTByIndex(rand.nextInt(numSamples()));
        }

        public String toString() {
            return suffix.toString();
        }
    }

}