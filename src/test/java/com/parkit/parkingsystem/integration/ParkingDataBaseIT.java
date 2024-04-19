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

        //given
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        //when a vehicle enter parking
        parkingService.processIncomingVehicle();

        //then

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
        // GIVEN an entering vehicle
        final String vehicleRegNumber = "ABCDEF";
        ParkingSpot parkingSpot = new ParkingSpot(1010,ParkingType.CAR,true);

//        final Ticket savedTicket = ticketDAO.getTicket(vehicleRegNumber);
//        final LocalDateTime now = LocalDateTime.now().minusHours(1);
//        final Date incomingDate = Date.from(now.toInstant(ZoneOffset.UTC));
//        savedTicket.setInTime(incomingDate);
//        ticketDAO.saveTicket(savedTicket);

//        parkingService.processIncomingVehicle();

//        final Ticket savedTicket = ticketDAO.getTicket(vehicleRegNumber);
        final LocalDateTime now = LocalDateTime.now().minusHours(2);
//        final Date incomingDate = Date.from(now.toInstant(ZoneOffset.UTC));
        final Date incomingDate = Date.from(now.toInstant(OffsetDateTime.now().getOffset()));

        Ticket savedTicket = new Ticket();
        savedTicket.setInTime(incomingDate);
        savedTicket.setParkingSpot(parkingSpot);
        savedTicket.setVehicleRegNumber(vehicleRegNumber);
