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

    if (!window.mk4ir) {
        window.mk4ir = function() {
            let parent = this.closest('._2ME7dqW8n0YSe687nDnGvI');
            if (!parent) return;
            let icons = parent.querySelectorAll('._19Ywgg4d5i4DQW6utjqGCk');
            if (icons.length == 1) icons[0].click();
        }
    }

    function obtainPopups(){

        let popups = document.querySelectorAll('._34dVbr5A8khk2N65H9Nl-j:not(.lihkg-popup-view) ._27SmIe4FGDNnK7apcCB3W7 ._3dbMg7zkkTIVJ5VZ3ygu4-');

        for(const popup of popups){
            let popupViewContainer = popup.closest('._34dVbr5A8khk2N65H9Nl-j');
            if(popupViewContainer){
                popupViewContainer.classList.add('lihkg-popup-view');
            }
        }

        popups = document.querySelectorAll('div.lihkg-popup-view')


        if(popups.length === 0) return null;
        return [...popups];
    }

    let busyDom = 0;


    // setup a object 'stateMgr' to manage the stuff.
    const stateMgr = {
        currentState: null,
        prevState: null,
        currentUrl: null,
        prevUrl: null,
        getUrl() {
            return location.pathname;
        },
        modifyByPushState(newState, newUrl) {

//            console.log('modifyByPushState1', simpleKeyValuesHash(this.currentState?this.currentState.pDs:null))
            this.prevState = this.currentState;
            this.currentState = newState;
            this.prevUrl = this.currentUrl;
            this.currentUrl = newUrl;
//            console.log('modifyByPushState2', simpleKeyValuesHash(this.currentState?this.currentState.pDs:null))
            this.currentState.pDs = Object.assign({}, this.currentState.pDs);
            this.onStateChanged(null, null);
        },
        modifyByOnPopState(evt) {
//            console.log('modifyByOnPopState1', simpleKeyValuesHash(this.currentState?this.currentState.pDs:null))
            this.prevState = this.currentState;
            this.currentState = evt.state;
            this.prevUrl = this.currentUrl;
            this.currentUrl = this.getUrl();
//            console.log('modifyByOnPopState2', simpleKeyValuesHash(this.currentState?this.currentState.pDs:null))
            this.onStateChanged(false, false);
        },
        modifyByReplaceState(newState, newUrl) {
//            console.log('modifyByReplaceState1', simpleKeyValuesHash(this.currentState?this.currentState.pDs:null))
            let replacedState = this.currentState;
            let replacedUrl = this.currentUrl;
            this.currentState = newState;
            this.currentUrl = newUrl;
            this.onStateChanged(replacedState, replacedUrl);
        },
        onStateChanged(replacedState, replacedUrl) {
            // here you can have this.currentState and this.prevState
            window.dispatchEvent(new CustomEvent('statechanged', {
                detail: {
                    currentState: this.currentState,
                    prevState: this.prevState,
                    currentUrl: this.currentUrl,
                    prevUrl: this.prevUrl,
                    replacedState,
                    replacedUrl
                }
            }))
        }
    }


    function stopScroll() {


        if (lastScrollTime > 0 && Date.now() - lastScrollTime < 178 && lastScrollElement !== null) {

            //        if('')
            //    console.log(232355, lastScrollElement, lastScrollElement.scrollTo);
            //            lastScrollTime = 0;
            //
            //            lastScrollElement.scrollTo( lastScrollElement.scrollTop, lastScrollElement.scrollLeft );

            //
            //lastScrollElement.scrollTop = lastScrollElement.scrollTop + 0.01;
            //lastScrollElement.scrollLeft = lastScrollElement.scrollLeft + 0.01;

            //stopScrollTime = Date.now();

            if (typeof xec2D == 'object' && xec2D.stopScroll) xec2D.stopScroll();

        }

    }

    // global methods for use
    function pushStateM(state, title, href) {
        title = '';
        href = href.replace('https://lihkg.com/', '/');
        if(href.endsWith('#')) href = href.substring(0, href.length-1);
        window.history.pushState533(state, title, href);
        stateMgr.modifyByPushState(state, href);
    }

    function replaceStateM(state, title, href) {
        title = '';
        href = href.replace('https://lihkg.com/', '/');
        if(href.endsWith('#')) href = href.substring(0, href.length-1);
        window.history.replaceState644(state, title, href);
        stateMgr.modifyByReplaceState(state, href);
    }
    // notify the popstate using modifyByOnPopState(event) in the event handling
    window.addEventListener('popstate', function(evt) {

        busyDom = Date.now() + 60;
        stateMgr.modifyByOnPopState(evt);

        setTimeout(() => {
            //            if(location.pathname.startsWith('/thread/')){
            stopScroll();
            //            }
        }, 1);

    }, false);



    async function looperIter(fn, n) {

        for (let i = 0; i < n; i++) {

            fn(i, n);
            await new Promise(r => setTimeout(r, 1));

        }

    }



    async function asyncStateChanged(evt, forceHistoryBack) {



        const {
            currentState,
            prevState,
            replacedState,
            currentUrl,
            prevUrl,
            replacedUrl
        } = evt.detail;



//                    console.log(32345, prevUrl, currentUrl)

        //    console.log(3154);
        if (prevUrl === currentUrl && prevState && currentState && prevState.pDs && currentState.pDs) {
            //    console.log(3156, !!prevState.pDs.subReplyOverlay, !!currentState.pDs.subReplyOverlay);


            //            console.log(32345, prevState.pDs.textEditing, currentState.pDs.textEditing)

            if (prevState.pDs.textEditing && !currentState.pDs.textEditing) {

                //            console.log(document.querySelector('.W15ZoWi7Cvr8G_0483Wfn a > i.i-close'))
                let p = document.querySelector('.W15ZoWi7Cvr8G_0483Wfn a > i.i-close')
                if (p) p.click();
            } else if (!currentState.pDs.gameIframe && prevState.pDs.gameIframe && !forceHistoryBack) {

                let p = document.querySelector('._3chEw1COjDsJDNGekjbxKP > i.i-game')
                if (p) p.click();


            } else if (!currentState.pDs.registerPopup && prevState.pDs.registerPopup) {

                 let p = document.querySelector('._34dVbr5A8khk2N65H9Nl-j');
                 if (p) p.click();

           } else if (   (currentState.pDs.popupCount||0) === (prevState.pDs.popupCount||0)-1  ){

                    let popupViews = obtainPopups();
                    if(popupViews && popupViews.length>=1){

                        let popupView = popupViews[popupViews.length-1];

                        if(popupView) popupView.click();


                    }

//             }  else if (!currentState.pDs.shareBoxShown && prevState.pDs.shareBoxShown) {
//
//                let p = document.querySelector('._34dVbr5A8khk2N65H9Nl-j ._1nqRVNQ2PyO3vnAwZIISAJ .i-close');
//                if (p) p.click();
//
//              }   else if (!currentState.pDs.settingsShown && prevState.pDs.settingsShown) {
//
//                let p = document.querySelector('._34dVbr5A8khk2N65H9Nl-j ._1nqRVNQ2PyO3vnAwZIISAJ .i-close');
//                if (p) p.click();

            } else if (!currentState.pDs.mediaArchiveOverlayShown && prevState.pDs.mediaArchiveOverlayShown) {

                let p = document.querySelector('.qS4Iosr432bIbXcjtpG0p header button > i.i-close');
                if (p) p.click();

            } else if (!currentState.pDs.imageGalleryOverlayShown && prevState.pDs.imageGalleryOverlayShown) {

                let p = document.querySelector('.Fjkeu8rp1XE7mqA3uW1tY > ._1fEx42oSb0uVYu_3Dhqm95 > i.i-close');
                if (p) p.click();


            } else if (currentState.pDs.imageGalleryOverlayShown && prevState.pDs.imageGalleryOverlayShown) {

                let m = /(\d+)\D+(\d+)\D+(\d+)\D+(\d+)/.exec(prevState.pDs.imageGalleryOverlayShown + "|" + currentState.pDs.imageGalleryOverlayShown);

                console.log(m.map(d => +d))

                if (m && +m[2] == +m[4] && +m[1] > +m[3]) {
                    // current is j-1 page; prev is j page

                    let diffPage = +m[1] - (+m[3]);
                    if (diffPage >= 1) {


                        for (let dj = 0; dj < diffPage; dj++) {

                            let p = document.querySelector('.Fjkeu8rp1XE7mqA3uW1tY > ._2CoXv969JWhD-TcmOsG5SY > i.i-arrow-round-left');
                            if (!p) break;
                            p.click();
                            await new Promise(r => setTimeout(r, 1));

                        }



                    }



                } else if (m && +m[2] == +m[4] && +m[3] > +m[1]) {
                    // prev is j-1 page; current is j page


                    let diffPage = +m[3] - (+m[1]);


                    if (diffPage >= 1) {

                        for (let dj = 0; dj < diffPage; dj++) {

                            let p = document.querySelector('.Fjkeu8rp1XE7mqA3uW1tY > ._2CoXv969JWhD-TcmOsG5SY > i.i-arrow-round-right');
                            if (!p) break;
                            p.click();
                            await new Promise(r => setTimeout(r, 1));

                        }


                    }

                }




            } else if (prevState.pDs.subReplyOverlay && !currentState.pDs.subReplyOverlay) {

                let p = document.querySelector('body > div[data-body-portal] ._3ENsL6YTH5utnKciHKGFd_');

                // body>[data-body-portal] ._3ENsL6YTH5utnKciHKGFd_ - reply overlay
                if (p) p.click();
            } else if (prevState.pDs.subReplyOverlay && currentState.pDs.subReplyOverlay) {
                if (currentState.pDs.subReplyOverlay.indexOf('|') === -1 && prevState.pDs.subReplyOverlay.indexOf(currentState.pDs.subReplyOverlay + '|') === 0) {
                    // no native way to undo

                    let pinnedPost = document.querySelector('._1ByCoA3XapNi7uIVAh-3uY > ._3IN5-WbppwUe2EeMjlVoHz > ._2U_hm3Etdirsq4ULjAd5r_ > .HHp27NcLOTxhyKMDMQugN');
                    if (pinnedPost && pinnedPost.textContent.endsWith('#' + currentState.pDs.subReplyOverlay)) {

                        let p = document.querySelector('body > div[data-body-portal] ._3ENsL6YTH5utnKciHKGFd_');
                        if (p) {
                            let portal = p.closest('body > div[data-body-portal]');
                            portal.classList.add('immediate-animation-forced');



                            await new Promise(r => setTimeout(r, 1));


                            p.click();
                            pinnedPost.click();


                            await new Promise(r => setTimeout(r, 1));

                            portal.classList.remove('immediate-animation-forced');

                        }
                    }


                } else {
                    // page button

                    let currentReplies = currentState.pDs.subReplyOverlay.split('|').map(d => +d || 0);

                    let prevReplies = prevState.pDs.subReplyOverlay.split('|').map(d => +d || 0);

                    if (currentReplies.length >= 2 && prevReplies.length >= 2) {

                        let c0 = currentReplies[0];
                        let p0 = prevReplies[0];
                        let c1 = currentReplies[1];
                        let c2 = currentReplies[currentReplies.length - 1];
                        let p1 = prevReplies[1];
                        let p2 = prevReplies[prevReplies.length - 1];


                        if (currentReplies.indexOf(p0) >= 1 && prevReplies.indexOf(c0) == -1) {

                            // current is prev page


                            let p = document.querySelector('body > div[data-body-portal] ._3ENsL6YTH5utnKciHKGFd_ ~ ._3JZS5NYIoxLyf_0hUKuQjj span._10gzFABm8AANm1DFN_JC6B > span.i-chevron-up');
                            if (p) {
                                p.click();
                            }

                        } else if (prevReplies.indexOf(c0) >= 1 && currentReplies.indexOf(p0) == -1) {


                            // prev is prev page


                            let p = document.querySelector('body > div[data-body-portal] ._3ENsL6YTH5utnKciHKGFd_ ~ ._3JZS5NYIoxLyf_0hUKuQjj span._10gzFABm8AANm1DFN_JC6B > span.i-chevron-down');
                            if (p) {
                                p.click();
                            }


                        }




                    }


                }
            }
        }

//        console.log('statechanged', currentState, prevState, replacedState, currentUrl, prevUrl, replacedUrl);

    }

    async function asyncStateChangedW(evt, forceHistoryBack) {


        const {
            currentState,
            prevState,
            replacedState,
            currentUrl,
            prevUrl,
            replacedUrl
        } = evt.detail;

        if (replacedState === false && replacedUrl === false) {

            while(Date.now() < busyDom){
                await new Promise(r=>setTimeout(r, 80));
            }

            busyDom = Date.now() + 800;
            await asyncStateChanged(evt, forceHistoryBack);
            busyDom = Date.now() + 140;

        }


    }

    // this is a customized event you can obtain all changes of state
    window.addEventListener('statechanged', function(evt) {

        asyncStateChangedW(evt, Date.now() - forceHistoryBackL < 80);
        forceHistoryBackL = 0;

        if(typeof xec2D =='object' && xec2D !== null) xec2D.stateChanged(location.href);


    }, false);




    //window.addEventListener('popstate', function(evt){
    // if(window.history && window.history.state){
    //    console.log('L351', simpleKeyValuesHash(window.history.state.pDs));
    //    }
    //    if(evt && evt.state){
    //        console.log('L352', simpleKeyValuesHash(evt.state.pDs));
    //    }
    //
    //    console.log('popstate');
    //
    //}, false);

    let touchRId = 0;
    let touchRT = 0;
    let touchQT = 0;
    document.addEventListener('touchstart', function(evt) {

        if (!evt || evt.isTrusted !== true || !evt.target) return;

        touchRT = Date.now();
        touchRId++;
        if (touchRId > 1e9) touchRId = 9;

        //        console.log("touchRT", touchRT);


    }, {
        capture: true,
        passive: true
    })

    let touchEndHandlerForPageUrl = ()=>{

        busyDom = 0;
        touchQT = 0;
        touchRT = Date.now();
        touchRId++;

        Promise.resolve(0).then(checker);
        requestAnimationFrame(checker);
    }

    document.addEventListener('touchend', touchEndHandlerForPageUrl , {
        capture: true,
        passive: true
    });


    document.addEventListener('touchcancel', touchEndHandlerForPageUrl , {
        capture: true,
        passive: true
    });

    window.addEventListener('popstate', function(evt) {

        if (!evt || evt.isTrusted !== true) return;
        // native back btn


        touchRT = Date.now();
        touchRId++;
        if (touchRId > 1e9) touchRId = 9;


        //        console.log("touchRT", touchRT);

    }, false);

    let forceHistoryBackL = 0;

    window.addEventListener('message', function(evt){

        if(evt.data === 'onGameClose' && evt.isTrusted && evt.origin === "https://game.lihkg.com" && history.length >= 1 && document.visibilityState === 'visible'){
            forceHistoryBackL = Date.now();
            history.go(-1);
        }
    });

