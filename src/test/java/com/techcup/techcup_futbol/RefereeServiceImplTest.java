package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.core.service.RefereeServiceImpl;
import com.techcup.techcup_futbol.core.exception.RefereeException;
import com.techcup.techcup_futbol.persistence.entity.MatchEntity;
import com.techcup.techcup_futbol.persistence.entity.RefereeEntity;
import com.techcup.techcup_futbol.persistence.repository.MatchRepository;
import com.techcup.techcup_futbol.persistence.repository.RefereeRepository;
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
class RefereeServiceImplTest {

    @Mock private RefereeRepository refereeRepository;
    @Mock private MatchRepository matchRepository;

    @InjectMocks private RefereeServiceImpl refereeService;

    private RefereeEntity refereeEntity;
    private MatchEntity matchEntity;

    @BeforeEach
    void setUp() {
        refereeEntity = new RefereeEntity();
        refereeEntity.setId("ref1");
        refereeEntity.setFullname("Darwin Elias Romero");
        refereeEntity.setEmail("darwinhp@gmail.com");

        matchEntity = new MatchEntity();
        matchEntity.setId("match1");
    }

    @Test
    void create_validData_createsReferee() {
        when(refereeRepository.existsByEmail("darwinhp@gmail.com")).thenReturn(false);
        when(refereeRepository.save(any(RefereeEntity.class))).thenReturn(refereeEntity);

        var result = refereeService.create("Darwin Elias Romero", "darwinhp@gmail.com");

        verify(refereeRepository).save(any(RefereeEntity.class));
        assertEquals("ref1", result.getId());
    }

    @Test
    void create_emailAlreadyExists_throwsException() {
        when(refereeRepository.existsByEmail("darwinhp@gmail.com")).thenReturn(true);

        RefereeException ex = assertThrows(RefereeException.class,
                () -> refereeService.create("Darwin Elias Romero", "darwinhp@gmail.com"));
        assertEquals("email", ex.getField());
    }

    @Test
    void assignToMatch_validAssignment_assigns() {
        when(refereeRepository.findById("ref1")).thenReturn(Optional.of(refereeEntity));
        when(matchRepository.findById("match1")).thenReturn(Optional.of(matchEntity));
        when(matchRepository.save(any(MatchEntity.class))).thenReturn(matchEntity);
        when(refereeRepository.save(refereeEntity)).thenReturn(refereeEntity);

        var result = refereeService.assignToMatch("match1", "ref1");

        verify(matchRepository).save(matchEntity);
        assertEquals("ref1", result.getId());
    }

    @Test
    void assignToMatch_refereeNotFound_throwsException() {
        when(refereeRepository.findById("ref999")).thenReturn(Optional.empty());

        RefereeException ex = assertThrows(RefereeException.class,
                () -> refereeService.assignToMatch("match1", "ref999"));
        assertEquals("refereeId", ex.getField());
    }

    @Test
    void assignToMatch_matchNotFound_throwsException() {
        when(refereeRepository.findById("ref1")).thenReturn(Optional.of(refereeEntity));
        when(matchRepository.findById("match999")).thenReturn(Optional.empty());

        RefereeException ex = assertThrows(RefereeException.class,
                () -> refereeService.assignToMatch("match999", "ref1"));
        assertEquals("matchId", ex.getField());
    }

    @Test
    void assignToMatch_matchAlreadyHasReferee_throwsException() {
        when(refereeRepository.findById("ref1")).thenReturn(Optional.of(refereeEntity));
        matchEntity.setReferee(refereeEntity);
        when(matchRepository.findById("match1")).thenReturn(Optional.of(matchEntity));

        RefereeException ex = assertThrows(RefereeException.class,
                () -> refereeService.assignToMatch("match1", "ref1"));
        assertEquals("match", ex.getField());
    }

    @Test
    void findById_existing_returnsReferee() {
        when(refereeRepository.findById("ref1")).thenReturn(Optional.of(refereeEntity));

        var result = refereeService.findById("ref1");

        assertEquals("ref1", result.getId());
    }

    @Test
    void findById_notFound_throwsException() {
        when(refereeRepository.findById("ref999")).thenReturn(Optional.empty());

        RefereeException ex = assertThrows(RefereeException.class,
                () -> refereeService.findById("ref999"));
        assertEquals("id", ex.getField());
    }

    @Test
    void findAll_returnsAllReferees() {
        when(refereeRepository.findAll()).thenReturn(List.of(refereeEntity));

        var result = refereeService.findAll();

        assertEquals(1, result.size());
    }

    @Test
    void findAll_empty_returnsEmptyList() {
        when(refereeRepository.findAll()).thenReturn(List.of());

        var result = refereeService.findAll();

        assertTrue(result.isEmpty());
    }
}