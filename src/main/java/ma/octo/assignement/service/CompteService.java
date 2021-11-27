package ma.octo.assignement.service;

import ma.octo.assignement.domain.Compte;
import ma.octo.assignement.repository.CompteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class CompteService {

    private final CompteRepository compteRepository;

    private static final Logger logger = LoggerFactory.getLogger(CompteService.class);


    public CompteService(CompteRepository compteRepository) {
        this.compteRepository = compteRepository;
    }


    public List<Compte> getAllComptes() {
        logger.info("Appel de la methode getAllComptes service");
        return compteRepository.findAll();
    }
}
