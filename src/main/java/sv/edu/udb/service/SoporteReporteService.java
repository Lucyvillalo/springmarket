package sv.edu.udb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sv.edu.udb.dto.SoporteEstadoRequest;
import sv.edu.udb.dto.SoporteReporteRequest;
import sv.edu.udb.dto.SoporteReporteResponse;
import sv.edu.udb.exception.ResourceNotFoundException;
import sv.edu.udb.model.Cliente;
import sv.edu.udb.model.Empleado;
import sv.edu.udb.model.SoporteReporte;
import sv.edu.udb.model.Sucursal;
import sv.edu.udb.repository.ClienteRepository;
import sv.edu.udb.repository.EmpleadoRepository;
import sv.edu.udb.repository.SoporteReporteRepository;
import sv.edu.udb.repository.SucursalRepository;

import java.util.List;
import java.util.Set;

@Service
@Transactional
public class SoporteReporteService {

    private static final Set<String> TIPOS = Set.of("Inventario", "Ventas", "Productos", "Login", "Facturación", "Sistema", "Otro");
    private static final Set<String> PRIORIDADES = Set.of("BAJA", "MEDIA", "ALTA");
    private static final Set<String> ESTADOS = Set.of("PENDIENTE", "EN_PROCESO", "RESUELTO");

    @Autowired private SoporteReporteRepository soporteRepository;
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private EmpleadoRepository empleadoRepository;
    @Autowired private SucursalRepository sucursalRepository;

    public SoporteReporteResponse crear(SoporteReporteRequest request) {
        String tipo = normalizarTipo(request.getTipoProblema());
        String prioridad = normalizarPrioridad(request.getPrioridad());

        SoporteReporte reporte = new SoporteReporte();
        reporte.setTitulo(request.getTitulo().trim());
        reporte.setTipoProblema(tipo);
        reporte.setPrioridad(prioridad);
        reporte.setDescripcion(request.getDescripcion().trim());
        reporte.setEstado("PENDIENTE");

        if (request.getSucursalId() != null) {
            Sucursal sucursal = sucursalRepository.findById(request.getSucursalId())
                    .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada: " + request.getSucursalId()));
            reporte.setSucursal(sucursal);
        }

        asignarUsuarioActual(reporte);
        return toResponse(soporteRepository.save(reporte));
    }

    @Transactional(readOnly = true)
    public List<SoporteReporteResponse> listarTodos() {
        return soporteRepository.findAllByOrderByFechaCreacionDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SoporteReporteResponse buscarPorId(Long id) {
        return toResponse(buscarEntidad(id));
    }

    public SoporteReporteResponse actualizarEstado(Long id, SoporteEstadoRequest request) {
        String estado = normalizarEstado(request.getEstado());
        SoporteReporte reporte = buscarEntidad(id);
        reporte.setEstado(estado);
        return toResponse(soporteRepository.save(reporte));
    }

    public void eliminar(Long id) {
        SoporteReporte reporte = buscarEntidad(id);
        soporteRepository.delete(reporte);
    }

    private SoporteReporte buscarEntidad(Long id) {
        return soporteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reporte de soporte no encontrado: " + id));
    }

    private void asignarUsuarioActual(SoporteReporte reporte) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return;
        }

        boolean esCliente = auth.getAuthorities().stream().anyMatch(a -> "ROLE_CLIENTE".equals(a.getAuthority()));
        if (esCliente) {
            clienteRepository.findByEmail(auth.getName()).ifPresent(reporte::setCliente);
            return;
        }

        empleadoRepository.findByUsername(auth.getName()).ifPresent(empleado -> {
            reporte.setEmpleado(empleado);
            if (reporte.getSucursal() == null && empleado.getSucursal() != null) {
                reporte.setSucursal(empleado.getSucursal());
            }
        });
    }

    private String normalizarTipo(String tipo) {
        String limpio = limpiar(tipo);
        if ("Facturación".equalsIgnoreCase(limpio) || "Facturacion".equalsIgnoreCase(limpio)) {
            return "Facturación";
        }
        return TIPOS.stream()
                .filter(t -> t.equalsIgnoreCase(limpio))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Tipo de problema invalido"));
    }

    private String normalizarPrioridad(String prioridad) {
        String limpio = limpiar(prioridad == null || prioridad.isBlank() ? "MEDIA" : prioridad).toUpperCase();
        if (!PRIORIDADES.contains(limpio)) throw new RuntimeException("Prioridad invalida");
        return limpio;
    }

    private String normalizarEstado(String estado) {
        String limpio = limpiar(estado).toUpperCase();
        if (!ESTADOS.contains(limpio)) throw new RuntimeException("Estado invalido");
        return limpio;
    }

    private String limpiar(String value) {
        if (value == null || value.isBlank()) throw new RuntimeException("Valor obligatorio");
        return value.trim();
    }

    private SoporteReporteResponse toResponse(SoporteReporte reporte) {
        Cliente cliente = reporte.getCliente();
        Empleado empleado = reporte.getEmpleado();
        Sucursal sucursal = reporte.getSucursal();
        String usuario = empleado != null ? empleado.getNombre() : cliente != null ? cliente.getNombre() : "Sin usuario";

        return SoporteReporteResponse.builder()
                .id(reporte.getId())
                .titulo(reporte.getTitulo())
                .tipoProblema(reporte.getTipoProblema())
                .descripcion(reporte.getDescripcion())
                .prioridad(reporte.getPrioridad())
                .estado(reporte.getEstado())
                .fechaCreacion(reporte.getFechaCreacion())
                .clienteId(cliente != null ? cliente.getId() : null)
                .clienteNombre(cliente != null ? cliente.getNombre() : null)
                .empleadoId(empleado != null ? empleado.getId() : null)
                .empleadoNombre(empleado != null ? empleado.getNombre() : null)
                .sucursalId(sucursal != null ? sucursal.getId() : null)
                .sucursalNombre(sucursal != null ? sucursal.getNombre() : null)
                .usuario(usuario)
                .build();
    }
}
