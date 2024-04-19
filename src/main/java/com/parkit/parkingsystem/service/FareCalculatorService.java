package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.*;
import com.parkit.parkingsystem.model.Ticket;

import java.math.*;
import java.text.*;
import java.util.*;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket, boolean discount){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        long inHour = ticket.getInTime().getTime();
        long outHour = ticket.getOutTime().getTime();

        double duration = (outHour - inHour) / (1000. * 60 * 60 );



//        //TODO: Some tests are failing here. Need to check if this logic is correct
//        double duration = ((outHour.getTime())/(double)(60*1000) - (inHour.getTime())/(double)(60*1000))/60;
//        DecimalFormat df = new DecimalFormat("0,00");
//        df.setRoundingMode(RoundingMode.HALF_UP);

        if (duration<0.5){
            ticket.setPrice(0);
            return;
        }

        if(discount) {


            switch (ticket.getParkingSpot().getParkingType()) {
                case CAR: {
                    ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR*.95);
                    break;
                }
                case BIKE: {
                    ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR*.95);
                    break;
                }
                default:
                    throw new IllegalArgumentException("Unkown Parking Type");
            }
        }

        else {


            switch (ticket.getParkingSpot().getParkingType()) {
                case CAR: {
                    ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR );
                    break;
                }
                case BIKE: {
                    ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR );
                    break;
                }
                default:
                    throw new IllegalArgumentException("Unkown Parking Type");
            }
        }

    }


//    public void calculateFare(Ticket ticket ,String fidelity){
//        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
//            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
//        }
//
//        Date inHour = ticket.getInTime();
//        Date outHour = ticket.getOutTime();
//
//        //TODO: Some tests are failing here. Need to check if this logic is correct
//        double duration = ((outHour.getTime())/(double)(60*1000) - (inHour.getTime())/(double)(60*1000))/60;
//        DecimalFormat df = new DecimalFormat("0,00");
//        df.setRoundingMode(RoundingMode.HALF_UP);
//
//        if (duration<0.5){
//            ticket.setPrice(0);
//        }
//        else {
//
//
//            switch (ticket.getParkingSpot().getParkingType()) {
//                case CAR: {
//                    ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR * 0.95);
//                    break;
//                }
//                case BIKE: {
//                    ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR * 0.95);
//                    break;
//                }
//                default:
//                    throw new IllegalArgumentException("Unkown Parking Type");
//            }
//        }

//    }





}


