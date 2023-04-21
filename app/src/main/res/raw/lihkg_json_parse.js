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
        let last_replies_notifications = [];
        window.last_replies_notifications=last_replies_notifications;
        JSON._parse = JSON.parse;
        JSON.parse = function(text, r) {
            let res = JSON._parse.apply(this, arguments);

//            console.log(   JSON.stringify(res, null , "\t"));
//                console.log( 4881);

            if(res && res.success === 1 && res.response && res.response.notifications && 'length' in res.response.notifications ){

//                console.log( 4882);
                try{
                    last_replies_notifications.length = 0 ;

                    for( const nt of res.response.notifications){
                        if(nt && nt.thread){
                            last_replies_notifications.push({
                                type: nt.type,
                                subtitle: nt.subtitle,
                                title: nt.title,
                                thread_id: ((+nt.thread.thread_id) || 0),
                                last_replied: (nt.thread.last_replied || null)
                            });
                        }else{
                            last_replies_notifications.push({
                                type: nt.type,
                                subtitle: nt.subtitle,
                                title: nt.title
                            });
                        }
                    }
                }catch(e){console.log(e)}
//                console.log( 4883);

//                console.log(JSON.stringify(last_replies_notifications, null , "\t") );

//                console.log( 4884);
            }


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