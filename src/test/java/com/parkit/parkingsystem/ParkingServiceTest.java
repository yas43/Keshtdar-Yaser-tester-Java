package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.*;
import com.parkit.parkingsystem.dao.*;
import com.parkit.parkingsystem.model.*;
import com.parkit.parkingsystem.service.*;
import com.parkit.parkingsystem.util.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;

import java.util.*;

import static com.parkit.parkingsystem.constants.ParkingType.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

    private static ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;

    @BeforeEach
    public void setUpPerTest() {
        try {
            lenient().when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
            Ticket ticket = new Ticket();
            ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");
            lenient().when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
            lenient().when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
            lenient().when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        } catch (Exception e) {
            e.printStackTrace();
            throw  new RuntimeException("Failed to set up test mock objects");
        }
    }

//    @Test
//    public void processExitingVehicleTest(){
//        parkingService.processExitingVehicle();
//        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
//    }



    @Test
    public void getVehichleType_should_return_CAR_parkingType (){
        //GIVEN a car

        when(inputReaderUtil.readSelection()).thenReturn(1);
        final ParkingType expectedResponse = ParkingType.CAR;

        //WHEN the method is triggered
        final  ParkingType response = parkingService.getVehicleType();

        //THEN the result is a car
        assertEquals(expectedResponse, response);
    }


    @Test

    public void getVehichleType_should_return_BIKE_parkingType (){
        //GIVEN A BIKE
        when(inputReaderUtil.readSelection()).thenReturn(2);
        final ParkingType expectedResponse = BIKE;
        //WHEN
        final ParkingType response = parkingService.getVehicleType();
        //THEN
        assertEquals(expectedResponse,response);

    }






    @Test

    public void getNextParkingNumberIfAvailable_should_return_a_Car_parking_spot(){
        //GIVEN
         when(inputReaderUtil.readSelection()).thenReturn(1);

         when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);

        ParkingSpot parkingSpotexpect = new ParkingSpot(1, CAR,true);

        //WHEN
        ParkingSpot parkingresponse = parkingService.getNextParkingNumberIfAvailable();

        //THEN
        assertEquals(parkingSpotexpect,parkingresponse);



    }


    @Test

    public void getNextParkingNumberIfAvailable_shouldNot_return_parking_spot(){
        //GIVEN
        when(inputReaderUtil.readSelection()).thenReturn(1);

        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(-1);

        //WHEN and THEN
         assertNull(parkingService.getNextParkingNumberIfAvailable());



    }


    @Test

    public void getNextParkingNumberIfAvailable_should_return_a_Bike_parking_spot(){
        //GIVEN
        when(inputReaderUtil.readSelection()).thenReturn(1);

        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);

        ParkingSpot parkingSpotexpect = new ParkingSpot(1, BIKE,true);

        //WHEN
        ParkingSpot parkingresponse = parkingService.getNextParkingNumberIfAvailable();

        //THEN
        assertEquals(parkingSpotexpect,parkingresponse);



    }

    @Test

    public void getNextParkingNumberIfAvailable_do_not_rise_an_exception_ThereIsNoParking(){
        //WHEN
        when(inputReaderUtil.readSelection()).thenReturn(1);

        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(-1);

        //THEN
//        assertThrows(Exception.class,()->parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class)));

        assertDoesNotThrow (()->parkingService.getNextParkingNumberIfAvailable());

    }



    @Test
//how dose it works????
    public void getNextParkingNumberIfAvailable_do_not_rise_an_exception(){
        when(inputReaderUtil.readSelection()).thenReturn(3);


        assertNull(parkingService.getNextParkingNumberIfAvailable());

    }



    @Test

    public void get_vehichle_regnumber_should_return_a_string(){
        //GIVEN
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("abcd45");

        String message = "abcd45";
        //THEN
        assertEquals(message,parkingService.getVehichleRegNumber());


    }


    @Test
    public void processIncomingVehicle(){

        // GIVEN a car enters in the parking
        final Integer carSelected = 1;
        final Integer nextAvailableSlot = 1;
        final String vehicleRegistrationNumber = "abcd45";

        when(inputReaderUtil.readSelection()).thenReturn(carSelected);
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(nextAvailableSlot);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehicleRegistrationNumber);

        // WHEN the system registers the car

        parkingService.processIncomingVehicle();

        // THEN

        ArgumentCaptor<Ticket> ticketArgumentCaptor = ArgumentCaptor.forClass(Ticket.class);
        verify(ticketDAO).saveTicket(ticketArgumentCaptor.capture());
        Ticket ticketValue = ticketArgumentCaptor.getValue();

        Assertions.assertNotNull(ticketValue);
        Assertions.assertEquals(CAR, ticketValue.getParkingSpot().getParkingType());
        Assertions.assertEquals(vehicleRegistrationNumber, ticketValue.getVehicleRegNumber());

    }




        @Test
    public void processExitingVehicleTest(){


            when(ticketDAO.ckeckDiscount(any(Ticket.class))).thenReturn(false);
//            Assertions.assertFalse(ticketDAO.ckeckDiscount(tickettest));
//            fareCalculatorService.calculateFare(tickettest,false);
//            verify(fareCalculatorService).calculateFare(any(Ticket.class),false);
//           ParkingSpot responseticket =  (ticketDAO.getTicket("ticket")).getParkingSpot();
//           ParkingSpot expectedparkingspot = new ParkingSpot(1,CAR,false);
//           Assertions.assertEquals(expectedparkingspot,responseticket);
//            Ticket testticket = ticketDAO.getTicket("ticket");
//            ParkingSpot testparkingspot = testticket.getParkingSpot();
//
//            Assertions.assertTrue(parkingSpotDAO.updateParking(testparkingspot));
//

parkingService.processExitingVehicle();



//            ParkingSpot parkingSpot = new ParkingSpot(2,CAR,true);
//            ticket.setParkingSpot(parkingSpot);
//            when(ticket.getParkingSpot()).thenReturn(parkingSpot);
//
//            Assertions.assertEquals(parkingSpot,ticket.getParkingSpot());

//           parkingService.processExitingVehicle();
//            verify(parkingService,atLeast(1)).processExitingVehicle();
//            verify(parkingService).processExitingVehicle();



ArgumentCaptor<ParkingSpot> parkingSpotArgumentCaptor = ArgumentCaptor.forClass(ParkingSpot.class);
verify(parkingSpotDAO,times(1)).updateParking(parkingSpotArgumentCaptor.capture());
 ParkingSpot response = parkingSpotArgumentCaptor.getValue();
// ParkingSpot expectedparkingspot = new ParkingSpot(1,CAR,true);
 Assertions.assertNotNull(response);
            Assertions.assertEquals(CAR, response.getParkingType());
            Assertions.assertTrue (response.getId()>0);





    }







}
