package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @author
 */
public class MovingRotorTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Rotor rotor;
    private String alpha = UPPER_STRING;

    /** Check that rotor has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkRotor(String testId,
                            String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, rotor.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d (%c)", ci, c),
                         ei, rotor.convertForward(ci));
            assertEquals(msg(testId, "wrong inverse of %d (%c)", ei, e),
                         ci, rotor.convertBackward(ei));
        }
    }

    /** Set the rotor to the one with given NAME and permutation as
     *  specified by the NAME entry in ROTORS, with given NOTCHES. */
    private void setRotor(String name, HashMap<String, String> rotors,
                          String notches) {
        rotor = new MovingRotor(name, new Permutation(rotors.get(name), UPPER),
                                notches);
    }

    /* ***** TESTS ***** */

    @Test
    public void checkRotorAtA() {
        setRotor("I", NAVALA, "");
        checkRotor("Rotor I (A)", UPPER_STRING, NAVALA_MAP.get("I"));
    }

    @Test
    public void checkRotorAdvance() {
        setRotor("I", NAVALA, "");
        rotor.advance();
        checkRotor("Rotor I advanced", UPPER_STRING, NAVALB_MAP.get("I"));
    }

    @Test
    public void checkSetRotors() {
        setRotor("I", NAVALA, "");
        String setting = "AXLE";
        rotor.set(setting.charAt(1));
    }

    @Test
    public void checkRotorSet() {
        setRotor("I", NAVALA, "");
        rotor.set(25);
        checkRotor("Rotor I set", UPPER_STRING, NAVALZ_MAP.get("I"));
    }
    @Test
    public void test1() {
        setRotor("I", NAVALA, "");
        rotor.advance();
        checkRotor("Rotor I advanced", UPPER_STRING, NAVALB_MAP.get("I"));
    }

    @Test
    public void test2() {
        ArrayList<Rotor> rotors = new ArrayList<Rotor>();
        rotors.add(new MovingRotor("I", new Permutation(
                "(AELTPHQXRU) (BKNW) (CMOY) (DFG) (IV) (JZ) (S)", UPPER), "Q"));
        rotors.add(new MovingRotor("II", new Permutation("(FIXVYOMW)"
                + " (CDKLHUP) (ESZ) (BJ) (GR) (NT) (A) (Q)", UPPER), "E"));
        rotors.add(new MovingRotor("III", new Permutation(
                "(ABDHPEJT) (CFLVMZOYQIRWUKXSG) (N)", UPPER), "V"));
        rotors.add(new MovingRotor("IV", new Permutation(
                "(AEPLIYWCOXMRFZBSTGJQNH) (DV) (KU)", UPPER), "J"));
        rotors.add(new MovingRotor("V", new Permutation(
                "(AVOLDRWFIUQ)(BZKSMNHYC) (EGTJPX)", UPPER), "Z"));
        rotors.add(new MovingRotor("VI", new Permutation(
                "(AJQDVLEOZWIYTS) (CGMNHFUX) (BPRK)", UPPER), "Z"));
        rotors.add(new MovingRotor("VII", new Permutation(
                "(ANOUPFRIMBZTLWKSVEGCJYDHXQ)", UPPER), "Z"));
        rotors.add(new MovingRotor("VIII", new Permutation(
                "(AFLSETWUNDHOZVICQ) (BKJ) (GXY) (MPR)", UPPER), "Z"));
        rotors.add(new FixedRotor("BETA", new Permutation(
                "(ALBEVFCYODJWUGNMQTZSKPR) (HIX)", UPPER)));
        rotors.add(new FixedRotor("GAMMA", new Permutation(
                "(AFNIRLBSQWVXGUZDKMTPCOYJHE)", UPPER)));
        rotors.add(new Reflector("B", new Permutation(
                "(AE) (BN) (CK) (DQ) (FU) (GY) (HW) (IJ)"
                        + " (LO) (MP) (RX) (SZ) (TV)", UPPER)));
        rotors.add(new Reflector("C", new Permutation(
                "(AR) (BD) (CO) (EJ) (FN) (GT) (HK)"
                        + " (IV) (LM) (PW) (QZ) (SX) (UY)", UPPER)));
        Machine machine = new Machine(UPPER, 5, 3, rotors);
        machine.insertRotors(new String[]{"B", "BETA", "III", "IV", "I"});
        machine.setRotors("AXLE");
        machine.setPlugboard(new Permutation(
                "(HQ) (EX) (IP) (TR) (BY)", UPPER));
        assertEquals("QVPQSOKOILPUBKJZPISF",
                machine.convert("FROMHISSHOULDERHIAWA"));

    }
    @Test
    public void test3() {
        String blort = "HIJKLMNOPQ";
        Alphabet alph = new Alphabet(blort);
        ArrayList<Rotor> rotors = new ArrayList<Rotor>();
        rotors.add(new Reflector("RF", new Permutation(
                "(HI) (JK) (LM) (NO) (PQ)", alph)));
        rotors.add(new MovingRotor("Rot1",
                new Permutation("(HIJK) (LMNOPQ)", alph), "J"));
        rotors.add(new MovingRotor("Rot2",
                new Permutation("(IHKJMLONQP)", alph), "N"));
        Machine machine = new Machine(alph, 3, 2, rotors);
        machine.insertRotors(new String[]{"RF", "Rot1", "Rot2"});
        machine.setRotors("HQ");
        assertEquals("NHQOHLMIJIKLQHQMNIK",
                machine.convert("MILLIONLOLLIPOPJILL"));
    }
    @Test
    public void test4() {
        String bloot = "HIJKLMNOPQ";
        Alphabet alph = new Alphabet(bloot);
        ArrayList<Rotor> rotors = new ArrayList<Rotor>();
        rotors.add(new Reflector("RF", new Permutation(
                "(HI) (JK) (LM) (NO) (PQ)", alph)));
        rotors.add(new MovingRotor("Rot1",
                new Permutation("(HIJK) (LMNOPQ)", alph), "J"));
        rotors.add(new MovingRotor("Rot2",
                new Permutation("(IHKJMLONQP)", alph), "N"));
        Machine machine = new Machine(alph, 3, 2, rotors);
        machine.insertRotors(new String[]{"RF", "Rot1", "Rot2"});
        machine.setRotors("KO");
        machine.convert(4);
    }
}
