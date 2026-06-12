package esprit.driver;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trajet {


    private Long id;
    private String adresseDepart;
    private String adresseArrivee;
    private Double latitudeDepart;
    private Double longitudeDepart;
    private Double latitudeArrivee;
    private Double longitudeArrivee;
    private Long passagerId;
    private Long chauffeurId;
    private String statut;
    private String type;
    private Double distanceKm;
    private Double prixEstime;
    private String dateCreation;
    private String dateDebut;
    private String dateFin;
}

