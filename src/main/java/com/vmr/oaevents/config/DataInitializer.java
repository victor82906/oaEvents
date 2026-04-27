package com.vmr.oaevents.config;

import com.vmr.oaevents.model.Recinto;
import com.vmr.oaevents.model.Rol;
import com.vmr.oaevents.service.RecintoService;
import com.vmr.oaevents.service.RolService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RecintoService recintoService;
    private final RolService rolService;

    @Override
     public void run(String... args) throws Exception {

        Rol recintoRol = rolService.findByNombre("RECINTO");

        Recinto recinto = new Recinto();
        recinto.setEmail("oaevents@gmail.com");
        recinto.setContrasena("vmr2908006");
        recinto.setRol(recintoRol);
        recinto.setUbicacion("Jaen");
        recinto.setTelefono("622706214");
        recinto.setNombre("Olivo Arena");

        recintoService.save(recinto);

    }


}
