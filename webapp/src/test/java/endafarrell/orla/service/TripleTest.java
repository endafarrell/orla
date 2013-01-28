package endafarrell.orla.service;

import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.*;

public class TripleTest {

//    @Test
//    public void basicTest() throws Exception {
//        Triple<Integer> triple = new Triple<Integer>(1, 2, 3);
//        assertNotNull(triple);
//    }
//
//    @Test
//    public void testOf() throws Exception {
//        Triple<Integer> triple = Triple.of(1, 2, 3);
//        assertNotNull(triple);
//    }
//
//    @Test
//    public void testEquals() throws Exception {
//        Triple<Integer> a = new Triple<Integer>(1, 2, 3);
//        Triple<Integer> b = new Triple<Integer>(1, 2, 3);
//        assertTrue(a.equals(b));
//        assertTrue(b.equals(a));
//        Triple<Integer> c = Triple.of(1, 2, 3);
//        assertTrue(a.equals(c));
//    }
//
//    @Test
//    public void testHashCodeForSame() throws Exception {
//        int wanted = 1000;
//        List<Triple<Integer>> triples = new ArrayList<Triple<Integer>>(wanted * 2);
//        for (int i = 0; i < wanted; i++) {
//            triples.add(new Triple<Integer>(123, 345, 456));
//        }
//        int smallestHashCode = Integer.MAX_VALUE;
//        int biggestHashCode = Integer.MIN_VALUE;
//        for (int i = 0; i < wanted; i++) {
//            Triple<Integer> triple = triples.get(i);
//            int thc = triple.hashCode();
//            smallestHashCode = (smallestHashCode > thc ? thc : smallestHashCode);
//            biggestHashCode = (biggestHashCode < thc ? thc : biggestHashCode);
//        }
//        assertEquals(smallestHashCode, biggestHashCode);
//    }
}
