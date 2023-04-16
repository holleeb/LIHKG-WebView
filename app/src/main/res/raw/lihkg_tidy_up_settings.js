function tidyUpModeSettings() {
  const strModesettings = localStorage.modesettings;
  let objModesettings = null;

  if (typeof strModesettings === 'string') {
    try {
      objModesettings = JSON.parse(strModesettings);
    } catch (e) {}
  }

  let objModesettingsChanged = false;

  if (objModesettings !== null && objModesettings && objModesettings[0]) {
    let lastIdx = -1;

    for (let i = 0; i < 99; i++) {
      if (objModesettings[i]) lastIdx = i;
    }

    if (lastIdx >= 1) {
      if ('length' in objModesettings) {
        objModesettings = [objModesettings[0]];
      } else {
        objModesettings = { "0": objModesettings[0] };
      }

      objModesettingsChanged = true;
    }
//    if(objModesettings[0].imageProxy === true){objModesettings[0].imageProxy = false; objModesettingsChanged = true;}

    if (objModesettingsChanged) {
      localStorage.modesettings = JSON.stringify(objModesettings);
    }
  }
}