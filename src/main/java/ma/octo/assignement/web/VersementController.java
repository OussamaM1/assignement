package ma.octo.assignement.web;


import ma.octo.assignement.dto.VersementDto;
import ma.octo.assignement.exceptions.CompteNonExistantException;
import ma.octo.assignement.exceptions.TransactionException;
import ma.octo.assignement.exceptions.VersementNonExistantException;
import ma.octo.assignement.mapper.VersementMapper;
import ma.octo.assignement.service.VersementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/service-versement")
public class VersementController {

    private static final Logger logger = LoggerFactory.getLogger(VersementController.class);

    private final VersementService versementService;

    @Autowired
    public VersementController(VersementService versementService) {
        this.versementService = versementService;
    }

    @GetMapping("versements")
    public List<VersementDto> getAllVersements() {
        logger.info("Liste des versements");
        var versementsDto = versementService.getAllVersements().stream()
                .map(VersementMapper::convertToDto).collect(Collectors.toList());
        if (versementsDto.isEmpty()) {
            logger.warn("La liste des versements est vide.");
        }
        return versementsDto;
    }

    @GetMapping("versements/{id}")
    public VersementDto getVersementById(@PathVariable("id") Long id) throws VersementNonExistantException {
        logger.info("Recuperer un versement par son id {}", id);
        return VersementMapper.convertToDto(versementService.getVersementById(id));
    }

    @PostMapping("/versements")
    @ResponseStatus(HttpStatus.CREATED)
    public VersementDto createTransaction(@RequestBody VersementDto versementDto)
            throws CompteNonExistantException, TransactionException {
        logger.info("Creation d'une nouvelle transaction -Versement");
        return VersementMapper.convertToDto(versementService.createTransaction(versementDto));
    }

}
