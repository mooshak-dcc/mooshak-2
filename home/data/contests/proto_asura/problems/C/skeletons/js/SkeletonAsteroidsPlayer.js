
// ------------------------ Skeleton --------------------------------

load('asteroids/wrappers/js/AsteroidsPlayer.js');

(function (exports) {'use strict';

    /**
     * Asteroids player in Javascript
     *
     * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
     */
    function MyAsteroidsPlayer() {
        AsteroidsPlayer.call(this);
    }

    MyAsteroidsPlayer.prototype = Object.create(AsteroidsPlayer.prototype);

    // ---------------------- Your code below ---------------------------

    /**
     * Get the name of the player
     *
     * @returns {string} name of the player
     */
    MyAsteroidsPlayer.prototype.getName = function () {
        return 'José Carlos Paiva';
    };

    /**
     * Initialize the player
     */
    MyAsteroidsPlayer.prototype.init = function () {


    };

    /**
     * Execute player action
     */
    MyAsteroidsPlayer.prototype.execute = function () {


    };

    // ------------------------ Skeleton --------------------------------

    exports.Player = MyAsteroidsPlayer;
}(this));

