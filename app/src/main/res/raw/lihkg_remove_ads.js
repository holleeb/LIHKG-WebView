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

    function removeAdsElements() {
        let elements = [...document.querySelectorAll("[data-post-id]~*:not([data-post-id]):not(.w10Yp)")];
        for (const elm of elements) {
            //        console.log('elm',elm.className)
            elm.classList.add('w10Yp');
        }

        elements = elements.filter(el => el.querySelectorAll('ins, iframe').length >= 1).filter(el => el.textContent.trim().length == 0);
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