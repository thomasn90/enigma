package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Thomas Nguyen
 */
class MovingRotor extends Rotor {
    /** Number of Notches I have. */
    private String _notches;


    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _notches = notches;
    }

    /** Return the notches I have. */
    String notches() {
        return _notches;
    }
    @Override
    boolean rotates() {
        return true;
    }

    @Override
    void advance() {
        this.set(permutation().wrap(this.setting() + 1));
    }

    @Override
    boolean atNotch() {
        for (int x = 0; x < _notches.length(); x++) {
            if (permutation().wrap(alphabet().
                    toInt(_notches.charAt(x))) == this.setting()) {
                return true;
            }
        }
        return false;
    }

}
