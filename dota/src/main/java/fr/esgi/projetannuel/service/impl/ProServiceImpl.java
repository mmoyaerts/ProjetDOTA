package fr.esgi.projetannuel.service.impl;

import fr.esgi.projetannuel.exception.ProInexistantExeption;
import fr.esgi.projetannuel.business.Pro;
import fr.esgi.projetannuel.repository.ProRepository;
import fr.esgi.projetannuel.service.ProService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProServiceImpl implements ProService {
    private ProRepository proRepository;

    public ProServiceImpl(ProRepository proRepository) {
        this.proRepository = proRepository;
    }

    @Override
    public Pro recupererPro(Long idPro) {
        return proRepository.findById(idPro).orElseThrow(
                () -> new ProInexistantExeption()
        );
    }

    @Override
    public List<Pro> recupererPros() {
        return proRepository.findAll();
    }
}
