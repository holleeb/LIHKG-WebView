// code reference : https://greasyfork.org/scripts/427766-enhancement-userscript-for-lihkg/code
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

    if (!JSON._parse && JSON.parse) {
        JSON._parse = JSON.parse;
        JSON.parse = function(text, r) {
            let res = JSON._parse.apply(this, arguments);

            const contentFix = (resObj) => {
                if (!resObj || typeof resObj !== 'object') return;

                for (let k in resObj) {
                    if (typeof resObj[k] === 'object') {
                        contentFix(resObj[k]);
                    } else if (k === 'display_vote' && resObj[k] === false) {
                        resObj[k] = true;
                    }
                }
            };

            contentFix(res);

            return res;
        };
    }

})();