package com.bolsadeideas.springboot.app.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class LocaleController {
    
    /**
     * Metodo para redirigir a la ultima ruta
     * @param request 
     * @return ruta a redirigir
     */
    @GetMapping("/locale")
    public String locale(HttpServletRequest request){
        //nos entrega la referencia de la ultima URL 
        String ultimaUrl = request.getHeader("referer");
        return "redirect:".concat(ultimaUrl);
    }
}
