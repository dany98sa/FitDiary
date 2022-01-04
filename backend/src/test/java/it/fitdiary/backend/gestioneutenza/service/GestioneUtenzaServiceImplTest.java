package it.fitdiary.backend.gestioneutenza.service;

import it.fitdiary.BackendApplicationTest;
import it.fitdiary.backend.entity.Ruolo;
import it.fitdiary.backend.entity.Utente;
import it.fitdiary.backend.gestioneutenza.repository.RuoloRepository;
import it.fitdiary.backend.gestioneutenza.repository.UtenteRepository;
import it.fitdiary.backend.utility.service.EmailServiceImpl;
import it.fitdiary.backend.utility.PasswordGenerator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = BackendApplicationTest.class)
@ActiveProfiles("test")
public class GestioneUtenzaServiceImplTest {

    @InjectMocks
    private GestioneUtenzaServiceImpl gestioneUtenzaService;
    @Mock
    private UtenteRepository utenteRepository;
    @Mock
    private RuoloRepository ruoloRepository;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private EmailServiceImpl emailService;
    @Mock
    private PasswordGenerator pwGen;
    private Ruolo ruoloCliente;
    private Ruolo ruoloPreparatore;
    private Utente cliente;
    private Utente clienteAggiornato;

    public GestioneUtenzaServiceImplTest() {
    }

    @Before
    public void setUp() {
        ruoloCliente = new Ruolo(3L, "CLIENTE", null, null);
        ruoloPreparatore = new Ruolo(2L, "PREPARATORE", null, null);
        cliente = new Utente(1L, "Rebecca", "Di Matteo", "beccadimatteoo@gmail.com", "Becca123*", true, null, null, null,
                        null, null, null, null, ruoloCliente, null, null, null, null, null);
        clienteAggiornato = new Utente(1L, "Rebecca", "Di Matteo", "beccadimatteoo@gmail.com", "Becca123*", true,
                LocalDate.parse("2000-10-30"), null, "3894685921", "Francesco rinaldo", "94061", "Agropoli", null,
                ruoloCliente, null, null, null, null, null);
    }

    @Test
    public void inserimentoDatiPersonaliCliente() {
        when(utenteRepository.findByEmail(cliente.getEmail())).thenReturn((clienteAggiornato));
        when(utenteRepository.save(clienteAggiornato)).thenReturn(clienteAggiornato);
        assertEquals(clienteAggiornato,
                gestioneUtenzaService.inserimentoDatiPersonaliCliente(clienteAggiornato, clienteAggiornato.getEmail()));
    }

    @Test
    public void inserimentoDatiPersonaliClienteUtenteNullo() {
        assertThrows(IllegalArgumentException.class,
                () -> this.gestioneUtenzaService.inserimentoDatiPersonaliCliente(null, null));
    }

    @Test
    public void inserimentoDatiPersonaliClienteUtenteNonPresenteNelDataBase() {
        when(utenteRepository.findByEmail(cliente.getEmail())).thenReturn(clienteAggiornato);
        assertThrows(IllegalArgumentException.class,
                () -> this.gestioneUtenzaService.inserimentoDatiPersonaliCliente(null, null));

    }

    @Test
    public void modificaDatiPersonaliCliente() {
        when(utenteRepository.findByEmail(cliente.getEmail())).thenReturn((clienteAggiornato));
        when(utenteRepository.save(clienteAggiornato)).thenReturn(clienteAggiornato);
        when(passwordEncoder.encode(clienteAggiornato.getPassword())).thenReturn(clienteAggiornato.getPassword());
        assertEquals(clienteAggiornato,
                gestioneUtenzaService.modificaDatiPersonaliCliente(clienteAggiornato, clienteAggiornato.getEmail()));
    }

