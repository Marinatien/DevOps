package com.example.location;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
public class LocationWebService {

    private final List<Car> cars = new ArrayList<>();

    public LocationWebService() {
        cars.add(new Car("AA11CC", "Ferrari", 100));
        cars.add(new Car("11AA22", "Tesla", 60));
        cars.add(new Car("BB22CC", "Peugeot", 30));
        cars.add(new Car("A1B2C3", "Renault", 40));
    }

    @GetMapping("/bonjour")
    public String disBonjour() {
        return "Bonjour";
    }

    /* Liste des voitures disponibles */
    @GetMapping("/cars")
    @ResponseStatus(HttpStatus.OK)
    public List<Car> listOfCars() {
        List<Car> availableCars = new ArrayList<>();
        for (Car car : cars) {
            if (!car.isRented()) {
                availableCars.add(car);
            }
        }
        return availableCars;
    }

    /* Données d'une voiture par plaque */
    @GetMapping("/cars/{plateNumber}")
    @ResponseStatus(HttpStatus.OK)
    public Car aCar(@PathVariable("plateNumber") String plateNumber) {
        return cars.stream()
                .filter(c -> c.getPlateNumber().equalsIgnoreCase(plateNumber))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Voiture non trouvée"));
    }

    /* Louer ou restituer une voiture */
    @PutMapping("/cars/{plateNumber}")
    @ResponseStatus(HttpStatus.OK)
    public void rent(
            @PathVariable("plateNumber") String plateNumber,
            @RequestParam(value = "rent") boolean rent,
            @RequestBody(required = false) Dates dates) {

        Car car = aCar(plateNumber);

        if (rent) {
            if (car.isRented()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "La voiture est déjà louée");
            }
            car.setRented(true);
            car.setDates(dates);
        } else {
            if (!car.isRented()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La voiture n'est pas louée");
            }
            car.setRented(false);
            car.setDates(null);
        }
    }
}