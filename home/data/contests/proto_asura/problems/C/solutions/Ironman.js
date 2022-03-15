
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

            this.tested = false;
            this.test_shot_time = undefined;

            this.target = undefined;

            this.onAsteroidDetected((asteroid) => {
                //this.log("asteroid: " + JSON.stringify(asteroid));
            });

            this.onShipDetected((ship) => {
                this.log("ship: " + JSON.stringify(ship));
            });

            this.onBulletDetected((bullet) => {
                //this.log("bullet: " + JSON.stringify(bullet));
            });
        }

        /**
         * Execute player action
         */
        execute() {

            if (this.target === undefined && this.tested) {
                this.steerLeft();
                this.tested = false;
                this.test_shot_time = undefined;
            } else if (this.target === undefined && !this.tested) {

                if (this.test_shot_time === undefined) {
                    const { position, heading } = this.state();
                    const counter = this.count;

                    this.firePrimary()
                        .then((res) => {

                            const cur = this.state();
                            if ((this.count - counter) > 200)
                                return;

                            this.tested = true;

                            switch (res) {
                                case "NO_HIT":
                                case "HIT_ASTEROID":
                                case "DESTROYED_ASTEROID":
                                case "DESTROYED_SHIP":
                                    break;
                                case "HIT_SHIP":
                                case "HIT_SHIELD":

                                    this.target = {
                                        position: position,
                                        heading: heading,
                                        travel_time: (this.count - counter)
                                    };
                                    this.test_shot_time = undefined;
                                    break;
                            }
                        });
                    this.test_shot_time = this.count;
                    this.tested = false;
                } else if (this.count - this.test_shot_time > 200) {
                    this.test_shot_time = undefined;
                    this.tested = true;
                }
            } else if (this.target) {
                this.firePrimary()
                    .then((res) => {
                        switch (res) {
                            case "HIT_SHIP":
                            case "HIT_SHIELD":
                                break;
                            case "NO_HIT":
                            case "HIT_ASTEROID":
                            case "DESTROYED_ASTEROID":
                            case "DESTROYED_SHIP":
                                this.target = undefined;
                                this.tested = false;
                                break;
                        }
                    });
            }

            /*this.log(steering + " " + shooting);*/

            /*if (this.count === 0 || this.target === undefined  || (this.tested_shot_time && (this.count - this.tested_shot_time) > 200)) {

                if (this.tested) {
                    this.steerRight();
                    this.tested = false;
                    this.tested_shot_time = undefined;
                } else {
                    this.firePrimary()
                        .then((res) => {
                            switch (res) {
                                case "NO_HIT":
                                case "HIT_ASTEROID":
                                case "DESTROYED_ASTEROID":
                                case "DESTROYED_SHIP":
                                    break;
                                case "HIT_SHIP":
                                case "HIT_SHIELD":
                                    if (!this.target && (this.count - counter) < 200) {
                                        this.target = {
                                            position: position,
                                            heading: heading,
                                            travel_time: (this.count - counter)
                                        };
                                    }
                                    break;
                            }
                        });
                    this.tested = true;
                    this.tested_shot_time = this.count;
                }
            } else if (this.target !== undefined) {

                const { position, heading } = this.state();
                if (this.target.heading !== heading)
                    this.steerRight();
                else
                    this.firePrimary()
                        .then((res) => {
                            switch (res) {
                                case "HIT_SHIP":
                                case "HIT_SHIELD":
                                    this.tested = true;
                                    this.tested_shot_time = this.count;
                                    break;
                                case "NO_HIT":
                                case "HIT_ASTEROID":
                                case "DESTROYED_ASTEROID":
                                case "DESTROYED_SHIP":
                                    if (this.target) {
                                        this.target = undefined;
                                    }
                                    break;
                            }
                        });
            }*/



            this.count++;
        }
    }


    // ------------------------ Skeleton --------------------------------

    exports.Player = Ironman;
}(this));
