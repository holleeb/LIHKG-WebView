function hideIdentity() {

  window.m2u3 = window.m2u3 || function () {

  };

  window.m2u4 = window.m2u4 || function () {

  };

  let currentDescriptor = Object.getOwnPropertyDescriptor(document, 'cookie');
  if (!currentDescriptor){

  let standardDescriptor = Object.getOwnPropertyDescriptor(Document.prototype, 'cookie');
  let standardGetter = standardDescriptor.get;
  let standardSetter = standardDescriptor.set;

  Object.defineProperty(document, 'cookie', {
    set(x) {
      //
    },
    get() {
      return '';
    },
    enumerable: true,
    configurable: true
  });


  console.log(344, document.cookie);
  }


   currentDescriptor = Object.getOwnPropertyDescriptor(window, 'localStorage');
  if (!currentDescriptor||!currentDescriptor.set){

  Object.defineProperty(window, 'localStorage', {
    set(x) {
      //
    },
    get() {
      return {};
    },
    enumerable: true,
    configurable: true
  });


  console.log(355, localStorage);
  }

  requestAnimationFrame(hideIdentity);



}