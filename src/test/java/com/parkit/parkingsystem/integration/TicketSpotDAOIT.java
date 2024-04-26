package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.*;
import com.parkit.parkingsystem.dao.*;
import com.parkit.parkingsystem.integration.config.*;
import com.parkit.parkingsystem.integration.service.*;
import com.parkit.parkingsystem.model.*;
import com.parkit.parkingsystem.service.*;
import com.parkit.parkingsystem.util.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.time.*;
import java.util.*;

import static org.mockito.Mockito.lenient;

public class TicketSpotDAOIT {
    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    private static Ticket ticket;

    @BeforeAll
    public static void setUp() throws Exception{
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();

         ticket = new Ticket();

    }
//
    @BeforeEach
    public void setUpPerTest() throws Exception {
        dataBasePrepareService.clearDataBaseEntries();
    }


    @Test
    public void testSaveTicket(){
        //given
        ParkingSpot parkingSpot = new ParkingSpot(1010, ParkingType.CAR,true);
        Date incommingTime = new Date();
        final LocalDateTime now = LocalDateTime.now().plusHours(2);
        final Date outcommingTime = Date.from(now.toInstant(OffsetDateTime.now().getOffset()));
        ticket.setParkingSpot(parkingSpot);
        ticket.setInTime(incommingTime);
        ticket.setOutTime(outcommingTime);
        ticket.setVehicleRegNumber("AB45cD");
        ticket.setPrice(12.5);


        //when
        ticketDAO.saveTicket(ticket);

        //then
       Ticket responseTicket = ticketDAO.getTicket("AB45cD");
        String expectedregnumber = "AB45cD";


       Assertions.assertEquals(parkingSpot,responseTicket.getParkingSpot());
       Assertions.assertEquals(12.5d,responseTicket.getPrice());
       Assertions.assertNotNull(responseTicket.getOutTime());
       Assertions.assertNotNull(responseTicket.getInTime());
       Assertions.assertEquals(expectedregnumber,responseTicket.getVehicleRegNumber());
       Assertions.assertNotNull(responseTicket);
       Assertions.assertDoesNotThrow(()->ticketDAO.saveTicket(ticket));
       Assertions.assertDoesNotThrow(()->ticketDAO.getTicket("AB45cD"));
        Assertions.assertFalse(ticketDAO.saveTicket(null));
        Assertions.assertDoesNotThrow(()->ticketDAO.getTicket(null));
        Assertions.assertDoesNotThrow(()->ticketDAO.saveTicket(null));



    }

    @Test
    public void testUpdateTicket(){

        //given
        ParkingSpot parkingSpot = new ParkingSpot(1010, ParkingType.CAR,true);
        Date incommingTime = new Date();
        final LocalDateTime now = LocalDateTime.now().plusHours(2);
        final Date outcommingTime = Date.from(now.toInstant(OffsetDateTime.now().getOffset()));
        ticket.setParkingSpot(parkingSpot);
        ticket.setInTime(incommingTime);


        ticket.setVehicleRegNumber("AB45cD");




        ticketDAO.saveTicket(ticket);


        Ticket ticketSaved = ticketDAO.getTicket("AB45cD");
        System.out.println("test");


        //when

                ticketSaved.setOutTime(outcommingTime);
                ticketSaved.setPrice(12.5);



                ticketDAO.updateTicket(ticketSaved);

                //then

        Ticket responseTicket = ticketDAO.getTicket("AB45cD");
        String expectedregnumber = "AB45cD";


        Assertions.assertEquals(parkingSpot,responseTicket.getParkingSpot());
        Assertions.assertEquals(12.5d,responseTicket.getPrice());
        Assertions.assertNotNull(responseTicket.getOutTime());
        Assertions.assertNotNull(responseTicket.getInTime());
        Assertions.assertEquals(expectedregnumber,responseTicket.getVehicleRegNumber());
        Assertions.assertNotNull(responseTicket);

        
    }
}
