
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
            this.last_shot = 0;
            this.count = 0;
            this.thrusted = false;
        }

        /**
         * Execute player action
         */
        execute() {

            this.count++;

            const {position, velocity, heading, energy, health, shield_counter} = this.state();

            if (!this.asteroids().length)
                return;

            const [closest, ...remaining] = this.asteroids();
            let dist = this.distance(position, { x: closest[0], y: closest[1] });
            if (!this.thrusted && dist > 400) {
                this.thrusted = true;
                this.thrust();
                return
            }

            this.log(heading);

            let headingToClosest = this.heading(position, { x: closest[0], y: closest[1] });

            this.log(headingToClosest);

            if (heading < headingToClosest - 3)
                this.steerRight();
            else if (heading > headingToClosest + 3)
                this.steerLeft();
            else if (dist <= 400 && this.count - this.last_shot > 300) {
                this.firePrimary();
                this.last_shot = this.count;
            }
        }

        heading(from, to) {
            return (Math.atan2((from.y - to.y), (from.x - to.x)) * 180 / Math.PI -90 + 360) % 360;
        }

        distance(from, to) {
            return Math.sqrt((from.y - to.y)*(from.y - to.y) + (from.x - to.x)*(from.x - to.x))
        }
    }


    // ------------------------ Skeleton --------------------------------

    exports.Player = Ironman;
}(this));