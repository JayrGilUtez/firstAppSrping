package mx.edu.utez.firstapp.controllers.person.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.firstapp.controllers.user.UserDto;
import mx.edu.utez.firstapp.models.person.Person;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor

public class PersonDto {
    private Long id;
    @NotEmpty
    @Size(min = 3,max = 40, message = "NameSizer")
    private String name;
    @NotEmpty
    private String surname;
    private String lastname;
    @NotEmpty
    private LocalDate birthDate;
    @NotEmpty
    private String curp;
    private UserDto user;


    public Person toEntity(){
        if(user != null){
            return new Person(name, surname, lastname, birthDate,curp,user.toEntity());
        }
        return new Person(name,surname,lastname,birthDate,curp);
    }

}