//    document.addEventListener('pointerdown', function(evt){
//            if(!evt || !evt.target || !evt.isTrusted || evt.target.nodeType != 1 || !evt.cancelable) return;
//            if(typeof xec2D != 'object' || !xec2D) return;
//            let href = evt.target.href;
//            if(href){
//                let m = /^(https?\:\/\/lihkg\.com\/thread\/\d+\/page\/\d+)\?post=\d+$/.exec(href);
//                if(m && m[1]){
//                    evt.target.href = m[1];
//                }
//                console.log(evt.target.href)
//                return;
//            }
//
//    })

    document.addEventListener('click', function(evt){
        if(!evt || !evt.target || !evt.isTrusted || evt.target.nodeType != 1 || !evt.cancelable) return;
        if(typeof xec2D != 'object' || !xec2D) return;
        let href = evt.target.href;
        if(href){
            // remove unnecessary "?post="
            let m = /^(https?\:\/\/lihkg\.com\/thread\/\d+\/page\/\d+)\?post=\d+$/.exec(href);
            if(m && m[1]){
                evt.target.href = m[1];
            }
            return;
        }
        if(typeof history != 'object' || !history || !history.length) return;
        let target = evt.target;
        let elmTarget = evt.target.closest('div[class]');
        if(!elmTarget) return;
        let action = '';
        for(const className of elmTarget.classList){

            switch(elmTarget.className){

                case 'qS4Iosr432bIbXcjtpG0p': //
                    if(target.matches('button[data-close-region]')) action = 'close-media-archive-cross';
                    break;

                case 'Fjkeu8rp1XE7mqA3uW1tY': //
                    if(target.matches('._1AEEI1goagMu9r5fVYt-Y4 button._1fEx42oSb0uVYu_3Dhqm95 > i.i-close')) action = 'close-photo-display-cross';
                    break;

                case '_10tWW0o-L-5oSH8lCBl9ai':
                    if(target.matches('._34dVbr5A8khk2N65H9Nl-j ._1nqRVNQ2PyO3vnAwZIISAJ .i-close')) action = isShareBoxOpened() ? 'close-sharebox-cross' : 'close-settings-cross';
                    break;

                case '_34dVbr5A8khk2N65H9Nl-j':
                    if(isShareBoxOpened() && target.matches('div._34dVbr5A8khk2N65H9Nl-j')) action = 'close-sharebox-div';
                    break;


                case '_34dVbr5A8khk2N65H9Nl-j':
                    if(target.matches('._34dVbr5A8khk2N65H9Nl-j') && document.querySelector('._34dVbr5A8khk2N65H9Nl-j a[href*="/register"]')) action = 'close-register-popup';
                    break;

                case '_3ENsL6YTH5utnKciHKGFd_':

                    if(target.matches('body > div[data-body-portal] ._15Y0ebHstpjSjX2xCZCZ8U ._3ENsL6YTH5utnKciHKGFd_')) action = 'close-pinned-reply-div';
                    break;

                case 'zcEinSkC-o0d0G7_NVOA8':

                    if(target.matches('body > div[data-body-portal] ._15Y0ebHstpjSjX2xCZCZ8U .zcEinSkC-o0d0G7_NVOA8 i.i-close')) action = 'close-nested-reply-cross';
                    break;


                case 'kuYE8juwPfVTS-qncq1Cp':

                    if(target.matches('.qS4Iosr432bIbXcjtpG0p div.kuYE8juwPfVTS-qncq1Cp[data-close-region]')) action =  'close-media-archive-div';
                    break;


                case 'T1aknDSneV_Pvm2nQkyYk': //
                    if(target.matches('._1AEEI1goagMu9r5fVYt-Y4 div.T1aknDSneV_Pvm2nQkyYk')) action = 'close-photo-display-div';
                    break;

            }
            if(action) break;

        }

        if(action && action.startsWith('close-')) {
            evt.preventDefault();
            evt.stopPropagation();
            evt.stopImmediatePropagation();

            history.go(-1);
        }
        console.log("NativeClick", action, elmTarget.className, target.tagName);
    }, true)

    function isShareBoxOpened(){

            let elm = document.querySelector('._27su4Zj_qATokwVdWIbEWB ._1nqRVNQ2PyO3vnAwZIISAJ ._10tWW0o-L-5oSH8lCBl9ai > i.i-close');

            if(elm){

                let parent = elm.closest('._27su4Zj_qATokwVdWIbEWB');
                if(parent){

                    if(parent.querySelector('textarea[readonly]')  ) {

                    return true;
                    }

                    }

                    }

                    return false;


    }

    function domStatus() {


        let hsObj = {

        }

        // photo page
        if (document.querySelector('.Fjkeu8rp1XE7mqA3uW1tY > ._1fEx42oSb0uVYu_3Dhqm95 > i.i-close')) {
            let galleryPage = document.querySelector('.Fjkeu8rp1XE7mqA3uW1tY > ._1uoGou7Kg7a75R2--DdF6B');
            if (galleryPage) galleryPage = (galleryPage.textContent || '').replace(/\s+/g, '');
            else galleryPage = '';
            hsObj.imageGalleryOverlayShown = galleryPage;

        }


        // media archive
        if (document.querySelector('.qS4Iosr432bIbXcjtpG0p header button > i.i-close')) {
            let totalMedia = document.querySelector('.qS4Iosr432bIbXcjtpG0p header button > i.i-close').closest('header').querySelector('span');
            if (totalMedia) totalMedia = (totalMedia.textContent || '').replace(/\D/g, '');
            else totalMedia = '';
            hsObj.mediaArchiveOverlayShown = totalMedia;

        }


        let popupViews = obtainPopups();

        if(popupViews && popupViews.length >=1){

            hsObj.popupCount = popupViews.length;

        }


        /*

        if(isShareBoxOpened()){
            // share button box (top right of post)
            hsObj.shareBoxShown = 1;

        } else if (document.querySelector('._34dVbr5A8khk2N65H9Nl-j ._1nqRVNQ2PyO3vnAwZIISAJ .i-close')) { // ._34dVbr5A8khk2N65H9Nl-j under body
            // settings
            hsObj.settingsShown = document.querySelectorAll('._34dVbr5A8khk2N65H9Nl-j li').length;

        }
        */

        if(document.querySelector('._34dVbr5A8khk2N65H9Nl-j a[href*="/register"]')) { // ._34dVbr5A8khk2N65H9Nl-j under body

           hsObj.registerPopup = document.querySelectorAll('._34dVbr5A8khk2N65H9Nl-j a[href*="/"]').length;

       }



        // settings
        if (document.querySelector('iframe[src*="game.lihkg.com"]')) { // ._34dVbr5A8khk2N65H9Nl-j under body

            hsObj.gameIframe = 1;

        }


        if (document.querySelector('div[data-body-portal] > ._15Y0ebHstpjSjX2xCZCZ8U > ._3ENsL6YTH5utnKciHKGFd_')) {

            let replyContainer = document.querySelector('div[data-body-portal] > ._15Y0ebHstpjSjX2xCZCZ8U > ._3ENsL6YTH5utnKciHKGFd_ ~ ._2lZuKUyyBIXiZ1Ynp5M1YT:last-child')

            if (replyContainer) {

                let replies = replyContainer.querySelectorAll('div[id][data-post-id]');



                hsObj.subReplyOverlay = [...replies].map(el => el.id).join('|');

            }



        }

        let divContenteditable = document.querySelector('div[contenteditable].ProseMirror')
        if (divContenteditable) {
            hsObj.textEditing = 1;
            if (!divContenteditable.hasAttribute('m4ir')) {
                divContenteditable.setAttribute('m4ir', '');
                if (window.mk4ir) divContenteditable.removeEventListener('pointerdown', window.mk4ir, true);
                if (window.mk4ir) divContenteditable.addEventListener('pointerdown', window.mk4ir, true);
            }
        }

        return hsObj;

    }

    function simpleKeyValuesHash(obj) {

        if (!obj) return "";
        let keys = Object.keys(obj);
        if (keys.length === 0) return '';
        return keys + ":" + Object.values(obj);

    }

    let bKey = null;
    let lastRTPushed = 0;



    function checker() {

        if (touchQT !== touchRT && touchRT > touchQT + 4 && touchRT + 140 < Date.now()) {

            touchQT = touchRT;

            let objDomStatus = domStatus();

            let tKey = simpleKeyValuesHash(objDomStatus);

            if (tKey != bKey) {

                //                console.log("tKey1", tKey);


                let sKeyHist = '';
                if (window.history.state && window.history.state.pDs) {
                    sKeyHist = simpleKeyValuesHash(window.history.state.pDs);
                }

                console.log("tKey", tKey, 'sKeyHist', sKeyHist);
                if (tKey !== sKeyHist) {
                    let m = Object.assign({}, window.history.state);
                    m.pDs = objDomStatus;


                    //                    let spaURL = location.href.replace(/\?(\w+\=[^\=\&]*\&?)+(\#[^\#\s]*)?$/, '');
                    let spaURL = location.pathname;

//                    console.log('pushStateM', objDomStatus.popupCount)

                    pushStateM(m, '', spaURL);
                    //                    console.log('T88.pushState-d1');
                    lastRTPushed = touchRT;
                }

                bKey = tKey;
            }

        } else if (touchQT === touchRT && touchRT + 800 < Date.now()) {

            let objDomStatus = domStatus();


            let tKey = simpleKeyValuesHash(objDomStatus);

            if (tKey != bKey) {
                //                console.log("tKey2", tKey);

                let sKeyHist = '';
                if (window.history.state && window.history.state.pDs) {
                    sKeyHist = simpleKeyValuesHash(window.history.state.pDs);
                }
                if (tKey !== sKeyHist) {

                    let m = Object.assign({}, window.history.state);
                    m.pDs = objDomStatus;

                    //                    let spaURL = location.href.replace(/\?(\w+\=[^\=\&]*\&?)+(\#[^\#\s]*)?$/, '');
                    let spaURL = location.pathname;

                    if (lastRTPushed === touchRT) {
                        replaceStateM(m, '', spaURL);
                        //                        console.log('T88.replaceState-d1');
                    } else {
                        pushStateM(m, '', spaURL);
                        //                        console.log('T88.pushState-e1');
                    }


                }

                bKey = tKey;
            }
            touchQT++;

        }

    }

    let lastScrollTime = 0;
    let lastScrollElement = null;
    let stopScrollTime = 0;

    document.addEventListener('scroll', function(evt) {
        lastScrollTime = Date.now();
        lastScrollElement = evt.target;
    }, true)

    function adjustor(arr) {

        let hsObj = {
            status: 0
        };
        if (arr[0] !== null && typeof arr[0] == 'object') hsObj = Object.assign(hsObj, arr[0]);
        arr[0] = hsObj;

        hsObj.pDs = domStatus();


        //        console.log('hsObj', JSON.stringify(hsObj));

        return arr;
    }

    let histUrls = new Set();

    function updateLocalStorageHistory(url) {

        try {

            if (url.indexOf('/page/') < 0) return;
            let m = /\/thread\/(\d+)\/page\/(\d+)/.exec(url);
            if (!m) return;
            let threadId = +m[1];
            let pageId = +m[2];
            if (threadId >= 1 && pageId >= 1) {} else return;
            if (histUrls.has(url)) {
                let strHistory = localStorage.history;
                let objHistory = null;
                if (typeof strHistory == 'string' && strHistory.length > 0) {
                    try {
                        objHistory = JSON.parse(strHistory);
                    } catch (e1) {}
                }
                if (objHistory != null) {
                    let wk = objHistory[threadId];
                    if (wk && wk.length == 5 && wk[3] !== pageId && wk[0] === threadId && wk[1] >= 1 && wk[2] >= 1681000960000 && wk[3] >= 1 && wk[4] >= 0) {
                        wk[2] = Date.now();
                        wk[3] = pageId;
                        wk[4] = 0;
                        localStorage.history = JSON.stringify(objHistory);
                    }
                }
            } else {
                histUrls.add(url);
            }

        } catch (e2) {}


    }

    function closeOverlay() {

        if (true) return;

        let closeBtn = document.querySelector('body>[data-body-portal] i.i-close');
        if (closeBtn) closeBtn.click();

        let p = document.querySelector('body [data-overlay], body > ._34dVbr5A8khk2N65H9Nl-j, body>[data-body-portal] ._3ENsL6YTH5utnKciHKGFd_');
        // body [data-overlay] - post reply ... overlay
        // body > ._34dVbr5A8khk2N65H9Nl-j - login popup overlay
        // body>[data-body-portal] ._3ENsL6YTH5utnKciHKGFd_ - reply overlay
        if (p) p.click();
    }

    let currentPageId = -1;
    let pushedUrl = null;
    let pendingUrls = [];
    let cid = 0;

    function notHandleByDOM(spaURL, url) {

        let m1 = /(\/thread\/\d+\/page\/)(\d+)/.exec(spaURL);
        let m2 = /(\/thread\/\d+\/page\/)(\d+)/.exec(url);
        //    console.log(!!m1, !!m2);
        if (m1 && m2 && m1[1] === m2[1] && m1[2] !== m2[2]) {

            //console.log('d66', !!document.querySelector('#page-'+m1[2]) , !!document.querySelector('#page-'+m2[2]))
            if (document.querySelector('#page-' + m1[2]) && document.querySelector('#page-' + m2[2])) {
                return false;
            }

            if (document.querySelector('#page-' + m1[2]) && !document.querySelector('#page-' + m2[2]) && +m1[2] === +m2[2] - 1) {
                return false;
            }


        }

        return true;

    }


    function replaceStateArrFx2() {


        clearTimeout(cid);
        cid = 0;
        let pendingUrlsX = [...pendingUrls];
        pendingUrls.length = 0;


        //        let spaURL = location.href.replace(/\?(\w+\=[^\=\&]*\&?)+(\#[^\#\s]*)?$/, '')
        let spaURL = location.pathname;

        const map = new Map();
        let i = 0;
        let lastThreadPage = null;
        for (const a of pendingUrlsX) {
            if (typeof a[2] != 'string') continue;
            if (/\/thread\/\d+\/page\/\d+/.test(a[2])) {
                if (lastThreadPage) {
                    map.delete(lastThreadPage);
                }
                lastThreadPage = a[2];
            }
            map.set(a[2], i);
            i++;
        }

        let k = -1;
        const bArr = [];
        i = 0;
        let startAt = 0;

        for (const a of pendingUrlsX) {
            if (typeof a[2] != 'string') continue;
            const h = map.get(a[2]);

            if (h > k && notHandleByDOM(spaURL, a[2])) {
                k = h;
                bArr.push(i);

                if (spaURL.endsWith(a[2])) startAt = bArr.length;

            }

            i++;
        }

        const newPendingUrls = bArr.map(idx => pendingUrlsX[idx]);


//        console.log(2545, spaURL);


        if (spaURL == 'https://lihkg.com/' || spaURL == '/') {


            for (let j = startAt; j < newPendingUrls.length; j++) {
                updateLocalStorageHistory(newPendingUrls[j][2]);
                replaceStateM(...adjustor(newPendingUrls[j]));
//                console.log('T88.replaceState', newPendingUrls[j][2]);
            }

        } else {

            for (let j = startAt; j < newPendingUrls.length; j++) {
                updateLocalStorageHistory(newPendingUrls[j][2]);
                pushStateM(...adjustor(newPendingUrls[j]));
                //                console.log('T88.pushState-c1', newPendingUrls[j][2]);
            }
        }

    }

    window.history.replaceState644 = window.history.replaceState;
    window.history.replaceState = function() {

        closeOverlay();

        clearTimeout(cid);
        cid = 0;
        if (typeof arguments[2] == 'string' && arguments[2].indexOf('?post=') > 0) {
            arguments[2] = arguments[2].replace(/(\/thread\/\d+\/page\/\d+)(\?post=\d*)/, '$1');
        }
        pendingUrls.push([...arguments]);
        cid = setTimeout(replaceStateArrFx2, 80);
    };



    window.history.pushState533 = window.history.pushState;

    window.history.pushState = function(type) {


        let spaURL = location.href.replace(/\?(\w+\=[^\=\&]*\&?)+(\#[^\#\s]*)?$/, '')


        if (typeof arguments[2] == 'string' && arguments[2].indexOf('?post=') > 0) {
            arguments[2] = arguments[2].replace(/(\/thread\/\d+\/page\/\d+)(\?post=\d*)/, '$1');
        }

        if (arguments[0] === null && typeof arguments[1] == 'string' && typeof arguments[2] == 'string') {

            if (spaURL.endsWith(arguments[2])) return;

        }

        closeOverlay();

        //        console.log("T88.pushState533", arguments[2]);

        updateLocalStorageHistory(arguments[2]);
        return pushStateM(...adjustor(arguments));


    }



    const insectObserver = new IntersectionObserver(entries => {
        for (const entry of entries) {
            if (!entry.target || entry.boundingClientRect.height < 1) {
                continue;
            }

            let pageNode = entry.target.parentNode;
            if (!pageNode) continue;

            let pageDiv = entry.target;

            const pageId = +((pageDiv.getAttribute('pageId66') || '').replace('page-', ''));
            //            console.log('pageId', pageId, entry.isIntersecting);

            if (pageId >= 1) {} else {
                continue;
            }

            if (entry.isIntersecting === true) {

                currentPageId = pageId;

            }


            let spaURL = location.pathname;

            const m = /(\/thread\/\d+\/page\/)(\d+)/.exec(spaURL);


            if (m && m[1]) {
                if (+currentPageId !== +m[2]) {
                    const newurl = `${m[1]}${currentPageId}`;
                    pushedUrl = newurl;
                    window.history.pushState(null, document.title, newurl);
                    //                        console.log('T88.pushState-cz', newurl);
                }
            }


        }
    }, {
        rootMargin: '-50% 0px -50% 0px',
    });



    let mcid = 0;

    let mct = 1e99;
    setInterval(() => {

        if (Date.now() < busyDom) return;

        if (Date.now() > mct + 140) {
            mct = 1e99;
            checker();

        }


    }, 340);

    const observer = new MutationObserver(mutations => {

        mct = Date.now();

        for (const s of document.querySelectorAll('[id^="page-"]:not(.y25Yt)')) {
            s.classList.add('y25Yt');
            let pageDiv = s.closest('._3jxQCFWg9LDtkSkIVLzQ8L');
            if (pageDiv != null) {
                if (pageDiv.getAttribute('pageId66') != s.id) {
                    pageDiv.setAttribute('pageId66', s.id);
                    insectObserver.observe(pageDiv);
                }
            }
        }

        // fix <a href="#">...</a>

        for (const s of document.querySelectorAll('li._2SqBoIdr5sJFLnuXp8xsqy > a[href="#"]:only-child')) {
            s.href='javascript:';
        }





    });

    observer.observe(document.body, {
        subtree: true,
        childList: true,
    });

    let lastUrl = null;

    function onUrlChanged(url) {
        document.documentElement.classList.toggle('isPageSearch', url.startsWith('/search'));
        console.log(document.documentElement.className);
    }

    const rafcb1 = () => {
        const url = location.pathname;

        if (url !== lastUrl) {
            lastUrl = url;
            onUrlChanged(url);
        }

        requestAnimationFrame(rafcb1);
    }


    rafcb1();


    window.addEventListener("popstate", function(e) {

        closeOverlay();
        // ...
    });
    window.addEventListener("hashchange", function(e) {


        closeOverlay();
        // ...
    });


})();