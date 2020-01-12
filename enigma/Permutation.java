package enigma;

import java.util.HashMap;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Thomas Nguyen
 */
class Permutation {
    /** Number of cycles I have. */
    private String _cycles;

    /** Map that I have. */
    private HashMap _map;
    /**
     * Set this Permutation to that specified by CYCLES, a string in the
     * form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     * is interpreted as a permutation in cycle notation.  Characters in the
     * alphabet that are not included in any cycle map to themselves.
     * Whitespace is ignored.
     */


    Permutation(String cycles, Alphabet alphabet) {
        HashMap<Character, Character> map = new HashMap<>();
        _cycles = cycles;
        _alphabet = alphabet;
        _map = map;
        char first = ' ';
        if (!paranthesis(cycles)) {
            throw new AssertionError("Incorrect cycle");
        }
        for (int x = 0; x < cycles.length(); x++) {
            if (cycles.charAt(x) != '('
                    && cycles.charAt(x) != ' ' && cycles.charAt(x) != ')') {
                if (cycles.charAt(x - 1) == '(') {
                    first = cycles.charAt(x);
                }
                if (cycles.charAt(x + 1) != ')') {
                    map.put(cycles.charAt(x), cycles.charAt(x + 1));
                } else {
                    map.put(cycles.charAt(x), first);
                }
            }
        }
        for (int e = 0; e < alphabet.size(); e++) {
            map.putIfAbsent(alphabet.toChar(e), alphabet.toChar(e));
        }
    }

    /**
     * Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     * c0c1...cm.
     */
    private void addCycle(String cycle) {
        this._cycles += cycle;
    }

    /** Return the map I have. */
    HashMap map() {
        return _map;
    }

    /** Return the number of cycles I have. */
    String cycles() {
        return _cycles;
    }
    /**
     * Return the value of P modulo the size of this permutation.
     */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /**
     * Returns the size of the alphabet I permute.
     */
    int size() {
        return _alphabet.size();
    }

    /**
     * Return the result of applying this permutation to P modulo the
     * alphabet size.
     */
    int permute(int p) {
        return _alphabet.toInt((char) _map.get(_alphabet.toChar(wrap(p))));
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        for (int x = 0; x < _alphabet.size(); x++) {
            if (_alphabet.toChar(wrap(c))
                    == (char) _map.get(_alphabet.toChar(x))) {
                return x;
            }
        }
        return c;
    }

    /** Return the result of applying this permutation
     * to the index of P
         *  in ALPHABET, and converting the result
     *  to a character of ALPHABET. */
    char permute(char p) {
        return (char) _map.get(p);
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        for (int x = 0; x < _alphabet.size(); x++) {
            if (c == (char) _map.get(_alphabet.toChar(x))) {
                return _alphabet.toChar(x);
            }
        }
        return c;
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        if (_map.get(this) == this) {
            return false;
        }
        return true;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** Return true iff there is even # parenthesis
     *  permutation for which no value maps to itself).
     *  @param p is string to check if there is parenthesis*/
    boolean paranthesis(String p) {
        int open = 0;
        int close = 0;
        for (int x = 0; x < p.length(); x++) {
            if (p.charAt(x) == '(') {
                open++;
            } else if (p.charAt(x) == ')') {
                close++;
            }
        }
        return open == close;
    }

}
