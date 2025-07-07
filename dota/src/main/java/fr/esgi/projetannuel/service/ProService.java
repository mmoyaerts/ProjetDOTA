package fr.esgi.projetannuel.service;

import fr.esgi.projetannuel.business.Pro;

import java.util.List;

public interface ProService {
    Pro recupererPro(Long idPro);

    List<Pro> recupererPros();
}
