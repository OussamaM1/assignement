package ma.octo.assignement.mapper;

import ma.octo.assignement.domain.Compte;
import ma.octo.assignement.domain.Versement;
import ma.octo.assignement.dto.VersementDto;

import java.util.Date;

public class VersementMapper {

    private VersementMapper() {
    }

    public static VersementDto convertToDto(Versement versement) {
        var versementDto = new VersementDto();
        versementDto.setId(versement.getId());
        versementDto.setNom_prenom_emetteur(versement.getNom_prenom_emetteur());
        versementDto.setNrCompteBeneficiaire(versement.getCompteBeneficiaire().getNrCompte());
        versementDto.setMotif(versement.getMotifVersement());
        versementDto.setMontantVersement(versement.getMontantVersement());
        versementDto.setDate(versement.getDateExecution());
        return versementDto;
    }


    public static Versement convertToEntity(VersementDto versementDto, Compte compteBeneficiaire) {
        var versement = new Versement();
        versement.setNom_prenom_emetteur(versementDto.getNom_prenom_emetteur());
        versement.setDateExecution(versementDto.getDate() == null ? new Date() : versementDto.getDate());
        versement.setCompteBeneficiaire(compteBeneficiaire);
        versement.setMontantVersement(versementDto.getMontantVersement());
        versement.setMotifVersement(versementDto.getMotif());
        return versement;
    }

}
