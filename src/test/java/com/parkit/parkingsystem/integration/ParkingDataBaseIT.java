package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.*;
import com.parkit.parkingsystem.constants.*;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.*;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.*;

import java.time.*;
import java.util.*;

import static junit.framework.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    private ParkingService parkingService;

    @BeforeAll
    public static void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();

    }

    @BeforeEach
    public void setUpPerTest() throws Exception {
        lenient().when(inputReaderUtil.readSelection()).thenReturn(1);
        lenient().when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();

        parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
    }

    @AfterAll
    public static void tearDown(){

    }

    @Test
    public void testParkingACar(){

        //GIVEN
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        //WHEN A VEHICLE ENTER PARKING
        parkingService.processIncomingVehicle();

        //THEN

        Ticket expectedticket = ticketDAO.getTicket("ABCDEF");
        Ticket responseticket = ticketDAO.getTicket("ABCDEF");
        assertNotNull(responseticket);
        assertEquals(expectedticket.getId(),responseticket.getId());
        assertEquals(0,responseticket.getPrice());
        assertFalse(responseticket.getParkingSpot().isAvailable());
        assertEquals(expectedticket.getVehicleRegNumber(),responseticket.getVehicleRegNumber());
        ParkingType parkingTypeexpected = ParkingType.CAR;
        assertEquals(parkingTypeexpected,responseticket.getParkingSpot().getParkingType());

        //TODO: check that a ticket is actualy saved in DB and Parking table is updated with availability


    }



    @Test
    public void testParkingLotExit(){
        // GIVEN AN ENTERING VEHICLE
        final String vehicleRegNumber = "ABCDEF";
        ParkingSpot parkingSpot = new ParkingSpot(1010,ParkingType.CAR,true);


        final LocalDateTime now = LocalDateTime.now().minusHours(2);

        final Date incomingDate = Date.from(now.toInstant(OffsetDateTime.now().getOffset()));

        Ticket savedTicket = new Ticket();
        savedTicket.setInTime(incomingDate);
        savedTicket.setParkingSpot(parkingSpot);
        savedTicket.setVehicleRegNumber(vehicleRegNumber);

        ticketDAO.saveTicket(savedTicket);

        // WHEN THE VEHICLE IS EXITING

        parkingService.processExitingVehicle();



        // THEN
        final Ticket responseTicket = ticketDAO.getTicket(vehicleRegNumber);

        assertNotNull(responseTicket.getOutTime());
        assertNotEquals(0d, responseTicket.getPrice());
        assertNotNull(responseTicket.getOutTime());





        //TODO: check that the fare generated and out time are populated correctly in the database




    }

    @Test
    public void testFareForRegularCustumer(){
//        FIRST ENTRY-EXIT VEHICLE (type = CAR , RegisterNumber ="ABCDEF")



        ParkingSpot parkingSpot = new ParkingSpot(1010,ParkingType.CAR,true);



        final LocalDateTime now = LocalDateTime.now().minusHours(3);
        final Date FirstIncomingDate = Date.from(now.toInstant(OffsetDateTime.now().getOffset()));

        final LocalDateTime now_ = LocalDateTime.now().minusHours(2);
        final Date FirstExitingDate = Date.from(now_.toInstant(OffsetDateTime.now().getOffset()));

        Ticket FirstTicket = new Ticket();
        FirstTicket.setInTime(FirstIncomingDate);
        FirstTicket.setOutTime(FirstExitingDate);

        FirstTicket.setPrice(0);
        FirstTicket.setVehicleRegNumber("ABCDEF");
        FirstTicket.setParkingSpot(parkingSpot);

        ticketDAO.saveTicket(FirstTicket);

        //SECOND ENTRY-EXIT VEHICLE
        final LocalDateTime now__ = LocalDateTime.now().minusHours(1);
        final Date SecondIncomingDate = Date.from(now__.toInstant(OffsetDateTime.now().getOffset()));



        Ticket SecondTicket = new Ticket();
        SecondTicket.setInTime(SecondIncomingDate);

        SecondTicket.setVehicleRegNumber("ABCDEF");
        SecondTicket.setParkingSpot(parkingSpot);

        ticketDAO.saveTicket(SecondTicket);






//       //WHEN THE VEHICLE EXITING

        parkingService.processExitingVehicle();

        //THEN

        double expectedPrice = Fare.CAR_RATE_PER_HOUR*0.95;
        double responsePrice = ticketDAO.getTicket("ABCDEF").getPrice();

        Assertions.assertEquals(expectedPrice ,responsePrice,0.01);




    }

    @Test
    public void testForIrregularCustomer(){
        // FIRST ENTRY  (type = CAR , RegisterNumber ="ABCDEF")



        ParkingSpot parkingSpot = new ParkingSpot(1010,ParkingType.CAR,true);



        final LocalDateTime now = LocalDateTime.now().minusHours(3);
        final Date FirstIncomingDate = Date.from(now.toInstant(OffsetDateTime.now().getOffset()));

        final LocalDateTime now_ = LocalDateTime.now().minusHours(2);
        final Date FirstExitingDate = Date.from(now_.toInstant(OffsetDateTime.now().getOffset()));

        Ticket FirstTicket = new Ticket();
        FirstTicket.setInTime(FirstIncomingDate);
        FirstTicket.setOutTime(FirstExitingDate);

        FirstTicket.setPrice(0);
        FirstTicket.setVehicleRegNumber("FEDCBA");
        FirstTicket.setParkingSpot(parkingSpot);

        ticketDAO.saveTicket(FirstTicket);


        // WHEN ANOTHER VEHICLE GET ENTER
//
        final LocalDateTime now__ = LocalDateTime.now().minusHours(1);
        final Date SecondVehicleIncomingDate = Date.from(now__.toInstant(OffsetDateTime.now().getOffset()));
        final String vehicleRegNumber = "ABCDEF";

        Ticket SecondTicket = new Ticket();
        SecondTicket.setInTime(SecondVehicleIncomingDate);
        SecondTicket.setVehicleRegNumber(vehicleRegNumber);
        SecondTicket.setParkingSpot(parkingSpot);

        ticketDAO.saveTicket(SecondTicket);



        parkingService.processExitingVehicle();

        //THEN

        double expectedPrice = Fare.CAR_RATE_PER_HOUR;
        double responsePrice = ticketDAO.getTicket("ABCDEF").getPrice();

        Assertions.assertEquals(expectedPrice ,responsePrice,0.001);


    }

}
