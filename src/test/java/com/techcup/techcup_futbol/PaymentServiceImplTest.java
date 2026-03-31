package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.core.exception.PaymentException;
import com.techcup.techcup_futbol.core.model.Payment;
import com.techcup.techcup_futbol.core.model.PaymentStatus;
import com.techcup.techcup_futbol.core.model.PositionEnum;
import com.techcup.techcup_futbol.core.model.StudentPlayer;
import com.techcup.techcup_futbol.core.model.Team;
import com.techcup.techcup_futbol.core.service.PaymentServiceImpl;
import com.techcup.techcup_futbol.repository.PaymentRepository;
import com.techcup.techcup_futbol.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private TeamRepository teamRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Team team;
    private Payment existingPayment;

    @BeforeEach
    void setUp() {
        StudentPlayer captain = new StudentPlayer();
        captain.setId("cap-001");
        captain.setFullname("Capitan");
        captain.setAge(22);
        captain.setPosition(PositionEnum.Defender);
        captain.setSemester(5);

        team = new Team();
        team.setId("team-001");
        team.setTeamName("Los Tigres");
        team.setCaptain(captain);
        team.setPlayers(List.of(captain));

        existingPayment = new Payment();
        existingPayment.setId("pay-001");
        existingPayment.setTeamId("team-001");
        existingPayment.setAmount(50.0);
        existingPayment.setCurrentStatus(PaymentStatus.PENDING);
    }

    // ── UPLOAD RECEIPT ──

    @Test
    void uploadReceipt_newPayment_createsAndSaves() {
        when(teamRepository.findById("team-001")).thenReturn(Optional.of(team));
        when(paymentRepository.findByTeamId("team-001")).thenReturn(Optional.empty());
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        Payment result = paymentService.uploadReceipt("team-001", "http://receipt.com/img.jpg");

        assertNotNull(result.getId());
        assertEquals("team-001", result.getTeamId());
        assertEquals(PaymentStatus.UNDER_REVIEW, result.getCurrentStatus());
        assertEquals("http://receipt.com/img.jpg", result.getReceiptUrl());
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void uploadReceipt_existingPayment_updatesReceipt() {
        when(teamRepository.findById("team-001")).thenReturn(Optional.of(team));
        when(paymentRepository.findByTeamId("team-001")).thenReturn(Optional.of(existingPayment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(existingPayment);

        Payment result = paymentService.uploadReceipt("team-001", "http://receipt.com/new.jpg");

        assertEquals(PaymentStatus.UNDER_REVIEW, result.getCurrentStatus());
        assertEquals("http://receipt.com/new.jpg", result.getReceiptUrl());
        verify(paymentRepository).save(existingPayment);
    }

    @Test
    void uploadReceipt_teamNotFound_throwsException() {
        when(teamRepository.findById("team-999")).thenReturn(Optional.empty());

        assertThrows(PaymentException.class,
                () -> paymentService.uploadReceipt("team-999", "http://receipt.com/img.jpg"));
    }

    @Test
    void uploadReceipt_blankUrl_throwsException() {
        when(teamRepository.findById("team-001")).thenReturn(Optional.of(team));

        assertThrows(PaymentException.class,
                () -> paymentService.uploadReceipt("team-001", "  "));
    }

    @Test
    void uploadReceipt_alreadyApproved_throwsException() {
        existingPayment.setCurrentStatus(PaymentStatus.APPROVED);
        when(teamRepository.findById("team-001")).thenReturn(Optional.of(team));
        when(paymentRepository.findByTeamId("team-001")).thenReturn(Optional.of(existingPayment));

        assertThrows(PaymentException.class,
                () -> paymentService.uploadReceipt("team-001", "http://receipt.com/img.jpg"));
    }

    // ── UPDATE STATUS ──

    @Test
    void updateStatus_validTransition_updatesStatus() {
        when(paymentRepository.findById("pay-001")).thenReturn(Optional.of(existingPayment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(existingPayment);

        Payment result = paymentService.updateStatus("pay-001", "UNDER_REVIEW");

        assertEquals(PaymentStatus.UNDER_REVIEW, result.getCurrentStatus());
        verify(paymentRepository).save(existingPayment);
    }

    @Test
    void updateStatus_invalidStatus_throwsException() {
        when(paymentRepository.findById("pay-001")).thenReturn(Optional.of(existingPayment));

        assertThrows(PaymentException.class,
                () -> paymentService.updateStatus("pay-001", "INVALID_STATUS"));
    }

    @Test
    void updateStatus_invalidTransition_throwsException() {
        existingPayment.setCurrentStatus(PaymentStatus.APPROVED);
        when(paymentRepository.findById("pay-001")).thenReturn(Optional.of(existingPayment));

        assertThrows(PaymentException.class,
                () -> paymentService.updateStatus("pay-001", "PENDING"));
    }

    // ── FIND BY ID ──

    @Test
    void findById_existing_returnsPayment() {
        when(paymentRepository.findById("pay-001")).thenReturn(Optional.of(existingPayment));

        Payment result = paymentService.findById("pay-001");

        assertEquals("pay-001", result.getId());
    }

    @Test
    void findById_nonExistent_throwsException() {
        when(paymentRepository.findById("pay-999")).thenReturn(Optional.empty());

        assertThrows(PaymentException.class,
                () -> paymentService.findById("pay-999"));
    }

    // ── FIND BY TEAM ID ──

    @Test
    void findByTeamId_existing_returnsPayment() {
        when(paymentRepository.findByTeamId("team-001")).thenReturn(Optional.of(existingPayment));

        Payment result = paymentService.findByTeamId("team-001");

        assertEquals("team-001", result.getTeamId());
    }
}
