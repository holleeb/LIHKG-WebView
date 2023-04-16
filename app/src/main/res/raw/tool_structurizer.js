
    function traverseDOM(node) {
    if (!node) {
        return null;
    }

    const info = {
        tagName: node.tagName || "",
        id: node.id || "",
        classList: node.classList ? Array.from(node.classList) : [],
        children: [],
        hashKey: getHashKey(node),
        pType: ''
    };

    for (const child of node.children) {
        const childInfo = traverseDOM(child);
        if (childInfo) {
            info.children.push(childInfo);
        }
    }

    const consolidatedChildren = [];
    let currentGroup = [];

    let kz = 0;

    info.children.forEach((childInfo, index) => {
        if (currentGroup.length === 0 || shouldGroup(currentGroup[0], childInfo)) {
            currentGroup.push(childInfo);

        } else {
            consolidatedChildren.push(consolidator(currentGroup));
            currentGroup = [childInfo];
        }

        if (index === info.children.length - 1) {
            consolidatedChildren.push(consolidator(currentGroup));
        }
    });

    info.children = consolidatedChildren;
    return info;
}

function shouldGroup(info1, info2) {
    if (info1.tagName !== info2.tagName || info1.id || info2.id) {
        return false;
    }

    return info1.classList.some((className) => info2.classList.includes(className));
}






function idModifier(id) {
    const regex1 = /\b(\w+)([-_])(\d+)\b/g;
    const regex2 = /\b(\w+)(\d+)\b/g;
    return id.replace(regex1, '$1$2{i}').replace(regex2, '$1{i}');
}

function pseudoElementSelector(elm) {
    const idPart = (elm.id ? `#${idModifier(elm.id)}` : '');
    const classes = Array.from(elm.classList).join('.');
    const line = `${elm.tagName.toLowerCase()}${idPart}${classes ? '.' + classes : ''}`;

    return line;
}

function getHashKey(elm) {
    return pseudoElementSelector(elm);
}

function pseudoElementSelectorByInfo(elmInfo) {
    if (!elmInfo) {
        return null;
    }

    const idPart = (elmInfo.id ? `#${idModifier(elmInfo.id)}` : '');
    const classes = Array.isArray(elmInfo.classList) ? elmInfo.classList.join('.') : '';
    const line = `${elmInfo.tagName.toLowerCase()}${idPart}${classes ? '.' + classes : ''}`;

    return line;
}

function consolidator(infoArray) {
    if (infoArray.length === 1) {
        return infoArray[0];
    }

    const consolidatedInfo = {
        tagName: infoArray[0].tagName,
        id: "",
        classList: [],
        children: [],
        pType: "*",
        hashKey: pseudoElementSelectorByInfo(infoArray[0])
    };

    const classSet = new Set();
    const hashKeyMap = new Map();
    infoArray.forEach((info) => {
        info.classList.forEach((className) => classSet.add(className));
        info.children.forEach((child) => {
            child.hashKey = getHashKey(child);
            hashKeyMap.set(child.hashKey, (hashKeyMap.get(child.hashKey) || 0) + 1);
        });
    });

    consolidatedInfo.classList = Array.from(classSet);

    infoArray.forEach(info => {
        info.children = info.children.filter(child => hashKeyMap.get(child.hashKey) === infoArray.length);
    });

    const pTypeMap = new Map();
    infoArray.forEach(info => {
        info.children.forEach(child => {
            pTypeMap.set(child.hashKey, (pTypeMap.get(child.hashKey) || 0) + 1);
        });
    });

    consolidatedInfo.children = infoArray[0].children.map(child => {
        const pType = pTypeMap.get(child.hashKey) === infoArray.length ? '+' : '*';
        return { ...child, pType };
    });

    return consolidatedInfo;
}

/*

function consolidator(infoArray) {
    if (infoArray.length === 1) {
        return infoArray[0];
    }

    const consolidatedInfo = {
        tagName: infoArray[0].tagName,
        id: "",
        classList: [],
        children: [],
    };

    const classSet = new Set();
    infoArray.forEach((info) => {
        info.classList.forEach((className) => classSet.add(className));
        consolidatedInfo.children.push(...info.children);
    });

    consolidatedInfo.classList = Array.from(classSet);
    return consolidatedInfo;
}

*/

function processDOM(node) {
    if (!node || !(node instanceof HTMLElement)) {
        throw new Error("Invalid DOM Node input");
    }

    return traverseDOM(node);
}


function giveText(elmInfo, depth = 0, isGrouped = false) {
    // console.log(elmInfo)
    if (!elmInfo) {
        return "";
    }
    isGrouped = elmInfo.pType === '*';

    let result = "";
    const indent = " ".repeat(depth * 4); // Using 4 spaces for each level of indentation
    const prefix = depth === 0 ? "!" : (isGrouped ? "*" : "+");

    // Append hashKey or elmInfo.hashKey as the first line
    const hashKey = elmInfo.hashKey || getHashKey(elmInfo);
    result += `${indent}${prefix}${hashKey}\n`;

    // Process children recursively
    if (Array.isArray(elmInfo.children) && elmInfo.children.length > 0) {
        elmInfo.children.forEach(child => {
            const childIsGrouped = child.pType === '*';
            result += giveText(child, depth + 1, childIsGrouped);
        });
    }

    return result;
}

let selector = '#app';
let count = document.querySelectorAll(selector).length
if( !count) throw 'no such element.'
if( count > 1) throw 'unique element is required.'
let elementA = document.querySelector(selector);
let resultStructureText = giveText(processDOM(elementA));
console.log(resultStructureText);

