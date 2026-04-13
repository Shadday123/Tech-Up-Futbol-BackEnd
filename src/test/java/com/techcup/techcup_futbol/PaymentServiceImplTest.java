package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.core.exception.PaymentException;
import com.techcup.techcup_futbol.core.model.Payment;
import com.techcup.techcup_futbol.core.model.PaymentStatus;
import com.techcup.techcup_futbol.core.model.PositionEnum;
import com.techcup.techcup_futbol.core.model.StudentPlayer;
import com.techcup.techcup_futbol.core.model.Team;
import com.techcup.techcup_futbol.core.service.PaymentServiceImpl;
import com.techcup.techcup_futbol.persistence.entity.PaymentEntity;
import com.techcup.techcup_futbol.persistence.entity.TeamEntity;
import com.techcup.techcup_futbol.persistence.mapper.PaymentPersistenceMapper;
import com.techcup.techcup_futbol.persistence.repository.PaymentRepository;
import com.techcup.techcup_futbol.persistence.repository.TeamRepository;
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

    private TeamEntity teamEntity;
    private PaymentEntity existingPaymentEntity;
    private Payment existingPayment;

    @BeforeEach
    void setUp() {
        // TeamEntity para el repositorio
        StudentPlayer captain = new StudentPlayer();
        captain.setId("cap-001");
        captain.setFullname("Capitan");
        captain.setAge(22);
        captain.setPosition(PositionEnum.Defender);
        captain.setSemester(5);

        Team team = new Team();
        team.setId("team-001");
        team.setTeamName("Los Tigres");
        team.setCaptain(captain);
        team.setPlayers(List.of(captain));

        teamEntity = new TeamEntity();
        teamEntity.setId("team-001");
        teamEntity.setTeamName("Los Tigres");

        // PaymentEntity para el repositorio
        existingPaymentEntity = new PaymentEntity();
        existingPaymentEntity.setId("pay-001");
        existingPaymentEntity.setTeamId("team-001");
        existingPaymentEntity.setAmount(550.0); // 50.0 * 11
        existingPaymentEntity.setCurrentStatus(PaymentStatus.PENDING);

        // Payment para las aserciones
        existingPayment = PaymentPersistenceMapper.toDomain(existingPaymentEntity);
    }

    // ── UPLOAD RECEIPT ──

    @Test
    void uploadReceipt_newPayment_createsAndSaves() {
        when(teamRepository.findById("team-001")).thenReturn(Optional.of(teamEntity));
        when(paymentRepository.findByTeamId("team-001")).thenReturn(Optional.empty());

        PaymentEntity newPaymentEntity = new PaymentEntity();
        newPaymentEntity.setId("pay-new");
        newPaymentEntity.setTeamId("team-001");
        newPaymentEntity.setAmount(550.0); // 50*11
        newPaymentEntity.setCurrentStatus(PaymentStatus.UNDER_REVIEW);
        newPaymentEntity.setReceiptUrl("http://receipt.com/img.jpg");

        when(paymentRepository.save(any(PaymentEntity.class))).thenReturn(newPaymentEntity);

        Payment result = paymentService.uploadReceipt("team-001", "http://receipt.com/img.jpg");

        assertEquals("pay-new", result.getId());
        assertEquals("team-001", result.getTeamId());
        assertEquals(550.0, result.getAmount());
        assertEquals(PaymentStatus.UNDER_REVIEW, result.getCurrentStatus());
        assertEquals("http://receipt.com/img.jpg", result.getReceiptUrl());
        verify(paymentRepository).save(any(PaymentEntity.class));
    }

    @Test
    void uploadReceipt_existingPayment_updatesReceipt() {
        when(teamRepository.findById("team-001")).thenReturn(Optional.of(teamEntity));
        when(paymentRepository.findByTeamId("team-001")).thenReturn(Optional.of(existingPaymentEntity));

        existingPaymentEntity.setReceiptUrl("http://receipt.com/new.jpg");
        existingPaymentEntity.setCurrentStatus(PaymentStatus.UNDER_REVIEW);
        when(paymentRepository.save(existingPaymentEntity)).thenReturn(existingPaymentEntity);

        Payment result = paymentService.uploadReceipt("team-001", "http://receipt.com/new.jpg");

        assertEquals("pay-001", result.getId());
        assertEquals(PaymentStatus.UNDER_REVIEW, result.getCurrentStatus());
        assertEquals("http://receipt.com/new.jpg", result.getReceiptUrl());
        verify(paymentRepository).save(existingPaymentEntity);
    }

    @Test
    void uploadReceipt_teamNotFound_throwsException() {
        when(teamRepository.findById("team-999")).thenReturn(Optional.empty());

        PaymentException exception = assertThrows(PaymentException.class,
                () -> paymentService.uploadReceipt("team-999", "http://receipt.com/img.jpg"));
        assertEquals("teamId", exception.getField());
    }

    @Test
    void uploadReceipt_blankUrl_throwsException() {
        when(teamRepository.findById("team-001")).thenReturn(Optional.of(teamEntity));

        PaymentException exception = assertThrows(PaymentException.class,
                () -> paymentService.uploadReceipt("team-001", "  "));
        assertEquals("receiptUrl", exception.getField());
    }

    @Test
    void uploadReceipt_alreadyApproved_throwsException() {
        existingPaymentEntity.setCurrentStatus(PaymentStatus.APPROVED);
        when(teamRepository.findById("team-001")).thenReturn(Optional.of(teamEntity));
        when(paymentRepository.findByTeamId("team-001")).thenReturn(Optional.of(existingPaymentEntity));

        PaymentException exception = assertThrows(PaymentException.class,
                () -> paymentService.uploadReceipt("team-001", "http://receipt.com/img.jpg"));
        assertEquals("status", exception.getField());
    }

    // ── UPDATE STATUS ──

    @Test
    void updateStatus_validTransition_updatesStatus() {
        when(paymentRepository.findById("pay-001")).thenReturn(Optional.of(existingPaymentEntity));
        existingPaymentEntity.setCurrentStatus(PaymentStatus.PENDING);
        when(paymentRepository.save(existingPaymentEntity)).thenReturn(existingPaymentEntity);

        Payment result = paymentService.updateStatus("pay-001", "UNDER_REVIEW");

        assertEquals(PaymentStatus.UNDER_REVIEW, result.getCurrentStatus());
        verify(paymentRepository).save(existingPaymentEntity);
    }

    @Test
    void updateStatus_invalidStatus_throwsException() {
        when(paymentRepository.findById("pay-001")).thenReturn(Optional.of(existingPaymentEntity));

        PaymentException exception = assertThrows(PaymentException.class,
                () -> paymentService.updateStatus("pay-001", "INVALID_STATUS"));
        assertEquals("status", exception.getField());
    }

    @Test
    void updateStatus_invalidTransition_throwsException() {
        existingPaymentEntity.setCurrentStatus(PaymentStatus.APPROVED);
        when(paymentRepository.findById("pay-001")).thenReturn(Optional.of(existingPaymentEntity));

        PaymentException exception = assertThrows(PaymentException.class,
                () -> paymentService.updateStatus("pay-001", "PENDING"));
        assertEquals("status", exception.getField());
    }

    // ── FIND BY ID ──

    @Test
    void findById_existing_returnsPayment() {
        when(paymentRepository.findById("pay-001")).thenReturn(Optional.of(existingPaymentEntity));

        Payment result = paymentService.findById("pay-001");

        assertEquals("pay-001", result.getId());
        assertEquals(550.0, result.getAmount());
    }

    @Test
    void findById_nonExistent_throwsException() {
        when(paymentRepository.findById("pay-999")).thenReturn(Optional.empty());

        PaymentException exception = assertThrows(PaymentException.class,
                () -> paymentService.findById("pay-999"));
        assertEquals("id", exception.getField());
    }

    // ── FIND BY TEAM ID ──

    @Test
    void findByTeamId_existing_returnsPayment() {
        when(paymentRepository.findByTeamId("team-001")).thenReturn(Optional.of(existingPaymentEntity));

        Payment result = paymentService.findByTeamId("team-001");

        assertEquals("team-001", result.getTeamId());
        assertEquals(550.0, result.getAmount());
    }

    @Test
    void findByTeamId_nonExistent_throwsException() {
        when(paymentRepository.findByTeamId("team-999")).thenReturn(Optional.empty());

        PaymentException exception = assertThrows(PaymentException.class,
                () -> paymentService.findByTeamId("team-999"));
        assertEquals("teamId", exception.getField());
    }
}