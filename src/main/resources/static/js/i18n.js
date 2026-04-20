/**
 * RentMIS i18n — lightweight translation engine.
 *
 * Usage in JS templates:  I18n.t('Save Changes')
 * Language is derived from the logged-in user's profile (rentmis_user.language).
 * The navbar switcher in layout.js writes a session-only override to sessionStorage.
 *
 * English is the default — no file is loaded for ENGLISH.
 * For other languages a /static/js/lang/<code>.js file is loaded
 * after DOMContentLoaded; it calls I18n.apply({ key: translation, … }).
 */
'use strict';

window.I18n = (function () {

  var _userLang = (function() {
    try { return (JSON.parse(localStorage.getItem('rentmis_user') || '{}').language || ''); } catch(e) { return ''; }
  })();
  const LANG = (sessionStorage.getItem('viewLangSession') || _userLang || 'ENGLISH').toUpperCase();
  let dict = {};

  /* ── Public: translate a single string ───────────────────────── */
  function t(key) {
    return dict[key] || key;
  }

  /* ── Public: bulk-apply a translation map then walk the DOM ──── */
  function apply(translations) {
    dict = translations || {};
    if (document.body) {
      translateNode(document.body);
    }
    /* Watch for async-injected content (table rows, modals, etc.) */
    new MutationObserver(function (mutations) {
      mutations.forEach(function (m) {
        m.addedNodes.forEach(function (node) {
          if (node.nodeType === 1) translateNode(node);
        });
      });
    }).observe(document.body, { childList: true, subtree: true });
  }

  /* ── Walk an element: translate text nodes + key attributes ──── */
  function translateNode(root) {
    if (!root || root.nodeType !== 1) return;

    var walker = document.createTreeWalker(root, NodeFilter.SHOW_TEXT, null);
    var node;
    while ((node = walker.nextNode())) {
      var original = node.textContent;
      var trimmed  = original.trim();
      if (trimmed && dict[trimmed] !== undefined) {
        node.textContent = original.replace(trimmed, dict[trimmed]);
      }
    }

    /* Translatable attributes */
    ['placeholder', 'title', 'aria-label'].forEach(function (attr) {
      root.querySelectorAll('[' + attr + ']').forEach(function (el) {
        var val = el.getAttribute(attr);
        if (val && dict[val] !== undefined) el.setAttribute(attr, dict[val]);
      });
    });
  }

  /* ── Boot: load language file unless already English ─────────── */
  var LANG_FILES = {
    FRENCH:      '/static/js/lang/fr.js',
    KINYARWANDA: '/static/js/lang/rw.js',
  };

  if (LANG !== 'ENGLISH' && LANG_FILES[LANG]) {
    var langFile = LANG_FILES[LANG];
    if (document.readyState === 'loading') {
      document.addEventListener('DOMContentLoaded', function () { loadFile(langFile); });
    } else {
      loadFile(langFile);
    }
  }

  function loadFile(src) {
    var s  = document.createElement('script');
    s.src  = src;
    s.onerror = function () {
      console.warn('RentMIS i18n: could not load', src);
    };
    document.head.appendChild(s);
  }

  return { t: t, apply: apply, lang: LANG };

})();
