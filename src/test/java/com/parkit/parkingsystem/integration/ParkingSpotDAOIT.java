package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.*;
import com.parkit.parkingsystem.dao.*;
import com.parkit.parkingsystem.integration.config.*;
import com.parkit.parkingsystem.integration.service.*;
import com.parkit.parkingsystem.model.*;
import com.parkit.parkingsystem.service.*;
import org.junit.jupiter.api.*;

import static org.mockito.Mockito.lenient;


public class ParkingSpotDAOIT {
    private static ParkingSpotDAO parkingSpotDAO;
    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static DataBasePrepareService dataBasePrepareService;


    @BeforeAll
    public static void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
//        ticketDAO = new TicketDAO();
//        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();

    }

    @BeforeEach
    public void setUpPerTest() throws Exception {
//        lenient().when(inputReaderUtil.readSelection()).thenReturn(1);
//        lenient().when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();

//        parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
    }





    @Test
    public void testGetNextAvailableSlot(){

//        dataBasePrepareService.clearDataBaseEntries();

        //given
        ParkingType parkingType = ParkingType.CAR;

        //when


       int responseParkingNumber = parkingSpotDAO.getNextAvailableSlot(parkingType);


       //then
       Assertions.assertNotSame(0,responseParkingNumber);
       Assertions.assertDoesNotThrow(()->parkingSpotDAO.getNextAvailableSlot(parkingType));
       Assertions.assertEquals(-1,parkingSpotDAO.getNextAvailableSlot(null));
       Assertions.assertDoesNotThrow(()->parkingSpotDAO.getNextAvailableSlot(null));


    }


    @Test
    public void testUpdateParking(){
        //given
        // (parking spot exist)
        ParkingSpot parkingSpot = new ParkingSpot(10,ParkingType.CAR,false);
        //(parking spot don't exist)
        ParkingSpot parkingSpot2 = new ParkingSpot(1010,ParkingType.CAR,false);

        //when
//        parkingSpotDAO.updateParking(parkingSpot);

        //then
//        System.out.println( parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR));
//        Assertions.assertThrows(Exception.class,()->parkingSpotDAO.updateParking(parkingSpot),"hh");
        Assertions.assertFalse(parkingSpotDAO.updateParking(parkingSpot));

        Assertions.assertTrue(parkingSpotDAO.updateParking(parkingSpot2));

    }
}
