function setDarkMode(d) {

    let currentDarkMode = null;

    let fallback = true;

    const strModesettings = localStorage.modesettings;
    let objModesettings = null;

    if (typeof strModesettings === 'string') {
        try {
            objModesettings = JSON.parse(strModesettings);
        } catch (e) {}
    }

    if (objModesettings && objModesettings[0]) {
        currentDarkMode = objModesettings[0].darkMode;
    }

    if ((currentDarkMode === true && d === false) || (currentDarkMode === false && d === true)) {


        let moonButton = document.querySelector('.i-moon');
        try {
            if (moonButton) {

                const key = Object.keys(moonButton).find((key) => key.startsWith('__reactEventHandlers$'));
                if (key) {
                    let res = moonButton[key].onClick();
                    if (res && res.type == "TOGGLE_MODE_SETTINGS" && res.payload == "darkMode") {
                        fallback = false;
                    }
                }
            }
        } catch (e) {}

    }



    if (fallback) {


        if (objModesettings && objModesettings[0]) {
            objModesettings[0].darkMode = d ? true : false;
            localStorage.modesettings = JSON.stringify(objModesettings);
            if (d) {
                document.body.dataset.appDm = '';
            } else {
                delete document.body.dataset.appDm;
            }
        }

    }

}