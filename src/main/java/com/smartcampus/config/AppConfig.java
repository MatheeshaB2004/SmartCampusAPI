/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.config;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 *
 * @author MATHEESHA
 */
@ApplicationPath("/api/v1")
public class AppConfig extends Application {
    // Jersey will auto-scan and register all @Provider and @Path classes
    // No manual registration needed if you use the default scanning
}