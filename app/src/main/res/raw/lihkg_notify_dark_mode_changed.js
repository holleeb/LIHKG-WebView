function notifyDarkModeChange() {
    if (typeof xec2D === "undefined" || !xec2D || !xec2D.notifyDarkMode) {
      return;
    }
    if (window.xec2E) {
      return;
    }

    window.xec2E = true;

    // Select the target node
    const targetNode = document.body;

    // Create a new instance of MutationObserver
    const observer = new MutationObserver((mutationsList) => {
      // Loop through the mutations list
      for (let mutation of mutationsList) {
        if (mutation.type === "attributes" && mutation.attributeName === "data-app-dm") {
          if (mutation.target && mutation.target.nodeType === 1) {
            // The 'data-app-dm' attribute has been changed on the target element
            xec2D.notifyDarkMode(mutation.target.hasAttribute("data-app-dm"));
          }
        }
      }
    });

    // Configuration options for the observer
    const config = { attributes: true, attributeFilter: ["data-app-dm"] };

    // Start observing the target node for attribute changes
    observer.observe(targetNode, config);
  }