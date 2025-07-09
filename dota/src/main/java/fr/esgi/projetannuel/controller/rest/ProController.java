package fr.esgi.projetannuel.controller.rest;

import fr.esgi.projetannuel.business.Pro;
import fr.esgi.projetannuel.service.ProService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pros")
@CrossOrigin(origins = "http://localhost:5173")
public class ProController {

    @Autowired
    private ProService proService;
    
    /*@Autowired
    public ProController(ProService proService) {
        this.proService = proService;
    }*/

    @GetMapping({"", "/"})
    @Operation(summary="Récupère la liste de joueurs professionnels")
    public List<Pro> getPros() {
        return proService.recupererPros();
    }

    @GetMapping("/{id}")
    @Operation(summary="Récupère un joueur professionnel par son ID")
    public Pro getProById(
        @PathVariable("id") Long id
    ) {
        return proService.recupererPro(id);
    }
}
