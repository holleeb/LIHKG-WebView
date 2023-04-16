// ==UserScript==
// @name         LIHKG Label Users
// @namespace    https://gist.github.com/kitce
// @version      0.18
// @description  Label users on LIHKG
// @author       kitce
// @include      https://lihkg.com/*
// @grant        none
// @run-at       document-start
// ==/UserScript==

(function () {
  // Constants
  const DEBUG = false;
  const PLUGIN_NAMESPACE = 'LIHKG-Label-Users';
  const USER_CARD_MODAL_TITLE = '會員資料';
  const SETTINGS_MODAL_TITLE = '設定';
  const PROFILE_URL_REGEX = /^\/profile\/(\d+)$/;
  const THREAD_URL_REGEX = /\/thread\/(\d+)/;
  const THREAD_LIST_API_URL_REGEX = /^https:\/\/lihkg\.com\/api_v2\/(thread(\?thread_ids|\/(hot|latest|custom|bookmark|category|following))|user\/\d+\/thread)/;
  const REPLY_LIST_API_URL_REGEX = /^https:\/\/lihkg\.com\/api_v2\/thread\/\d+\/page\/\d+/;
  const QUOTE_LIST_API_URL_REGEX = /^https:\/\/lihkg\.com\/api_v2\/thread\/(\d+)\/[a-f0-9]+\/quotes\/page\/\d+/;

  const ATTRIBUTES = {
    dataPostId: 'data-post-id',
    dataUser: 'data-user'
  };

  const SELECTORS = {
    thread: '.wQ4Ran7ySbKd8PdMeHZZR',
    messageNumber: '._3SqN3KZ8m8vCsD9FNcxcki',
    nickname: '.ZZtOrmcIRcvdpnW09DzFk',
    nicknameLink: '.ZZtOrmcIRcvdpnW09DzFk > a',
    threadLink: '._2A_7bGY9QAXcGu1neEYDJB',
    threadUsername: '.CxY4XDSSItTeLVg0cKCN0',
    modal: '._34dVbr5A8khk2N65H9Nl-j',
    modalTitle: '._2b5VMoBy8yIXlX-wC8v57F',
    modalContentInner: '._3dbMg7zkkTIVJ5VZ3ygu4- > div',
    userCardButtonsContainer: '._2c5AwJ_0ePFIYub8OFE97J._2F7zIQl_1y5nHpDllTwX17',
    submissionForm: '.Vo7qCfQ1zcxviGmeMySZl._2GqETuCmp5bDDgJPTevkJA'
  };

  const CSS_CLASSES = {
    replyItem: '_2bokd4pLvU5_-Lc97NVqzn',
    replyToolbarButton: 'RShOJL_DxoIHgMmNd0nlJ',
    settingsSectionTitle: '_1RL1LDMdfBS-OSJAqzcNQ9',
    settingsOptionsList: '_39HZdiaaYG298Upj1zG7uD',
    settingsOptionsListItem: '_2NVUL5Ow1xg006Qaf5T5u-',
    settingsOptionButton: '_2NN6My4jKn3uId__keqMVj',
    exportButton: `${PLUGIN_NAMESPACE}-export`,
    importButton: `${PLUGIN_NAMESPACE}-import`,
    labelList: `${PLUGIN_NAMESPACE}-labels`,
    labelInfo: `${PLUGIN_NAMESPACE}-label-info`,
    labelInfoReason: `${PLUGIN_NAMESPACE}-label-info-reason`,
    labelInfoButtons: `${PLUGIN_NAMESPACE}-label-info-buttons`,
    labelEditButton: `${PLUGIN_NAMESPACE}-label-edit`,
    labelSourceButton: `${PLUGIN_NAMESPACE}-label-source`,
    labelDeleteButton: `${PLUGIN_NAMESPACE}-label-delete`,
    snipeButton: `${PLUGIN_NAMESPACE}-snipe`
  };

  const PLACEHOLDERS = {
    threadId: '{THREAD_ID}',
    registrationDate: '{REGISTGRATION_DATE}',
    year: '{YEAR}',
    month: '{MONTH}',
    day: '{DAY}',
    page: '{PAGE}',
    messageNumber: '{MESSAGE_NUMBER}',
    label: '{LABEL}',
    reason: '{REASON}',
    timestamp: '{UNIX_TIMESTAMP}',
    numUsers: '{NUM_USERS}',
    numLabels: '{NUM_LABELS}',
    userId: '{USER_ID}',
    sourceURL: '{SOURCE_URL}',
    username: '{USERNAME}',
    snipeReplyLabelsContent: '{SNIPE_REPLY_LABELS_CONTENT}'
  };

  // Variables
  const localStorageDataKey = `${PLUGIN_NAMESPACE}-data`;
  const settingsSectionTitle = '會員標籤';
  const addLabelButtonLabel = '標籤';
  const addLabelQuestion = '請輸入標籤';
  const addLabelReasonQuestion = '原因（建議輸入回覆編號及具體原因，方便日後追查）';
  const snipeButtonLabel = '狙擊';
  const snipeEmptyReasonFallback = '（沒有記錄原因）'
  const snipeEmptySourceURLFallback = '（沒有來源）'
  const snipeNothingAlertMessage = '沒有標籤記錄可以狙擊';
  const sourceButtonLabel = '來源';
  const sourceURLTemplate = `https://lihkg.com/thread/${PLACEHOLDERS.threadId}/page/${PLACEHOLDERS.page}?post=${PLACEHOLDERS.messageNumber}`;
  const editButtonLabel = '修改';
  const deleteLabelQuestion = `確認刪除標籤【${PLACEHOLDERS.label}】？`;
  const deleteButtonLabel = '刪除';
  // import file
  const importFileInputId = `${PLUGIN_NAMESPACE}-import-file`;
  const importFileButtonLabel = '匯入標籤';
  const importFileButtonReminder = '所有記錄將會被覆蓋，建議先匯出一次作備份';
  const importFileSuccessMessage = `檔案匯入成功，共 ${PLACEHOLDERS.numUsers} 個會員、${PLACEHOLDERS.numLabels} 個標籤`;
  const importFileGenericErrorMessage = '檔案匯入失敗';
  const importFileDataFormatErrorMessage = '檔案內容格式錯誤';
  // export file
  const exportFileButtonLabel = '匯出標籤';
  const exportFileSuccessMessage = `檔案匯出成功，共 ${PLACEHOLDERS.numUsers} 個會員、${PLACEHOLDERS.numLabels} 個標籤`;
  const exportFileNameTemplate = `${PLUGIN_NAMESPACE}-${PLACEHOLDERS.timestamp}.json`;
  const exportFileEmptyDataErrorMessage = '沒有標籤記錄';
  // templates
  const _addButtonTemplate = `<a href="#">${addLabelButtonLabel}</a>`;
  const _labelListTemplate = `<ul class="${CSS_CLASSES.labelList}" data-user="${PLACEHOLDERS.userId}"></ul>`;
  const _labelItemTemplate = `
    <li tabindex="0" aria-label="${PLACEHOLDERS.label}">
      <div class="${CSS_CLASSES.labelInfo}">
        <div class="${CSS_CLASSES.labelInfoReason}">${PLACEHOLDERS.reason}</div>
        <div class="${CSS_CLASSES.labelInfoButtons}">
          <a class="${CSS_CLASSES.labelEditButton}" href="#" role="button" aria-label="${editButtonLabel}" data-tip="${editButtonLabel}" title="${editButtonLabel}" currentitem="false"><i class="i-pencil"></i></a>
          <a class="${CSS_CLASSES.labelSourceButton}" href="${PLACEHOLDERS.sourceURL}" target="_blank" aria-label="${sourceButtonLabel}" data-tip="${sourceButtonLabel}" title="${sourceButtonLabel}" currentitem="false"><i class="i-link"></i></a>
          <a class="${CSS_CLASSES.labelDeleteButton}" href="#" role="button" aria-label="${deleteButtonLabel}" data-tip="${deleteButtonLabel}" title="${deleteButtonLabel}" currentitem="false"><i class="i-delete-forever"></i></a>
        </div>
      </div>
    </li>
  `;
  const _settingsSectionTitleTemplate = `<small class="${CSS_CLASSES.settingsSectionTitle}">${settingsSectionTitle}</small>`;
  const _settingsSectionOptionsListTemplate = `
    <ul class="${CSS_CLASSES.settingsOptionsList}">
      <li class="${CSS_CLASSES.settingsOptionsListItem}">
        <a href="#" role="button" class="${CSS_CLASSES.settingsOptionButton} ${CSS_CLASSES.exportButton}">${exportFileButtonLabel}</a>
      </li>
      <li class="${CSS_CLASSES.settingsOptionsListItem}">
        <div>
          <input id="${importFileInputId}" type="file" accept="text/json">
          <label for="${importFileInputId}" class="${CSS_CLASSES.settingsOptionButton}">${importFileButtonLabel}</label>
          <small>${importFileButtonReminder}</small>
        </div>
      </li>
    </ul>
  `;
  const _snipeButtonTemplate = `<i class="i-hot ${CSS_CLASSES.replyToolbarButton} ${CSS_CLASSES.snipeButton}" data-tip="${snipeButtonLabel}" title="${snipeButtonLabel}" currentitem="false"></i>`;
  const _snipeReplyBodyTemplate = `
    [size=5]會員編號：${PLACEHOLDERS.userId}
    會員名稱：${PLACEHOLDERS.username}
    註冊日期：${PLACEHOLDERS.registrationDate}[/size=5]
    ${PLACEHOLDERS.snipeReplyLabelsContent}
  `;
  const _snipeReplyLabelContentTemplate = `
    [size=5]【${PLACEHOLDERS.label}】
    事蹟：${PLACEHOLDERS.reason}[/size=5]
    詳細來源：${PLACEHOLDERS.sourceURL}
  `;
  const _registrationDateTemplate = `${PLACEHOLDERS.year}年${PLACEHOLDERS.month}月${PLACEHOLDERS.day}日`;

  const _caches = { data: {}, threads: {}, replies: {}, users: {} };
  let _sourceReply = null;
  let _isSniping = false;
  let _snipingUser = null;

  try {
    _caches.data = _loadDataFromStorage();
  } catch (err) {
    console.error(err);
  }

  window[PLUGIN_NAMESPACE] = {
    // Backup localStorage
    localStorage: window.localStorage
  };

  // Create stylesheet
  _createStylesheet(`
    .${CSS_CLASSES.labelList} {
      list-style: none;
      margin: 0;
      padding: 0;
    }
    .${CSS_CLASSES.labelList}:empty {
      display: none;
    }
    .${CSS_CLASSES.labelList} li {
      display: inline-flex;
      align-items: center;
      border: solid 1px #444443;
      border-radius: 4px;
      margin: 4px;
      padding: 4px;
      position: relative;
    }
    .${CSS_CLASSES.labelList} li::before {
      content: attr(aria-label);
    }
    .${CSS_CLASSES.labelList} li:hover > i {
      display: inline;
    }
    .${CSS_CLASSES.labelList} li:focus-within > .${CSS_CLASSES.labelInfo},
    .${CSS_CLASSES.labelList} li:hover > .${CSS_CLASSES.labelInfo} {
      visibility: visible;
      opacity: 1;
      bottom: 100%;
      transition-delay: 0s;
    }
    .${CSS_CLASSES.labelInfo} {
      margin-bottom: 8px;
      padding: 8px;
      background-color: #222;
      border: solid 1px #444443;
      border-radius: 4px;
      text-align: center;
      visibility: hidden;
      opacity: 0;
      position: absolute;
      bottom: 50%;
      max-width: 200px;
      width: intrinsic;
      width: -moz-max-content;
      width: -webkit-max-content;
      width: max-content;
      left: 50%;
      transform: translateX(-50%);
      transition-property: all;
      transition-duration: .3s;
      transition-delay: .3s;
    }
    .${CSS_CLASSES.labelInfoReason},
    .${CSS_CLASSES.labelInfoButtons} > a {
      font-size: 1rem;
    }
    .${CSS_CLASSES.labelInfoReason} + .${CSS_CLASSES.labelInfoButtons} {
      margin-top: 8px;
    }
    .${CSS_CLASSES.labelInfoButtons} {
      display: flex;
      align-items: center;
      justify-content: center;
    }
    .${CSS_CLASSES.labelInfoButtons} > a {
      display: inline-flex;
      color: #aaa;
      cursor: pointer;
      text-decoration: none;
    }
    .${CSS_CLASSES.labelInfoButtons} > a + a {
      margin-left: 4px;
    }
    ${SELECTORS.thread} .${CSS_CLASSES.labelList} {
      margin-left: 6px;
    }
    ${SELECTORS.thread} .${CSS_CLASSES.labelList} li {
      line-height: normal;
    }
    ${SELECTORS.nickname} ~ .${CSS_CLASSES.labelList} {
      margin-left: .4em;
    }
    ${SELECTORS.nickname} ~ .${CSS_CLASSES.labelList} li {
      margin: 0;
    }
    ${SELECTORS.nickname} ~ .${CSS_CLASSES.labelList} li + li {
      margin-left: .4em;
    }
    ${SELECTORS.modal} .${CSS_CLASSES.labelList} {
      margin: 4px -4px -4px;
    }
    #${importFileInputId} {
      display: none;
    }
    #${importFileInputId} + label:hover {
      cursor: pointer;
      text-decoration: underline;
    }
    #${importFileInputId} + label + small {
      display: block;
      margin-top: 4px;
    }
    .${CSS_CLASSES.replyItem} {
      overflow: visible;
    }
  `);

  // Intercept XHR
  _interceptXHR('load', function () {
    const isThreadList = THREAD_LIST_API_URL_REGEX.test(this.responseURL);
    const isReplyList = REPLY_LIST_API_URL_REGEX.test(this.responseURL);
    const isQuoteList = QUOTE_LIST_API_URL_REGEX.test(this.responseURL);
    if (isThreadList || isReplyList || isQuoteList) {
      const data = JSON.parse(this.responseText);
      if (data.success === 1) {
        if (isThreadList) {
          _cacheThreads(data.response);
        }
        if (isReplyList || isQuoteList) {
          _cacheReplies(data.response);
        }
        _cacheUsers(data.response);
      }
    }
  });

  // store the message number of the clicked username
  document.addEventListener('click', (event) => {
    try {
      const { parentNode } = event.target;
      if (parentNode && parentNode.matches(SELECTORS.nickname)) {
        const { parentNode: replyElement } = parentNode.parentNode.parentNode;
        const postId = replyElement.getAttribute(ATTRIBUTES.dataPostId);
        _sourceReply = _caches.replies[postId];
      }
    } catch (err) {
      console.error(err);
      _sourceReply = null;
    }
  });

  // sync data between browser tabs
  window.addEventListener('storage', (event) => {
    const { key } = event;
    if (key === localStorageDataKey) {
      _caches.data = _loadDataFromStorage();
      _renderExistingLabelLists();
    }
  });

  function renderThread (node) {
    window.requestAnimationFrame(() => {
      const threadLink = node.querySelector(SELECTORS.threadLink);
      const href = threadLink.getAttribute('href');
      const threadId = href.match(THREAD_URL_REGEX)[1];
      const thread = _caches.threads[threadId];
      const { user_id: user } = thread;
      const labelList = _createLabelList(user);
      const threadUsername = node.querySelector(SELECTORS.threadUsername);
      _insertAfter(labelList, threadUsername);
    });
  }

  function renderUserCardModal (node) {
    const addButton = _createAddButton(node);
    const userCardButtonsContainer = node.querySelector(SELECTORS.userCardButtonsContainer);
    userCardButtonsContainer.appendChild(addButton);
    const user = _getUserIdFromUserCardModal(node);
    const labelList = _createLabelList(user);
    const modelContentInner = node.querySelector(SELECTORS.modalContentInner);
    modelContentInner.appendChild(labelList);
  }

  function renderSettingsModal (node) {
    const settingsSectionTitle = _createSettingsSectionTitle();
    const modelContentInner = node.querySelector(SELECTORS.modalContentInner);
    modelContentInner.appendChild(settingsSectionTitle);
    const settingsSectionOptionsList = _createSettingsSectionOptionsList();
    modelContentInner.appendChild(settingsSectionOptionsList)
  }

  function renderSubmissionForm (node) {
    if (_isSniping && _snipingUser) {
      const formComponent = _findReactComponent(node, 1);
      if (formComponent) {
        const snipeReplyBody = _generateSnipeReplyBody(_snipingUser);
        formComponent.replaceEditorContent(snipeReplyBody);
        _isSniping = false;
        _snipingUser = null;
      }
    }
  }

  function renderNickname (node) {
    const nicknameLink = node.querySelector(SELECTORS.nicknameLink);
    if (nicknameLink) {
      const href = nicknameLink.getAttribute('href');
      const matched = href.match(PROFILE_URL_REGEX);
      if (matched) {
        const user = matched[1];
        const labelList = _createLabelList(user);
        _insertAfter(labelList, node);
        const snipeButton = _createSnipeButton(user);
        _insertAfter(snipeButton, labelList);
      }
    }
  }

  function unrenderNickname (node) {
    const { parentNode } = node;
    const labelLists = parentNode.querySelectorAll(`.${CSS_CLASSES.labelList}`);
    for (const labelList of labelLists) {
      labelList.remove();
    }
    const snipeButtons = parentNode.querySelectorAll(`.${CSS_CLASSES.snipeButton}`);
    for (const snipeButton of snipeButtons) {
      snipeButton.remove();
    }
  }

  function _renderLabelLists (user) {
    const selector = `.${CSS_CLASSES.labelList}[${ATTRIBUTES.dataUser}="${user}"]`;
    const labelLists = document.querySelectorAll(selector);
    for (const labelList of labelLists) {
      const _labelList = _createLabelList(user);
      _insertAfter(_labelList, labelList);
      labelList.remove();
    }
  }

  function _renderExistingLabelLists () {
    const users = _getExistingUserIds();
    for (const user of users) {
      _renderLabelLists(user);
    }
  }

  function _createAddButton (node) {
    const addButton = _createElementFromHTML(_addButtonTemplate);
    addButton.addEventListener('click', (event) => {
      event.preventDefault();
      const user = _getUserIdFromUserCardModal(node);
      const { text, reason } = _prompt();
      if (text) {
        _add(user, text, reason);
      }
    });
    return addButton;
  }

  function _createSnipeButton (user) {
    const snipeButton = _createElementFromHTML(_snipeButtonTemplate);
    snipeButton.addEventListener('click', (event) => {
      event.preventDefault();
      const labels = _getLabels(user);
      if (labels.length > 0) {
        _isSniping = true;
        _snipingUser = user;
        const { target } = event;
        const replyButton = target.parentNode.querySelector('.i-reply');
        replyButton.click();
      } else {
        window.alert(snipeNothingAlertMessage);
      }
    });
    return snipeButton;
  }

  function _createLabelList (user) {
    const labelListHTML = _labelListTemplate.replace(PLACEHOLDERS.userId, user);
    const labelList = _createElementFromHTML(labelListHTML);
    const labels = _getLabels(user);
    for (const label of labels) {
      const labelItem = _createLabelItem(user, label);
      labelList.appendChild(labelItem);
    }
    return labelList;
  }

  function _createLabelItem (user, label) {
    const reason = label.reason || '';
    const sourceURL = _getSourceURL(label) || '';
    const labelItemHTML = _labelItemTemplate.replace(PLACEHOLDERS.label, label.text)
                                            .replace(PLACEHOLDERS.reason, reason)
                                            .replace(PLACEHOLDERS.sourceURL, sourceURL);
    const labelItem = _createElementFromHTML(labelItemHTML);

    const editButton = labelItem.querySelector(`.${CSS_CLASSES.labelEditButton}`);
    editButton.addEventListener('click', (event) => {
      event.preventDefault();
      const { text, reason } = _prompt(label.text, label.reason);
      if (text) {
        _edit(user, label, text, reason);
      }
    });

    const deleteButton = labelItem.querySelector(`.${CSS_CLASSES.labelDeleteButton}`);
    deleteButton.addEventListener('click', (event) => {
      event.preventDefault();
      const message = deleteLabelQuestion.replace(PLACEHOLDERS.label, label.text);
      const confirmed = window.confirm(message);
      if (confirmed) {
        _delete(user, label);
      }
    });

    if (reason === '') {
      const reason = labelItem.querySelector(`.${CSS_CLASSES.labelInfoReason}`);
      reason.remove();
    }

    if (sourceURL === '') {
      const source = labelItem.querySelector(`.${CSS_CLASSES.labelSourceButton}`);
      source.remove();
    }

    return labelItem;
  }

  // Helper functions
  function _createStylesheet (stylesheet) {
    const style = document.createElement('style');
//    style.setAttribute('type', 'text/css');
    style.innerHTML = stylesheet;
    document.head.appendChild(style);
  }

  function _interceptXHR (event, handler) {
    const _open = XMLHttpRequest.prototype.open;
    XMLHttpRequest.prototype.open = function () {
      this.addEventListener(event, handler.bind(this));
      return _open.apply(this, arguments);
    };
  }

  function _findReactComponent (element, traverseUp = 0) {
    const key = Object.keys(element).find((key) => key.startsWith('__reactInternalInstance$'));
    const fiber = element[key];
    if (!fiber) return null;
    // react <16
    if (fiber._currentElement) {
        let compFiber = fiber._currentElement._owner;
        for (let i = 0; i < traverseUp; i++) {
            compFiber = compFiber._currentElement._owner;
        }
        return compFiber._instance;
    }
    // react 16+
    const getComponentFiber = (fiber) => {
        // return fiber._debugOwner; // this also works, but is __DEV__ only
        let parentFiber = fiber.return;
        while (typeof parentFiber.type === 'string') {
            parentFiber = parentFiber.return;
        }
        return parentFiber;
    };
    let compFiber = getComponentFiber(fiber);
    for (let i = 0; i < traverseUp; i++) {
        compFiber = getComponentFiber(compFiber);
    }
    return compFiber.stateNode;
  }

  function _getExistingUserIds () {
    const selector = `.${CSS_CLASSES.labelList}`;
    const labelLists = document.querySelectorAll(selector);
    const users = [];
    for (const labelList of labelLists) {
      const user = labelList.getAttribute(ATTRIBUTES.dataUser);
      users.push(user);
    }
    return users;
  }

  function _getUserIdFromUserCardModal (node) {
    const userCardButtonsContainer = node.querySelector(SELECTORS.userCardButtonsContainer);
    const anchors = userCardButtonsContainer.querySelectorAll('a');
    for (const a of anchors) {
      const href = a.getAttribute('href');
      const matched = href.match(PROFILE_URL_REGEX);
      if (matched) {
        return matched[1];
      }
    }
  }

  function _createSettingsSectionTitle () {
    const settingsSectionTitle = _createElementFromHTML(_settingsSectionTitleTemplate);
    return settingsSectionTitle;
  }

  function _createSettingsSectionOptionsList () {
    const settingsSectionOptionsList = _createElementFromHTML(_settingsSectionOptionsListTemplate);

    // export button
    const exportButton = settingsSectionOptionsList.querySelector(`.${CSS_CLASSES.exportButton}`);
    exportButton.addEventListener('click', (event) => {
      event.preventDefault();
      _export();
    });

    // import button
    const importButton = settingsSectionOptionsList.querySelector(`#${importFileInputId}`);
    importButton.addEventListener('change', (event) => {
      const { files } = event.target;
      const file = files[0];
      _import(file);
      event.target.value = '';
    });

    return settingsSectionOptionsList;
  }

  function _generateSnipeReplyBody (user) {
    const labels = _getLabels(user);
    const snipeReplyLabelsContent = labels.map((label) => {
      const { text, reason } = label;
      const sourceURL = _getSourceURL(label);
      return _snipeReplyLabelContentTemplate.replace(PLACEHOLDERS.label, text)
                                            .replace(PLACEHOLDERS.reason, reason || snipeEmptyReasonFallback)
                                            .replace(PLACEHOLDERS.sourceURL, sourceURL || snipeEmptySourceURLFallback)
                                            .trim();
    }).join('\n\n');
    const _user = _caches.users[user];
    const registrationDate = _getRegistrationTime(_user);
    let snipeReplyBody = _snipeReplyBodyTemplate.replace(PLACEHOLDERS.userId, _user.user_id)
                                                .replace(PLACEHOLDERS.username, _user.nickname)
                                                .replace(PLACEHOLDERS.registrationDate, _formatDate(registrationDate))
                                                .replace(PLACEHOLDERS.snipeReplyLabelsContent, snipeReplyLabelsContent);
    snipeReplyBody = snipeReplyBody.split('\n').map((line) => line.trim()).join('\n');
    return snipeReplyBody.trim();
  }

  function _getRegistrationTime (user) {
    return new Date(user.create_time * 1000);
  }

  function _formatDate (date) {
    const year = date.getFullYear();
    const month = date.getMonth() + 1;
    const day = date.getDate();
    return _registrationDateTemplate.replace(PLACEHOLDERS.year, year)
                                    .replace(PLACEHOLDERS.month, month)
                                    .replace(PLACEHOLDERS.day, day);
  }

  function _getData () {
    return _caches.data;
  }

  function _getLabels (user) {
    const data = _getData();
    return data[user] || [];
  }

  function _findLabelIndexByText (labels, text) {
    return labels.findIndex((label) => label.text === text);
  }

  function _findLabelByText (labels, text) {
    const index = _findLabelIndexByText(labels, text);
    return labels[index];
  }

  function _getSourceURL (label) {
    if (label.source) {
      return sourceURLTemplate.replace(PLACEHOLDERS.threadId, label.source.thread)
                              .replace(PLACEHOLDERS.page, label.source.page)
                              .replace(PLACEHOLDERS.messageNumber, label.source.messageNumber);
    }
    return label.url;
  }

  function _prompt (_text = '', _reason = '') {
    const text = (window.prompt(addLabelQuestion, _text) || '').trim();
    if (text) {
      const reason = (window.prompt(addLabelReasonQuestion, _reason) || '').trim();
      return { text, reason };
    }
    return {};
  }

  function _add (user, text, reason) {
    const labels = _getLabels(user);
    const label = _findLabelByText(labels, text);
    if (!label) {
      const url = window.location.href;
      const date = Date.now();
      const label = { text, reason, url, date };
      if (_sourceReply) {
        label.source = {
          thread: _sourceReply.thread_id,
          page: _sourceReply.page,
          messageNumber: _sourceReply.msg_num
        };
      }
      labels.push(label);
      return _save(user, labels);
    }
  }

  function _edit (user, label, text, reason) {
    const labels = _getLabels(user);
    // edit object reference
    label.text = text;
    label.reason = reason;
    return _save(user, labels);
  }

  function _delete (user, label) {
    const labels = _getLabels(user);
    const index = _findLabelIndexByText(labels, label.text);
    if (index !== -1) {
      labels.splice(index, 1);
      return _save(user, labels);
    }
  }

  function _save (user, labels) {
    const data = _getData();
    if (labels.length === 0) {
      delete data[user];
    } else {
      data[user] = labels;
    }
    const value = JSON.stringify(data);
    const { localStorage } = window[PLUGIN_NAMESPACE];
    localStorage.setItem(localStorageDataKey, value);
    _renderLabelLists(user);
    return data;
  }

  function _validate (json) {
    let valid = false;
    try {
      const data = JSON.parse(json);
      const _data = _massage(data);
      const users = Object.keys(_data);
      valid = users.every((user) => typeof user === 'string');
      if (valid) {
        for (const user of users) {
          const labels = _data[user];
          valid = labels.every((label) => {
            if (typeof label !== 'string') {
              const { text, reason, url, date, source } = label;
              return (
                typeof text === 'string'
                && typeof reason === 'string'
                && typeof url === 'string'
                && typeof date === 'number'
                && (
                  !source ? true : (
                    (typeof source.thread === 'string' && /\d+/.test(source.thread))
                    && (typeof source.page === 'number')
                    && (typeof source.messageNumber === 'string' && /\d+/.test(source.messageNumber))
                  )
                )
              );
            }
            return typeof label === 'string';
          });
          if (!valid) {
            break;
          }
        }
      }
    } catch (err) {
      console.error(err);
    }
    return valid;
  }

  function _import (file) {
    const reader = new FileReader();
    reader.readAsText(file, 'UTF-8');
    reader.addEventListener('load', (event) => {
      const { result } = event.target;
      if (_validate(result)) {
        const { localStorage } = window[PLUGIN_NAMESPACE];
        localStorage.setItem(localStorageDataKey, result);
        const data = JSON.parse(result);
        _caches.data = _massage(data);
        _renderExistingLabelLists();
        const aggregated = _aggregate(data);
        const message = importFileSuccessMessage
          .replace(PLACEHOLDERS.numUsers, aggregated.users.length)
          .replace(PLACEHOLDERS.numLabels, aggregated.labels.length);
        window.alert(message);
      } else {
        window.alert(importFileDataFormatErrorMessage);
      }
    });
    reader.addEventListener('error', (event) => {
      window.alert(importFileGenericErrorMessage);
    });
  }

  function _export () {
    const data = _getData();
    const users = Object.keys(data);
    if (users.length) {
      const json = JSON.stringify(data);
      _download(json);
      // TODO so sad, there is no way to detect whether the user has downloaded the file or not
      const aggregated = _aggregate(data);
      const message = exportFileSuccessMessage
        .replace(PLACEHOLDERS.numUsers, aggregated.users.length)
        .replace(PLACEHOLDERS.numLabels, aggregated.labels.length);
      window.alert(message);
    } else {
      window.alert(exportFileEmptyDataErrorMessage);
    }
  }

  function _download (json) {
    const blob = new Blob([json], { type: 'text/json' });
    const a = document.createElement('a');
    const url = URL.createObjectURL(blob);
    a.setAttribute('href', url);
    a.download = exportFileNameTemplate.replace(PLACEHOLDERS.timestamp, _getTimestamp());
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
  }

  // convert data structure to new version
  function _massage (data) {
    if (Array.isArray(data)) {
      return data.reduce((data, entry) => {
        const { user, labels } = entry;
        data[user] = labels.map((label) => {
          if (typeof label === 'string') {
            // backward compatible
            return { text: label };
          }
          return label;
        });
        return data;
      }, {});
    }
    return data;
  }

  function _aggregate (data) {
    const users = Object.keys(data);
    const labels = users.reduce((labels, user) => {
      labels = labels.concat(data[user]);
      return labels;
    }, []);
    return { users, labels };
  }

  function _getTimestamp () {
    const now = new Date();
    const year = now.getFullYear();
    const month = _padZero(now.getMonth() + 1);
    const date = _padZero(now.getDate());
    const hours = _padZero(now.getHours());
    const minutes = _padZero(now.getMinutes());
    const seconds = _padZero(now.getSeconds());
    return `${year}${month}${date}${hours}${minutes}${seconds}`;
  }

  function _padZero (value) {
    return value < 10 ? `0${value}` : `${value}`;
  }

  function _isThread (node) {
    return node.matches(SELECTORS.thread);
  }

  function _isUserCardModal (node) {
    return _isModalTitleMatched(node, USER_CARD_MODAL_TITLE);
  }

  function _isSettingsModal (node) {
    return _isModalTitleMatched(node, SETTINGS_MODAL_TITLE);
  }

  function _isModalTitleMatched (node, title) {
    if (node.matches(SELECTORS.modal)) {
      const modalTitle = node.querySelector(SELECTORS.modalTitle);
      if (modalTitle) {
        return modalTitle.textContent === title;
      }
    }
    return false;
  }

  function _isSubmissionForm (node) {
    return node.matches(SELECTORS.submissionForm);
  }

  function _insertAfter (newNode, referenceNode) {
    referenceNode.parentNode.insertBefore(newNode, referenceNode.nextSibling);
  }

  function _createElementFromHTML (html) {
    const parser = new DOMParser();
    const document = parser.parseFromString(html, 'text/html');
    return document.body.firstChild;
  }

  function _cacheThreads (response) {
    const { items } = response;
    for (const item of items) {
      const { thread_id: threadId } = item;
      _caches.threads[threadId] = item;
    }
  }

  function _cacheReplies (response) {
    const { item_data: items } = response;
    for (const item of items) {
      const { post_id: postId } = item;
      _caches.replies[postId] = item;
    }
  }

  function _cacheUsers (response) {
    const { item_data, items } = response;
    const _items = item_data || items;
    for (const item of _items) {
      const { user } = item;
      _caches.users[user.user_id] = user;
    }
  }

  function _loadDataFromStorage () {
    const json = localStorage.getItem(localStorageDataKey) || '{}';
    const data = JSON.parse(json);
    return _massage(data);
  }

  const observer = new MutationObserver((mutations) => {
    for (const mutation of mutations) {

      if (DEBUG) {
        const { type, target, addedNodes, removedNodes } = mutation;
        console.info(type, target, addedNodes, removedNodes);
        console.info('==========================================================');
      }

      window.requestAnimationFrame(() => {
        switch (mutation.type) {
          case 'childList': {
            for (const node of mutation.addedNodes) {
              if (node.nodeType === document.ELEMENT_NODE) {
                if (_isThread(node)) {
                  renderThread(node);
                } else if (_isUserCardModal(node)) {
                  renderUserCardModal(node);
                } else if (_isSettingsModal(node)) {
                  renderSettingsModal(node);
                } else if (_isSubmissionForm(node)) {
                  renderSubmissionForm(node);
                } else {
                  const nicknames = node.querySelectorAll(SELECTORS.nickname);
                  for (const node of nicknames) {
                    renderNickname(node);
                  }
                }
              }
            }
            break;
          }
          case 'attributes': {
            if (mutation.attributeName === ATTRIBUTES.dataPostId) {
              const { target } = mutation;
              const nickname = target.querySelector(SELECTORS.nickname);
              unrenderNickname(nickname);
              renderNickname(nickname);
            }
            break;
          }
        }
      });
    }
  });

  observer.observe(document.body, {
    subtree: true,
    childList: true,
    attributes: true,
    attributeFilter: [ATTRIBUTES.dataPostId]
  });
})();