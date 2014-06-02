package lejos.internal.brickpi;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

import lejos.hardware.LED;

public class BrickPiLED implements LED {
	private GpioController gpio;;
	private GpioPinDigitalOutput pin1, pin2;
	
	@Override
	public void setPattern(int pattern) {
        if (gpio == null) gpio = GpioFactory.getInstance();
        if (pin1 == null) pin1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "", PinState.LOW);
        if (pin2 == null)  pin2 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, "", PinState.LOW);
        
        switch (pattern) {
        case 0:
        	pin1.low();
        	pin2.low();
        	break;
        case 1:
        	pin1.high();
        	pin2.low();
        	break;
        case 2:
        	pin1.low();
        	pin2.high();
        	break;
        case 3:
        	pin1.high();
        	pin2.high();
        	break;
        }
	}
}
