package sv.edu.udb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import sv.edu.udb.model.Cliente;
import sv.edu.udb.model.Empleado;
import sv.edu.udb.repository.ClienteRepository;
import sv.edu.udb.repository.EmpleadoRepository;
import sv.edu.udb.service.JwtUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired private JwtUtil jwtUtil;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private EmpleadoRepository empleadoRepo;
    @Autowired private ClienteRepository clienteRepo;

    // POST /api/auth/login-empleado
    @PostMapping("/login-empleado")
    public ResponseEntity<Map<String, Object>> loginEmpleado(@RequestBody LoginRequest req) {
        Map<String, Object> error = new HashMap<>();

        if (req.getUsername() == null || req.getUsername().isBlank()) {
            error.put("error", "El usuario es obligatorio");
            error.put("message", "El usuario es obligatorio");
            return ResponseEntity.status(400).body(error);
        }

        Optional<Empleado> optEmp = empleadoRepo.findByUsername(req.getUsername().trim());
        if (optEmp.isEmpty()) {
            error.put("error", "Usuario no encontrado");
            error.put("message", "Usuario no encontrado");
            return ResponseEntity.status(401).body(error);
        }

        Empleado emp = optEmp.get();

        if (!passwordEncoder.matches(req.getPassword(), emp.getPassword())) {
            error.put("error", "Contraseña incorrecta");
            error.put("message", "Contraseña incorrecta");
            return ResponseEntity.status(401).body(error);
        }

        String token = jwtUtil.generarToken(emp.getUsername(), emp.getCargo());
        Map<String, Object> resp = new HashMap<>();
        resp.put("token",      token);
        resp.put("nombre",     emp.getNombre());
        resp.put("cargo",      emp.getCargo());
        resp.put("sucursalId", emp.getSucursal() != null ? emp.getSucursal().getId() : null);
        return ResponseEntity.ok(resp);
    }

    // POST /api/auth/login-cliente
    @PostMapping("/login-cliente")
    public ResponseEntity<Map<String, Object>> loginCliente(@RequestBody LoginClienteRequest req) {
        Map<String, Object> error = new HashMap<>();

        if (req.getEmail() == null || req.getEmail().isBlank()) {
            error.put("error", "El correo es obligatorio");
            error.put("message", "El correo es obligatorio");
            return ResponseEntity.status(400).body(error);
        }

        Optional<Cliente> optCli = clienteRepo.findByEmail(req.getEmail().trim());
        if (optCli.isEmpty()) {
            error.put("error", "Correo no registrado");
            error.put("message", "Correo no registrado");
            return ResponseEntity.status(401).body(error);
        }

        Cliente cli = optCli.get();

        if (!passwordEncoder.matches(req.getPassword(), cli.getPassword())) {
            error.put("error", "Contraseña incorrecta");
            error.put("message", "Contraseña incorrecta");
            return ResponseEntity.status(401).body(error);
        }

        String token = jwtUtil.generarToken(cli.getEmail(), "CLIENTE");
        Map<String, Object> resp = new HashMap<>();
        resp.put("token",     token);
        resp.put("nombre",    cli.getNombre());
        resp.put("clienteId", cli.getId());
        return ResponseEntity.ok(resp);
    }

    public static class LoginRequest {
        private String username, password;
        public String getUsername() { return username; }
        public void setUsername(String u) { this.username = u; }
        public String getPassword() { return password; }
        public void setPassword(String p) { this.password = p; }
    }

    public static class LoginClienteRequest {
        private String email, password;
        public String getEmail() { return email; }
        public void setEmail(String e) { this.email = e; }
        public String getPassword() { return password; }
        public void setPassword(String p) { this.password = p; }
    }
}
