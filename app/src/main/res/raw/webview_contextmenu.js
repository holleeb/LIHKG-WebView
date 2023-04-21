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

    if (!location.hostname.endsWith('lihkg.com')) return;

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

    function spanToolOnClickHandler(evt) {
        if (!evt || !evt.isTrusted || !evt.target) return;
//        console.log(565634);

        if (typeof xec2D != 'object') return;

        let span = evt.target.closest('span[data-tip]');

        if(!span) return;

        const key = Object.keys(span).find((key) => key.startsWith('__reactEventHandlers$'));

        if(!key) return;

        evt.preventDefault();
        evt.stopPropagation();
        evt.stopImmediatePropagation();

        span[key].onClick();

        xec2D.notifySpanToolClicked();


    }

    function spanToolOnPointerDownHandler(evt){
        evt.preventDefault();
        evt.stopPropagation();
        evt.stopImmediatePropagation();
    }



    let divCE = null;

    let toolPanel = 0;

    async function toolPanelChanged(){

        console.log(toolPanel, divCE, !divCE.classList.contains('ProseMirror-focused'))
        if(!toolPanel && divCE && !divCE.classList.contains('ProseMirror-focused')){
            document.body.focus();
            divCE.focus();
        }

    }


    function observeDomNodeChanges(node, callback) {
      const observer = new MutationObserver((mutationsList) => {
        for (let mutation of mutationsList) {
          if (mutation.type === 'childList') {
            callback(mutation.target, 0);
          } else if (mutation.type === 'attributes' && mutation.attributeName === 'class') {
            callback(mutation.target, 1);
          }
        }
      });

      if(node && node.nodeType >= 1) observer.observe(node, { childList: true, attributes: true, attributeFilter: ['class'] });

      return observer;
    }

    let baseCid = 0;

    let editorContainerObserver = observeDomNodeChanges(null, ()=>{

        checkElementChanged();

    })

    let editorRootParentObserver = null;

    let appObserver = null;

    function editorContainerTouchMoveHandler(evt){

//
//        evt.preventDefault();
//        evt.stopPropagation();
//        evt.stopImmediatePropagation();

    }

    function createMetaElement(attributes) {
      const meta = document.createElement('meta');

      for (const attr of Object.keys(attributes)) {
        meta.setAttribute(attr, attributes[attr]);
      }

      return meta;
    }

    function checkElementChanged(){

        if(!appObserver){
            let app = document.querySelector('#app');
            if(app){
                appObserver = observeDomNodeChanges(app, (target, type)=>{
                    if(type === 0){
                        checkElementChanged();
                    }
                })
            }
            if(baseCid>0) {

                clearInterval(baseCid);
                baseCid = 0;



                if(!document.querySelector('meta[name="viewport"]')){
                    let meta= document.createElement('meta')
                    meta.setAttribute('name','viewport');
                    meta.setAttribute('content', 'width=device-width,initial-scale=1,maximum-scale=1,user-scalable=0,viewport-fit=cover');
                    document.head.appendChild(meta);
                }

//                console.log('dm377');
//                let meta = createMetaElement({
//                'http-equiv':"Content-Security-Policy",
//                'content':"default-src * self blob: data: gap:; style-src * self 'unsafe-inline' blob: data: gap:; script-src * 'self' 'unsafe-eval' 'unsafe-inline' blob: data: gap:; object-src * 'self' blob: data: gap:; img-src * self 'unsafe-inline' blob: data: gap:; connect-src self * 'unsafe-inline' blob: data: gap:; frame-src * self blob: data: gap:;"
//                });
//
//                if(document.querySelector('meta[http-equiv="Content-Security-Policy"]')) document.querySelector('meta[http-equiv="Content-Security-Policy"]').remove();


//                document.head.appendChild(meta);
            }
        }



        divCE = document.querySelector('.Vo7qCfQ1zcxviGmeMySZl div[contenteditable].ProseMirror');

        if (!divCE) {
            return;
        }

        if(!document.querySelector('.lihkg-editor-container')) divCE.classList.remove('emk52');

        if (divCE.classList.contains('emk52')) {
            return;
        }


        divCE.classList.add('emk52');

        let editorContainer = divCE.closest('.Vo7qCfQ1zcxviGmeMySZl')
        if(editorContainer){
            editorContainer.classList.add('lihkg-editor-container');
            let topbar = editorContainer.querySelector('.W15ZoWi7Cvr8G_0483Wfn');
            if(topbar) topbar.classList.add('lihkg-editor-topbar');
            editorContainerObserver.takeRecords();
            editorContainerObserver.disconnect();
            editorContainerObserver.observe(editorContainer, { childList: true, attributes: true, attributeFilter: ['class'] });
            editorContainer.addEventListener('touchmove', editorContainerTouchMoveHandler, {passive: false})
        }





        setTimeout(()=> {

            if(editorRootParentObserver !== null){

                editorRootParentObserver.takeRecords();
                editorRootParentObserver.disconnect();
                editorRootParentObserver = null;
            }


            let editorRoot = divCE.closest('[data-editor-root]');

            if(!editorRoot) return;

            editorRootParentObserver = observeDomNodeChanges( editorRoot.parentNode, (editorRootParent, type)=>{

                if(!editorRootParent) return;
                let lastElm = editorRootParent.lastElementChild;
                if(!lastElm) return;

                let withToolPanel = 0;
                if(editorRootParent.classList.contains('_2bkS8mlPHwIWkfFdPg38rj') && editorRootParent.querySelector('._2bkS8mlPHwIWkfFdPg38rj ._1m7EaebkdoQj6IdUElH2nl')){
                    // lihkg icon panel
                    withToolPanel |= 2;
                } else
                    if(editorRootParent.querySelector('[data-editor-root] + ul._24QwwVkZYxCCSyOU0moUyg')){
                        withToolPanel |= 1;
                    }
//                let withToolPanel = !lastElm.hasAttribute('data-editor-root');



                if(toolPanel !== withToolPanel){
                    toolPanel = withToolPanel;
                    setTimeout(()=>{
                    console.log("toolPanelChanged")

                    toolPanelChanged();
                    },80);
                }


            })

            let p = divCE;
            while (p = p.parentNode) {
                if (p.querySelector('span[data-tip]')) break;
            }
            if (!p) return;

            let qb = 0;
            for (const s of p.querySelectorAll('span[data-tip]')) {
                if (s.hasAttribute('tabindex') || s.hasAttribute('role') || s.hasAttribute('aria-pressed')) continue;
                qb++;
                s.setAttribute('tabindex', '-1');
//                s.setAttribute('role', 'button');
//                s.setAttribute('aria-pressed', 'false');
//                 s.setAttribute('aria-disabled', 'true');
//                 s.setAttribute('disabled', '');

                s.addEventListener('click', spanToolOnClickHandler, true);
                s.addEventListener('pointerdown', spanToolOnPointerDownHandler, true);
            }
        }, 80)

    }


    baseCid = setInterval(()=> {

        checkElementChanged();

    }, 400)




})();