package ma.octo.assignement.service;

import ma.octo.assignement.domain.Versement;
import ma.octo.assignement.dto.VersementDto;
import ma.octo.assignement.exceptions.CompteNonExistantException;
import ma.octo.assignement.exceptions.TransactionException;
import ma.octo.assignement.exceptions.VersementNonExistantException;
import ma.octo.assignement.mapper.VersementMapper;
import ma.octo.assignement.repository.CompteRepository;
import ma.octo.assignement.repository.VersementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class VersementService {

    private static final Logger logger = LoggerFactory.getLogger(VersementService.class);

    private final VersementRepository versementRepository;
    private final CompteRepository compteRepository;
    private final AuditService auditService;

    public static final int MONTANT_MAXIMAL = 10000;

    private static final int MONTANT_MINIMAL = 10;

    @Autowired
    public VersementService(VersementRepository versementRepository, CompteRepository compteRepository, AuditService auditService) {
        this.versementRepository = versementRepository;
        this.compteRepository = compteRepository;
        this.auditService = auditService;
    }

    public List<Versement> getAllVersements() {
        logger.info("Appel de la methode getAllVersements service");
        return versementRepository.findAll();
    }

    public Versement createTransaction(VersementDto versementDto) throws CompteNonExistantException, TransactionException {
        logger.info("Appel de la methode createTransaction -Versement service");
        var compteBeneficiaire = compteRepository
                .findByNrCompte(versementDto.getNrCompteBeneficiaire());

        if (compteBeneficiaire == null) {
            logger.error("Compte benificiaire inexistant");
            throw new CompteNonExistantException("Compte benificiaire inexistant");
        }

        if (versementDto.getMontantVersement() == null || versementDto.getMontantVersement().intValue() == 0) {
            logger.error("Montant vide");
            throw new TransactionException("Montant vide");
        } else if (versementDto.getMontantVersement().intValue() < MONTANT_MINIMAL) {
            logger.error("Montant minimal de versement non atteint");
            throw new TransactionException("Montant minimal de versement non atteint");
        } else if (versementDto.getMontantVersement().intValue() > MONTANT_MAXIMAL) {
            logger.error("Montant maximal de versement dépassé");
            throw new TransactionException("Montant maximal de versement dépassé");
        }

        if (versementDto.getMotif() == null || versementDto.getMotif().isEmpty()) {
            logger.error("Motif vide");
            throw new TransactionException("Motif vide");
        }
        if (versementDto.getNom_prenom_emetteur() == null || versementDto.getNom_prenom_emetteur().isEmpty()) {
            logger.error("Nom et prenom de l'emetteur est vide");
            throw new TransactionException("Nom et prenom de l'emetteur est vide");
        }


        compteBeneficiaire
                .setSolde(new BigDecimal(compteBeneficiaire.getSolde().intValue() + versementDto.getMontantVersement().intValue()));
        compteRepository.save(compteBeneficiaire);

        var newVersement = versementRepository.save(
                VersementMapper.convertToEntity(versementDto, compteBeneficiaire)
        );

        auditService.auditVersement("Versement de " + versementDto.getNom_prenom_emetteur() + " vers " + versementDto
                .getNrCompteBeneficiaire() + " d'un montant de " + versementDto.getMontantVersement()
                .toString());

        return newVersement;
    }

    public Versement getVersementById(Long id) throws VersementNonExistantException {
        logger.info("Appel de la methode getVersementById service");
        var versement = versementRepository.findById(id);
        if (versement.isEmpty()) {
            logger.error("Virement introuvable");
            throw new VersementNonExistantException("Versement introuvable");
        }
        return versement.get();
    }
}
