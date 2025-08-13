(function () {
  // ——— utils ———
  const $ = (sel, root = document) => root.querySelector(sel);
  const fmtInt = (n) => {
    const v = Number(n);
    return Number.isFinite(v) ? v.toLocaleString() : '–';
  };

  // ——— 1) Goal bar ———
  function initGoal() {
    const goal = $('.goal');
    if (!goal) return;
    const current = Number(goal.getAttribute('data-current')) || 0;
    const target  = Number(goal.getAttribute('data-target'))  || 100;
    const pct = Math.max(0, Math.min(100, Math.round((current / target) * 100)));
    const bar = $('#goal-bar', goal);
    const text = $('#goal-text', goal);
    if (bar) requestAnimationFrame(() => { bar.style.width = pct + '%'; });
    if (text) text.textContent = `${fmtInt(current)} / ${fmtInt(target)}`;
  }

  // ——— 2) Community block (monthly downloads & sponsors) ———
  // Берём pluginId из live-блока, а total downloads — из marketplace-stats.js (через событие или кеш).
  function monthKey() {
    const d = new Date();
    return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}`;
  }
  function baselineKey(pluginId) {
    return `rs:baseline:${pluginId}:${monthKey()}`;
  }
  function parseTotalDownloads(val) {
    if (val == null) return 0;
    const n = Number(String(val).replace(/[^\d.-]/g, ''));
    return Number.isFinite(n) && n > 0 ? Math.floor(n) : 0;
  }

  function computeDownloadsThisMonth(pluginId, total) {
    try {
      const key = baselineKey(pluginId);
      const stored = localStorage.getItem(key);
      if (stored == null) {
        // первая встреча в этом месяце: фиксируем baseline = текущий total
        localStorage.setItem(key, String(total));
        return 0;
      }
      const baseline = Number(stored) || 0;
      return Math.max(0, total - baseline);
    } catch (_) {
      // Без localStorage считаем 0
      return 0;
    }
  }

  function readSponsorsCount() {
    // Опционально можно задать через meta:
    // <meta name="app-config" data-sponsors-count="3">
    const meta = document.querySelector('meta[name="app-config"]');
    const v = meta?.dataset?.sponsorsCount;
    if (v != null && v !== '') {
      const n = Number(v);
      if (Number.isFinite(n)) return n;
    }
    return null; // оставим "–", если не задано
  }

  function fillCommunityStatsFromData(pluginId, data) {
    const elMonth   = $('#downloads-month');
    const elSponsor = $('#sponsors-count');
    if (!elMonth && !elSponsor) return;

    const total = parseTotalDownloads(data?.downloads);
    if (elMonth) {
      const monthVal = computeDownloadsThisMonth(pluginId, total);
      elMonth.textContent = fmtInt(monthVal);
    }

    if (elSponsor) {
      const sponsors = readSponsorsCount();
      if (sponsors != null) elSponsor.textContent = fmtInt(sponsors);
      // если не задано — оставим "–"
    }
  }

  function tryInitCommunityStats() {
    // Определяем pluginId из live-контейнера
    const live = document.querySelector('.stats[data-plugin-id]');
    const pluginId = live ? live.getAttribute('data-plugin-id') : null;
    if (!pluginId) return;

    // Если marketplace-stats уже успел положить данные — используем их
    const cache = window.RemoteSync && window.RemoteSync.statsByPluginId && window.RemoteSync.statsByPluginId[pluginId];
    if (cache) {
      fillCommunityStatsFromData(pluginId, cache);
      return;
    }

    // Иначе ждём событие от marketplace-stats.js
    const onStats = (e) => {
      if (e.detail && e.detail.pluginId === pluginId) {
        fillCommunityStatsFromData(pluginId, e.detail.data);
        document.removeEventListener('marketplace:stats', onStats);
      }
    };
    document.addEventListener('marketplace:stats', onStats, { once: true });

    // Доп. защита: таймаут на случай, если событие не придёт (например, ошибка сети)
    setTimeout(() => {
      const late = window.RemoteSync && window.RemoteSync.statsByPluginId && window.RemoteSync.statsByPluginId[pluginId];
      if (late) fillCommunityStatsFromData(pluginId, late);
    }, 3000);
  }

  // ——— init ———
  function init() {
    initGoal();
    tryInitCommunityStats();
  }

  if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', init);
  else init();
})();