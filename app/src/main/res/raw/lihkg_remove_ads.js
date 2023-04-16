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

    let adsRemovalCount = 0;

    function adsElementFilter(el){

        if(!el || el.nodeType !==1) return false;

        let elementLevelAds = false;

        if(el.childElementCount === 1){
            let divs = el.querySelectorAll('div');
            if(divs.length === 1 && divs[0].id.includes('-') ) elementLevelAds = true;
            else if(['INS','IFRAME'].includes(el.firstChild.tagName)) elementLevelAds = true;
            else elementLevelAds = false;
        }else {
            elementLevelAds = el.querySelectorAll('ins, iframe').length >= 1;
        }

        if(!elementLevelAds) return false;

        let textContent = el.textContent;
        if(typeof textContent !== 'string') return false;
        if(textContent.length === 0) return true;
        if(textContent.trim().length === 0) return true;

        return false;

    }

    function removeAdsElements() {
        // w11Yp for actual removal checking
        let elements = [...document.querySelectorAll("[data-post-id]~*:not([data-post-id]):not(.w11Yp)")];
        for (const elm of elements) {
            //        console.log('elm',elm.className)
            elm.classList.add('w11Yp');
        }

        elements = elements.filter(adsElementFilter);
        if (elements.length > 0) {
            for (const el of elements) el.classList.add('lihkg-ads-block');
            // do not remove the elm on DOM. It creates ERROR when reading sub-quoted messages.
            adsRemovalCount += elements.length;
        }
        return adsRemovalCount;
    }

    window.lwelv = removeAdsElements;

    document.addEventListener('removeAdsElements', removeAdsElements, false);


    const observer = new MutationObserver((mutations) => {

        // w10Yt for new post checking

        let mz = 0;
        for (const s of document.querySelectorAll('[data-post-id]~*:not([data-post-id]):not(.w10Yt)')) {
            s.classList.add('w10Yt');
            mz++;
        }
        if (mz > 0) {
            window.requestAnimationFrame(removeAdsElements)
        }
    });

    observer.observe(document.body, {
        subtree: true,
        childList: true
    });


})();