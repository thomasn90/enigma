package enigma;

import java.util.HashMap;
import java.util.Collection;
import java.util.HashSet;

/** Class that represents a complete enigma machine.
 *  @author Thomas Nguyen
 */
class Machine {
    /**
     * initialize a new numRotors variable.
     */
    private int _numRotors;
    /**
     * initialize a new pawls variable.
     */
    private int _pawls;
    /** Return the number of rotor slots I have. */
    private Collection<Rotor> _allRotors;
    /** Return the plugbaord I have. */
    private Permutation _plugboard;
    /** Return the number of rotors I have. */
    private Rotor[] _rotors;
    /** Return the number of rotors to move. */
    private HashSet<Rotor> _moveRotors;
    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _allRotors = allRotors;
    }

    /** Return the number of rotor slots I have. */
    Permutation plugboard() {
        return _plugboard;
    }
    /** Return the number of rotors I have. */
    Rotor[] rotors() {
        return _rotors;
    }
    /** Return the number of rotor slots I have. */
    HashSet<Rotor> moveRotors() {
        return _moveRotors;
    }
    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /**
     * Return the all Rotors I have.
     */
    Collection<Rotor> allRotors() {
        return _allRotors;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        _rotors = new Rotor[rotors.length];
        HashMap<String, Rotor> mapping = new HashMap<>();
        if (rotors.length != _numRotors) {
            throw new AssertionError("bad length");
        }
        for (Rotor x : _allRotors) {
            mapping.put(x.name(), x);
        }
        for (int x = 0; x < rotors.length; x++) {
            _rotors[x] = mapping.get(rotors[x]);
        }
        if (!_rotors[0].reflecting()) {
            throw new AssertionError("first rotor is not reflector");
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (setting.length() != _numRotors - 1) {
            throw new EnigmaException("incorrect setting length");
        }
        for (int x = 0; x < setting.length(); x++) {
            char setnum = setting.charAt(x);
            _rotors[x + 1].set(setnum);
        }
    }

    /** Set the _ring to RINGS. */
    void setAlphabetRing(String rings) {
        if (rings.length() != _numRotors - 1) {
            throw new EnigmaException("incorrect ring length");
        }
        for (int x = 0; x < rings.length(); x++) {
            _rotors[x + 1].setRing(rings.charAt(x));
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        movingRotors();
        for (Rotor y : _moveRotors) {
            y.advance();
        }
        if (_plugboard != null) {
            c = _plugboard.permute(_plugboard.wrap(c));
        }
        for (int x = _rotors.length - 1; x >= 0; x--) {
            c = _rotors[x].convertForward(c);
        }
        for (int i = 1; i < _rotors.length; i += 1) {
            c = _rotors[i].convertBackward(c);
        }
        if (_plugboard != null) {
            c = _plugboard.permute(_plugboard.wrap(c));
        }
        return c;
    }
    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        char[] message = new char[msg.length()];
        for (int x = 0; x < msg.length(); x++) {
            int integer = _alphabet.toInt(msg.charAt(x));
            int y = convert(integer);
            message[x] = _alphabet.toChar(y);
        }
        return new String(message);
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /**Make a HashSet that includes all the Rotors that need to be moved. */
    private void movingRotors() {
        HashSet<Rotor> moveRotors = new HashSet<>();
        _moveRotors = moveRotors;
        for (int x = _rotors.length - 1; x > 0; x--) {
            if (x == _rotors.length - 1) {
                moveRotors.add(_rotors[x]);

            } else if (moveRotors.contains(_rotors[x + 1])) {
                if (_rotors[_rotors.length - 2].atNotch()) {
                    if (_rotors[_rotors.length - 2].rotates()
                            && !_rotors[_rotors.length - 2].atNotch()) {
                        moveRotors.add(_rotors[_rotors.length - 2]);
                    }
                    if (_rotors[_rotors.length - 2].rotates()
                            && _rotors[_rotors.length - 2].atNotch()
                            && _rotors[_rotors.length - 3].rotates()) {
                        moveRotors.add(_rotors[_rotors.length - 2]);
                    }
                }
                if (_rotors[x + 1].atNotch() && _rotors[x].rotates()) {
                    if (_rotors[x].atNotch() && _rotors[x - 1].rotates()) {
                        moveRotors.add(_rotors[x]);
                    }
                    if (!_rotors[x].atNotch()) {
                        moveRotors.add(_rotors[x]);
                    }
                }
            }
        }
    }
}
