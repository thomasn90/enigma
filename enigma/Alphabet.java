package enigma;


/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author Thomas Nguyen
 */
class Alphabet {
    /** Return the chars I have. */
    private String _chars;

    /** A new alphabet containing CHARS.  Character number #k has index
     *  K (numbering from 0). No character may be duplicated. */
    Alphabet(String chars) {
        for (int i = 0; i < chars.length(); i++) {
            for (int j = 0; j < chars.length(); j++) {
                if (i != j && chars.charAt(i) == chars.charAt(j)) {
                    throw new AssertionError("Duplicates Found");
                }
            }
        }
        this._chars = chars;
    }

    /** A default alphabet of all upper-case characters. */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /** Return the number of rotor slots I have. */
    String chars() {
        return _chars;
    }

    /** Returns the size of the alphabet. */
    int size() {
        return this._chars.length();
    }

    /** Returns true if preprocess(CH) is in this alphabet. */
    boolean contains(char ch) {
        for (int index = 0; index < _chars.length(); index++) {
            if (ch == this._chars.charAt(index)) {
                return true;
            }
        }
        return false;
    }


    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {
        assert 0 <= index && index < size();
        return this._chars.charAt(index);
    }


    /** Returns the index of character preprocess(CH), which must be in
     *  the alphabet. This is the inverse of toChar(). */
    int toInt(char ch) {
        int x = 0;
        if (contains(ch)) {
            for (int check = 0; check < size(); check++) {
                if (ch == toChar(check)) {
                    x = check;
                }
            }
        }
        return x;
    }

}
