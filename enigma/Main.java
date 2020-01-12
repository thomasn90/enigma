package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Set;


import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Thomas Nguyen
 */
public final class Main {
    /**
     * Initialize everyRotor as collection.
     */
    private Collection<Rotor> _everyRotor = new HashSet<>();
    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name ofia configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine m = readConfig();
        boolean initial = true;
        while (_input.hasNextLine()) {
            String line = _input.nextLine();
            while (line.isEmpty()) {
                line = " ";
            }
            if (line.charAt(0) != '*' && initial) {
                throw new EnigmaException("wrong config");
            }
            initial = false;
            if (line.charAt(0) == '*') {
                setUp(m, line);
            } else {
                String msg = line.replace(" ", "");
                printMessageLine(m.convert(msg));
            }
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            Collection<Rotor> everyRotor = new HashSet<>();
            String alphabet = _config.next();
            if (alphabet.contains("(") || alphabet.contains(")")
                    || alphabet.contains("*")) {
                throw new EnigmaException("Incorrect Format");
            }
            _alphabet = new Alphabet(alphabet);
            int numRotors = _config.nextInt();
            int pawls = _config.nextInt();
            while (_config.hasNext()) {
                everyRotor.add(this.readRotor());
            }
            _everyRotor = everyRotor;
            return new Machine(_alphabet, numRotors, pawls, everyRotor);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            String name = _config.next();
            String type = _config.next();
            StringBuilder cycles = new StringBuilder();
            while (_config.hasNext("\\(.*\\)")) {
                cycles.append(_config.next());
            }
            if (type.charAt(0) == 'M') {
                return new MovingRotor(name,
                        new Permutation(cycles.toString(),
                                _alphabet), type.substring(1));
            } else if (type.charAt(0) == 'N') {
                return new FixedRotor(name,
                        new Permutation(cycles.toString(), _alphabet));
            } else if (type.charAt(0) == 'R') {
                return new Reflector(name,
                        new Permutation(cycles.toString(), _alphabet));
            } else {
                throw new EnigmaException("wrong rotor");
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        String[] tokens = settings.split(" ");
        String[] rotors = Arrays.copyOfRange(tokens, 1, M.numRotors() + 1);
        String rings = "";
        String temp = "";
        Boolean checkring = false;
        checkNames(M, rotors);
        checkRepeat(rotors);
        reflect(rotors);
        if (rotors.length - 1 != tokens[M.numRotors() + 1].length()) {
            throw error("Number of settings is not equal to number of rotors");
        }
        M.insertRotors(rotors);
        if (tokens.length >= M.numRotors() + 3) {
            temp = tokens[M.numRotors() + 2];
            if (!temp.contains("(")) {
                rings = temp;
                M.setAlphabetRing(rings);
                checkring = true;
            }
        }
        if (tokens[M.numRotors() + 1].contains("(")) {
            M.setRotors("AAAA");
            String[] plugCycles = Arrays.copyOfRange(tokens,
                    M.numRotors() + 1, tokens.length);
            String cycles = String.join(",", plugCycles);
            M.setPlugboard(new Permutation(cycles, _alphabet));
        } else {
            if (tokens[M.numRotors() + 1].length() != M.numRotors() - 1) {
                throw new EnigmaException("Wrong Number of Arguments");
            }
            M.setRotors(tokens[M.numRotors() + 1]);
            if (!checkring) {
                String[] plugCycles = Arrays.copyOfRange(tokens,
                        M.numRotors() + 2, tokens.length);
                String cycles = String.join(",", plugCycles);
                M.setPlugboard(new Permutation(cycles, _alphabet));
            } else {
                String[] plugCycles = Arrays.copyOfRange(tokens,
                        M.numRotors() + 3, tokens.length);
                String cycles = String.join(",", plugCycles);
                M.setPlugboard(new Permutation(cycles, _alphabet));
            }
        }

    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        int point = 0;
        for (char c : msg.toCharArray()) {
            if (point != 0) {
                if (point % 5 == 0) {
                    _output.print(" ");
                }
            }
            _output.print(c);
            point++;
        }
        _output.println();
    }

    /**
     * Checks if any rotor in setting misnamed.
     * @param m machine.
     * @param rotors is array of strings of rotor names.
     */
    private void checkNames(Machine m, String[] rotors) {
        ArrayList<Boolean> matches = new ArrayList<>();
        Collection<Rotor> allofRotors = m.allRotors();
        for (String rotor : rotors) {
            for (Rotor x : allofRotors) {
                if (x.name().equals(rotor)) {
                    matches.add(true);
                }
            }
        }
        if (matches.size() < rotors.length) {
            throw new EnigmaException("Improper Name");
        }
    }

    /**
     * Checks if any of the rotors are repeated.
     * @param rotors are array of string rotors names.
     */
    private void checkRepeat(String[] rotors) {
        Set<String> rotorSet = new HashSet<>(Arrays.asList(rotors));
        if (rotorSet.size() < rotors.length) {
            throw new EnigmaException("Repeated Rotor");
        }
    }

    /**
     * Checks if the first rotor is a reflector.
     * @param rotors is array of string rotors names.
     */
    private void reflect(String[] rotors) {
        String initial = rotors[0];
        for (Rotor x : _everyRotor) {
            if (x.reflecting() && x.name().equals(initial)) {
                return;
            }
        }
        throw new EnigmaException("first rotor is not reflector");
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;
}
