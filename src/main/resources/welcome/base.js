(function () {
  // Версия из ?v=...
  try {
    const v = new URL(location.href).searchParams.get('v');
    if (v) {
      const el = document.querySelector('.ver');
      if (el) el.textContent = 'v' + v;
    }
  } catch (_) {}

  // Инициализация цели (может быть перезаписана из IDE)
  function initGoal() {
    const goal = document.querySelector('.goal');
    if (!goal) return;
    const cur = parseInt(goal.getAttribute('data-current') || '0', 10);
    const tgt = parseInt(goal.getAttribute('data-target') || '100', 10);
    updateGoal(cur, tgt);
  }

  function updateGoal(current, target) {
    const bar = document.getElementById('goal-bar');
    const txt = document.getElementById('goal-text');
    if (!bar || !txt) return;
    const pct = Math.max(0, Math.min(100, Math.round(100 * current / Math.max(target, 1))));
    bar.style.width = pct + '%';
    txt.textContent = current + ' / ' + target;
  }

  function updateStats(downloads, sponsors) {
    const dm = document.getElementById('downloads-month');
    const sc = document.getElementById('sponsors-count');
    if (dm && typeof downloads === 'number') dm.textContent = downloads.toLocaleString();
    if (sc && typeof sponsors === 'number') sc.textContent = sponsors.toLocaleString();
  }

  // Экспорт для IDE
  window.RemoteSyncSetData = function (data) {
    try {
      if (data) {
        if (data.monthly_goal) updateGoal(+data.monthly_goal.current || 0, +data.monthly_goal.target || 100);
        if ('downloads_month' in data || 'sponsors_count' in data) {
          updateStats(+data.downloads_month || 0, +data.sponsors_count || 0);
        }
      }
    } catch (_) {}
  };

  document.addEventListener('DOMContentLoaded', initGoal);
})();
