package sv.edu.udb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sv.edu.udb.exception.ResourceNotFoundException;
import sv.edu.udb.model.Cliente;
import sv.edu.udb.model.Empleado;
import sv.edu.udb.model.DetalleVenta;
import sv.edu.udb.model.Producto;
import sv.edu.udb.model.Sucursal;
import sv.edu.udb.model.Venta;
import sv.edu.udb.repository.ClienteRepository;
import sv.edu.udb.repository.EmpleadoRepository;
import sv.edu.udb.repository.ProductoRepository;
import sv.edu.udb.repository.SucursalRepository;
import sv.edu.udb.repository.VentaRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class VentaService {

    @Autowired private VentaRepository ventaRepository;
    @Autowired private EmpleadoRepository empleadoRepository;
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private ProductoRepository productoRepository;
    @Autowired private SucursalRepository sucursalRepository;

    public List<Venta> listarTodos() {
        return ventaRepository.findAll();
    }

    public Venta buscarPorId(Long id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada: " + id));
    }

    public Venta buscarPermitida(Long id) {
        Venta venta = buscarPorId(id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean esAdmin = auth.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        boolean esCajero = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_CAJERO".equals(a.getAuthority()) || "ROLE_ADMIN".equals(a.getAuthority()));
        boolean esCliente = auth.getAuthorities().stream().anyMatch(a -> "ROLE_CLIENTE".equals(a.getAuthority()));

        if (esAdmin) return venta;
        if (esCajero && venta.getEmpleado() != null && venta.getEmpleado().getUsername().equals(auth.getName())) return venta;
        if (esCliente && venta.getCliente() != null && venta.getCliente().getEmail().equals(auth.getName())) return venta;
        throw new RuntimeException("No tienes permiso para ver esta venta");
    }

    public List<Venta> porSucursal(Long sucursalId) {
        return ventaRepository.findBySucursalId(sucursalId);
    }

    public List<Venta> porCliente(Long clienteId) {
        return ventaRepository.findByClienteId(clienteId);
    }

    public List<Venta> porEmpleado(Long empleadoId) {
        return ventaRepository.findByEmpleadoId(empleadoId);
    }

    public List<Venta> ventasDelUsuarioActual() {
        Empleado empleado = empleadoActual();
        return ventaRepository.findByEmpleadoId(empleado.getId());
    }

    public List<Venta> comprasDelClienteActual() {
        Cliente cliente = clienteActual();
        return ventaRepository.findByClienteId(cliente.getId());
    }

    public List<Venta> hoy() {
        LocalDate hoy = LocalDate.now();
        return ventaRepository.findByFechaBetween(hoy.atStartOfDay(), hoy.plusDays(1).atStartOfDay());
    }

    public Venta registrar(Venta venta) {
        asignarUsuarioActual(venta);
        cargarRelaciones(venta);

        if (venta.getFecha() == null) {
            venta.setFecha(LocalDateTime.now());
        }
        if (venta.getEstado() == null || venta.getEstado().isBlank()) {
            venta.setEstado("COMPLETADA");
        }
        if (venta.getDetalles() == null) {
            venta.setDetalles(new ArrayList<>());
        }
        if (venta.getPagos() == null) {
            venta.setPagos(new ArrayList<>());
        }

        venta.getDetalles().forEach(detalle -> prepararDetalle(detalle, venta));
        venta.getPagos().forEach(pago -> {
            pago.setVenta(venta);
            if (pago.getFechaPago() == null) pago.setFechaPago(LocalDateTime.now());
        });
        if (venta.getTotal() == null && !venta.getDetalles().isEmpty()) {
            BigDecimal total = venta.getDetalles().stream()
                    .map(d -> d.getSubtotal() == null ? BigDecimal.ZERO : d.getSubtotal())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            venta.setTotal(total);
        }
        return ventaRepository.save(venta);
    }

    private void cargarRelaciones(Venta venta) {
        if (venta.getCliente() != null && venta.getCliente().getId() != null) {
            Cliente cliente = clienteRepository.findById(venta.getCliente().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: " + venta.getCliente().getId()));
            venta.setCliente(cliente);
        }

        if (venta.getEmpleado() != null && venta.getEmpleado().getId() != null) {
            Empleado empleado = empleadoRepository.findById(venta.getEmpleado().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Empleado no encontrado: " + venta.getEmpleado().getId()));
            venta.setEmpleado(empleado);
        }

        if (venta.getSucursal() != null && venta.getSucursal().getId() != null) {
            Sucursal sucursal = sucursalRepository.findById(venta.getSucursal().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada: " + venta.getSucursal().getId()));
            venta.setSucursal(sucursal);
        }
    }

    private void asignarUsuarioActual(Venta venta) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) return;

        boolean esCajero = auth.getAuthorities().stream().anyMatch(a -> "ROLE_CAJERO".equals(a.getAuthority()));
        boolean esCliente = auth.getAuthorities().stream().anyMatch(a -> "ROLE_CLIENTE".equals(a.getAuthority()));

        if (esCajero && venta.getEmpleado() == null) {
            Empleado empleado = empleadoActual();
            venta.setEmpleado(empleado);
            if (venta.getSucursal() == null && empleado.getSucursal() != null) {
                venta.setSucursal(empleado.getSucursal());
            }
        }

        if (esCliente && venta.getCliente() == null) {
            venta.setCliente(clienteActual());
        }
    }

    private void prepararDetalle(DetalleVenta detalle, Venta venta) {
        if (detalle.getProducto() == null || detalle.getProducto().getId() == null) {
            throw new RuntimeException("Cada detalle debe incluir un producto");
        }
        if (detalle.getCantidad() == null || detalle.getCantidad() <= 0) {
            throw new RuntimeException("La cantidad debe ser mayor a cero");
        }

        Producto producto = productoRepository.findById(detalle.getProducto().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + detalle.getProducto().getId()));

        int stockActual = producto.getStock() == null ? 0 : producto.getStock();
        if (stockActual < detalle.getCantidad()) {
            throw new RuntimeException("Stock insuficiente para " + producto.getNombre());
        }

        detalle.setVenta(venta);
        detalle.setProducto(producto);
        if (detalle.getPrecioUnitario() == null) {
            detalle.setPrecioUnitario(producto.getPrecio());
        }
        if (detalle.getSubtotal() == null) {
            detalle.setSubtotal(detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(detalle.getCantidad())));
        }
        producto.setStock(stockActual - detalle.getCantidad());
    }

    public Venta marcarDevolucion(Long id) {
        Venta venta = buscarPorId(id);
        venta.setEstado("DEVUELTA");
        return ventaRepository.save(venta);
    }

    public Map<String, Object> reporteGlobal() {
        return construirReporte(ventaRepository.findAll());
    }

    public Map<String, Object> reporteSucursal(Long sucursalId) {
        return construirReporte(ventaRepository.findBySucursalId(sucursalId));
    }

    public Map<String, Object> reporteEmpleadoActual() {
        return construirReporte(ventasDelUsuarioActual());
    }

    public Map<String, Object> reporteClienteActual() {
        return construirReporte(comprasDelClienteActual());
    }

    private Map<String, Object> construirReporte(List<Venta> ventas) {
        BigDecimal total = ventas.stream()
                .filter(v -> !"CANCELADA".equalsIgnoreCase(v.getEstado()))
                .map(Venta::getTotal)
                .filter(t -> t != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> reporte = new LinkedHashMap<>();
        reporte.put("totalVentas", ventas.size());
        reporte.put("montoTotal", total);
        reporte.put("ventas", ventas);
        return reporte;
    }

    private Empleado empleadoActual() {
        String username = usuarioActual();
        return empleadoRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado autenticado no encontrado"));
    }

    private Cliente clienteActual() {
        String email = usuarioActual();
        return clienteRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente autenticado no encontrado"));
    }

    private String usuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new RuntimeException("No hay usuario autenticado");
        }
        return auth.getName();
    }
}
