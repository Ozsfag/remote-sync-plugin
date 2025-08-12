(function () {
  function setText(el, val) {
    if (el && val != null && val !== '') el.textContent = String(val);
  }
  function formatNumberHuman(v) {
    if (v == null || v === '') return '–';
    var n = Number(String(v).replace(/[^\d.-]/g, ''));
    return Number.isFinite(n) ? n.toLocaleString() : String(v);
  }

  async function fetchAndFill(container) {
    var endpoint = container.getAttribute('data-endpoint');
    var pluginId = container.getAttribute('data-plugin-id');
    if (!endpoint || !pluginId) return;

    try {
      var url = new URL(endpoint);
      url.searchParams.set('pluginId', pluginId);
      var res = await fetch(url.toString(), { headers: { 'Accept': 'application/json' } });
      if (!res.ok) throw new Error('HTTP ' + res.status);
      var data = await res.json();

      // Заполняем только то, что получили
      var elTotal = container.querySelector('#mp-downloads-total');
      var elVer   = container.querySelector('#mp-version');

      if (elTotal && data.downloads) setText(elTotal, formatNumberHuman(data.downloads));
      if (elVer && data.version)     setText(elVer, data.version);

      // Расшарим данные для других скриптов
      window.RemoteSync = window.RemoteSync || {};
      window.RemoteSync.statsByPluginId = window.RemoteSync.statsByPluginId || {};
      window.RemoteSync.statsByPluginId[pluginId] = data;

      // Сообщим, что данные пришли
      document.dispatchEvent(new CustomEvent('marketplace:stats', {
        detail: { pluginId: pluginId, data: data }
      }));
    } catch (e) {
      // Оставляем "–" по умолчанию
      // console.warn('Marketplace stats error:', e);
    }
  }

  function init() {
    document.querySelectorAll('.stats[data-endpoint][data-plugin-id]').forEach(fetchAndFill);
  }

  if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', init);
  else init();
})();