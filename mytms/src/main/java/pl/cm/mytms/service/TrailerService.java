package pl.cm.mytms.service;

import pl.cm.mytms.model.Trailer;

import java.util.List;

public interface TrailerService extends RegistrableUnitService {

    List<Trailer> getAll();
}
