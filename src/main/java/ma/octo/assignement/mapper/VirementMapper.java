package ma.octo.assignement.mapper;

import ma.octo.assignement.domain.Compte;
import ma.octo.assignement.domain.Virement;
import ma.octo.assignement.dto.VirementDto;
import ma.octo.assignement.repository.CompteRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class VirementMapper {

    public static VirementDto convertToDto(Virement virement) {
        var virementDto = new VirementDto();
        virementDto.setId(virement.getId());
        virementDto.setNrCompteEmetteur(virement.getCompteEmetteur().getNrCompte());
        virementDto.setNrCompteBeneficiaire(virement.getCompteBeneficiaire().getNrCompte());
        virementDto.setDate(virement.getDateExecution());
        virementDto.setMotif(virement.getMotifVirement());
        virementDto.setMontantVirement(virement.getMontantVirement());
        return virementDto;
    }

    public static Virement convertToEntity(VirementDto virementDto, Compte compteBeneficiaire, Compte compteEmetteur) {
        var virement = new Virement();

        virement.setDateExecution(virementDto.getDate() == null ? new Date() : virementDto.getDate());
        virement.setCompteBeneficiaire(compteBeneficiaire);
        virement.setCompteEmetteur(compteEmetteur);
        virement.setMontantVirement(virementDto.getMontantVirement());
        virement.setMotifVirement(virementDto.getMotif());
        return virement;
    }
}