//        dataBasePrepareService.clearDataBaseEntries();
        ticketDAO.saveTicket(savedTicket);

        // When the vehicle is exiting

        parkingService.processExitingVehicle();



        // THEN
        final Ticket responseTicket = ticketDAO.getTicket(vehicleRegNumber);

        assertNotNull(responseTicket.getOutTime());
        assertNotEquals(0d, responseTicket.getPrice());
        assertNotNull(responseTicket.getOutTime());

        /*


//        doThrow().when(ticket).setInTime(new Date(System.currentTimeMillis() - (5*60*60*1000)));
        testParkingACar();
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

//when( new Date()).thenReturn(new Date(System.currentTimeMillis() - (60*60*1000)));  

        parkingService.processExitingVehicle();
        //TODO: check that the fare generated and out time are populated correctly in the database

         Ticket responseTicket = ticketDAO.getTicket("ABCDEF");
         Double expectedPrice = Fare.CAR_RATE_PER_HOUR*1;
        System.out.println(responseTicket.getPrice());
            Assertions.assertEquals(expectedPrice,responseTicket.getPrice(),0.1);

//        Ticket responseTicket = ticketDAO.getTicket("ABCDEF");
//        ParkingType expectedParkingType = ParkingType.CAR;

//        Assertions.assertTrue(responseTicket.getParkingSpot().isAvailable());
//        Assertions.assertEquals(1010,parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR));

         */
    }

    @Test
    public void testFareForRegularCustumer(){
//        Firs Entry (type = CAR , RegisterNumber ="ABCDEF")


//        dataBasePrepareService.clearDataBaseEntries();
        ParkingSpot parkingSpot = new ParkingSpot(1010,ParkingType.CAR,true);

//        parkingSpotDAO.updateParking(parkingSpot);

//        final Ticket FirstTicket = ticketDAO.getTicket(vehicleRegNumber);
//
        final LocalDateTime now = LocalDateTime.now().minusHours(3);
        final Date FirstIncomingDate = Date.from(now.toInstant(OffsetDateTime.now().getOffset()));

        final LocalDateTime now_ = LocalDateTime.now().minusHours(2);
        final Date FirstExitingDate = Date.from(now_.toInstant(OffsetDateTime.now().getOffset()));

        Ticket FirstTicket = new Ticket();
        FirstTicket.setInTime(FirstIncomingDate);
        FirstTicket.setOutTime(FirstExitingDate);
        FirstTicket.setId(1010);
//        FirstTicket.setOutTime(null);
        FirstTicket.setPrice(0);
        FirstTicket.setVehicleRegNumber("ABCDEF");
        FirstTicket.setParkingSpot(parkingSpot);

        ticketDAO.saveTicket(FirstTicket);


        final LocalDateTime now__ = LocalDateTime.now().minusHours(1);
        final Date SecondIncomingDate = Date.from(now__.toInstant(OffsetDateTime.now().getOffset()));

//        final LocalDateTime now___ = LocalDateTime.now().minusHours(2);
//        final Date FirstExitingDate = Date.from(now___.toInstant(OffsetDateTime.now().getOffset()));

        Ticket SecondTicket = new Ticket();
        SecondTicket.setInTime(SecondIncomingDate);
//        SecondTicket.setOutTime(FirstExitingDate);
        SecondTicket.setId(1010);
//        FirstTicket.setOutTime(null);
//        SecondTicket.setPrice(0);
        SecondTicket.setVehicleRegNumber("ABCDEF");
        SecondTicket.setParkingSpot(parkingSpot);

        ticketDAO.saveTicket(SecondTicket);



        final String vehicleRegNumber = "ABCDEF";
//        parkingService.processIncomingVehicle();
////
//        final Ticket secondTicket = ticketDAO.getTicket(vehicleRegNumber);
//        final LocalDateTime now__ = LocalDateTime.now().minusHours(1);
//
//        final Date incomingDate = Date.from(now__.toInstant(OffsetDateTime.now().getOffset()));
//        savedTicket.setInTime(incomingDate);
//
//        ticketDAO.saveTicket(savedTicket);
//
//       // When the vehicle is exiting
//
        parkingService.processExitingVehicle();

        double expectedPrice = Fare.CAR_RATE_PER_HOUR*0.95;
        double responsePrice = ticketDAO.getTicket("ABCDEF").getPrice();
//        System.out.println("yaser  is "+ responsePrice);
//        System.out.println("gisele is "+expectedPrice);
        Assertions.assertEquals(expectedPrice ,responsePrice,0.1);
//
//
//



//        final LocalDateTime now_ = LocalDateTime.now().plusHours(3);
//        final Date FirstExitDate = Date.from(now.toInstant(OffsetDateTime.now().getOffset()));
//
//        FirstTicket.setOutTime(FirstExitDate);
//
//
//        ticketDAO.saveTicket(FirstTicket);

//
//        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
//        parkingService.processIncomingVehicle();

        //First Exit

//        final Ticket savedTicket = ticketDAO.getTicket(vehicleRegNumber);
//        final LocalDateTime now = LocalDateTime.now().minusHours(2);
//        final Date incomingDate = Date.from(now.toInstant(OffsetDateTime.now().getOffset()));
//        savedTicket.setInTime(incomingDate);
//        dataBasePrepareService.clearDataBaseEntries();
//        ticketDAO.saveTicket(savedTicket);
//
//
//        parkingService.processExitingVehicle();

        //Second Entry Same Customer


//        parkingService.processIncomingVehicle();
//
//        final Ticket savedSecondTicket = ticketDAO.getTicket(vehicleRegNumber);
//        final LocalDateTime now_ = LocalDateTime.now().plusHours(1);
//        final Date incomingDate_ = Date.from(now.toInstant(OffsetDateTime.now().getOffset()));
//        savedTicket.setInTime(incomingDate);
//        ticketDAO.saveTicket(savedTicket);

//        parkingService.processIncomingVehicle();


        //Second Exit Same Customer
//        parkingService.processExitingVehicle();


//         Ticket customerTicket = ticketDAO.getTicket("ABCDEF");
//         Double actuallPrice = customerTicket.getPrice();
//         Double expectedPrice = Fare.CAR_RATE_PER_HOUR*0.95;
//        assertEquals(expectedPrice,actuallPrice);

    }
    @Disabled
    @Test
    public void testForIrregularCustomer(){
        //        Firs Entry (type = CAR , RegisterNumber ="ABCDEF")


//        dataBasePrepareService.clearDataBaseEntries();
        ParkingSpot parkingSpot = new ParkingSpot(1010,ParkingType.CAR,true);

//        parkingSpotDAO.updateParking(parkingSpot);

//        final Ticket FirstTicket = ticketDAO.getTicket(vehicleRegNumber);
//
        final LocalDateTime now = LocalDateTime.now().minusHours(3);
        final Date FirstIncomingDate = Date.from(now.toInstant(OffsetDateTime.now().getOffset()));

        final LocalDateTime now_ = LocalDateTime.now().minusHours(2);
        final Date FirstExitingDate = Date.from(now_.toInstant(OffsetDateTime.now().getOffset()));

        Ticket FirstTicket = new Ticket();
        FirstTicket.setInTime(FirstIncomingDate);
        FirstTicket.setOutTime(FirstExitingDate);
        FirstTicket.setId(1010);
//        FirstTicket.setOutTime(null);
        FirstTicket.setPrice(0);
        FirstTicket.setVehicleRegNumber("FEDCBA");
        FirstTicket.setParkingSpot(parkingSpot);

        ticketDAO.saveTicket(FirstTicket);



        final String vehicleRegNumber = "ABCDEF";
        parkingService.processIncomingVehicle();

        final Ticket savedTicket = ticketDAO.getTicket(vehicleRegNumber);
        final LocalDateTime now__ = LocalDateTime.now().minusHours(1);

        final Date incomingDate = Date.from(now__.toInstant(OffsetDateTime.now().getOffset()));
        savedTicket.setInTime(incomingDate);

        ticketDAO.saveTicket(savedTicket);

        // When the vehicle is exiting

        parkingService.processExitingVehicle();


    }

}
