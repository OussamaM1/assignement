package ma.octo.assignement.web;

import ma.octo.assignement.dto.VirementDto;
import ma.octo.assignement.exceptions.CompteNonExistantException;
import ma.octo.assignement.exceptions.TransactionException;
import ma.octo.assignement.exceptions.VirementNonExistantException;
import ma.octo.assignement.mapper.VirementMapper;
import ma.octo.assignement.service.VirementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/service-virement")
class VirementController {

    private static final Logger logger = LoggerFactory.getLogger(VirementController.class);

    private final VirementService virementService;

    @Autowired
    VirementController(VirementService virementService) {
        this.virementService = virementService;
    }


    @GetMapping("virements")
    public List<VirementDto> getAllVirements() {
        logger.info("Liste des virements");
        var virementsDTO = virementService.getAllVirements().stream()
                .map(VirementMapper::convertToDto).collect(Collectors.toList());
        if (virementsDTO.isEmpty()) {
            logger.warn("La liste des virements est vide.");
        }
        return virementsDTO;
    }

    @GetMapping("virements/{id}")
    public VirementDto getVirementById(@PathVariable("id") Long id) throws VirementNonExistantException {
        logger.info("Recuperer un virement par son id {}", id);
        var virement = virementService.getVirementById(id);
        if (virement.isEmpty()) {
            logger.error("Virement introuvable");
            throw new VirementNonExistantException("Virement introuvable");
        }
        return VirementMapper.convertToDto(virement.get());
    }


    @PostMapping("/virements")
    @ResponseStatus(HttpStatus.CREATED)
    public VirementDto createTransaction(@RequestBody VirementDto virementDto)
            throws CompteNonExistantException, TransactionException {
        logger.info("Creation d'une nouvelle transaction -Virements");
        return VirementMapper.convertToDto(virementService.createTransaction(virementDto));
    }

}
