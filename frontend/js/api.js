// =============================================
// SPRING MARKET — API Helper
// =============================================

const API_BASE = 'http://localhost:8085/api';

// Determinar la ruta base del frontend dinámicamente
function getLoginUrl() {
  const path = window.location.pathname;
  if (path.includes('/pages/')) {
    return 'login.html';          // ya estamos en /pages/
  }
  return 'pages/login.html';      // estamos en la raíz
}

// Obtener token guardado
function getToken() {
  return localStorage.getItem('token');
}

// Headers con JWT
function authHeaders() {
  return {
    'Content-Type': 'application/json',
    'Authorization': 'Bearer ' + getToken()
  };
}

// Función base para llamadas HTTP
async function api(method, endpoint, body = null) {
  const options = {
    method,
    headers: authHeaders()
  };
  if (body) options.body = JSON.stringify(body);

  const res = await fetch(API_BASE + endpoint, options);

  if (res.status === 401) {
    localStorage.clear();
    window.location.href = getLoginUrl();
    return;
  }

  if (!res.ok) {
    const err = await res.json().catch(() => ({ message: 'Error desconocido' }));
    throw new Error(err.message || err.error || 'Error en la solicitud');
  }

  if (res.status === 204) return null;
  return res.json();
}

// Métodos HTTP
const get    = (url)       => api('GET',    url);
const post   = (url, body) => api('POST',   url, body);
const put    = (url, body) => api('PUT',    url, body);
const del    = (url)       => api('DELETE', url);

// =============================================
// AUTH
// =============================================
const Auth = {
  loginEmpleado: (username, password) =>
    fetch(API_BASE + '/auth/login-empleado', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password })
    }).then(r => r.json()),

  loginCliente: (email, password) =>
    fetch(API_BASE + '/auth/login-cliente', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password })
    }).then(r => r.json()),

  logout: () => {
    localStorage.clear();
    window.location.href = getLoginUrl();
  },

  isLoggedIn: () => !!localStorage.getItem('token'),

  getCargo: () => localStorage.getItem('cargo'),
  getUsername: () => localStorage.getItem('username'),

  requireAuth: (rolesPermitidos = []) => {
    if (!Auth.isLoggedIn()) {
      window.location.href = getLoginUrl();
      return false;
    }
    if (rolesPermitidos.length > 0) {
      const cargo = Auth.getCargo();
      if (!rolesPermitidos.includes(cargo)) {
        alert('No tienes permiso para acceder a esta página');
        history.back();
        return false;
      }
    }
    return true;
  }
};

