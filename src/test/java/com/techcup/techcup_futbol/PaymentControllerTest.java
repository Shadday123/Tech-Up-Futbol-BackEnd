package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.Controller.PaymentController;
import com.techcup.techcup_futbol.Controller.dto.*;
import com.techcup.techcup_futbol.core.exception.PaymentException;
import com.techcup.techcup_futbol.core.model.PaymentStatus;
import com.techcup.techcup_futbol.core.service.PaymentService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentController Tests")
class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    private PaymentController controller;

    @BeforeEach
    void setUp() {
        controller = new PaymentController(paymentService);
    }

    // ── Happy Path

    @Nested
    @DisplayName("Happy Path")
    class HappyPath {

        @Test
        @DisplayName("HP-PAYC-01: uploadReceipt() retorna 201 CREATED")
        void uploadReceiptRetorna201() {
            UploadReceiptRequest req = new UploadReceiptRequest("E001", "http://r.pdf");
            PaymentResponse resp = buildResponse("PAY-001", "E001", PaymentStatus.UNDER_REVIEW);
            when(paymentService.uploadReceipt(req)).thenReturn(resp);

            ResponseEntity<PaymentResponse> response = controller.uploadReceipt(req);

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody());
        }

        @Test
        @DisplayName("HP-PAYC-02: updateStatus() retorna 200 OK con estado actualizado")
        void updateStatusRetorna200() {
            UpdatePaymentStatusRequest req = new UpdatePaymentStatusRequest("APPROVED");
            PaymentResponse resp = buildResponse("PAY-001", "E001", PaymentStatus.APPROVED);
            when(paymentService.updateStatus("PAY-001", "APPROVED")).thenReturn(resp);

            ResponseEntity<PaymentResponse> response = controller.updateStatus("PAY-001", req);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(PaymentStatus.APPROVED, response.getBody().currentStatus());
        }

        @Test
        @DisplayName("HP-PAYC-03: findById() retorna 200 OK con el pago encontrado")
        void findByIdRetorna200() {
            PaymentResponse resp = buildResponse("PAY-002", "E002", PaymentStatus.PENDING);
            when(paymentService.findById("PAY-002")).thenReturn(resp);

            ResponseEntity<PaymentResponse> response = controller.findById("PAY-002");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("PAY-002", response.getBody().id());
        }

        @Test
        @DisplayName("HP-PAYC-04: findAll() retorna 200 OK con lista de pagos")
        void findAllRetorna200() {
            when(paymentService.findAll()).thenReturn(List.of(
                    buildResponse("PAY-001", "E001", PaymentStatus.PENDING),
                    buildResponse("PAY-002", "E002", PaymentStatus.APPROVED)
            ));

            ResponseEntity<List<PaymentResponse>> response = controller.findAll();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(2, response.getBody().size());
        }

        @Test
        @DisplayName("HP-PAYC-05: findByTeam() retorna 200 OK con pago del equipo")
        void findByTeamRetorna200() {
            PaymentResponse resp = buildResponse("PAY-003", "E003", PaymentStatus.UNDER_REVIEW);
            when(paymentService.findByTeamId("E003")).thenReturn(resp);

            ResponseEntity<PaymentResponse> response = controller.findByTeam("E003");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("E003", response.getBody().teamId());
        }
    }

    // ── Error Path

    @Nested
    @DisplayName("Error Path")
    class ErrorPath {

        @Test
        @DisplayName("EP-PAYC-01: uploadReceipt() propaga PaymentException si equipo no existe")
        void uploadReceiptPropagaExcepcion() {
            UploadReceiptRequest req = new UploadReceiptRequest("NO-EXISTE", "http://r.pdf");
            doThrow(new PaymentException("teamId",
                    PaymentException.TEAM_NOT_FOUND.formatted("NO-EXISTE")))
                    .when(paymentService).uploadReceipt(req);

            assertThrows(PaymentException.class, () -> controller.uploadReceipt(req));
        }

        @Test
        @DisplayName("EP-PAYC-02: updateStatus() propaga PaymentException si pago no existe")
        void updateStatusPropagaExcepcion() {
            UpdatePaymentStatusRequest req = new UpdatePaymentStatusRequest("APPROVED");
            doThrow(new PaymentException("id",
                    PaymentException.PAYMENT_NOT_FOUND.formatted("NO-EXISTE")))
                    .when(paymentService).updateStatus("NO-EXISTE", "APPROVED");

            assertThrows(PaymentException.class,
                    () -> controller.updateStatus("NO-EXISTE", req));
        }

        @Test
        @DisplayName("EP-PAYC-03: updateStatus() propaga PaymentException si transición inválida")
        void updateStatusTransicionInvalidaPropaga() {
            UpdatePaymentStatusRequest req = new UpdatePaymentStatusRequest("REJECTED");
            doThrow(new PaymentException("status",
                    PaymentException.INVALID_TRANSITION.formatted("APPROVED", "REJECTED")))
                    .when(paymentService).updateStatus("PAY-001", "REJECTED");

            assertThrows(PaymentException.class,
                    () -> controller.updateStatus("PAY-001", req));
        }

        @Test
        @DisplayName("EP-PAYC-04: findById() propaga PaymentException si no existe")
        void findByIdPropagaExcepcion() {
            doThrow(new PaymentException("id",
                    PaymentException.PAYMENT_NOT_FOUND.formatted("NO-EXISTE")))
                    .when(paymentService).findById("NO-EXISTE");

            assertThrows(PaymentException.class, () -> controller.findById("NO-EXISTE"));
        }

        @Test
        @DisplayName("EP-PAYC-05: findByTeam() propaga PaymentException si equipo sin pago")
        void findByTeamPropagaExcepcion() {
            doThrow(new PaymentException("teamId",
                    PaymentException.PAYMENT_NOT_FOUND.formatted("E-SIN-PAGO")))
                    .when(paymentService).findByTeamId("E-SIN-PAGO");

            assertThrows(PaymentException.class, () -> controller.findByTeam("E-SIN-PAGO"));
        }
    }

    // ── Conditional Scenarios

    @Nested
    @DisplayName("Conditional Scenarios")
    class ConditionalScenarios {

        @Test
        @DisplayName("CS-PAYC-01: findAll() retorna lista vacía con 200 OK si no hay pagos")
        void findAllVacioConOk() {
            when(paymentService.findAll()).thenReturn(List.of());

            ResponseEntity<List<PaymentResponse>> response = controller.findAll();
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(response.getBody().isEmpty());
        }

        @Test
        @DisplayName("CS-PAYC-02: updateStatus() pasa el status del request al servicio")
        void updateStatusPasaStatusAlServicio() {
            UpdatePaymentStatusRequest req = new UpdatePaymentStatusRequest("REJECTED");
            when(paymentService.updateStatus("PAY-001", "REJECTED"))
                    .thenReturn(buildResponse("PAY-001", "E001", PaymentStatus.REJECTED));

            controller.updateStatus("PAY-001", req);

            verify(paymentService, times(1)).updateStatus("PAY-001", "REJECTED");
        }

        @Test
        @DisplayName("CS-PAYC-03: uploadReceipt() llama al servicio exactamente una vez")
        void uploadReceiptLlamaServicioUnaVez() {
            UploadReceiptRequest req = new UploadReceiptRequest("E005", "http://e005.pdf");
            when(paymentService.uploadReceipt(req))
                    .thenReturn(buildResponse("PAY-005", "E005", PaymentStatus.UNDER_REVIEW));

            controller.uploadReceipt(req);
            verify(paymentService, times(1)).uploadReceipt(req);
        }
    }

    // ── Helpers

    private PaymentResponse buildResponse(String id, String teamId, PaymentStatus status) {
        return new PaymentResponse(id, teamId, "Equipo " + teamId, "http://r.pdf", 200.0, status);
    }
}
