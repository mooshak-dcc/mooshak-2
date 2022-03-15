
/**
 * Utilities for dealing with Input/Output
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
class IOProcessor {

    /**
     * Read a line from standard input
     *
     * @returns {string} the line read from the input
     */
    static readln() {

        // spider monkey has synchronous mode
        return readline();
    }

    /**
     * Write a line with a message to the standard output
     *
     * @param message {string} the message to write to the standard output
     */
    static writeln(message) {
        print(message);
    }
}