// =============================================
// LAYOUT RESPONSIVE
// =============================================
function setupResponsiveSidebar() {
  const sidebar = document.querySelector('.admin-sidebar, .store-sidebar, .sidebar');
  const shell = document.querySelector('.admin-shell, .store-shell, .layout-with-sidebar');
  if (!sidebar || !shell || document.getElementById('sidebar-toggle')) return;

  const style = document.createElement('style');
  style.id = 'responsive-sidebar-style';
  style.textContent = `
    .sidebar-toggle {
      display: none;
      width: 40px;
      height: 40px;
      border: 1px solid #dbe4ef;
      border-radius: 8px;
      background: #ffffff;
      color: #153826;
      cursor: pointer;
      align-items: center;
      justify-content: center;
      gap: 4px;
      flex-direction: column;
      box-shadow: 0 2px 10px rgba(15,23,42,.06);
      flex: 0 0 auto;
    }
    .sidebar-toggle span {
      width: 18px;
      height: 2px;
      border-radius: 999px;
      background: currentColor;
      display: block;
    }
    .admin-sidebar .logout,
    .store-sidebar .logout,
    .sidebar .logout {
      display: none !important;
    }
    .admin-sidebar .side-nav a[href="dashboard.html"] { order: 1; }
    .admin-sidebar .side-nav a[href="productos.html"] { order: 2; }
    .admin-sidebar .side-nav a[href="ventas.html"] { order: 3; }
    .admin-sidebar .side-nav a[href="sucursales.html"] { order: 4; }
    .admin-sidebar .side-nav a[href="proveedores.html"] { order: 5; }
    .admin-sidebar .side-nav a[href="reportes.html"] { order: 6; }
    .manager,
    .customer {
      position: relative;
    }
    .user-menu-toggle {
      width: 32px;
      height: 32px;
      border: 1px solid #dbe4ef;
      border-radius: 8px;
      background: #ffffff;
      color: #334155;
      cursor: pointer;
      font-size: 15px;
      font-weight: 800;
      line-height: 1;
    }
    .user-menu {
      display: none;
      position: absolute;
      right: 0;
      top: calc(100% + 8px);
      min-width: 170px;
      background: #ffffff;
      border: 1px solid #e2e8f0;
      border-radius: 8px;
      box-shadow: 0 14px 34px rgba(15,23,42,.14);
      padding: 6px;
      z-index: 1100;
    }
    .user-menu.open {
      display: block;
    }
    .user-menu button {
      width: 100%;
      border: 0;
      background: transparent;
      color: #dc2626;
      border-radius: 7px;
      padding: 9px 10px;
      cursor: pointer;
      font-family: var(--font-main);
      font-size: 13px;
      font-weight: 800;
      text-align: left;
    }
    .user-menu button:hover {
      background: #fee2e2;
    }
    .page-header h1,
    .dash-header h1,
    .report-header h1,
    .store-header h1 {
      font-family: var(--font-main) !important;
      letter-spacing: 0 !important;
    }
    .sidebar-overlay {
      display: none;
      position: fixed;
      inset: 0;
      background: rgba(15, 23, 42, .38);
      z-index: 900;
    }
    @media (max-width: 900px) {
      .admin-shell,
      .store-shell,
      .layout-with-sidebar {
        display: block !important;
      }
      .admin-sidebar,
      .store-sidebar,
      .sidebar {
        position: fixed !important;
        top: 0 !important;
        left: 0 !important;
        width: min(82vw, 280px) !important;
        min-width: 0 !important;
        height: 100vh !important;
        z-index: 1000 !important;
        transform: translateX(-104%);
        transition: transform .2s ease;
        overflow-y: auto;
        box-shadow: 14px 0 32px rgba(15,23,42,.16);
      }
      body.sidebar-open .admin-sidebar,
      body.sidebar-open .store-sidebar,
      body.sidebar-open .sidebar {
        transform: translateX(0);
      }
      body.sidebar-open .sidebar-overlay {
        display: block;
      }
      .sidebar-toggle {
        display: inline-flex;
      }
      .topbar {
        align-items: center;
      }
      .topbar .top-actions {
        margin-left: auto;
      }
      .dash-main,
      .prod-main,
      .sales-main,
      .report-main,
      .store-main,
      .main-content {
        padding: 18px 14px 28px !important;
      }
      .side-nav {
        flex-direction: column !important;
      }
      .page-header,
      .store-header {
        align-items: flex-start !important;
        flex-direction: column;
      }
    }
  `;
  document.head.appendChild(style);

  const toggle = document.createElement('button');
  toggle.type = 'button';
  toggle.id = 'sidebar-toggle';
  toggle.className = 'sidebar-toggle';
  toggle.setAttribute('aria-label', 'Abrir menu');
  toggle.setAttribute('aria-expanded', 'false');
  toggle.innerHTML = '<span></span><span></span><span></span>';

  const overlay = document.createElement('div');
  overlay.className = 'sidebar-overlay';
  overlay.id = 'sidebar-overlay';

  const topbar = document.querySelector('.topbar');
  if (topbar) {
    topbar.prepend(toggle);
  } else {
    const firstMain = shell.querySelector('main') || shell;
    firstMain.prepend(toggle);
  }
  document.body.appendChild(overlay);

  const closeMenu = () => {
    document.body.classList.remove('sidebar-open');
    toggle.setAttribute('aria-expanded', 'false');
    toggle.setAttribute('aria-label', 'Abrir menu');
  };
  const openMenu = () => {
    document.body.classList.add('sidebar-open');
    toggle.setAttribute('aria-expanded', 'true');
    toggle.setAttribute('aria-label', 'Cerrar menu');
  };

  toggle.addEventListener('click', () => {
    document.body.classList.contains('sidebar-open') ? closeMenu() : openMenu();
  });
  overlay.addEventListener('click', closeMenu);
  sidebar.querySelectorAll('a, button').forEach(item => {
    item.addEventListener('click', () => {
      if (window.matchMedia('(max-width: 900px)').matches) closeMenu();
    });
  });
  window.addEventListener('resize', () => {
    if (!window.matchMedia('(max-width: 900px)').matches) closeMenu();
  });
}

function setupUserDropdown() {
  const userBox = document.querySelector('.manager, .customer');
  if (!userBox || document.getElementById('user-menu-toggle')) return;

  const toggle = document.createElement('button');
  toggle.type = 'button';
  toggle.id = 'user-menu-toggle';
  toggle.className = 'user-menu-toggle';
  toggle.setAttribute('aria-label', 'Opciones de usuario');
  toggle.setAttribute('aria-expanded', 'false');
  toggle.textContent = 'v';

  const menu = document.createElement('div');
  menu.id = 'user-menu';
  menu.className = 'user-menu';
  menu.innerHTML = '<button type="button" id="user-menu-logout">Cerrar sesion</button>';

  userBox.appendChild(toggle);
  userBox.appendChild(menu);

  toggle.addEventListener('click', (event) => {
    event.stopPropagation();
    menu.classList.toggle('open');
    toggle.setAttribute('aria-expanded', menu.classList.contains('open') ? 'true' : 'false');
  });
  document.getElementById('user-menu-logout').addEventListener('click', Auth.logout);
  document.addEventListener('click', () => {
    menu.classList.remove('open');
    toggle.setAttribute('aria-expanded', 'false');
  });
}

