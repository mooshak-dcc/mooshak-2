
// ------------------------ Skeleton --------------------------------

load('asteroids/wrappers/es6/AsteroidsPlayer.js');

(function (exports) {'use strict';

    /**
     * Asteroids player in Javascript
     *
     * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
     */
    class Ironman extends AsteroidsPlayer {

        // ---------------------- Your code below ---------------------------

        /**
         * Get the name of the player
         *
         * @returns {string} name of the player
         */
        getName() {
            return 'Iron Man';
        }

        /**
         * Initialize the player
         */
        init() {
            this.count = 0;

            this.onAsteroidDetected((asteroid) => {
                const {position, energy, shield_counter} = this.state();
                this.log(JSON.stringify(shield_counter ));
                this.log(energy)
                if (shield_counter < 5) {
                    if (this.distance(position, { x: asteroid[0], y: asteroid[1] }) < 60)
                        this.shield();
                }
            });

            this.onShipDetected((ship) => {
                //this.log("ship: " + JSON.stringify(ship));
            });

            this.onBulletDetected((bullet) => {
                //this.log("bullet: " + JSON.stringify(bullet));
            });
        }

        /**
         * Execute player action
         */
        execute() {

            this.log(this.state().health);

            this.count++;
        }

        distance(from, to) {
            return Math.sqrt((from.y - to.y)*(from.y - to.y) + (from.x - to.x)*(from.x - to.x))
        }
    }


    // ------------------------ Skeleton --------------------------------

    exports.Player = Ironman;
}(this));
