'use strict';
(function() {
    'use strict';

    /*
     * Copyright (C) 2023 culefa.
     *
     * Licensed under the Apache License, Version 2.0 (the "License"); you may not
     * use this file except in compliance with the License. You may obtain a copy of
     * the License at
     *
     * http://www.apache.org/licenses/LICENSE-2.0
     *
     * Unless required by applicable law or agreed to in writing, software
     * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
     * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
     * License for the specific language governing permissions and limitations under
     * the License.
     */

    if(!location.hostname.endsWith('lihkg.com')) return;

    let touchAsClick = false;

    const preventTouchStart = evt => {
        touchAsClick = false;
        if(evt && evt.isTrusted){

            let elm = document.querySelector('._27su4Zj_qATokwVdWIbEWB ._1nqRVNQ2PyO3vnAwZIISAJ ._10tWW0o-L-5oSH8lCBl9ai > i.i-close');

            if(elm){

                let parent = elm.closest('._27su4Zj_qATokwVdWIbEWB');
                if(parent){

                    if(parent.querySelector('textarea[readonly]') && evt.target.matches('._34dVbr5A8khk2N65H9Nl-j, ._10tWW0o-L-5oSH8lCBl9ai, ._10tWW0o-L-5oSH8lCBl9ai > i.i-close')   ) {

                        touchAsClick = true;
                    }

                }
            }


        }
        if (window.getSelection().isCollapsed === false) {
            evt.stopPropagation();
            evt.stopImmediatePropagation();
        }
    };

    document.addEventListener('touchstart', preventTouchStart, {
        capture: true,
        passive: false
    });


    const touchEndHandler = (evt)=>{


        if(touchAsClick && evt.isTrusted){
            let div = evt.target;
            setTimeout(()=>{
                if(document.body.contains(div)){
                    div.click();
                }
            },80);
        }


        touchAsClick = false;

    }

    document.addEventListener('touchend', touchEndHandler, true);
    document.addEventListener('touchcancel', touchEndHandler, true);

    const createNewTouch = (touch, offsetX) => new Touch({
        identifier: Date.now(),
        target: touch.target,
        clientX: touch.clientX + offsetX,
        clientY: touch.clientY,
        radiusX: touch.radiusX,
        radiusY: touch.radiusY,
        rotationAngle: touch.rotationAngle,
        force: touch.force,
    });


    const fixTouchHandler = func => e => {
        const offsetX = 20;
        const createTouches = (touches) => touches.length > 0 ? Array.from(touches, touch => createNewTouch(touch, offsetX)) : [];
        e = new TouchEvent(e.type, {
            cancelable: true,
            bubbles: true,
            touches: createTouches(e.touches),
            targetTouches: [],
            changedTouches: createTouches(e.changedTouches)
        });
        func.call(this, e);
    };



    let mTouch0 = null;

    function fixTouch(touch) {
        if (!mTouch0) return touch;

        const deltaX = touch.clientX - mTouch0.clientX;
        const deltaY = touch.clientY - mTouch0.clientY;
        const d = 45;
        const distanceSquared = deltaX * deltaX + deltaY * deltaY;
        const dSquared = d * d;

        if (mTouch0.lockXY) {
            touch.clientX = mTouch0.clientX;
            touch.clientY = mTouch0.clientY;
        } else if (mTouch0.lockY) {
            touch.clientY = mTouch0.clientY;
        } else if (mTouch0.lockX) {
            touch.clientX = mTouch0.clientX;
        } else if (distanceSquared > dSquared) {
            if (deltaX * deltaX / dSquared > 0.8) {
                touch.clientY = mTouch0.clientY;
                mTouch0.lockY = true;
                mTouch0.lockX = false;
                mTouch0.lockXY = false;
            } else if (deltaY * deltaY / dSquared > 0.8) {
                touch.clientX = mTouch0.clientX;
                mTouch0.lockX = true;
                mTouch0.lockY = false;
                mTouch0.lockXY = false;
            } else {
                touch.clientX = mTouch0.clientX;
                touch.clientY = mTouch0.clientY;
                mTouch0.lockXY = true;
                mTouch0.lockX = false;
                mTouch0.lockY = false;
            }
        } else {
            touch.clientX = mTouch0.clientX;
            touch.clientY = mTouch0.clientY;
        }


        return touch;
    }


    (function fixAddEventListener() {
        if (EventTarget.prototype.euLI9) return;
        EventTarget.prototype.euLI9 = EventTarget.prototype.addEventListener;
        EventTarget.prototype.addEventListener = function(type, func, option) {
            const touchEventTypes = ['touchstart', 'touchmove', 'touchend', 'touchcancel'];
            if (touchEventTypes.includes(type) && option === undefined) {

                if (type === 'touchmove' && (func + "").indexOf(".drawer.") > 4) func.isOpenDrawerFunc = true;
                const gunc = (e) => {

                    e = {
                        isTrusted: e.isTrusted,
                        touches: e.touches[0] ? [{
                            clientX: e.touches[0].clientX,
                            clientY: e.touches[0].clientY
                        }] : e.touches,
                        preventDefault: e.preventDefault.bind(e),
                        target: e.target,
                        stopPropagation: e.stopPropagation.bind(e),
                        stopImmediatePropagation: e.stopImmediatePropagation.bind(e),
                        cancelable: e.cancelable
                    };
                    if (type === 'touchstart' && mTouch0 === null) mTouch0 = e.touches[0];
                    else if (mTouch0 !== null && e.touches[0]) {
                        e.touches[0] = fixTouch(e.touches[0]);
                    }

                    if (func.isOpenDrawerFunc && type === 'touchmove') {

                        if (mTouch0 !== null && e.cancelable && mTouch0.lockY) func.call(this, e);
                        return;

                    }

                    let r = func.call(this, e);
                    if (type === 'touchend' || type === 'touchcancel') {
                        mTouch0 = null;
                    }
                    return r;
                };
                return this.euLI9.call(this, type, gunc, {
                    passive: false
                });
            }
            return this.euLI9.apply(this, arguments);
        }
    })();

})();