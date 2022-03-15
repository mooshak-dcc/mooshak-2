/**
 * Utilities for dealing with Input/Output
 */
var IOProcessor = {};

/**
 * Read a line from standard input
 */
IOProcessor.readln = function () {

    return readline();
};

/**
 * Write a line with a message to the standard output
 *
 * @param message {string} the message to write to the standard output
 */
IOProcessor.writeln = function (message) {
    print(message);
};

