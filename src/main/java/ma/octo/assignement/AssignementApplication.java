package ma.octo.assignement;

import ma.octo.assignement.domain.Compte;
import ma.octo.assignement.domain.Utilisateur;
import ma.octo.assignement.domain.Virement;
import ma.octo.assignement.repository.CompteRepository;
import ma.octo.assignement.repository.UtilisateurRepository;
import ma.octo.assignement.repository.VirementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigDecimal;
import java.util.Date;

@SpringBootApplication
public class AssignementApplication implements CommandLineRunner {

    private final CompteRepository compteRepository;

    private final UtilisateurRepository utilisateurRepository;

    private final VirementRepository virementRepository;

    @Autowired
    public AssignementApplication(CompteRepository compteRepository, UtilisateurRepository utilisateurRepository, VirementRepository virementRepository) {
        this.compteRepository = compteRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.virementRepository = virementRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(AssignementApplication.class, args);
    }

    @Override
    public void run(String... strings) {
        var utilisateur1 = new Utilisateur();
        utilisateur1.setUsername("user1");
        utilisateur1.setLastname("last1");
        utilisateur1.setFirstname("first1");
        utilisateur1.setGender("Male");

        utilisateurRepository.save(utilisateur1);


        var utilisateur2 = new Utilisateur();
        utilisateur2.setUsername("user2");
        utilisateur2.setLastname("last2");
        utilisateur2.setFirstname("first2");
        utilisateur2.setGender("Female");

        utilisateurRepository.save(utilisateur2);

        var compteEmetteur = new Compte();
        compteEmetteur.setNrCompte("010000A000001000");
        compteEmetteur.setRib("RIB1");
        compteEmetteur.setSolde(BigDecimal.valueOf(200000L));
        compteEmetteur.setUtilisateur(utilisateur1);

        compteRepository.save(compteEmetteur);

        Compte compteBenificiaire = new Compte();
        compteBenificiaire.setNrCompte("010000B025001000");
        compteBenificiaire.setRib("RIB2");
        compteBenificiaire.setSolde(BigDecimal.valueOf(140000L));
        compteBenificiaire.setUtilisateur(utilisateur2);

        compteRepository.save(compteBenificiaire);


    }
}
