package ma.octo.assignement.service;


import ma.octo.assignement.domain.Compte;
import ma.octo.assignement.domain.Virement;
import ma.octo.assignement.dto.VirementDto;
import ma.octo.assignement.exceptions.CompteNonExistantException;
import ma.octo.assignement.exceptions.TransactionException;
import ma.octo.assignement.exceptions.VirementNonExistantException;
import ma.octo.assignement.repository.CompteRepository;
import ma.octo.assignement.repository.VirementRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class VirementServiceTest {

    @MockBean
    private VirementRepository virementRepository;

    @MockBean
    private CompteRepository compteRepository;

    @MockBean
    private AuditService auditService;


    private VirementService virementService;

    @BeforeEach
    void setUp() {
        virementService = new VirementService(virementRepository, compteRepository, auditService);
    }


    @Test
    void testGetAllVirmentsTest() {

        // TEST : La methode service pour recuperer tout les virements
        // Service layer

        //given
        var virementN1 = new Virement();
        virementN1.setMotifVirement("Test");
        var virementN2 = new Virement();
        //when
        when(virementRepository.findAll()).thenReturn(Arrays.asList(virementN1, virementN2));
        List<Virement> virementsList = virementService.getAllVirements();
        //then
        assertThat(virementsList).isEqualTo(Arrays.asList(virementN1, virementN2));
    }

    @Test
    void testGetVirementByIdTest() {

        // TEST : La methode service pour recuperer un virement par son id
        // Service layer

        Virement virement = new Virement();
        virement.setId(1L);
        virement.setMontantVirement(BigDecimal.TEN);
        virement.setDateExecution(new Date());
        virement.setMotifVirement("Test case");
        when(virementRepository.findById(1L)).thenReturn(Optional.of(virement));
        assertThat(virementService.getVirementById(1L)).isEqualTo(Optional.of(virement));
    }

    @Test
    void testCreateTransactionTest() throws TransactionException, CompteNonExistantException {

        // TEST : La methode service pour effectuer un virement (Cas parfait)
        // Service layer

        var compteEmetteur = new Compte();
        compteEmetteur.setSolde(BigDecimal.valueOf(1000));
        var compteBeneficiaire = new Compte();
        compteBeneficiaire.setSolde(BigDecimal.valueOf(0));
        VirementDto virementDto = new VirementDto();
        virementDto.setMotif("Assignement 2021");
        virementDto.setMontantVirement(BigDecimal.TEN);
        virementDto.setDate(new Date());
        virementDto.setNrCompteEmetteur("RIB1");
        virementDto.setNrCompteBeneficiaire("RIB2");

        when(compteRepository.findByNrCompte(virementDto.getNrCompteEmetteur())).thenReturn(compteEmetteur);
        when(compteRepository.findByNrCompte(virementDto.getNrCompteBeneficiaire())).thenReturn(compteBeneficiaire);

        var virement = new Virement();
        virement.setMotifVirement("Assignement 2021");
        virement.setMontantVirement(BigDecimal.TEN);
        virement.setDateExecution(virement.getDateExecution());
        virement.setCompteBeneficiaire(compteBeneficiaire);
        virement.setCompteEmetteur(compteEmetteur);

        when(virementRepository.save(any(Virement.class))).thenReturn(virement);

        var virementAjoute = virementService.createTransaction(virementDto);

        assertThat(virementAjoute).isNotNull();
        assertThat(virementAjoute.getMotifVirement()).isEqualTo(virementDto.getMotif());
        assertThat(virementAjoute.getCompteBeneficiaire().getSolde()).isEqualTo(BigDecimal.TEN);
        assertThat(virementAjoute.getCompteEmetteur().getSolde()).isEqualTo(BigDecimal.valueOf(990));
    }

    @Test
    void testCreateTransactionTest_whenDuplicatedCompte() {

        // TEST : La methode service pour effectuer un virement (Cas ou l'emetteur est le Benificiaire en meme temps )
        // Service layer

        var thrown = Assertions.assertThrows(
                TransactionException.class,
                () -> {
                    var compteEmetteur = new Compte();
                    compteEmetteur.setSolde(BigDecimal.valueOf(1000));
                    var compteBeneficiaire = new Compte();
                    compteBeneficiaire.setSolde(BigDecimal.valueOf(0));
                    VirementDto virementDto = new VirementDto();
                    virementDto.setMotif("Assignement 2021");
                    virementDto.setMontantVirement(BigDecimal.TEN);
                    virementDto.setDate(new Date());
                    virementDto.setNrCompteEmetteur("RIB1");
                    virementDto.setNrCompteBeneficiaire("RIB2");

                    // Le cas d'une transaction vers le meme compte
                    when(compteRepository.findByNrCompte(virementDto.getNrCompteEmetteur())).thenReturn(compteEmetteur);
                    when(compteRepository.findByNrCompte(virementDto.getNrCompteBeneficiaire())).thenReturn(compteEmetteur);

                    var virement = new Virement();
                    virement.setMotifVirement("Assignement 2021");
                    virement.setMontantVirement(BigDecimal.TEN);
                    virement.setDateExecution(virement.getDateExecution());
                    virement.setCompteBeneficiaire(compteBeneficiaire);
                    virement.setCompteEmetteur(compteEmetteur);

                    when(virementRepository.save(any(Virement.class))).thenReturn(virement);
                    virementService.createTransaction(virementDto);

                });

        Assertions.assertEquals("Transaction vers le meme compte", thrown.getMessage());
    }

    @Test
    void testCreateTransactionTest_whenMontant_Maximal_Depasse() {

        // TEST : La methode service pour effectuer un virement si on depasse le Montant maximal de 10000
        // Service layer
        //Le cas ou le montant de la transaction a depasse le montant maximal


        var thrown = Assertions.assertThrows(
                TransactionException.class,
                () -> {
                    var compteEmetteur = new Compte();
                    compteEmetteur.setSolde(BigDecimal.valueOf(1000));
                    var compteBeneficiaire = new Compte();
                    compteBeneficiaire.setSolde(BigDecimal.valueOf(0));
                    VirementDto virementDto = new VirementDto();
                    virementDto.setMotif("Assignement 2021");

                    //Le cas ou le montant de la transaction a depasse le montant maximal
                    virementDto.setMontantVirement(BigDecimal.valueOf(1000000));
                    virementDto.setDate(new Date());
                    virementDto.setNrCompteEmetteur("RIB1");
                    virementDto.setNrCompteBeneficiaire("RIB2");

                    when(compteRepository.findByNrCompte(virementDto.getNrCompteEmetteur())).thenReturn(compteEmetteur);
                    when(compteRepository.findByNrCompte(virementDto.getNrCompteBeneficiaire())).thenReturn(compteBeneficiaire);

                    var virement = new Virement();
                    virement.setMotifVirement("Assignement 2021");
                    virement.setMontantVirement(BigDecimal.TEN);
                    virement.setDateExecution(virement.getDateExecution());
                    virement.setCompteBeneficiaire(compteBeneficiaire);
                    virement.setCompteEmetteur(compteEmetteur);

                    when(virementRepository.save(any(Virement.class))).thenReturn(virement);
                    virementService.createTransaction(virementDto);

                });

        Assertions.assertEquals("Montant maximal de virement dépassé", thrown.getMessage());
    }
    @Test
    void testCreateTransactionTest_whenMontant_Minimal_NonAtteint() {

        // TEST : La methode service pour effectuer un virement si on depasse le Montant maximal de 10000
        // Service layer
        //Le cas ou le montant de la transaction n'atteint pas le montant minimal

        var thrown = Assertions.assertThrows(
                TransactionException.class,
                () -> {
                    var compteEmetteur = new Compte();
                    compteEmetteur.setSolde(BigDecimal.valueOf(1000));
                    var compteBeneficiaire = new Compte();
                    compteBeneficiaire.setSolde(BigDecimal.valueOf(0));
                    VirementDto virementDto = new VirementDto();
                    virementDto.setMotif("Assignement 2021");

                    //Le cas ou le montant de la transaction n'atteint pas le montant minimal
                    virementDto.setMontantVirement(BigDecimal.valueOf(6));
                    virementDto.setDate(new Date());
                    virementDto.setNrCompteEmetteur("RIB1");
                    virementDto.setNrCompteBeneficiaire("RIB2");

                    when(compteRepository.findByNrCompte(virementDto.getNrCompteEmetteur())).thenReturn(compteEmetteur);
                    when(compteRepository.findByNrCompte(virementDto.getNrCompteBeneficiaire())).thenReturn(compteBeneficiaire);

                    var virement = new Virement();
                    virement.setMotifVirement("Assignement 2021");
                    virement.setMontantVirement(BigDecimal.TEN);
                    virement.setDateExecution(virement.getDateExecution());
                    virement.setCompteBeneficiaire(compteBeneficiaire);
                    virement.setCompteEmetteur(compteEmetteur);

                    when(virementRepository.save(any(Virement.class))).thenReturn(virement);
                    virementService.createTransaction(virementDto);

                });

        Assertions.assertEquals("Montant minimal de virement non atteint", thrown.getMessage());
    }

    @Test
    void testCreateTransactionTest_whenSolde_insufissant() {

        // TEST : La methode service pour effectuer un virement si on depasse le Montant maximal de 10000
        // Service layer
        //Le cas ou l'emetteur veut transmetteur une somme plus que son solde
        // 2000 > 1000

        var thrown = Assertions.assertThrows(
                TransactionException.class,
                () -> {
                    var compteEmetteur = new Compte();

                    //Le cas ou l'emetteur veut transmetteur une somme plus que son solde
                    // 2000 > 1000
                    compteEmetteur.setSolde(BigDecimal.valueOf(1000));
                    var compteBeneficiaire = new Compte();
                    compteBeneficiaire.setSolde(BigDecimal.valueOf(0));
                    VirementDto virementDto = new VirementDto();
                    virementDto.setMotif("Assignement 2021");

                    virementDto.setMontantVirement(BigDecimal.valueOf(2000));
                    virementDto.setDate(new Date());
                    virementDto.setNrCompteEmetteur("RIB1");
                    virementDto.setNrCompteBeneficiaire("RIB2");

                    when(compteRepository.findByNrCompte(virementDto.getNrCompteEmetteur())).thenReturn(compteEmetteur);
                    when(compteRepository.findByNrCompte(virementDto.getNrCompteBeneficiaire())).thenReturn(compteBeneficiaire);

                    var virement = new Virement();
                    virement.setMotifVirement("Assignement 2021");
                    virement.setMontantVirement(BigDecimal.TEN);
                    virement.setDateExecution(virement.getDateExecution());
                    virement.setCompteBeneficiaire(compteBeneficiaire);
                    virement.setCompteEmetteur(compteEmetteur);

                    when(virementRepository.save(any(Virement.class))).thenReturn(virement);
                    virementService.createTransaction(virementDto);

                });

        Assertions.assertEquals("Solde insuffisant pour l'utilisateur", thrown.getMessage());
    }

}
