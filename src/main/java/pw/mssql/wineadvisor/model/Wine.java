package pw.mssql.wineadvisor.model;

import lombok.*;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(exclude = "alcoholPercentage")
public class Wine {

    @Getter
    @Setter
    private int alcoholPercentage;

    @Getter
    @Setter
    private String grapeVariety;

    @Getter
    @Setter
    private String wineType;

    @Getter
    @Setter
    private String wineDryness;

    @Getter
    @Setter
    private String wineOrigin;

    @Getter
    @Setter
    private String purpose;
}