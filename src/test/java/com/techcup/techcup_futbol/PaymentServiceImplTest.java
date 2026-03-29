package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.Controller.dto.*;
import com.techcup.techcup_futbol.core.exception.PaymentException;
import com.techcup.techcup_futbol.core.model.*;
import com.techcup.techcup_futbol.core.service.PaymentServiceImpl;
import com.techcup.techcup_futbol.repository.PaymentRepository;
import com.techcup.techcup_futbol.repository.TeamRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("PaymentServiceImpl Tests")
class PaymentServiceImplTest {

    @InjectMocks
    private PaymentServiceImpl service;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private TeamRepository teamRepository;

    private final Map<String, Payment> paymentStore = new HashMap<>();

    @BeforeEach
    void setUp() {
        DataStore.limpiarDatos();
        paymentStore.clear();

        // Team repository bridge to DataStore
        when(teamRepository.findById(anyString()))
                .thenAnswer(inv -> Optional.ofNullable(DataStore.equipos.get(inv.getArgument(0))));

        // Payment repository with local store
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> {
            Payment p = inv.getArgument(0);
            paymentStore.put(p.getId(), p);
            return p;
        });
        when(paymentRepository.findById(anyString()))
                .thenAnswer(inv -> Optional.ofNullable(paymentStore.get(inv.getArgument(0))));
        when(paymentRepository.findAll())
                .thenAnswer(inv -> new ArrayList<>(paymentStore.values()));
        when(paymentRepository.findByTeamId(anyString()))
                .thenAnswer(inv -> paymentStore.values().stream()
                        .filter(p -> inv.getArgument(0).equals(p.getTeamId()))
                        .findFirst());
    }

    // ── Happy Path

    @Nested
    @DisplayName("Happy Path")
    class HappyPath {

        @Test
        @DisplayName("HP-PAY-01: uploadReceipt() crea un pago en UNDER_REVIEW para equipo existente")
        void uploadReceiptCreapagoUnderReview() {
            Team team = buildTeam("Pagadores");
            DataStore.equipos.put(team.getId(), team);

            UploadReceiptRequest req = new UploadReceiptRequest(team.getId(), "http://comprobante.com/pago.pdf");
            PaymentResponse resp = service.uploadReceipt(req);

            assertNotNull(resp.id());
            assertEquals(PaymentStatus.UNDER_REVIEW, resp.currentStatus());
            assertEquals(team.getId(), resp.teamId());
        }

        @Test
        @DisplayName("HP-PAY-02: uploadReceipt() actualiza comprobante si ya existe pago (resubida)")
        void uploadReceiptActualizaComprobante() {
            Team team = buildTeam("Resubida");
            DataStore.equipos.put(team.getId(), team);

            UploadReceiptRequest req1 = new UploadReceiptRequest(team.getId(), "http://v1.pdf");
            PaymentResponse r1 = service.uploadReceipt(req1);

            service.updateStatus(r1.id(), "REJECTED");
            service.updateStatus(r1.id(), "PENDING");

            UploadReceiptRequest req2 = new UploadReceiptRequest(team.getId(), "http://v2.pdf");
            PaymentResponse r2 = service.uploadReceipt(req2);

            assertEquals(PaymentStatus.UNDER_REVIEW, r2.currentStatus());
            assertEquals("http://v2.pdf", r2.receiptUrl());
        }

        @Test
        @DisplayName("HP-PAY-03: updateStatus() PENDING → UNDER_REVIEW")
        void updateStatusPendingAUnderReview() {
            String paymentId = crearPagoConEstado("Equipo A");
            service.updateStatus(paymentId, "REJECTED");
            service.updateStatus(paymentId, "PENDING");

            PaymentResponse resp = service.updateStatus(paymentId, "UNDER_REVIEW");
            assertEquals(PaymentStatus.UNDER_REVIEW, resp.currentStatus());
        }

        @Test
        @DisplayName("HP-PAY-04: updateStatus() UNDER_REVIEW → APPROVED")
        void updateStatusUnderReviewAApproved() {
            String paymentId = crearPagoConEstado("Equipo B");
            PaymentResponse resp = service.updateStatus(paymentId, "APPROVED");
            assertEquals(PaymentStatus.APPROVED, resp.currentStatus());
        }

        @Test
        @DisplayName("HP-PAY-05: updateStatus() UNDER_REVIEW → REJECTED")
        void updateStatusUnderReviewARejected() {
            String paymentId = crearPagoConEstado("Equipo C");
            PaymentResponse resp = service.updateStatus(paymentId, "REJECTED");
            assertEquals(PaymentStatus.REJECTED, resp.currentStatus());
        }

        @Test
        @DisplayName("HP-PAY-06: updateStatus() REJECTED → PENDING")
        void updateStatusRejectedAPending() {
            String paymentId = crearPagoConEstado("Equipo D");
            service.updateStatus(paymentId, "REJECTED");
            PaymentResponse resp = service.updateStatus(paymentId, "PENDING");
            assertEquals(PaymentStatus.PENDING, resp.currentStatus());
        }

        @Test
        @DisplayName("HP-PAY-07: findById() retorna el pago si existe")
        void findByIdRetornaPago() {
            String paymentId = crearPagoConEstado("Equipo E");
            PaymentResponse resp = service.findById(paymentId);
            assertNotNull(resp);
            assertEquals(paymentId, resp.id());
        }

        @Test
        @DisplayName("HP-PAY-08: findAll() retorna todos los pagos")
        void findAllRetornaTodos() {
            crearPagoConEstado("Equipo F");
            crearPagoConEstado("Equipo G");
            List<PaymentResponse> lista = service.findAll();
            assertEquals(2, lista.size());
        }

        @Test
        @DisplayName("HP-PAY-09: findByTeamId() retorna pago del equipo correcto")
        void findByTeamIdRetornaPago() {
            Team team = buildTeam("Equipo H");
            DataStore.equipos.put(team.getId(), team);
            service.uploadReceipt(new UploadReceiptRequest(team.getId(), "http://h.pdf"));

            PaymentResponse resp = service.findByTeamId(team.getId());
            assertNotNull(resp);
            assertEquals(team.getId(), resp.teamId());
        }
    }

    // ── Error Path

    @Nested
    @DisplayName("Error Path")
    class ErrorPath {

        @Test
        @DisplayName("EP-PAY-01: uploadReceipt() lanza PaymentException si equipo no existe")
        void uploadReceiptEquipoNoExisteLanza() {
            UploadReceiptRequest req = new UploadReceiptRequest("NO-EXISTE", "http://r.pdf");
            PaymentException ex = assertThrows(PaymentException.class,
                    () -> service.uploadReceipt(req));
            assertEquals("teamId", ex.getField());
        }

        @Test
        @DisplayName("EP-PAY-02: uploadReceipt() lanza PaymentException si URL está vacía")
        void uploadReceiptUrlVaciaLanza() {
            Team team = buildTeam("Sin URL");
            DataStore.equipos.put(team.getId(), team);
            UploadReceiptRequest req = new UploadReceiptRequest(team.getId(), "");
            PaymentException ex = assertThrows(PaymentException.class,
                    () -> service.uploadReceipt(req));
            assertEquals("receiptUrl", ex.getField());
        }

        @Test
        @DisplayName("EP-PAY-03: uploadReceipt() lanza PaymentException si pago ya está APPROVED")
        void uploadReceiptPagoAprobadoLanza() {
            Team team = buildTeam("Equipo Aprobado");
            DataStore.equipos.put(team.getId(), team);
            PaymentResponse r1 = service.uploadReceipt(
                    new UploadReceiptRequest(team.getId(), "http://pago.pdf"));
            service.updateStatus(r1.id(), "APPROVED");

            UploadReceiptRequest req = new UploadReceiptRequest(team.getId(), "http://nuevo.pdf");
            PaymentException ex = assertThrows(PaymentException.class,
                    () -> service.uploadReceipt(req));
            assertEquals("status", ex.getField());
        }

        @Test
        @DisplayName("EP-PAY-04: updateStatus() lanza PaymentException si pago no existe")
        void updateStatusPagoNoExisteLanza() {
            PaymentException ex = assertThrows(PaymentException.class,
                    () -> service.updateStatus("NO-EXISTE", "APPROVED"));
            assertEquals("id", ex.getField());
        }

        @Test
        @DisplayName("EP-PAY-05: updateStatus() lanza PaymentException si estado inválido")
        void updateStatusEstadoInvalidoLanza() {
            String paymentId = crearPagoConEstado("Equipo Invalido");
            PaymentException ex = assertThrows(PaymentException.class,
                    () -> service.updateStatus(paymentId, "INVALIDO"));
            assertEquals("status", ex.getField());
        }

        @Test
        @DisplayName("EP-PAY-06: updateStatus() lanza PaymentException en transición inválida PENDING → APPROVED")
        void updateStatusTransicionInvalidaLanza() {
            String paymentId = crearPagoConEstado("Equipo Trans");
            service.updateStatus(paymentId, "REJECTED");
            service.updateStatus(paymentId, "PENDING");

            PaymentException ex = assertThrows(PaymentException.class,
                    () -> service.updateStatus(paymentId, "APPROVED"));
            assertEquals("status", ex.getField());
        }

        @Test
        @DisplayName("EP-PAY-07: findById() lanza PaymentException si no existe")
        void findByIdNoExisteLanza() {
            PaymentException ex = assertThrows(PaymentException.class,
                    () -> service.findById("NO-EXISTE"));
            assertEquals("id", ex.getField());
        }

        @Test
        @DisplayName("EP-PAY-08: findByTeamId() lanza PaymentException si equipo no tiene pago")
        void findByTeamIdSinPagoLanza() {
            assertThrows(PaymentException.class,
                    () -> service.findByTeamId("EQUIPO-SIN-PAGO"));
        }

        @Test
        @DisplayName("EP-PAY-09: updateStatus() APPROVED es terminal — no permite más transiciones")
        void updateStatusApprovedEsTerminal() {
            String paymentId = crearPagoConEstado("Equipo Terminal");
            service.updateStatus(paymentId, "APPROVED");

            for (String next : new String[]{"PENDING", "UNDER_REVIEW", "REJECTED", "APPROVED"}) {
                assertThrows(PaymentException.class,
                        () -> service.updateStatus(paymentId, next),
                        "APPROVED → " + next + " debería fallar");
            }
        }
    }

    // ── Conditional Scenarios

    @Nested
    @DisplayName("Conditional Scenarios")
    class ConditionalScenarios {

        @Test
        @DisplayName("CS-PAY-01: findAll() retorna lista vacía si no hay pagos")
        void findAllVacioSiNoHayPagos() {
            assertTrue(service.findAll().isEmpty());
        }

        @Test
        @DisplayName("CS-PAY-02: flujo completo PENDING→UNDER_REVIEW→APPROVED")
        void flujoCompletoAprobacion() {
            String paymentId = crearPagoConEstado("Flujo Completo");
            PaymentResponse resp = service.updateStatus(paymentId, "APPROVED");
            assertEquals(PaymentStatus.APPROVED, resp.currentStatus());
        }

        @Test
        @DisplayName("CS-PAY-03: flujo completo con rechazo y resubida")
        void flujoConRechazoYResubida() {
            Team team = buildTeam("Reintento");
            DataStore.equipos.put(team.getId(), team);
            PaymentResponse r1 = service.uploadReceipt(new UploadReceiptRequest(team.getId(), "v1.pdf"));
            service.updateStatus(r1.id(), "REJECTED");
            service.updateStatus(r1.id(), "PENDING");
            PaymentResponse r2 = service.uploadReceipt(new UploadReceiptRequest(team.getId(), "v2.pdf"));
            assertEquals(PaymentStatus.UNDER_REVIEW, r2.currentStatus());
        }

        @Test
        @DisplayName("CS-PAY-04: uploadReceipt() calcula monto basado en número de jugadores")
        void uploadReceiptCalculaMonto() {
            Team team = buildTeam("Con Jugadores");
            team.setPlayers(buildJugadores(4));
            DataStore.equipos.put(team.getId(), team);

            PaymentResponse resp = service.uploadReceipt(
                    new UploadReceiptRequest(team.getId(), "http://r.pdf"));

            assertEquals(200.0, resp.amount()); // 4 jugadores * 50.0
        }

        @Test
        @DisplayName("CS-PAY-05: múltiples equipos tienen pagos independientes")
        void multiplesEquiposPagosIndependientes() {
            Team t1 = buildTeam("T1"); DataStore.equipos.put(t1.getId(), t1);
            Team t2 = buildTeam("T2"); DataStore.equipos.put(t2.getId(), t2);

            service.uploadReceipt(new UploadReceiptRequest(t1.getId(), "r1.pdf"));
            service.uploadReceipt(new UploadReceiptRequest(t2.getId(), "r2.pdf"));

            assertEquals(2, service.findAll().size());
        }
    }

    // ── Helpers

    private Team buildTeam(String name) {
        Team team = new Team();
        team.setId(UUID.randomUUID().toString());
        team.setTeamName(name);
        team.setShieldUrl("shield.png");
        team.setUniformColors("Rojo");
        team.setPlayers(new ArrayList<>());
        return team;
    }

    private List<Player> buildJugadores(int count) {
        List<Player> lista = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            StudentPlayer p = new StudentPlayer();
            p.setId(UUID.randomUUID().toString());
            lista.add(p);
        }
        return lista;
    }

    private String crearPagoConEstado(String teamName) {
        Team team = buildTeam(teamName);
        DataStore.equipos.put(team.getId(), team);
        PaymentResponse resp = service.uploadReceipt(
                new UploadReceiptRequest(team.getId(), "http://comprobante.pdf"));
        return resp.id();
    }
}
