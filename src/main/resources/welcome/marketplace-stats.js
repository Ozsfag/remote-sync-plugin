// src/main/resources/welcome/marketplace-stats.js
(function () {
  function setText(el, val) { if (el && val != null && val !== '') el.textContent = String(val); }

  function updateContainer(container, data) {
    // Ищем элементы внутри контейнера (есть id, но дадим и fallback по data-field)
    var elTotal = container.querySelector('#mp-downloads-total') || container.querySelector('[data-field="total-downloads"]');
    var elVer   = container.querySelector('#mp-version')         || container.querySelector('[data-field="latest-version"]');

    if (elTotal) {
      var v = data && data.downloads != null ? data.downloads : '—';
      // если приходит число/строка числа — красиво форматируем
      var n = Number(v);
      setText(elTotal, Number.isFinite(n) ? n.toLocaleString() : v);
    }
    if (elVer) setText(elVer, (data && data.version) || '—');
  }

  function fetchAndFill(container) {
    var pluginId = container.getAttribute('data-plugin-id') || 'org.blacksoil.remotesync';
    var endpoint = container.getAttribute('data-endpoint') || '/api/marketplace/stats';
    var url = endpoint + '?pluginId=' + encodeURIComponent(pluginId);

    fetch(url, { credentials: 'omit' })
      .then(function (r) { return r.ok ? r.json() : Promise.reject(new Error('HTTP ' + r.status)); })
      .then(function (j) { updateContainer(container, j || {}); })
      .catch(function () { /* тихий фолбэк — плейсхолдеры остаются */ });
  }

  function init() {
    // Берём только те .stats, где указан endpoint (чтобы не трогать твой первый блок со «downloads this month»)
    var containers = document.querySelectorAll('.stats[data-endpoint]');
    if (!containers.length) return;
    containers.forEach(fetchAndFill);
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
  } else {
    init();
  }
})();
