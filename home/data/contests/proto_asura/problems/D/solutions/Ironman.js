
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
                //this.log("asteroid: " + JSON.stringify(asteroid));
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

            this.shield();

            if (this.count % 100 === 0)
                this.fireSecondary();
            else
                this.firePrimary()
                    .then((res) => {
                        this.log(res);
                    })
                    .catch(() => {
                        this.log("LOCK")
                    });

            if (this.count % 100 === 0)
                this.steerRight();

            this.count++;
        }
    }


    // ------------------------ Skeleton --------------------------------

    exports.Player = Ironman;
}(this));
