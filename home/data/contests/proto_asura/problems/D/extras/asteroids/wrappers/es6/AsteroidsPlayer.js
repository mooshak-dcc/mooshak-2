(function (exports) {'use strict';

    const DEFAULT_ACTION = {
        thrust: false,
        steerLeft: false,
        steerRight: false,
        shield: false,
        fire: 0,
        fire_promise: undefined
    };

    const BULLET_ID_PREFIX = "bullet";

    /**
     * Functions that provide game-specific functionality to Asteroids players
     *
     * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
     */
    class AsteroidsPlayer extends PlayerWrapper {

        constructor () {
            super();

            this._state = {
                position: {
                    x: 0,
                    y: 0
                },
                velocity: 0.0,
                heading: 0.0,
                energy: 0,
                health: 0,
                shieldCounter: 0
            };

            this._current_action = Object.assign({}, DEFAULT_ACTION);
            this._fire_count = 0;

            // radar of the ship - has different handlers for each type of detected actors
            this._radar = {
                asteroid_handler: undefined,
                ship_handler: undefined,
                bullet_handler: undefined
            };

            // promises for bullets indexed by bullet ID
            this._bullet_promises = new Map();
        }

        /**
         * Update the state of the game on the player
         *
         * @param state_update {object} the state update
         */
        update (state_update) {

            if (!state_update || state_update.type === undefined)
                return;

            switch (state_update.type) {
                case "FULL_UPDATE":
                    this._updateCurrentState(state_update.object);
                    this._processBulletResults(state_update.object.bullets);
                    this._processNearestActors(state_update.object.actors);
                    break;
            }
        }

        /**
         * Manage player lifecycle during the game, invoking the other methods when required
         */
        run () {

            // game flow
            while (true) {

                this.readAndUpdate();
                this.execute();
                this.doAction("SHIP", [
                    this._current_action.thrust,
                    this._current_action.steerLeft,
                    this._current_action.steerRight,
                    this._current_action.shield,
                    this._current_action.fire,
                ]);
                this.sendAction();

                // if fired, increase fire counter and add promise to map
                if (this._current_action.fire > 0) {
                    if (this._current_action.fire_promise) {
                        this._bullet_promises.set(BULLET_ID_PREFIX + this._fire_count,
                            this._current_action.fire_promise);
                    }

                    this._fire_count++;
                }

                // reset current action
                this._current_action = Object.assign({}, DEFAULT_ACTION);
            }
        }

        /**
         * Sensors
         */

        /*x () {
            return this._state.x;
        }

        y () {
            return this._state.y;
        }

        velocity () {
            return this._state.velocity;
        }

        heading () {
            return this._state.heading;
        }

        health () {
            return this._state.health;
        }

        energy () {
            return this._state.energy;
        }

        shieldCounter () {
            return this._state.shield_counter;
        }*/

        state () {
            return Object.assign({}, this._state);
        }

        /**
         * Actuators
         */

        thrust () {
            this._current_action.thrust = true;
        }

        steerLeft () {
            this._current_action.steerLeft = true;
        }

        steerRight () {
            this._current_action.steerRight = true;
        }

        shield () {
            this._current_action.shield = true;
        }

        firePrimary () {
            this._current_action.fire = 1;

            this._current_action.fire_promise = new DeferredPromise();

            return this._current_action.fire_promise;
        }

        fireSecondary () {
            this._current_action.fire = 2;
        }

        /**
         * Radar handlers
         */

        onAsteroidDetected (handler) {
            this._radar.asteroid_handler = handler;
        }

        onShipDetected (handler) {
            this._radar.ship_handler = handler;
        }

        onBulletDetected (handler) {
            this._radar.bullet_handler = handler;
        }

        /**
         * Helper Functions
         */

        _updateCurrentState (state) {
            this._state = {
                position: {
                    x: state.x,
                    y: state.y
                },
                velocity: state.velocity,
                heading: state.heading,
                energy: state.energy,
                health: state.health,
                shield_counter: state.shield_counter
            };
        }

        _processBulletResults (results) {

            if (!results)
                return;

            for (let bullet_id in results) {
                if (results.hasOwnProperty(bullet_id)) {
                    const result = results[bullet_id];
                    const p = this._bullet_promises.get(bullet_id);

                    if (p) {
                        if (result === "WEAPON_LOCKED") {
                            p.reject();
                        } else {
                            p.resolve(result);
                        }

                        this._bullet_promises.delete(bullet_id);
                    }
                }
            }
        }

        _processNearestActors (actors) {

            if (!actors)
                return;

            for (let actor_type in actors) {
                if (actors.hasOwnProperty(actor_type)) {

                    switch (actor_type) {

                        case 'ASTEROID':
                            this._processNearestAsteroids(actors[actor_type]);
                            break;
                        case 'BULLET':
                            this._processNearestBullets(actors[actor_type]);
                            break;
                        case 'SHIP':
                            this._processNearestShips(actors[actor_type]);
                            break;
                    }
                }
            }
        }

        _processNearestAsteroids (asteroids) {

            if (!asteroids || !this._radar.asteroid_handler)
                return;

            asteroids.forEach(asteroid => {
                this._radar.asteroid_handler(asteroid);
            });
        }

        _processNearestShips (ships) {

            if (!ships || !this._radar.ship_handler)
                return;

            ships.forEach(ship => {
                this._radar.ship_handler(ship);
            });
        }

        _processNearestBullets (bullets) {

            if (!bullets || !this._radar.bullet_handler)
                return;

            bullets.forEach(bullet => {
                this._radar.bullet_handler(bullet);
            });
        }
    }

    /**
     * A promise which can be resolved/rejected from the outside.
     */
    class DeferredPromise {
        constructor() {
            this._promise = new Promise((resolve, reject) => {
                this.resolve = resolve;
                this.reject = reject;
            });

            this.then = this._promise.then.bind(this._promise);
            this.catch = this._promise.catch.bind(this._promise);
            this[Symbol.toStringTag] = 'Promise';
        }
    }

    /**
     * Fake promise - synchronous calls
     */

    var PENDING = 'pending';
    var SEALED = 'sealed';
    var FULFILLED = 'fulfilled';
    var REJECTED = 'rejected';
    var NOOP = function(){};

    function isArray(value) {
        return Object.prototype.toString.call(value) === '[object Array]';
    }

// sync calls
    var syncQueue = [];
    var syncTimer;

    function syncFlush(){
        // run promise callbacks
        for (var i = 0; i < syncQueue.length; i++)
            syncQueue[i][0](syncQueue[i][1]);

        // reset async asyncQueue
        syncQueue = [];
        syncTimer = false;
    }

    function syncCall(callback, arg){
        syncQueue.push([callback, arg]);

        if (!syncTimer)
        {
            syncTimer = true;
            syncFlush();
        }
    }


    function invokeResolver(resolver, promise) {
        function resolvePromise(value) {
            resolve(promise, value);
        }

        function rejectPromise(reason) {
            reject(promise, reason);
        }

        try {
            resolver(resolvePromise, rejectPromise);
        } catch(e) {
            rejectPromise(e);
        }
    }

    function invokeCallback(subscriber){
        var owner = subscriber.owner;
        var settled = owner.state_;
        var value = owner.data_;
        var callback = subscriber[settled];
        var promise = subscriber.then;

        if (typeof callback === 'function')
        {
            settled = FULFILLED;
            try {
                value = callback(value);
            } catch(e) {
                reject(promise, e);
            }
        }

        if (!handleThenable(promise, value))
        {
            if (settled === FULFILLED)
                resolve(promise, value);

            if (settled === REJECTED)
                reject(promise, value);
        }
    }

    function handleThenable(promise, value) {
        var resolved;

        try {
            if (promise === value)
                throw new TypeError('A promises callback cannot return that same promise.');

            if (value && (typeof value === 'function' || typeof value === 'object'))
            {
                var then = value.then;  // then should be retrived only once

                if (typeof then === 'function')
                {
                    then.call(value, function(val){
                        if (!resolved)
                        {
                            resolved = true;

                            if (value !== val)
                                resolve(promise, val);
                            else
                                fulfill(promise, val);
                        }
                    }, function(reason){
                        if (!resolved)
                        {
                            resolved = true;

                            reject(promise, reason);
                        }
                    });

                    return true;
                }
            }
        } catch (e) {
            if (!resolved)
                reject(promise, e);

            return true;
        }

        return false;
    }

    function resolve(promise, value){
        if (promise === value || !handleThenable(promise, value))
            fulfill(promise, value);
    }

    function fulfill(promise, value){
        if (promise.state_ === PENDING)
        {
            promise.state_ = SEALED;
            promise.data_ = value;

            syncCall(publishFulfillment, promise);
        }
    }

    function reject(promise, reason){
        if (promise.state_ === PENDING)
        {
            promise.state_ = SEALED;
            promise.data_ = reason;

            syncCall(publishRejection, promise);
        }
    }

    function publish(promise) {
        var callbacks = promise.then_;
        promise.then_ = undefined;

        for (var i = 0; i < callbacks.length; i++) {
            invokeCallback(callbacks[i]);
        }
    }

    function publishFulfillment(promise){
        promise.state_ = FULFILLED;
        publish(promise);
    }

    function publishRejection(promise){
        promise.state_ = REJECTED;
        publish(promise);
    }

    /**
     * @class
     */
    function Promise(resolver){
        if (typeof resolver !== 'function')
            throw new TypeError('Promise constructor takes a function argument');

        if (this instanceof Promise === false)
            throw new TypeError('Failed to construct \'Promise\': Please use the \'new\' operator, this object constructor cannot be called as a function.');

        this.then_ = [];

        invokeResolver(resolver, this);
    }

    Promise.prototype = {
        constructor: Promise,

        state_: PENDING,
        then_: null,
        data_: undefined,

        then: function(onFulfillment, onRejection){
            var subscriber = {
                owner: this,
                then: new this.constructor(NOOP),
                fulfilled: onFulfillment,
                rejected: onRejection
            };

            if (this.state_ === FULFILLED || this.state_ === REJECTED)
            {
                // already resolved, call callback async
                syncCall(invokeCallback, subscriber);
            }
            else
            {
                // subscribe
                this.then_.push(subscriber);
            }

            return subscriber.then;
        },

        'catch': function(onRejection) {
            return this.then(null, onRejection);
        }
    };

    Promise.all = function(promises){
        var Class = this;

        if (!isArray(promises))
            throw new TypeError('You must pass an array to Promise.all().');

        return new Class(function(resolve, reject){
            var results = [];
            var remaining = 0;

            function resolver(index){
                remaining++;
                return function(value){
                    results[index] = value;
                    if (!--remaining)
                        resolve(results);
                };
            }

            for (var i = 0, promise; i < promises.length; i++)
            {
                promise = promises[i];

                if (promise && typeof promise.then === 'function')
                    promise.then(resolver(i), reject);
                else
                    results[i] = promise;
            }

            if (!remaining)
                resolve(results);
        });
    };

    Promise.race = function(promises){
        var Class = this;

        if (!isArray(promises))
            throw new TypeError('You must pass an array to Promise.race().');

        return new Class(function(resolve, reject) {
            for (var i = 0, promise; i < promises.length; i++)
            {
                promise = promises[i];

                if (promise && typeof promise.then === 'function')
                    promise.then(resolve, reject);
                else
                    resolve(promise);
            }
        });
    };

    Promise.resolve = function(value){
        var Class = this;

        if (value && typeof value === 'object' && value.constructor === Class)
            return value;

        return new Class(function(resolve){
            resolve(value);
        });
    };

    Promise.reject = function(reason){
        var Class = this;

        return new Class(function(resolve, reject){
            reject(reason);
        });
    };

    exports.AsteroidsPlayer = AsteroidsPlayer;
}(this));
