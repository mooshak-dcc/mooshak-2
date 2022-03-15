
// ------------------------ Skeleton --------------------------------

load('asteroids/wrappers/es6/AsteroidsPlayer.js');

(function (exports) {'use strict';

    /**
     * Asteroids player in Javascript
     *
     * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
     */
    class Superman extends AsteroidsPlayer {

        // ---------------------- Your code below ---------------------------

        /**
         * Get the name of the player
         *
         * @returns {string} name of the player
         */
        getName() {
            return 'José Carlos Paiva';
        }

        /**
         * Initialize the player
         */
        init() {
            this.count = 0;

            this.onAsteroidDetected((asteroid) => {
                //this.log("asteroid: " + JSON.stringify(asteroid));
            });

            this.onShipDetected((ship) => {
                //this.log("ship: " + JSON.stringify(ship));
            });

            this.onBulletDetected((bullet) => {
                this.log("bullet: " + JSON.stringify(bullet));
            });
        }

        /**
         * Execute player action
         */
        execute() {

            if (this.count % 1000 === 0)
                this.thrust();

            if (this.count % 100 === 0)
                this.fireSecondary();
            else
                this.firePrimary()
                    .then((res) => {
                        this.log(res);
                    });

            this.steerLeft();

            //this.shield();

            this.count++;
        }
    }


    // ------------------------ Skeleton --------------------------------

    exports.Player = Superman;
}(this));
