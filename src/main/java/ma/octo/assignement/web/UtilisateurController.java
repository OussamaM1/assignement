package ma.octo.assignement.web;


import ma.octo.assignement.domain.Utilisateur;
import ma.octo.assignement.service.UtilisateurService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/service-utilisateur")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    private static final Logger logger = LoggerFactory.getLogger(UtilisateurController.class);

    @Autowired
    public UtilisateurController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }


    @GetMapping("utilisateurs")
    public List<Utilisateur> getAllUtilisateurs() {
        logger.info("Liste des utilisateurs");
        return utilisateurService.getAllUtilisateurs();
    }
}
