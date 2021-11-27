package ma.octo.assignement.repository;

import ma.octo.assignement.domain.Virement;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class VirementRepositoryTest {

    @Autowired
    private VirementRepository virementRepository;


    @Test
    @Order(1)
    void testFindOneVirement() {

        // TEST : Récuperer un virement avec son id

        //given
        var virement = new Virement();
        virement.setMontantVirement(BigDecimal.TEN);
        virement.setDateExecution(new Date());
        virement.setMotifVirement("Test case");
        virementRepository.save(virement);

        //when
        var virementExpected = virementRepository.findById(5L).orElse(null);

        //Then
        assertThat(virementExpected).isNotNull();
        assertThat(virementExpected.getId()).isPositive();
        assertThat(virementExpected.getMotifVirement()).isEqualTo("Test case");
    }

    @Test
    @Order(4)
    void testFindAllVirement() {

        //TEST : Consulter tout les virements

        var virement = new Virement();
        virement.setMontantVirement(BigDecimal.TEN);
        virement.setDateExecution(new Date());
        virement.setMotifVirement("Test case");
        virementRepository.save(virement);
        List<Virement> allVirements = virementRepository.findAll();
        assertThat(allVirements.size()).isPositive();
    }

    @Test
    @Order(3)
    void testSaveVirement() {

        //TEST : Effectuer un virement

        //Given
        var virement = new Virement();
        virement.setMontantVirement(BigDecimal.TEN);
        virement.setDateExecution(new Date());
        virement.setMotifVirement("Motif save");

        //when
        virementRepository.save(virement);

        //Then
        assertThat(virement).isNotNull();
        assertThat(virement.getId()).isPositive();
    }

    @Test
    @Order(2)
    void testDeleteVirement() {

        //TEST : Supprimer un virement

        var virement = new Virement();
        virement.setMontantVirement(BigDecimal.TEN);
        virement.setDateExecution(new Date());
        virement.setMotifVirement("Test case");
        virementRepository.save(virement);
        virementRepository.delete(virement);
        var deletedVirement = virementRepository.findById(5L).orElse(null);
        assertThat(deletedVirement).isNull();

    }
}