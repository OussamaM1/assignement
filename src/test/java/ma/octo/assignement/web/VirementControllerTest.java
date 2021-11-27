package ma.octo.assignement.web;

import ma.octo.assignement.domain.Compte;
import ma.octo.assignement.domain.Virement;
import ma.octo.assignement.dto.VirementDto;
import ma.octo.assignement.service.VirementService;

import static org.hamcrest.CoreMatchers.containsString;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class VirementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VirementService virementService;


    @Test
    public void givenVirements_whenGetVirements_thenStatus200() throws Exception {

        //TEST : requete GET : recuperer tous les virements

        mockMvc.perform(get("/service-virement/virements"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

    }

    @Test
    public void givenVirementById_whenGetVirementByIdAndNotFound_thenStatus451() throws Exception {

        //TEST : requete GET : recuperer un virement mais introuvable (fail 451)

        when(virementService.getVirementById(any(Long.class))).thenReturn(Optional.empty());
        mockMvc.perform(get("/service-virement/virements/100"))
                .andExpect(status().is4xxClientError())
                .andExpect(content()
                        .string(containsString("Virement introuvable")));
    }

    @Test
    public void givenVirementById_whenGetVirementById_thenStatus200() throws Exception {

        //TEST : Requete GET : REcupere un virement by id (Succee 200)

        var compteEmetteur = new Compte();
        compteEmetteur.setNrCompte("RIB1");
        var compteBeneficiaire = new Compte();
        compteBeneficiaire.setNrCompte("RIB2");
        var virementDto = new VirementDto();
        virementDto.setId(100L);
        var virement = new Virement();
        virement.setId(100L);
        virement.setMotifVirement("Assignement 2021");
        virement.setMontantVirement(BigDecimal.TEN);
        virement.setDateExecution(virement.getDateExecution());
        virement.setCompteBeneficiaire(compteBeneficiaire);
        virement.setCompteEmetteur(compteEmetteur);


        when(virementService.getVirementById(any(Long.class))).thenReturn(Optional.of(virement));


        String resultExpected = "{'id':100,'nrCompteEmetteur':'RIB1','nrCompteBeneficiaire':'RIB2'," +
                "'motif':'Assignement 2021','montantVirement':10,'date': null}";

        mockMvc.perform(get("/service-virement/virements/100"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .json(resultExpected));
    }

}
