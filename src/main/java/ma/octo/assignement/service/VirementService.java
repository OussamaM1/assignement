package ma.octo.assignement.service;

import ma.octo.assignement.domain.Virement;
import ma.octo.assignement.dto.VirementDto;
import ma.octo.assignement.exceptions.CompteNonExistantException;
import ma.octo.assignement.exceptions.TransactionException;
import ma.octo.assignement.exceptions.VirementNonExistantException;
import ma.octo.assignement.mapper.VirementMapper;
import ma.octo.assignement.repository.CompteRepository;
import ma.octo.assignement.repository.VirementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class VirementService {

    private final VirementRepository virementRepository;

    private final CompteRepository compteRepository;

    private final AuditService auditService;

    private static final Logger logger = LoggerFactory.getLogger(VirementService.class);

    public static final int MONTANT_MAXIMAL = 10000;

    private static final int MONTANT_MINIMAL = 10;

    @Autowired
    public VirementService(VirementRepository virementRepository, CompteRepository compteRepository, AuditService auditService) {
        this.virementRepository = virementRepository;
        this.compteRepository = compteRepository;
        this.auditService = auditService;
    }

    public List<Virement> getAllVirements() {
        logger.info("Appel de la methode getAllVirements service");
        return virementRepository.findAll();
    }

    public Virement createTransaction(VirementDto virementDto) throws CompteNonExistantException, TransactionException {
        logger.info("Appel de la methode createTransaction -Virement service");
        var compteEmetteur = compteRepository.findByNrCompte(virementDto.getNrCompteEmetteur());
        var compteBeneficiaire = compteRepository
                .findByNrCompte(virementDto.getNrCompteBeneficiaire());


        if (compteEmetteur == null) {
            logger.error("Compte emetteur inexistant");
            throw new CompteNonExistantException("Compte emetteur inexistant");
        }

        if (compteBeneficiaire == null) {
            logger.error("Compte benificiaire inexistant");
            throw new CompteNonExistantException("Compte benificiaire inexistant");
        }

        if (compteBeneficiaire.equals(compteEmetteur)) {
            logger.error("Transaction vers le meme compte");
            throw new TransactionException("Transaction vers le meme compte");
        }

        if (virementDto.getMontantVirement() == null || virementDto.getMontantVirement().intValue() == 0) {
            logger.error("Montant vide");
            throw new TransactionException("Montant vide");
        } else if (virementDto.getMontantVirement().intValue() < MONTANT_MINIMAL) {
            logger.error("Montant minimal de virement non atteint");
            throw new TransactionException("Montant minimal de virement non atteint");
        } else if (virementDto.getMontantVirement().intValue() > MONTANT_MAXIMAL) {
            logger.error("Montant maximal de virement dépassé");
            throw new TransactionException("Montant maximal de virement dépassé");
        }

        if (virementDto.getMotif() == null || virementDto.getMotif().isEmpty()) {
            logger.error("Motif vide");
            throw new TransactionException("Motif vide");
        }

        if (compteEmetteur.getSolde().intValue() - virementDto.getMontantVirement().intValue() < 0) {
            logger.error("Solde insuffisant pour l'utilisateur");
            throw new TransactionException("Solde insuffisant pour l'utilisateur");
        }


        compteEmetteur.setSolde(compteEmetteur.getSolde().subtract(virementDto.getMontantVirement()));
        compteRepository.save(compteEmetteur);

        compteBeneficiaire
                .setSolde(new BigDecimal(compteBeneficiaire.getSolde().intValue() + virementDto.getMontantVirement().intValue()));
        compteRepository.save(compteBeneficiaire);


        var newVirement = virementRepository.save(
                VirementMapper.convertToEntity(virementDto, compteBeneficiaire, compteEmetteur)
        );

        auditService.auditVirement("Virement depuis " + virementDto.getNrCompteEmetteur() + " vers " + virementDto
                .getNrCompteBeneficiaire() + " d'un montant de " + virementDto.getMontantVirement()
                .toString());

        return newVirement;
    }

    public Optional<Virement> getVirementById(Long id){
        logger.info("Appel de la methode getVirementById service");
        return virementRepository.findById(id);
    }

}
