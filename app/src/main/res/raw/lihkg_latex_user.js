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

    const createLatexNode = (t) => {
        const img = document.createElement('img');
        img.className = 'lihkg-userscript-latex';
        img.loading = 'lazy';
        img.src = `https://math.now.sh?bgcolor=auto&from=${encodeURIComponent(t)}`;
        return img;
    };

    const processTextNode = (textNode) => {
        if (!textNode) return;
        const textContent = textNode.textContent.trim();
        if (typeof textContent === 'string' && textContent.length > 15) {
            const split = textContent.split(/\[latex\]((?:(?!\[latex\]|\[\/latex\]).)*)\[\/latex\]/g);
            if (split.length >= 3 && (split.length % 2) === 1) {
                const newNodes = split.map((t, j) => ((j % 2) === 0 ? document.createTextNode(t) : createLatexNode(t)));
                textNode.replaceWith(...newNodes);
            }
        }
    };

    const checkPostDiv = (postDiv) => {
        const html = postDiv.innerHTML;
        if (html.indexOf('[latex]') >= 0) {
            const divs = postDiv.querySelectorAll('div[class]:not(:empty)');
            for (const div of divs) {
                let textNode = div.firstChild;
                while (textNode) {
                    if (textNode.nodeType === Node.TEXT_NODE) {
                        processTextNode(textNode);
                    }
                    textNode = textNode.nextSibling;
                }
            }
        }
    };

    function delayedCheck(arr) {
        window.requestAnimationFrame(() => {
            for (const s of arr) {
                checkPostDiv(s);
            }
        });
    }


    const observer = new MutationObserver((mutations) => {
        let arr = [];
        for (const s of document.querySelectorAll('[data-post-id]:not(.y24Yt)')) {
            s.classList.add('y24Yt');
            arr.push(s);
        }
        delayedCheck(arr);
    });

    observer.observe(document.body, {
        subtree: true,
        childList: true
    });

})();