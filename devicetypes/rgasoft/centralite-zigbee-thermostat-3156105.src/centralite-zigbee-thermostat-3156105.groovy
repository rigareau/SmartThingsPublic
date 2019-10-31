/**
 *  Centralite Zigbee Thermostat 3156105
 *
 *  Copyright 2019 Richard Gareau
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 */
metadata {
	definition (name: "Centralite Zigbee Thermostat 3156105", namespace: "RGASoft", author: "Richard Gareau", cstHandler: true) {
		capability "Temperature Measurement"
		capability "Thermostat Heating Setpoint"
        capability "Thermostat Cooling Setpoint"
        capability "Thermostat Fan Mode"
        capability "Thermostat Mode"
        capability "Configuration"
        capability "Health Check"
        capability "Refresh"
        
        fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0201, 0204", outClusters: "0402", manufacturer: "Centralite", model: "3156105", deviceJoinName: "Centralite Thermostat 3156105"
	}


	simulator {
		// TODO: define status and reply messages here
	}

	tiles {
    	valueTile("temperature", "device.temperature", width: 2, height: 2) {
			state("temperature", label:'${currentValue}°', unit:"F",
				backgroundColors:[
					[value: 31, color: "#153591"],
					[value: 44, color: "#1e9cbb"],
					[value: 59, color: "#90d2a7"],
					[value: 74, color: "#44b621"],
					[value: 84, color: "#f1d801"],
					[value: 95, color: "#d04e00"],
					[value: 96, color: "#bc2323"]
				]
			)
		}
     
        valueTile("heatingSetpoint", "device.heatingSetpoint", width: 2, height: 2) {
			state "heatingSetpoint", label:'Setpoint ${currentValue}°', backgroundColors:[
					// Celsius
					[value: 0, color: "#153591"],
					[value: 7, color: "#1e9cbb"],
					[value: 15, color: "#90d2a7"],
					[value: 23, color: "#44b621"],
					[value: 28, color: "#f1d801"],
					[value: 35, color: "#d04e00"],
					[value: 37, color: "#bc2323"],
					// Fahrenheit
					[value: 40, color: "#153591"],
					[value: 44, color: "#1e9cbb"],
					[value: 59, color: "#90d2a7"],
					[value: 74, color: "#44b621"],
					[value: 84, color: "#f1d801"],
					[value: 95, color: "#d04e00"],
					[value: 96, color: "#bc2323"]
				]
		}
	}
    
    main("temperature")
    details(["temperature"])
}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing --- Start"
	
    log.debug "parse got called with '${description}'"
    
    // TODO: handle 'temperature' attribute
    
	// TODO: handle 'heatingSetpoint' attribute
	
    log.debug "Parsing --- Done"
}

// handle commands
def setHeatingSetpoint(Double heatSetPoint) {
	log.debug "setHeatingSetpoint --- Start"
	
    log.debug "Setting heat set point to " + heatSetPoint
    // TODO: handle 'setHeatingSetpoint' command
    
    log.debug "setHeatingSetpoint --- Done"
}

def setCoolingSetpoint(Double coolSetPoint) {
	log.debug "setCoolingSetpoint --- Start"
	
    log.debug "Setting cooling set point to " + coolSetPoint
    // TODO: handle 'setCoolingSetpoint' command
    
    log.debug "setCoolingSetpoint --- Done"
}

def fanAuto(){
	log.debug "fanAuto --- Start"
    
    log.debug "fanAuto --- Done"
}

def fanCirculate(){
	log.debug "fanCirculate --- Start"
    
    log.debug "fanCirculate --- Done"
}

def fanOn(){
	log.debug "fanOn --- Start"
    
    log.debug "fanOn --- Done"
}

def setThermostatFanMode(String mode){
	log.debug "setThermostatFanMode --- Start"
    
    log.debug "Received mode: " + mode
    
    log.debug "setThermostatFanMode --- Done"
}

def refresh()
{
	log.debug "refresh --- Start"

	log.debug "refresh --- Done"
}

def configure() {
	log.debug "configure --- Start"

	log.debug "configure --- Done"
}

def ping() {
	log.debug "ping --- Start"
    
    log.debug "ping --- Done"
}