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

    if (typeof xec2D === 'undefined') {
        return;
    }

    const androidApp = xec2D;

    let uid = 0;
    const uMap = new Map();
    const wMap = new WeakMap();

    /* globals WeakRef:false */

    const mWeakRef = typeof WeakRef === 'function' ?
        (o => o ? new WeakRef(o) : null) :
        (o => o || null); // typeof InvalidVar == 'undefined'

    const kRef = wr => wr && wr.deref ? wr.deref() : wr;

    function fix(url) {

        if (!url) return url;

        return url.replace(/(\/page\/\d+)\?\w+\=\w+$/, '$1');
    }

    document.addEventListener('touchstart', function(evt) {
        if (!evt || evt.isTrusted !== true || !evt.target || evt.target.nodeType !== 1) {
            return;
        }

        const strResult = [];
        if (wMap.has(evt.target)) {
            strResult.push('uid\t' + wMap.get(evt.target));
        } else {
            uid++;
            uMap.set(uid, mWeakRef(evt.target));
            wMap.set(evt.target, uid);
            strResult.push('uid\t' + uid);
        }

        strResult.push('action\ttouchstart');

        if (evt.target.matches('img[src]')) {
            strResult.push('img\t' + fix(evt.target.src));
        }
        const linkA = evt.target.closest('a[href]');
        if (linkA) {

            strResult.push('link\t' + fix(linkA.href));
        }

        if (strResult.length > 0) {
            androidApp.notifyContextMenu(strResult.join('\n'));
        }
    }, true);

    document.addEventListener('touchend', function(evt) {
        if (!evt || evt.isTrusted !== true || !evt.target || evt.target.nodeType !== 1) {
            return;
        }

        const strResult = [];
        if (wMap.has(evt.target)) {
            //      strResult.push('uid\t' + wMap.get(evt.target));
            //      strResult.push('action\ttouchend');
        }

        if (strResult.length > 0) {
            androidApp.notifyContextMenu(strResult.join('\n'));
        }
    });
})();