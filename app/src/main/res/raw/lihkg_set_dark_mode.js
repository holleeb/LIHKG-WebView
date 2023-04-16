function setDarkMode(d) {
  const strModesettings = localStorage.modesettings;
  let objModesettings = null;

  if (typeof strModesettings === 'string') {
    try {
      objModesettings = JSON.parse(strModesettings);
    } catch (e) {}
  }

  if (objModesettings && objModesettings[0]) {
 objModesettings[0].darkMode = d?true:false;
     localStorage.modesettings = JSON.stringify(objModesettings);
   if(d) {document.body.dataset.appDm = '';} else {delete document.body.dataset.appDm;}
  }

}