    @Test
    public void modificaDatiPersonali_ClienteUtenteNullo_ThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> this.gestioneUtenzaService.modificaDatiPersonaliCliente(null, null));
    }

    @Test
    public void modificaDatiPersonaliCliente_UtenteNonPresenteNelDataBase_ThrowException() {
        when(utenteRepository.findByEmail(cliente.getEmail())).thenReturn(clienteAggiornato);
        assertThrows(IllegalArgumentException.class,
                () -> this.gestioneUtenzaService.modificaDatiPersonaliPreparatore(null, null));

    }

    //@Test
    public void modificaDatiPersonaliPreparatore_Success() {
        Utente utente = new Utente(1L, "Michele", "De Marco", "dani5@gmail.com", "Trappo#98", true,
                LocalDate.parse("2000-03-03"), null, "3459666587", "Francesco La Francesca", "84126", "Salerno", null,
                ruoloPreparatore, null, null, null, null, null);
        Utente updatedUtente =
                new Utente(1L, "Daniele", "De Marco", "diodani5@gmail.com", "Trappo#98", true, null, null, null, null,
                        null, null, null, ruoloPreparatore, null, null, null, null, null);
        when(utenteRepository.save(updatedUtente)).thenReturn(updatedUtente);
        when(utenteRepository.findByEmail(utente.getEmail())).thenReturn(updatedUtente);
        when(passwordEncoder.encode(utente.getPassword())).thenReturn(utente.getPassword());
        assertEquals(utente,
                gestioneUtenzaService.modificaDatiPersonaliPreparatore(updatedUtente, updatedUtente.getEmail()));
    }

    @Test
    public void modificaDatiPersonali_PreparatoreUtenteNullo_ThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> this.gestioneUtenzaService.modificaDatiPersonaliPreparatore(null, null));
    }

    @Test
    public void modificaDatiPersonali_PreparatoreUtenteNonPresenteNelDataBase_ThrowException() {
        Utente updatedUtente =
                new Utente(1L, "Daniele", "De Marco", "diodani5@gmail.com", "Trappo#98", true, null, null, null, null,
                        null, null, null, ruoloPreparatore, null, null, null, null, null);
        when(utenteRepository.findById(updatedUtente.getId())).thenReturn(java.util.Optional.of(updatedUtente));
        assertThrows(IllegalArgumentException.class,
                () -> this.gestioneUtenzaService.modificaDatiPersonaliPreparatore(null, null));

    }

    @Test
    public void inserisciCliente_Success() {
        String nome = "Rebecca";
        String cognome = "Melenchi";
        String email = "rebmel@gmail.com";
        String emailPrep = "davide@gmail.com";
        String password = "Melenchi123*";
        Utente preparatore =
                new Utente(1L, "Davide", "La Gamba", emailPrep, "Davide123*", true, LocalDate.parse("2000-03-03"), null,
                        "3313098075", "Michele Santoro", "81022", "Caserta", null, ruoloPreparatore, null, null, null,
                        null, null);
        Utente newUtentePre =
                new Utente(null, nome, cognome, email, password, true, LocalDate.parse("1990-01-01"), null, null, null,
                        null, null, preparatore, ruoloCliente, null, null, null, null, null);
        Utente newUtentePost =
                new Utente(2L, nome, cognome, email, password, true, LocalDate.parse("1990-01-01"), null, null, null,
                        null, null, preparatore, ruoloCliente, null, null, null, null, null);
        when(utenteRepository.findByEmail(emailPrep)).thenReturn(preparatore);
        when(utenteRepository.findByEmail(email)).thenReturn(null);
        when(utenteRepository.save(newUtentePre)).thenReturn(newUtentePost);
        when(ruoloRepository.findByNome("CLIENTE")).thenReturn(ruoloCliente);
        when(pwGen.generate()).thenReturn("Melenchi123*");
        doNothing().when(emailService).sendSimpleMessage(newUtentePre.getEmail(), "Benvenuto in FitDiary!",
                "Ecco la tua password per accedere: \n" + password);
        when(passwordEncoder.encode(password)).thenReturn(password);
        assertEquals(newUtentePost, gestioneUtenzaService.inserisciCliente(nome, cognome, email, emailPrep));
    }

    @Test
    public void inserisciClientethrowsIllegalPrep() {
        String nome = "Rebecca";
        String cognome = "Melenchi";
        String email = "rebmel@gmail.com";
        String emailPrep = "davide@gmail.com";
        String password = "Melenchi123*";
        Utente preparatore =
                new Utente(1L, "Davide", "La Gamba", emailPrep, "Davide123*", true, LocalDate.parse("2000-03-03"), null,
                        "3313098075", "Michele Santoro", "81022", "Caserta", null, ruoloPreparatore, null, null, null,
                        null, null);
        Utente newUtentePre =
                new Utente(null, nome, cognome, email, password, true, LocalDate.parse("1990-01-01"), null, null, null,
                        null, null, preparatore, ruoloCliente, null, null, null, null, null);
        Utente newUtentePost =
                new Utente(2L, nome, cognome, email, password, true, LocalDate.parse("1990-01-01"), null, null, null,
                        null, null, preparatore, ruoloCliente, null, null, null, null, null);
        when(utenteRepository.findByEmail(emailPrep)).thenReturn(null);
        when(utenteRepository.findByEmail(email)).thenReturn(null);
        when(utenteRepository.save(newUtentePre)).thenReturn(newUtentePost);
        when(ruoloRepository.findByNome("CLIENTE")).thenReturn(ruoloCliente);
        when(pwGen.generate()).thenReturn("Melenchi123*");
        doNothing().when(emailService).sendSimpleMessage(newUtentePre.getEmail(), "Benvenuto in FitDiary!",
                "Ecco la tua password per accedere: \n" + password);
        when(passwordEncoder.encode(password)).thenReturn(password);
        assertThrows(IllegalArgumentException.class, () -> gestioneUtenzaService.inserisciCliente(nome, cognome, email, emailPrep));
    }

    @Test
    public void inserisciClientethrowsIllegalCliente() {
        String nome = "Rebecca";
        String cognome = "Melenchi";
        String email = "rebmel@gmail.com";
        String emailPrep = "davide@gmail.com";
        String password = "Melenchi123*";
        Utente preparatore =
                new Utente(1L, "Davide", "La Gamba", emailPrep, "Davide123*", true, LocalDate.parse("2000-03-03"), null,
                        "3313098075", "Michele Santoro", "81022", "Caserta", null, ruoloPreparatore, null, null, null,
                        null, null);
        Utente newUtentePre =
                new Utente(null, nome, cognome, email, password, true, LocalDate.parse("1990-01-01"), null, null, null,
                        null, null, preparatore, ruoloCliente, null, null, null, null, null);
        Utente newUtentePost =
                new Utente(2L, nome, cognome, email, password, true, LocalDate.parse("1990-01-01"), null, null, null,
                        null, null, preparatore, ruoloCliente, null, null, null, null, null);
        when(utenteRepository.findByEmail(emailPrep)).thenReturn(preparatore);
        when(utenteRepository.findByEmail(email)).thenReturn(newUtentePost);
        when(utenteRepository.save(newUtentePre)).thenReturn(newUtentePost);
        when(ruoloRepository.findByNome("CLIENTE")).thenReturn(ruoloCliente);
        when(pwGen.generate()).thenReturn("Melenchi123*");
        doNothing().when(emailService).sendSimpleMessage(newUtentePre.getEmail(), "Benvenuto in FitDiary!",
                "Ecco la tua password per accedere: \n" + password);
        when(passwordEncoder.encode(password)).thenReturn(password);
        assertThrows(IllegalArgumentException.class, () -> gestioneUtenzaService.inserisciCliente(nome, cognome, email, emailPrep));
    }

    @Test
    public void getUtenteByEmail() {
        String email = "davide@gmail.com";
        Utente utente =
                new Utente(1L, "Davide", "La Gamba", email, "Davide123*", true, LocalDate.parse("2000-03-03"), null,
                        "3313098075", "Michele Santoro", "81022", "Caserta", null, ruoloPreparatore, null, null, null,
                        null, null);
        when(utenteRepository.findByEmail(email)).thenReturn(utente);
        assertEquals(utente, gestioneUtenzaService.getUtenteByEmail(email));
    }

    @Test
    public void getUtenteByEmailNull_ThrowsIllegalEmail() {
        Utente utente = new Utente(1L, "Davide", "La Gamba", "davide@gmail.com", "Davide123*", true,
                LocalDate.parse("2000-03-03"), null, "3313098075", "Michele Santoro", "81022", "Caserta", null,
                ruoloPreparatore, null, null, null, null, null);
        when(utenteRepository.findByEmail(null)).thenReturn(utente);
        assertThrows(IllegalArgumentException.class, () -> gestioneUtenzaService.getUtenteByEmail(null));
    }

    @Test
    public void getUtenteByEmailThrowsIllegalUtente() {
        String email = "davide@gmail.com";
        when(utenteRepository.findByEmail(email)).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> gestioneUtenzaService.getUtenteByEmail(email));
    }

    @Test
    public void registrazioneEmailError() {
        when(this.utenteRepository.existsByEmail(any())).thenReturn(true);
        Utente utente = new Utente(null, "Daniele", "De Marco", "fabrizio" + "@gmail" + ".com", "Daniele123*", true,
                LocalDate.parse("2000-03-03"), null, "33985458", "Salvo D'Acquisto", "84047", "Capaccio", null,
                ruoloPreparatore, null, null, null, null, null);
        assertThrows(IllegalArgumentException.class, () -> this.gestioneUtenzaService.registrazione(utente));
        verify(this.utenteRepository).existsByEmail(any());
    }

    @Test
    public void registrazione() {
        Utente utente = new Utente(null, "Daniele", "De Marco", "fabrizio" + "@gmail" + ".com", "Daniele123*", true,
                LocalDate.parse("2000-03-03"), null, "33985458", "Salvo D'Acquisto", "84047", "Capaccio", null,
                ruoloPreparatore, null, null, null, null, null);
        Utente newUtente = new Utente(1L, "Daniele", "De Marco", "fabrizio" + "@gmail.com", "Daniele123*", true,
                LocalDate.parse("2000-03-03"), null, "33985458", "Salvo D'Acquisto", "84047", "Capaccio", null,
                ruoloPreparatore, null, null, null, null, null);
        when(this.utenteRepository.save(utente)).thenReturn(newUtente);
        when(this.utenteRepository.existsByEmail(any())).thenReturn(false);
        when(this.ruoloRepository.findByNome(any())).thenReturn(ruoloPreparatore);
        assertSame(newUtente, this.gestioneUtenzaService.registrazione(utente));
        verify(this.utenteRepository).existsByEmail(any());
        verify(this.utenteRepository).save(utente);
    }

    @Test
    public void registrazioneUtenteNull() {
        assertThrows(IllegalArgumentException.class, () -> this.gestioneUtenzaService.registrazione(null));
    }
}