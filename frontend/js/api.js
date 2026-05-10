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
