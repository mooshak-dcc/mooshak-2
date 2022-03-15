
(function (exports) {'use strict';

    /**
     * Functions that provide game-specific functionality to Asteroids players
     *
     * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
     */
    var AsteroidsPlayer = function () {
        PlayerWrapper.call(this);
    };

    AsteroidsPlayer.prototype = Object.create(PlayerWrapper.prototype);

    /**
     * Update the state of the game on the player
     *
     * @param state_update {object} the state update
     */
    AsteroidsPlayer.prototype.update = function (state_update) {


    };

    /**
     * Manage player lifecycle during the game, invoking the other methods when required
     */
    AsteroidsPlayer.prototype.run = function () {


    };

    exports.AsteroidsPlayer = AsteroidsPlayer;
}(this));
