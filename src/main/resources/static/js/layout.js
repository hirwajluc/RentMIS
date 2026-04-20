/**
 * RentMIS Layout — injects navbar + sidebar, guards auth
 */
'use strict';

(function () {
  if (Auth.redirectIfNotLoggedIn()) return;

  const user = Auth.getUser();
  const role = user.role || '';

  // ── Role-based page access guard (uses currentPath declared below) ──
  const PAGE_ROLES = [
    { prefix: '/html/tenant/',     roles: ['TENANT'] },
    { prefix: '/html/landlord/',   roles: ['LANDLORD', 'ADMIN'] },
    { prefix: '/html/admin/',      roles: ['ADMIN'] },
    { prefix: '/html/agent/',      roles: ['AGENT'] },
    { prefix: '/html/reports/',    roles: ['ADMIN', 'LANDLORD'] },
    { prefix: '/html/payments/',   roles: ['ADMIN'] },
    { prefix: '/html/invoices/',   roles: ['ADMIN'] },
    { prefix: '/html/tenants/',    roles: ['ADMIN', 'LANDLORD'] },
    { prefix: '/html/properties/', roles: ['ADMIN', 'LANDLORD', 'AGENT'] },
    { prefix: '/html/units/',      roles: ['ADMIN', 'LANDLORD'] },
    { prefix: '/html/contracts/',  roles: ['ADMIN', 'LANDLORD', 'TENANT'] },
  ];

  // ── Sidebar menu items per role ──────────────────────────────
  const adminMenu = [
    { href: '/dashboard', icon: 'ti tabler-smart-home',   label: 'Dashboard' },
    { href: '/html/properties/list.html', icon: 'ti tabler-building-community', label: 'Properties' },
    { href: '/html/units/list.html',      icon: 'ti tabler-door',               label: 'Units' },
    { href: '/html/tenants/list.html',    icon: 'ti tabler-users',              label: 'Tenants' },
    { href: '/html/landlord/list.html',   icon: 'ti tabler-user-check',         label: 'Landlords' },
    { href: '/html/contracts/list.html',  icon: 'ti tabler-file-text',          label: 'Contracts' },
    { href: '/html/payments/list.html',   icon: 'ti tabler-credit-card',        label: 'Payments' },
    { href: '/html/reports/dashboard.html', icon: 'ti tabler-chart-bar',        label: 'Reports' },
    { href: '/html/admin/users.html',     icon: 'ti tabler-users',              label: 'Users' },
  ];
  const landlordMenu = [
    { href: '/dashboard',                       icon: 'ti tabler-smart-home',          label: 'Dashboard' },
    { href: '/html/properties/list.html',       icon: 'ti tabler-building-community',  label: 'My Properties' },
    { href: '/html/units/list.html',            icon: 'ti tabler-door',                label: 'Units' },
    { href: '/html/contracts/list.html',        icon: 'ti tabler-file-text',           label: 'Contracts' },
    { href: '/html/landlord/payments.html',     icon: 'ti tabler-credit-card',         label: 'Payments' },
    { href: '/html/reports/dashboard.html',     icon: 'ti tabler-chart-bar',           label: 'Reports' },
  ];
  const tenantMenu = [
    { href: '/html/tenant/dashboard.html',  icon: 'ti tabler-smart-home',   label: 'Dashboard' },
    { href: '/html/tenant/my-unit.html',    icon: 'ti tabler-door',         label: 'My Units' },
    { href: '/html/tenant/contracts.html',  icon: 'ti tabler-file-text',    label: 'My Contract' },
    { href: '/html/tenant/payments.html',   icon: 'ti tabler-credit-card',  label: 'Pay Rent' },
    { href: '/html/tenant/history.html',    icon: 'ti tabler-history',      label: 'Payment History' },
  ];
  const agentMenu = [
    { href: '/html/agent/dashboard.html',    icon: 'ti tabler-smart-home',         label: 'Dashboard' },
    { href: '/html/agent/properties.html',   icon: 'ti tabler-building-community', label: 'Vacant Properties' },
    { href: '/html/agent/linkages.html',     icon: 'ti tabler-link',               label: 'My Linkages' },
    { href: '/html/agent/commissions.html',  icon: 'ti tabler-coin',               label: 'Commissions' },
  ];

  const menu = role === 'TENANT'   ? tenantMenu
             : role === 'LANDLORD' ? landlordMenu
             : role === 'AGENT'    ? agentMenu
             : adminMenu;
  const currentPath = window.location.pathname;

  for (const rule of PAGE_ROLES) {
    if (currentPath.startsWith(rule.prefix)) {
      if (!rule.roles.includes(role)) {
        window.location.href = Auth.getHomeByRole(role);
        return;
      }
      break;
    }
  }

  const menuHtml = menu.map(item => {
    const active = currentPath === item.href || currentPath.startsWith(item.href.replace('.html','')) ? 'active' : '';
    return `<li class="menu-item ${active}">
      <a href="${item.href}" class="menu-link">
        <i class="menu-icon icon-base ${item.icon}"></i>
        <div>${item.label}</div>
      </a>
    </li>`;
  }).join('');

  // ── Brand SVGs ───────────────────────────────────────────────
  // Navbar: larger, has room to breathe
  const brandSvg = `<svg width="200" height="38" viewBox="0 0 360 68" xmlns="http://www.w3.org/2000/svg">
    <defs><linearGradient id="rmGrad" x1="0" y1="0" x2="1" y2="1">
      <stop offset="0%" stop-color="#0D2B55"/><stop offset="100%" stop-color="#122F5E"/>
    </linearGradient></defs>
    <rect x="0" y="2" width="56" height="56" rx="12" fill="url(#rmGrad)"/>
    <polygon points="28,9 10,23 46,23" fill="none" stroke="#fff" stroke-width="1.8" stroke-linejoin="round"/>
    <rect x="14" y="23" width="28" height="20" rx="2" fill="none" stroke="#fff" stroke-width="1.8"/>
    <rect x="21" y="31" width="10" height="12" rx="2" fill="#2A9D5C"/>
    <text font-family="Arial,Helvetica,sans-serif" font-weight="700" fill="#0D2B55" letter-spacing="0.04em" x="68" y="32" font-size="22">RentMIS</text>
    <text font-family="Arial,Helvetica,sans-serif" font-weight="400" fill="#4A6A8A" letter-spacing="0.14em" x="69" y="50" font-size="9">RENT MANAGEMENT SYSTEM</text>
    <rect x="69" y="54" width="280" height="1.2" rx="1" fill="#C8A84B" opacity="0.7"/>
  </svg>`;
  // Sidebar: compact so the collapse toggle button still fits
  const sidebarBrandSvg = `<svg width="148" height="28" viewBox="0 0 360 68" xmlns="http://www.w3.org/2000/svg">
    <defs><linearGradient id="rmGradS" x1="0" y1="0" x2="1" y2="1">
      <stop offset="0%" stop-color="#0D2B55"/><stop offset="100%" stop-color="#122F5E"/>
    </linearGradient></defs>
    <rect x="0" y="2" width="56" height="56" rx="12" fill="url(#rmGradS)"/>
    <polygon points="28,9 10,23 46,23" fill="none" stroke="#fff" stroke-width="1.8" stroke-linejoin="round"/>
    <rect x="14" y="23" width="28" height="20" rx="2" fill="none" stroke="#fff" stroke-width="1.8"/>
    <rect x="21" y="31" width="10" height="12" rx="2" fill="#2A9D5C"/>
    <text font-family="Arial,Helvetica,sans-serif" font-weight="700" fill="#0D2B55" letter-spacing="0.04em" x="68" y="32" font-size="22">RentMIS</text>
    <text font-family="Arial,Helvetica,sans-serif" font-weight="400" fill="#4A6A8A" letter-spacing="0.14em" x="69" y="50" font-size="9">RENT MANAGEMENT SYSTEM</text>
    <rect x="69" y="54" width="280" height="1.2" rx="1" fill="#C8A84B" opacity="0.7"/>
  </svg>`;

  // ── Language switcher helpers ────────────────────────────────
  const LANG_META = {
    ENGLISH:     { flag: '🇬🇧', label: 'English' },
    KINYARWANDA: { flag: '🇷🇼', label: 'Kinyarwanda' },
    FRENCH:      { flag: '🇫🇷', label: 'French' },
  };
  const activeLang = sessionStorage.getItem('viewLangSession')
                  || (user.language || '').toUpperCase()
                  || 'ENGLISH';
  const activeMeta = LANG_META[activeLang] || LANG_META.ENGLISH;

  const langItems = Object.entries(LANG_META).map(([code, m]) => `
    <li>
      <a class="dropdown-item d-flex align-items-center gap-2 ${code === activeLang ? 'active' : ''}"
         href="javascript:void(0)" onclick="setViewLanguage('${code}')">
        <span style="font-size:1.15rem;line-height:1">${m.flag}</span>
        <span>${m.label}</span>
      </a>
    </li>`).join('');

  // ── Inject navbar ────────────────────────────────────────────
  const navbarPlaceholder = document.getElementById('rentmis-navbar');
  if (navbarPlaceholder) {
    navbarPlaceholder.innerHTML = `
    <nav class="layout-navbar navbar navbar-expand-xl align-items-center bg-navbar-theme" id="layout-navbar">
      <div class="container-xxl">
        <div class="navbar-brand app-brand demo d-none d-xl-flex py-0 me-4">
          <a href="/dashboard" class="app-brand-link">${brandSvg}</a>
        </div>
        <button class="navbar-toggler border-0 px-0 me-2" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
          <i class="icon-base ti tabler-menu-2 icon-md"></i>
        </button>
        <div class="navbar-nav-right d-flex align-items-center justify-content-end ms-auto" id="navbarNav">
          <ul class="navbar-nav flex-row align-items-center gap-1">

            <!-- ── Language switcher ── -->
            <li class="nav-item dropdown">
              <a class="nav-link dropdown-toggle hide-arrow d-flex align-items-center px-2"
                 href="javascript:void(0)" data-bs-toggle="dropdown" aria-expanded="false"
                 title="Switch language" style="gap:5px">
                <span style="font-size:1.35rem;line-height:1">${activeMeta.flag}</span>
                <span class="d-none d-xl-inline small fw-medium" style="color:inherit">${activeMeta.label}</span>
              </a>
              <ul class="dropdown-menu dropdown-menu-end" style="min-width:160px">
                <li><h6 class="dropdown-header small">Display Language</h6></li>
                ${langItems}
              </ul>
            </li>

            <!-- ── User avatar ── -->
            <li class="nav-item navbar-dropdown dropdown-user dropdown">
              <a class="nav-link dropdown-toggle hide-arrow" href="javascript:void(0)" data-bs-toggle="dropdown">
                <div class="avatar avatar-online">
                  <span class="avatar-initial rounded-circle bg-label-primary">${(user.full_name||'U').charAt(0).toUpperCase()}</span>
                </div>
              </a>
              <ul class="dropdown-menu dropdown-menu-end">
                <li><div class="dropdown-item"><div class="d-flex"><div class="flex-grow-1">
                  <span class="fw-semibold d-block small">${user.full_name || 'User'}</span>
                  <small class="text-muted">${user.role || ''}</small>
                </div></div></div></li>
                <li><div class="dropdown-divider"></div></li>
                <li><a class="dropdown-item" href="/html/profile/index.html"><i class="icon-base ti tabler-user me-2"></i>Profile</a></li>
                <li><a class="dropdown-item" href="javascript:void(0)" onclick="logout()"><i class="icon-base ti tabler-logout me-2"></i>Logout</a></li>
              </ul>
            </li>

          </ul>
        </div>
      </div>
    </nav>`;
  }

  // ── Inject sidebar ───────────────────────────────────────────
  const sidebarPlaceholder = document.getElementById('rentmis-sidebar');
  if (sidebarPlaceholder) {
    sidebarPlaceholder.innerHTML = `
    <aside id="layout-menu" class="layout-menu menu-vertical menu bg-menu-theme">
      <div class="app-brand demo px-4 py-3">
        <a href="/dashboard" class="app-brand-link">${sidebarBrandSvg}</a>
        <a href="javascript:void(0);" class="layout-menu-toggle menu-link text-large ms-auto">
          <i class="icon-base ti tabler-chevron-left icon-md align-middle"></i>
        </a>
      </div>
      <div class="menu-inner-shadow"></div>
      <ul class="menu-inner py-1">${menuHtml}</ul>
    </aside>`;

    // Re-wire toggle buttons (menu.js ran before this HTML existed)
    document.querySelectorAll('.layout-menu-toggle').forEach(function(el) {
      el.addEventListener('click', function(e) {
        e.preventDefault();
        if (window.Helpers && window.Helpers.toggleCollapsed) {
          window.Helpers.toggleCollapsed();
        } else {
          document.documentElement.classList.toggle('layout-menu-collapsed');
        }
      });
    });

    // Hide logo when sidebar is collapsed, show when expanded
    const collapseStyle = document.createElement('style');
    collapseStyle.textContent = `
      html.layout-menu-collapsed #layout-menu .app-brand-link { display: none !important; }
      html.layout-menu-collapsed #layout-menu .app-brand { justify-content: center; padding-left: 0 !important; padding-right: 0 !important; }
    `;
    document.head.appendChild(collapseStyle);
  }

  // ── Inject horizontal menu (for layouts without sidebar) ─────
  const hMenuPlaceholder = document.getElementById('rentmis-hmenu');
  if (hMenuPlaceholder) {
    hMenuPlaceholder.innerHTML = `<ul class="menu-inner">${menuHtml}</ul>`;
  }

  // ── Set page title ───────────────────────────────────────────
  const pageTitleEl = document.getElementById('page-title');
  if (pageTitleEl) document.title = pageTitleEl.textContent + ' — RentMIS';

})();

// Global logout
function logout() {
  const token = Auth.getToken();
  fetch('/api/auth/logout', { method: 'POST', headers: { Authorization: 'Bearer ' + token } })
    .finally(() => { Auth.clear(); window.location.href = '/login'; });
}
window.logout = logout;

// Global view-language switcher — session-only override, does not change profile preference
function setViewLanguage(code) {
  const user = Auth.getUser();
  const current = sessionStorage.getItem('viewLangSession') || (user.language || '').toUpperCase() || 'ENGLISH';
  if (current === code) return;
  sessionStorage.setItem('viewLangSession', code);
  window.location.reload();
}
window.setViewLanguage = setViewLanguage;