document.addEventListener('DOMContentLoaded', () => {
  setupResponsiveSidebar();
  setupUserDropdown();
});

// =============================================
// PRODUCTOS
// =============================================
const Productos = {
  listar:       ()          => get('/productos'),
  disponibles:  ()          => get('/productos/disponibles'),
  masVendidos:  ()          => get('/productos/mas-vendidos'),
  buscar:       (id)        => get('/productos/' + id),
  buscarNombre: (nom)       => get('/productos/buscar?nombre=' + encodeURIComponent(nom)),
  porCategoria: (id)        => get('/productos/categoria/' + id),
  crear:        (data)      => post('/productos', data),
  actualizar:   (id, data)  => put('/productos/' + id, data),
  eliminar:     (id)        => del('/productos/' + id)
};

// =============================================
// CATEGORIAS
// =============================================
const Categorias = {
  listar:     ()          => get('/categorias'),
  crear:      (data)      => post('/categorias', data),
  actualizar: (id, data)  => put('/categorias/' + id, data),
  eliminar:   (id)        => del('/categorias/' + id)
};

// =============================================
// SUCURSALES
// =============================================
const Sucursales = {
  listar:     ()          => get('/sucursales'),
  buscar:     (id)        => get('/sucursales/' + id),
  crear:      (data)      => post('/sucursales', data),
  actualizar: (id, data)  => put('/sucursales/' + id, data),
  eliminar:   (id)        => del('/sucursales/' + id)
};

// =============================================
// VENTAS
// =============================================
const Ventas = {
  listar:          ()      => get('/ventas'),
  buscar:          (id)    => get('/ventas/' + id),
  porSucursal:     (id)    => get('/ventas/sucursal/' + id),
  porCliente:      (id)    => get('/ventas/cliente/' + id),
  porEmpleado:     (id)    => get('/ventas/empleado/' + id),
  misVentas:       ()      => get('/ventas/mis-ventas'),
  misCompras:      ()      => get('/ventas/mis-compras'),
  hoy:             ()      => get('/ventas/hoy'),
  registrar:       (data)  => post('/ventas', data),
  devolucion:      (id)    => put('/ventas/' + id + '/devolucion'),
  reporteSucursal: (id)    => get('/ventas/reporte/sucursal/' + id),
  reporteGlobal:   ()      => get('/ventas/reporte/global'),
  reporteMisVentas: ()     => get('/ventas/reporte/mis-ventas'),
  reporteMisCompras: ()    => get('/ventas/reporte/mis-compras')
};

// =============================================
// CLIENTES
// =============================================
const Clientes = {
  listar:     ()          => get('/clientes'),
  buscar:     (id)        => get('/clientes/' + id),
  registrar:  (data)      => post('/clientes/registro', data),
  actualizar: (id, data)  => put('/clientes/' + id, data),
  eliminar:   (id)        => del('/clientes/' + id)
};

// =============================================
// INVENTARIO
// =============================================
const Inventario = {
  listar:      ()              => get('/inventario'),
  porSucursal: (id)            => get('/inventario/sucursal/' + id),
  stockBajo:   ()              => get('/inventario/stock-bajo'),
  crear:       (data)          => post('/inventario', data),
  actualizar:  (id, data)      => put('/inventario/' + id, data),
  ajustar:     (pId, sId, c)   => put(`/inventario/ajustar?productoId=${pId}&sucursalId=${sId}&cantidad=${c}`)
};

// =============================================
// PROVEEDORES
// =============================================
const Proveedores = {
  listar:     ()          => get('/proveedores'),
  buscar:     (id)        => get('/proveedores/' + id),
  crear:      (data)      => post('/proveedores', data),
  actualizar: (id, data)  => put('/proveedores/' + id, data),
  eliminar:   (id)        => del('/proveedores/' + id)
};

// =============================================
// UTILIDADES UI
// =============================================
function showAlert(tipo, mensaje, containerId = 'alert-box') {
  const el = document.getElementById(containerId);
  if (!el) return;
  el.className = `alert alert-${tipo} show`;
  el.textContent = mensaje;
  setTimeout(() => el.classList.remove('show'), 4000);
}

function showLoading(tbodyId, cols) {
  const tbody = document.getElementById(tbodyId);
  if (tbody) tbody.innerHTML = `<tr><td colspan="${cols}" style="text-align:center;padding:24px;color:#9ca3af">Cargando...</td></tr>`;
}

function formatPrice(n) {
  return '$' + parseFloat(n).toFixed(2);
}

function formatDate(str) {
  return new Date(str).toLocaleString('es-SV');
}

function formatEstado(estado) {
  const map = {
    COMPLETADA: 'badge-success',
    PENDIENTE:  'badge-warning',
    CANCELADA:  'badge-danger',
    DEVUELTA:   'badge-gray'
  };
  return `<span class="badge ${map[estado] || 'badge-gray'}">${estado}</span>`;
}
